import { registerPlugin } from "@capacitor/core";
import type { SmartScannerPlugin } from "./definitions";

const SmartScanner = registerPlugin<SmartScannerPlugin>(
  'SmartScanner',
  {
    web: () => import("./web").then((m) => new m.SmartScannerPluginWeb())
  }
);

export * from "./definitions";
export { SmartScanner };
