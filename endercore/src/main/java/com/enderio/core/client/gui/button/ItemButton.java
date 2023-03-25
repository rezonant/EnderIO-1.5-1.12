package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import net.minecraft.client.Minecraft;

public class ItemButton extends BaseButton {

  public static final int DEFAULT_WIDTH = 24;
  public static final int HWIDTH = DEFAULT_WIDTH / 2;
  public static final int DEFAULT_HEIGHT = 24;
  public static final int HHEIGHT = DEFAULT_HEIGHT / 2;

  private @Nonnull ItemStack item;

  protected int hwidth;
  protected int hheight;

  public ItemButton(int x, int y, @Nonnull Item item) {
    super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.empty());
    this.item = new ItemStack(item, 1);
    hwidth = HWIDTH;
    hheight = HHEIGHT;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    hwidth = width / 2;
    hheight = height / 2;
  }

  @Override
  public void renderButton(PoseStack pPoseStack, int x, int y, float partialTicks) {
    if (visible) {

      RenderUtil.bindTexture("textures/gui/widgets.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int hoverState = this.isHoveredOrFocused() ? 2 : 1;

      // x, y, u, v, width, height

      // top half
      blit(pPoseStack, this.x, this.y, 0, 46 + hoverState * 20, hwidth, hheight);
      blit(pPoseStack, this.x + hwidth, this.y, 200 - hwidth, 46 + hoverState * 20, hwidth, hheight);

      // bottom half
      blit(pPoseStack, this.x, this.y + hheight, 0, 66 - hheight + (hoverState * 20), hwidth, hheight);
      blit(pPoseStack, this.x + hwidth, this.y + hheight, 200 - hwidth, 66 - hheight + (hoverState * 20), hwidth, hheight);

      int xLoc = this.x + hwidth - 8;
      int yLoc = this.y + hheight - 10;
      Minecraft.getInstance().getItemRenderer().renderGuiItem(item, xLoc, yLoc);
    }
  }

}