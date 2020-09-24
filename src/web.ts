import { WebPlugin } from '@capacitor/core';
import { MLKitPluginInterface } from './definitions';
export * from './web';

export class MLKitPluginWeb extends WebPlugin implements MLKitPluginInterface {
  constructor() {
    super({
      name: 'MLKitPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async startMLActivity(options: { mode: string }): Promise<void> {
    console.log('startMLActivity', options);
  }
  
  async executeMLKit(options: { mode: string, action: string }): Promise<void> {
    console.log('startMLActivity', options);
  }
}

const MLKitPlugin = new MLKitPluginWeb();

export { MLKitPluginInterface };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(MLKitPlugin);
