import { registerPlugin } from "@capacitor/core";
import type { SmartScannerPlugin } from "./definitions";

const SmartScannerPlugin = registerPlugin<SmartScannerPlugin>(
  'SmartScanner',
  {
    web: () => import("./web").then((m) => new m.SmartScannerPluginWeb())
  }
);

export * from "./definitions";
export { SmartScannerPlugin };