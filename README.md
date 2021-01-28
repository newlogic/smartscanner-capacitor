# SmartScanner Capacitor

Capacitor plugin for the [SmartScanner Core](https://github.com/idpass/smartscanner-core) library to scan MRZ and barcodes.

## Installation

This plugin can be installed from NPM:

```bash
# Using npm
npm install smartscanner-capacitor

# Using yarn
yarn add smartscanner-capacitor
```

## Usage

The plugin can be used through Capacitor's `Plugins` object, which is the registry of all available plugins.

```js
import { Plugins } from '@capacitor/core';

const { SmartScannerPlugin } = Plugins;
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

Refer to the [API Reference](https://github.com/newlogic/smartscanner-capacitor/wiki/API-Reference) for more information about the available API options and the returned result.

## Related projects

- [smartscanner-core](https://github.com/idpass/smartscanner-core) - Android library for scanning MRZ, Barcode, and ID PASS Lite cards
- [smartscanner-android-api](https://github.com/idpass/smartscanner-android-api) - Provides convenience methods to simplify the SmartScanner intent call out process
- [smartscanner-cordova](https://github.com/idpass/smartscanner-cordova) - SmartScanner [Cordova](https://cordova.apache.org/) plugin

## License

[Apache-2.0 License](LICENSE)
