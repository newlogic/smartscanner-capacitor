
declare module '@capacitor/core' {
  interface PluginRegistry {
    MLKitPluginInterface: MLKitPluginInterface;
  }
}

export interface MLKitPluginInterface {
  echo(options: { value: string }): Promise<{ value: string }>;
  executeScanner(options: { mode: string, action: string}): Promise< void >;
}
