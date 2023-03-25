package com.enderio.core.client.gui.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.enderio.core.client.render.RenderUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IIconButton extends BaseButton {

  public static final int DEFAULT_WIDTH = 24;
  public static final int HWIDTH = DEFAULT_WIDTH / 2;
  public static final int DEFAULT_HEIGHT = 24;
  public static final int HHEIGHT = DEFAULT_HEIGHT / 2;

  protected int hwidth;
  protected int hheight;

  protected @Nullable TextureAtlasSprite icon;
  protected @Nullable ResourceLocation texture;

  public IIconButton(@Nonnull Font fr, int x, int y, @Nullable TextureAtlasSprite icon, @Nullable ResourceLocation texture) {
    super(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Component.empty(), (button) -> {});
    hwidth = HWIDTH;
    hheight = HHEIGHT;
    this.icon = icon;
    this.texture = texture;
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    hwidth = width / 2;
    hheight = height / 2;
  }

  public @Nullable TextureAtlasSprite getIcon() {
    return icon;
  }

  public void setIcon(@Nullable TextureAtlasSprite icon) {
    this.icon = icon;
  }

  public @Nullable ResourceLocation getTexture() {
    return texture;
  }

  public void setTexture(@Nullable ResourceLocation textureName) {
    this.texture = textureName;
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    if (visible) {

      RenderUtil.bindTexture("textures/gui/widgets.png");
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int hoverState = isHovered ? 2 : 1;

      // x, y, u, v, width, height

      // top half
      blit(pPoseStack, x, y, 0, 46 + hoverState * 20, hwidth, hheight);
      blit(pPoseStack, x, y, 0, 46 + hoverState * 20, hwidth, hheight);

      // bottom half
      blit(pPoseStack, x, y + hheight, 0, 66 - hheight + (hoverState * 20), hwidth, hheight);
      blit(pPoseStack, x + hwidth, y + hheight, 200 - hwidth, 66 - hheight + (hoverState * 20), hwidth, hheight);

      //mouseDragged(par1Minecraft, mouseX, mouseY);

      final TextureAtlasSprite icon2 = icon;
      final ResourceLocation texture2 = texture;
      if (icon2 != null && texture2 != null) {

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderUtil.bindTexture(texture2);
        int xLoc = x + 2;
        int yLoc = y + 2;

        blit(pPoseStack, xLoc, yLoc, 0, width - 4, height - 4, icon2);
        RenderSystem.disableBlend();
      }
    }
  }

}
