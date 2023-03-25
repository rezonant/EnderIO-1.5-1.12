package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class InvisibleButton extends TooltipButton {

  private static final int DEFAULT_WIDTH = 8;
  private static final int DEFAULT_HEIGHT = 6;

  public InvisibleButton(@Nonnull IGuiScreen gui, int x, int y, OnPress onPress) {
    super(gui, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.empty(), onPress);
  }

  public InvisibleButton(@Nonnull IGuiScreen gui, int x, int y, int width, int height, OnPress onPress) {
    super(gui, x, y, width, height, Component.empty(), onPress);
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
    updateTooltip(mouseX, mouseY);
  }

}
