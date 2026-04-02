<script setup>
import { ref } from 'vue'
import { SmartScanner } from '@idpass/smartscanner-capacitor'

const idResult = ref(null)
const mrzResult = ref(null)
const error = ref(null)
const scanning = ref(false)

async function scanID() {
  error.value = null
  idResult.value = null
  scanning.value = true
  try {
    const result = await SmartScanner.executeScanner({
      action: 'START_SCANNER',
      options: {
        mode: 'ocr',
        ocrOptions: {
          analyzeStart: 1000,
          // Empty list triggers built-in defaults (Colombia, Venezuela, Nicaragua, Guatemala).
          // Pass custom configs here to override or extend.
          scanIDOCRCountryOptions: [],
        },
        config: {
          branding: true,
          label: 'Scan ID',
          isManualCapture: false,
          showOcrGuide: true,
          orientation: 'portrait',
          widthGuide: 300,
          heightGuide: 200,
          xGuide: 0.5,
          yGuide: 0.5,
        },
      },
    })
    idResult.value = result.scanner_result
  } catch (e) {
    error.value = e.message ?? 'Scanning cancelled'
  } finally {
    scanning.value = false
  }
}

async function scanMRZ() {
  error.value = null
  mrzResult.value = null
  scanning.value = true
  try {
    const result = await SmartScanner.executeScanner({
      action: 'START_SCANNER',
      options: {
        mode: 'mrz',
        mrzFormat: 'MRTD_TD1',
        config: {
          background: '#89837c',
          branding: false,
          isManualCapture: true,
          imageResultType: 'base_64',
        },
      },
    })
    mrzResult.value = result.scanner_result
  } catch (e) {
    error.value = e.message ?? 'Scanning cancelled'
  } finally {
    scanning.value = false
  }
}
</script>

<template>
  <div class="container">
    <h1>SmartScanner Example</h1>

    <div class="button-group">
      <button :disabled="scanning" @click="scanID">
        {{ scanning ? 'Scanning…' : 'Scan ID Document' }}
      </button>

      <button :disabled="scanning" @click="scanMRZ">
        {{ scanning ? 'Scanning…' : 'Scan MRZ' }}
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="idResult" class="result">
      <h2>ID Scan Result</h2>
      <div v-if="idResult.fields" class="fields">
        <div v-for="(value, label) in idResult.fields" :key="label" class="field">
          <span class="field-label">{{ label }}</span>
          <span class="field-value">{{ value }}</span>
        </div>
      </div>
      <details>
        <summary>Raw JSON</summary>
        <pre>{{ JSON.stringify(idResult, null, 2) }}</pre>
      </details>
    </div>

    <div v-if="mrzResult" class="result">
      <h2>MRZ Scan Result</h2>
      <details open>
        <summary>Raw JSON</summary>
        <pre>{{ JSON.stringify(mrzResult, null, 2) }}</pre>
      </details>
    </div>
  </div>
</template>

<style scoped>
.container {
  font-family: sans-serif;
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem;
}

.button-group {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  background: #4a90d9;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.5;
  cursor: default;
}

.error {
  color: #c0392b;
  margin-top: 1rem;
}

.result {
  margin-top: 1.5rem;
}

.fields {
  display: grid;
  gap: 0.5rem;
}

.field {
  display: flex;
  gap: 1rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
}

.field-label {
  font-weight: 600;
  min-width: 120px;
  color: #555;
}

.field-value {
  color: #222;
}

details {
  margin-top: 1rem;
}

pre {
  background: #f5f5f5;
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 0.8rem;
}
</style>
