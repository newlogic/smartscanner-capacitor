#!/usr/bin/env bash
#
# Rebuild the smartscanner-core AARs and copy them into this plugin's
# local-maven directory under the names the plugin's android/build.gradle
# expects (smartscannerlib-{debug,release}-1.0.aar).
#
# The rename is necessary because smartscanner-core's Kotlin DSL build (post-
# 0.6.0) emits core-lib-{debug,release}.aar — that filename mismatch is
# otherwise a hidden trap when refreshing the vendored AARs by hand.
#
# Usage:
#   scripts/refresh-aars.sh [path-to-smartscanner-core] [git-ref]
#
# Defaults:
#   path-to-smartscanner-core : ../smartscanner-core
#   git-ref                   : (current HEAD of that checkout)
#
# Pass an explicit git-ref (tag, branch, or SHA) to assert that the source
# checkout is on that ref before building. The script refuses to run if the
# working tree is dirty so the published AAR is always traceable to a clean
# commit.

set -euo pipefail

CORE_DIR="${1:-../smartscanner-core}"
EXPECTED_REF="${2:-}"

PLUGIN_DIR=$(cd "$(dirname "$0")/.." && pwd)
CORE_DIR=$(cd "$CORE_DIR" && pwd)
DEST_DIR="$PLUGIN_DIR/android/local-maven/org/idpass/smartscanner"

echo "smartscanner-core : $CORE_DIR"
echo "plugin destination: $DEST_DIR"

# Source-checkout sanity.
if ! git -C "$CORE_DIR" rev-parse --git-dir >/dev/null 2>&1; then
  echo "error: $CORE_DIR is not a git checkout" >&2
  exit 2
fi

if [[ -n "$(git -C "$CORE_DIR" status --porcelain)" ]]; then
  echo "error: $CORE_DIR has uncommitted changes; commit or stash first" >&2
  exit 2
fi

if [[ -n "$EXPECTED_REF" ]]; then
  expected_sha=$(git -C "$CORE_DIR" rev-parse "$EXPECTED_REF^{commit}")
  actual_sha=$(git -C "$CORE_DIR" rev-parse HEAD)
  if [[ "$expected_sha" != "$actual_sha" ]]; then
    echo "error: $CORE_DIR HEAD is $actual_sha but expected $EXPECTED_REF ($expected_sha)" >&2
    exit 2
  fi
  echo "source ref        : $EXPECTED_REF ($actual_sha)"
else
  actual_sha=$(git -C "$CORE_DIR" rev-parse HEAD)
  echo "source ref        : HEAD ($actual_sha)"
fi

echo
echo "==> Building :core-lib:assembleDebug :core-lib:assembleRelease"
(cd "$CORE_DIR" && ./gradlew :core-lib:assembleDebug :core-lib:assembleRelease)

src_debug="$CORE_DIR/core-lib/build/outputs/aar/core-lib-debug.aar"
src_release="$CORE_DIR/core-lib/build/outputs/aar/core-lib-release.aar"
dst_debug="$DEST_DIR/smartscannerlib-debug/1.0/smartscannerlib-debug-1.0.aar"
dst_release="$DEST_DIR/smartscannerlib-release/1.0/smartscannerlib-release-1.0.aar"

for f in "$src_debug" "$src_release"; do
  if [[ ! -f "$f" ]]; then
    echo "error: expected build output not found: $f" >&2
    exit 1
  fi
done

mkdir -p "$(dirname "$dst_debug")" "$(dirname "$dst_release")"

echo
echo "==> Copying AARs (renaming core-lib-*.aar -> smartscannerlib-*-1.0.aar)"
cp -v "$src_debug" "$dst_debug"
cp -v "$src_release" "$dst_release"

echo
echo "Done. Source SHA: $actual_sha"
echo "Suggested commit message:"
echo "  chore(android): refresh vendored AARs from smartscanner-core $actual_sha"
