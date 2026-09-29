package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class BlockOutlineModule extends Module {
   private final ColorSetting color;
   private final ColorSetting fillColor;
   private final NumberSetting fillOpacity;
   private final NumberSetting opacity;
   private final NumberSetting thickness;

   public BlockOutlineModule() {
      super("block_outline", "Block Outline", "Customize the block selection outline.", ModuleCategory.COMBAT);
      thickness = this.register(new NumberSetting(this, "thickness", "Thickness", 1, 0.5, 4, 0.1, "Thickness"));
      color = this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      opacity = this.register(new NumberSetting(this, "opacity", "Opacity", 0.6, 0, 1, 0.01, "Opacity"));
      this.register(new BooleanSetting(this, "filledFace", "Filled Face", false, "Filled Face"));
      fillColor = this.register(new ColorSetting(this, "fillColor", "Fill Color", KroxTheme.TEXT_PRIMARY, "Fill Color"));
      fillOpacity = this.register(new NumberSetting(this, "fillOpacity", "Fill Opacity", 0, 0, 1, 0.01, "Fill Opacity"));
      this.register(new BooleanSetting(this, "alwaysShow", "Always Show", false, "outline even without selection"));
   }
   @Override
   public String renderText() {
      return null;
   }

   /** The outline/fill colours the block-outline mixin tints the selection box with. */
   public int outlineColor() {
      return this.color.withAlpha((float)this.opacity.get());
   }

   public int faceColor() {
      return this.fillColor.get() & 0xFFFFFF | (int)Math.round(255.0F * this.fillOpacity.get()) << 24;
   }

   public float lineWidth() {
      return (float)this.thickness.get();
   }
}
