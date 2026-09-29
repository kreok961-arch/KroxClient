package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class TouchGesturesModule extends Module {
   public TouchGesturesModule() {
      super("touch_gestures", "Mobile Touch Gesture Controls", "Tap, drag, multi-touch gestures for mobile.", ModuleCategory.MOBILE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new NumberSetting(this, "tapDelayMs", "Tap Delay Ms", 100, 0, 500, 1, "Tap Delay Ms"));
      this.register(new NumberSetting(this, "longPressMs", "Long Press Ms", 500, 200, 2000, 1, "Long Press Ms"));
      this.register(new BooleanSetting(this, "pinchZoom", "Pinch Zoom", true, "Pinch Zoom"));
      this.register(new BooleanSetting(this, "twoFingerScroll", "Two Finger Scroll", true, "Two Finger Scroll"));
   }
   // TODO(D-44): tap/long-press/pinch are GLFW touch callbacks on Window;
   // Module has no input-callback hook to classify them.
}
