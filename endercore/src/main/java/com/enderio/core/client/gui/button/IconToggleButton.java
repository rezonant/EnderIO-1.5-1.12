package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class IconToggleButton extends IIconButton {

  private boolean selected = false;

  public IconToggleButton(@Nonnull Font fr, int x, int y, @Nullable TextureAtlasSprite icon, @Nullable ResourceLocation texture) {
    super(fr, x, y, icon, texture);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override
  public boolean isHoveredOrFocused() {
    if (selected)
      return false;
    return super.isHoveredOrFocused();
  }
}
