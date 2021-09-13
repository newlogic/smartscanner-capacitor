import { registerPlugin } from '@capacitor/core';
import type { SmartScannerPluginInterface } from './definitions';

const MySmartScannerPlugin = registerPlugin<SmartScannerPluginInterface>('MyCoolPlugin', {
  web: () => import('./web').then(m => new m.SmartScannerPluginWeb()),
});

export * from './definitions';
export { MySmartScannerPlugin };
