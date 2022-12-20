export interface SmartScannerPluginInterface {
  executeScanner(config: ScannerInputConfig): Promise<any>;
}

/**
 * This parameter is used to set the input configuration of the scanner
 *
 * @since 0.5.0
 */
export interface ScannerInputConfig {
  /**
   * @description This parameter is used to set the action
   *   
   * - Example: `START_SCANNER`
   *
   * @since 0.5.0
   */
  action?: string;
  /**
   * This parameter is used to set options
   *
   * @since 0.5.0
   */
  options?: ScannerOptions;
}

/**
 * This parameter is used to set the options for the scanner
 *
 * @since 0.5.0
 */
export interface ScannerOptions {
  /**
   * This parameter is used to set the mode of Scanner
   * 
   * - Example: `mrz`, `nfc-scan`, `barcode`
   *
   * @default `mrz`
   * @since 0.5.0
   */
  mode?: string;
  /**
   * This parameter is used to set the configurations depending on the mode
   *
   * @since 0.5.0
   */
  config?: Config;
  /**
   * This parameter is used to set the language
   *
   * @since 0.5.0
   */
  language?: string;
  /**
   * This parameter is used to set the NFC Locale
   *
   * @since 0.5.0
   */
  nfcLocale?: string;
  /**
   * This parameter is used to set the MRZ format
   * 
   * - Example: `MRTD_TD1`, `MRTD_TD2`, `MRP`, `MRV_A`, `MRV_B`
   *
   * @since 0.5.0
   */
  mrzFormat?: string;
  /**
   * This parameter is used to set the Scanner Size
   *
   * @since 0.5.0
   */
  scannerSize?: string;
  /**
   * This parameter is used to set Barcode Options
   * 
   * If mode is set to `barcode`
   *
   * @since 0.5.0
   */
  barcodeOptions?: BarcodeOptions;
  /**
   * This parameter is used to set NFC Options
   * 
   * If mode is set to `nfc`
   *
   * @since 0.5.0
   */
  nfcOptions?: NfcOptions;
  /**
   * This parameter is used to set the Scanner Size
   *
   * @since 0.5.0
   */
  captureOptions?: CaptureOptions;
}

/**
 * This parameter is used to set the configuration of the scanner
 *
 * @since 0.5.0
 */
export interface Config {
  /**
   * This parameter is used to set the background color when the scanner is active. Input can be any Hex Color
   * 
   * - Example: `#ffc234`
   * 
   * @since 0.5.0
   */
  background?: string;
  /**
   * This parameter is used to display or remove the IDPASS Brand on the scanner
   * 
   * @default true
   * @since 0.5.0
   */
  branding?: boolean;
  /**
   * This parameter is used to set the font for the scanner
   * 
   * @since 0.5.0
   */
  font?: string;
  /**
   * This parameter is used to set the type of image result
   * 
   * - Example: `path`, `base_64`
   * 
   * @default `path`
   * @since 0.5.0
   */
  imageResultType?: string;
  /**
   * This parameter is used to capture id manually
   * 
   * @default false
   * @since 0.5.0
   */
  isManualCapture?: boolean;
  /**
   * This parameter is used to set the header
   * 
   * @since 0.5.0
   */
  header?: string;
  /**
   * This parameter is used to set the subheader
   * 
   * @since 0.5.0
   */
  subHeader?: string;
  /**
   * This parameter is used to set the label
   * 
   * @since 0.5.0
   */
  label?: string;
}

/**
 * This parameter is used to set the options for the barcode scanner
 *
 * @since 0.5.0
 */
export interface BarcodeOptions {
  /**
   * This parameter is used to set the supported barcode formats
   *
   * @since 0.5.0
   */
  barcodeFormats?: string[];
  /**
   * This parameter is used to set the IdPass lite support
   *
   * @since 0.5.0
   */
  idPassLiteSupport?: boolean;
}

/**
 * This parameter is used to set the options for capturing
 *
 * @since 0.5.0
 */
export interface CaptureOptions {
  /**
   * This parameter is used to set the type of subject
   * 
   * - Example: `id`, `document`
   *
   * @default `id`
   * @since 0.5.0
   */
  type?: string;
  /**
   * This parameter is used to set the height of the capture box
   *
   * @default 180
   * @since 0.5.0
   */
  height?: number;
  /**
   * This parameter is used to set the width of the capture box
   *
   * @default 285
   * @since 0.5.0
   */
  width?: number;
}

/**
 * This parameter is used to set NFC Options
 *  If mode is set to `nfc`
 *
 * @since 0.5.0
 */
export interface NfcOptions {
  /**
   * This parameter is used to set the label for scanning the NFC
   *
   * @default null
   * @since 0.5.0
   */
  label?: string;
  /**
   * This parameter is used to set the locale for scanning the NFC
   *
   * @default null
   * @since 0.5.0
   */
  locale?: string;
  /**
   * This parameter is used to set if the result should include the photo
   *
   * @default true
   * @since 0.5.0
   */
  withPhoto?: boolean;
  /**
   * This parameter is used to set if the result should include the mrz photo
   *
   * @default false
   * @since 0.5.0
   */
  withMrzPhoto?: boolean;
  /**
   * This parameter is used to set the logging
   *
   * @default false
   * @since 0.5.0
   */
  enableLogging?: boolean;
}

