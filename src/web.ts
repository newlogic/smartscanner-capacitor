import { WebPlugin } from '@capacitor/core';

import { SmartScannerPlugin } from './definitions';
export * from './web';

export class SmartScannerPluginWeb extends WebPlugin implements SmartScannerPlugin {
  constructor() {
    super();
  }

  public async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  public async executeScanner(options: { mode: string, action: string }): Promise<void> {
    console.log('executeScanner', options);
  }
}

