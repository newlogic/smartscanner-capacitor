import { WebPlugin } from '@capacitor/core';
import { ScannerInputConfig, SmartScannerPluginInterface } from './definitions';

export class SmartScannerPluginWeb extends WebPlugin implements SmartScannerPluginInterface {
  
  async executeScanner(_options?: ScannerInputConfig): Promise<void> {
    throw new Error('method not available in web');
  }
}

