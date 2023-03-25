package com.enderio.core.client.gui.button;

import com.enderio.core.api.client.gui.IHideable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class HideableButton extends Button implements IHideable {
  public HideableButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
    super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
  }

  public HideableButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, OnTooltip pOnTooltip) {
    super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
  }

  @Override
  public void setIsVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }
}
