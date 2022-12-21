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

The plugin can be used through Capacitor's `Plugins` object, which is the registry of all available plugins.

```js
import { ScannerInputConfig, ScannerOptions, SmartScannerPlugin } from '@idpass/smartscanner-capacitor';
```

MRZ scanning example:

```js
const scannerOptions: ScannerOptions = {
  mode: 'mrz',
  config: {
    background: '#89837c',
    header: 'Sample Header',
    subHeader: 'Sample SubHeader',
    label: 'Sample Config Label',
    isManualCapture: true,
    branding: false
  }, 
  mrzFormat: 'MRTD_TD1',
}

const inputConfig: ScannerInputConfig = {
  action: 'START_SCANNER',
  options: scannerOptions
}

await SmartScannerPlugin.executeScanner(inputConfig);
```
NFC scanning example:

```js
const scannerOptions: ScannerOptions = {
  mode: 'nfc-scan',
  config: {
    background: '#89837c',
    header: 'Sample Header',
    subHeader: 'Sample SubHeader',
    label: 'Sample Config Label'
  }, 
  nfcOptions: {
    label: 'This is a sample label',
    withPhoto: true,
    withMrzPhoto: false
  }
}

const inputConfig: ScannerInputConfig = {
  action: 'START_SCANNER',
  options: scannerOptions
}


await SmartScannerPlugin.executeScanner(inputConfig);
```

Barcode scanning example:

```js
const scannerOptions: ScannerOptions = {
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
    label: 'Sample Config Label',
  }
}

const inputConfig: ScannerInputConfig = {
  action: 'START_SCANNER',
  options: scannerOptions
}

await SmartScannerPlugin.executeScanner(inputConfig);
```

Refer to the [API Reference](https://github.com/idpass/smartscanner-capacitor/wiki/API-Reference) for more information about the available API options and the returned result.

## Related projects

- [smartscanner-core](https://github.com/idpass/smartscanner-core) - Android library for scanning MRZ, Barcode, and ID PASS Lite cards
- [smartscanner-android-api](https://github.com/idpass/smartscanner-android-api) - Provides convenience methods to simplify the SmartScanner intent call out process
- [smartscanner-cordova](https://github.com/idpass/smartscanner-cordova) - SmartScanner [Cordova](https://cordova.apache.org/) plugin

## License

[Apache-2.0 License](LICENSE)
