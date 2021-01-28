import { WebPlugin } from '@capacitor/core';
import { SmartScannerPluginInterface } from './definitions';
export * from './web';

export class SmartScannerPluginWeb extends WebPlugin implements SmartScannerPluginInterface {
  constructor() {
    super({
      name: 'SmartScannerPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  
  async executeScanner(options: { mode: string, action: string }): Promise<void> {
    console.log('executeScanner', options);
  }
}

const SmartScannerPlugin = new SmartScannerPluginWeb();

export { SmartScannerPluginInterface };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(SmartScannerPlugin);
