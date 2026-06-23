#!/usr/bin/env bash
#
# Check that every 64-bit native library inside an Android AAB / APK has
# 16KB-aligned ELF LOAD segments. This is the alignment Google Play actually
# enforces on SDK-35 uploads; `zipalign -c -P 16` is misleading because it
# exempts compressed entries and reports "Verification successful" even when
# the LOAD segments are 4KB.
#
# Usage:
#   scripts/check-16kb-alignment.sh <path-to.aab|apk> [abi1 abi2 ...]
#
# Default ABIs are the 64-bit ones (arm64-v8a, x86_64) since 16KB pages only
# exist on 64-bit devices. Override the list to inspect 32-bit too.
#
# Exits 0 if every checked .so has LOAD align 0x4000. Exits 1 otherwise and
# prints a per-lib failure summary.

set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <path-to.aab|apk> [abi ...]" >&2
  exit 2
fi

BUNDLE="$1"
shift
ABIS=("$@")
if [[ ${#ABIS[@]} -eq 0 ]]; then
  ABIS=(arm64-v8a x86_64)
fi

if [[ ! -f "$BUNDLE" ]]; then
  echo "error: $BUNDLE not found" >&2
  exit 2
fi

# Locate llvm-readelf from the Android NDK. We prefer the one bundled with
# the SDK so we never disagree with what ndk-build produced.
find_readelf() {
  local exe=llvm-readelf
  case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) exe=llvm-readelf.exe ;; esac

  # On Windows, env vars come back with backslashes (C:\Users\...). Bash's
  # [[ -d ]] and find don't handle those — normalize to forward slashes.
  norm() { printf '%s' "${1//\\//}"; }

  local roots=()
  [[ -n "${ANDROID_HOME:-}" ]] && roots+=("$(norm "$ANDROID_HOME")/ndk")
  [[ -n "${ANDROID_SDK_ROOT:-}" ]] && roots+=("$(norm "$ANDROID_SDK_ROOT")/ndk")
  [[ -n "${LOCALAPPDATA:-}" ]] && roots+=("$(norm "$LOCALAPPDATA")/Android/Sdk/ndk")
  [[ -d "$HOME/Library/Android/sdk/ndk" ]] && roots+=("$HOME/Library/Android/sdk/ndk")
  [[ -d "$HOME/Android/Sdk/ndk" ]] && roots+=("$HOME/Android/Sdk/ndk")

  local r found
  for r in "${roots[@]}"; do
    [[ -d "$r" ]] || continue
    # Highest-numbered NDK first
    # NDK lays llvm-readelf out at <ndk>/<version>/toolchains/llvm/prebuilt/<host>/bin/llvm-readelf
    # — depth 7 from the ndk root. Use 8 to leave headroom.
    found=$(find "$r" -maxdepth 8 -type f \( -name "$exe" -o -name llvm-readelf \) 2>/dev/null \
            | sort -rV | head -1 || true)
    if [[ -n "$found" ]]; then
      echo "$found"
      return 0
    fi
  done

  # Fall back to PATH (some Linux distros ship llvm-readelf directly)
  if command -v llvm-readelf >/dev/null 2>&1; then
    command -v llvm-readelf
    return 0
  fi

  return 1
}

READELF=$(find_readelf) || {
  echo "error: cannot locate llvm-readelf. Install Android NDK or set ANDROID_HOME." >&2
  exit 2
}

TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

# Where in the archive the libs live: AABs nest under base/lib/<abi>/, APKs put
# them at lib/<abi>/. Detect by sampling the entry list.
listing=$(unzip -l "$BUNDLE")
if grep -q '   base/lib/' <<<"$listing"; then
  LIB_PREFIX=base/lib
elif grep -q '   lib/' <<<"$listing"; then
  LIB_PREFIX=lib
else
  echo "error: $BUNDLE contains no native libs (no base/lib/ or lib/ entries)" >&2
  exit 2
fi

failures=0
total=0

printf "%-42s %-13s %-10s %s\n" "LIBRARY" "ABI" "LOAD ALIGN" "STATUS"
printf "%-42s %-13s %-10s %s\n" "------------------------------------------" "-------------" "----------" "------"

for abi in "${ABIS[@]}"; do
  outdir="$TMP/$abi"
  mkdir -p "$outdir"
  if ! unzip -q -j "$BUNDLE" "$LIB_PREFIX/$abi/*.so" -d "$outdir" 2>/dev/null; then
    echo "warn: no .so files in $LIB_PREFIX/$abi/ — skipping" >&2
    continue
  fi

  for f in "$outdir"/*.so; do
    [[ -e "$f" ]] || continue
    total=$((total+1))
    aligns=$("$READELF" -l "$f" 2>/dev/null | awk '/^  LOAD/ {print $NF}' | sort -u | tr '\n' ' ')
    aligns=${aligns% }
    status="OK"
    if [[ "$aligns" != "0x4000" ]]; then
      status="FAIL"
      failures=$((failures+1))
    fi
    printf "%-42s %-13s %-10s %s\n" "$(basename "$f")" "$abi" "$aligns" "$status"
  done
done

echo
if [[ $failures -eq 0 ]]; then
  echo "PASS: all $total libs across [${ABIS[*]}] are 16KB-aligned"
  exit 0
fi

echo "FAIL: $failures of $total libs are not 16KB-aligned" >&2
exit 1
