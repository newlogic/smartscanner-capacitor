import { registerPlugin } from '@capacitor/core';

import type { SmartScannerPluginInterface } from './definitions';

const SmartScannerPlugin = registerPlugin<SmartScannerPluginInterface>('SmartScannerPlugin', {
  web: () => import('./web').then(m => new m.SmartScannerPluginWeb()),
});

export * from './definitions';

export { SmartScannerPlugin };
