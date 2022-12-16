export interface SmartScannerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  executeScanner(options: any): Promise<any>;
}
