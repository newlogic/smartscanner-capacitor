export interface SmartScannerPluginInterface {
  echo(options: { value: string }): Promise<{ value: string }>;
  executeScanner(options: { options: {}, action: string}): Promise< void >;
}
