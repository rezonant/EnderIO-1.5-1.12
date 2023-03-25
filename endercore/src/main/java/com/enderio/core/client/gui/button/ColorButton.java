package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.vecmath.Vector3f;

import net.minecraft.client.Minecraft;

public class ColorButton extends IconButton {

  private int colorIndex = 0;

  private @Nonnull String tooltipPrefix = "";

  public ColorButton(@Nonnull IGuiScreen gui, int x, int y) {
    super(gui, x, y, (button) -> {}, null);
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
    if (pButton == 0) {
      nextColor();
      return true;
    } else if (pButton == 1) {
      prevColor();
      return true;
    }

    return super.mouseClicked(pMouseX, pMouseY, pButton);
  }

  public @Nonnull String getTooltipPrefix() {
    return tooltipPrefix;
  }

  public void setToolTipHeading(@Nullable String tooltipPrefix) {
    if (tooltipPrefix == null) {
      this.tooltipPrefix = "";
    } else {
      this.tooltipPrefix = tooltipPrefix;
    }
  }

  private void nextColor() {
    colorIndex++;
    if (colorIndex >= DyeColor.values().length) {
      colorIndex = 0;
    }
    setColorIndex(colorIndex);
  }

  private void prevColor() {
    colorIndex--;
    if (colorIndex < 0) {
      colorIndex = DyeColor.values().length - 1;
    }
    setColorIndex(colorIndex);
  }

  public int getColorIndex() {
    return colorIndex;
  }

  public void setColorIndex(int colorIndex) {
    this.colorIndex = Mth.clamp(colorIndex, 0, DyeColor.values().length - 1);

    String colStr = DyeColor.values()[colorIndex].getName();
    if (tooltipPrefix.length() > 0) {
      setToolTip(tooltipPrefix, colStr);
    } else {
      setToolTip(colStr);
    }
  }

  @Override
  public void renderButton(PoseStack pPoseStack, int mouseX, int mouseY, float pPartialTick) {
    super.renderButton(pPoseStack, mouseX, mouseY, pPartialTick);
    if (visible) {
      BufferBuilder tes = Tesselator.getInstance().getBuilder();
      tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

      int xAdj = this.x + 2;
      int yAdj = this.y + 2;
      int zLevel = getBlitOffset();

      RenderSystem.disableTexture();
      int col = DyeColor.byId(colorIndex).getMaterialColor().col;
      Vector3f c = ColorUtil.toFloat(col);
      RenderSystem.setShaderColor(c.x, c.y, c.z, 1);

      tes.vertex(xAdj, yAdj + height - 4, zLevel).color(c.x, c.y, c.z, 1).endVertex();
      tes.vertex(xAdj + width - 4, yAdj + height - 4, zLevel).color(c.x, c.y, c.z, 1).endVertex();
      tes.vertex(xAdj + width - 4, yAdj + 0, zLevel).color(c.x, c.y, c.z, 1).endVertex();
      tes.vertex(xAdj, yAdj + 0, zLevel).color(c.x, c.y, c.z, 1).endVertex();

      tes.end();
      RenderSystem.enableTexture();

    }
  }
}
