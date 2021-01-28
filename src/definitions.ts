
declare module '@capacitor/core' {
  interface PluginRegistry {
    SmartScannerPluginInterface: SmartScannerPluginInterface;
  }
}

export interface SmartScannerPluginInterface {
  echo(options: { value: string }): Promise<{ value: string }>;
  executeScanner(options: { mode: string, action: string}): Promise< void >;
}
