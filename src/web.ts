import { WebPlugin } from '@capacitor/core';
import { SmartScannerPluginInterface } from './definitions';

export class SmartScannerPluginWeb extends WebPlugin implements SmartScannerPluginInterface {

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  
  async executeScanner(options: { options: {}, action: string }): Promise<void> {
    console.log('executeScanner', options);
    throw new Error('method not available in web');
  }
}

