# SmartScanner Capacitor

Capacitor plugin for the [SmartScanner Core](https://github.com/idpass/smartscanner-core) library to scan MRZ, NFC and barcodes.

## Installation

This plugin can be installed from NPM:

```bash
# Using npm
npm install @idpass/smartscanner-capacitor

# Using yarn
yarn add @idpass/smartscanner-capacitor
```

## Usage

The plugin can be used by accessing SmartScannerPlugin from @idpass/smartscanner-capacitor directly.

```js
import { SmartScannerPlugin } from '@idpass/smartscanner-capacitor';

```

MRZ scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'mrz',
    mrzFormat: 'MRTD_TD1',
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: true,
    },
  },
});

```
OCR scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'ocr',
    ocrOptions: {
      type: 'documentNumber',
      regex: '\\d{4} \\d{4} \\d{5}'
    },
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: false, //if true user will be required to tap the capture button
      showGuide: true, //values below will only be used when this is set to true
      xGuide: 0.8, //accepts values from 0.0 - 1.0. Offsets the guide horizontally based on percentage.
      yGuide: 0.5, //accepts values from 0.0 - 1.0. Offsets the guide vertically based on percentage.
      widthGuide: 150, //sets the guide width. Default is 150
      heightGuide: 40 //sets the guide height. Default is 40
    },
  },
});

```
Table OCR scanning example (attendance table):

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'table-ocr',
    ocrOptions: {
      analyzeStart: 1000,
      tableConfig: {
        // Months and Total column are auto-detected from the table grid
        columnHeaders: [],
        autoDetectMonths: true,
        // Number of data rows and their labels
        dataRows: 3,
        rowLabels: ['School', 'Present', 'Absent'],
        // Cell validation
        regex: '[0-9]{1,2}',
        validMin: 1,
        validMax: 31,
        transforms: ['LETTER_TO_DIGIT'],
        tableTransforms: ['ATTENDANCE'],
      },
    },
    config: {
      background: '#89837c',
      branding: true,
      label: 'Table OCR',
      isManualCapture: true,
      showOcrGuide: true,
      showOcrRegions: true,
      orientation: 'portrait',
      widthGuide: 900,
      heightGuide: 400,
      xGuide: 0.5,
      yGuide: 0.5,
    },
  },
});

// result.scanner_result.fields contains flat "row.col" keys:
// { "school.jun": "22", "school.jul": "20", ..., "school.total": "198", ... }
//
// result.table_meta contains the auto-detected table structure:
// { columnHeaders: ["Jun","Jul",...,"Total"], rowLabels: ["School","Present","Absent"], ... }
```

A runnable Vue 3 + Vite example is available in the [`example/`](./example) folder.
See [`example/src/App.vue`](./example/src/App.vue) for the full Table OCR implementation.

To build and run:

```bash
cd example
nvm use           # switches to Node 22 via .nvmrc (requires Node >= 18)
npm install
npm run sync      # builds Vue app into www/ and syncs to Android
# then open example/android in Android Studio and run on device
```

NFC scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'nfc-scan',
    config: {
      background: '#89837c',
      branding: false,
      isManualCapture: false,
    },
  },
});
```

Barcode scanning example:

```js
const result = await SmartScannerPlugin.executeScanner({
  action: 'START_SCANNER',
  options: {
    mode: 'barcode',
    barcodeOptions: {
      barcodeFormats: [
        'AZTEC',
        'CODABAR',
        'CODE_39',
        'CODE_93',
        'CODE_128',
        'DATA_MATRIX',
        'EAN_8',
        'EAN_13',
        'QR_CODE',
        'UPC_A',
        'UPC_E',
        'PDF_417',
      ],
    },
    config: {
      background: '#ffc234',
      label: 'Sample Label',
    },
  },
});
```

Refer to the [API Reference](https://github.com/idpass/smartscanner-capacitor/wiki/API-Reference) for more information about the available API options and the returned result.

## Related projects

- [smartscanner-core](https://github.com/idpass/smartscanner-core) - Android library for scanning MRZ, OCR, Barcode, and ID PASS Lite cards
- [smartscanner-android-api](https://github.com/idpass/smartscanner-android-api) - Provides convenience methods to simplify the SmartScanner intent call out process
- [smartscanner-cordova](https://github.com/idpass/smartscanner-cordova) - SmartScanner [Cordova](https://cordova.apache.org/) plugin

## License

[Apache-2.0 License](LICENSE)
