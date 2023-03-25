package com.enderio.core.client.gui.button;

import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.widget.GuiToolTip;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TooltipButton extends HideableButton {

  protected int xOrigin;
  protected int yOrigin;
  protected @Nonnull IGuiScreen gui;
  protected @Nullable String[] toolTipText;
  protected @Nullable GuiToolTip toolTip;

  public TooltipButton(@Nonnull IGuiScreen gui, int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress) {
    super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
    this.gui = gui;
    this.xOrigin = x;
    this.yOrigin = y;
  }


  public void setToolTip(String... tooltipText) {
    if (toolTip != null) {
      toolTip.setToolTipText(tooltipText);
    } else {
      toolTip = new GuiToolTip(getBounds(), tooltipText);
    }
    this.toolTipText = tooltipText;
    updateTooltipBounds();
  }

  public void setToolTip(GuiToolTip newToolTip) {
    boolean addTooltip = false;
    if (toolTip != null) {
      addTooltip = gui.removeToolTip(toolTip);
    }
    toolTip = newToolTip;
    if (addTooltip && toolTip != null) {
      gui.addToolTip(toolTip);
    }
    updateTooltipBounds();
  }

  public final @Nonnull Rectangle getBounds() {
    return new Rectangle(xOrigin, yOrigin, getWidth(), getHeight());
  }

  public void onGuiInit() {
    gui.addButton(this);
    if (toolTip != null) {
      gui.addToolTip(toolTip);
    }
    this.x = xOrigin + gui.getGuiRootLeft();
    this.y = yOrigin + gui.getGuiRootTop();
  }

  public void detach() {
    if (toolTip != null) {
      gui.removeToolTip(toolTip);
    }
    gui.removeButton(this);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public @Nullable GuiToolTip getToolTip() {
    return toolTip;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    updateTooltipBounds();
  }

  public TooltipButton setPosition(int x, int y) {
    xOrigin = x;
    yOrigin = y;
    updateTooltipBounds();
    return this;
  }

  public void setXOrigin(int xOrigin) {
    this.xOrigin = xOrigin;
  }

  public void setYOrigin(int yOrigin) {
    this.yOrigin = yOrigin;
  }

  private void updateTooltipBounds() {
    if (toolTip != null) {
      toolTip.setBounds(new Rectangle(xOrigin, yOrigin, width, height));
    }
  }

  protected void updateTooltip(int mouseX, int mouseY) {
    if (toolTip != null) {
      toolTip.setIsVisible(visible && active);
    }
  }

  protected final void doRenderButton(@Nonnull PoseStack ps, int mouseX, int mouseY, float partialTicks) {
    super.renderButton(ps, mouseX, mouseY, partialTicks);
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    updateTooltip(pMouseX, pMouseY);
    doRenderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
  }

}