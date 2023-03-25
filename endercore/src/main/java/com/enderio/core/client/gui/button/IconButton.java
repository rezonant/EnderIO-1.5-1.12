package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.render.EnderWidget;

import net.minecraft.client.Minecraft;

public class IconButton extends TooltipButton {

  public static final int DEFAULT_WIDTH = 16;
  public static final int DEFAULT_HEIGHT = 16;

  protected @Nullable IWidgetIcon icon;

  private int marginY = 0;
  private int marginX = 0;

  public IconButton(@Nonnull IGuiScreen gui, int x, int y, OnPress onPress, @Nullable IWidgetIcon icon) {
    super(gui, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.empty(), onPress);
    this.icon = icon;
  }

  @Override
  public IconButton setPosition(int x, int y) {
    super.setPosition(x, y);
    return this;
  }

  public IconButton setIconMargin(int x, int y) {
    marginX = x;
    marginY = y;
    return this;
  }

  public @Nullable IWidgetIcon getIcon() {
    return icon;
  }

  public void setIcon(@Nullable IWidgetIcon icon) {
    this.icon = icon;
  }

  @Override
  public void renderButton(PoseStack pPoseStack, int mouseX, int mouseY, float pPartialTick) {
    updateTooltip(mouseX, mouseY);
    if (isVisible()) {
      int hoverState = isHoveredOrFocused() ? 2 : 1;

      IWidgetIcon background = getIconForHoverState(hoverState);

      RenderSystem.setShaderColor(1, 1, 1, 1);

      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

      background.getMap().render(background, x, y, width, height, 0, true);
      final @Nullable IWidgetIcon icon2 = icon;
      if (icon2 != null) {
        icon2.getMap().render(icon2, x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY, 0, true);
      }

      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
    }
  }

  protected @Nonnull IWidgetIcon getIconForHoverState(int hoverState) {
    if (hoverState == 0) {
      return EnderWidget.BUTTON_DISABLED;
    }
    if (hoverState == 2) {
      return EnderWidget.BUTTON_HIGHLIGHT;
    }
    return EnderWidget.BUTTON;
  }
}
