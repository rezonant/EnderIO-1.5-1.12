package com.enderio.core.api.client.render;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

public interface IWidgetMap {

  int getSize();

  @Nonnull
  ResourceLocation getTexture();

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, boolean doDraw);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, boolean doDraw, boolean flipY);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, double zLevel, boolean doDraw);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, double zLevel, boolean doDraw, boolean flipY);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw);

  @OnlyIn(Dist.CLIENT)
  void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY);

  static class WidgetMapImpl implements IWidgetMap {

    private final int size;
    private final @Nonnull ResourceLocation res;

    public WidgetMapImpl(int size, @Nonnull ResourceLocation res) {
      this.size = size;
      this.res = res;
    }

    @Override
    public int getSize() {
      return size;
    }

    @Override
    public @Nonnull ResourceLocation getTexture() {
      return res;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y) {
      render(widget, x, y, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, boolean doDraw) {
      render(widget, x, y, widget.getWidth(), widget.getHeight(), 0, doDraw);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, boolean doDraw, boolean flipY) {
      render(widget, x, y, widget.getWidth(), widget.getHeight(), 0, doDraw, flipY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double zLevel, boolean doDraw) {
      render(widget, x, y, widget.getWidth(), widget.getHeight(), zLevel, doDraw);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double zLevel, boolean doDraw, boolean flipY) {
      render(widget, x, y, widget.getWidth(), widget.getHeight(), zLevel, doDraw, flipY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw) {
      render(widget, x, y, width, height, zLevel, doDraw, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull IWidgetIcon widget, double x, double y, double width, double height, double zLevel, boolean doDraw, boolean flipY) {

      final BufferBuilder tes = Tesselator.getInstance().getBuilder();
      if (doDraw) {
        RenderUtil.bindTexture(getTexture());
        tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      }

      float minU = (float) widget.getX() / getSize();
      float maxU = (float) (widget.getX() + widget.getWidth()) / getSize();
      float minV = (float) widget.getY() / getSize();
      float maxV = (float) (widget.getY() + widget.getHeight()) / getSize();

      if (flipY) {
        tes.vertex(x, y + height, zLevel).uv(minU, minV).endVertex();

        tes.vertex(x + width, y + height, zLevel).uv(maxU, minV).endVertex();
        tes.vertex(x + width, y + 0, zLevel).uv(maxU, maxV).endVertex();
        tes.vertex(x, y + 0, zLevel).uv(minU, maxV).endVertex();
      } else {
        tes.vertex(x, y + height, zLevel).uv(minU, maxV).endVertex();
        tes.vertex(x + width, y + height, zLevel).uv(maxU, maxV).endVertex();
        tes.vertex(x + width, y + 0, zLevel).uv(maxU, minV).endVertex();
        tes.vertex(x, y + 0, zLevel).uv(minU, minV).endVertex();
      }
      final IWidgetIcon overlay = widget.getOverlay();
      if (overlay != null) {
        overlay.getMap().render(overlay, x, y, width, height, zLevel, false, flipY);
      }
      if (doDraw) {
        tes.end();
      }
    }
  }
}
