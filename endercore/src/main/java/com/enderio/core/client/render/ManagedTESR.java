package com.enderio.core.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.GL11;

import com.enderio.core.common.BlockEntityBase;

public abstract class ManagedTESR<T extends BlockEntityBase> implements BlockEntityRenderer<T> {
  protected final BlockEntityRendererProvider.Context context;

  public ManagedTESR(BlockEntityRendererProvider.Context context) {
    this.context = context;
  }

  @SuppressWarnings({ "null", "unused" })
  @Override
  public final void render(@Nonnull T te, float partialTicks, PoseStack poseStack, MultiBufferSource renderer, int light, int overlayLight) {
    if (te != null && te.hasLevel() && !te.isRemoved()) {
      final BlockState blockState = te.getLevel().getBlockState(te.getBlockPos());

      final int renderPass = 0; // TODO MinecraftForgeClient.getRenderPass();

      if (shouldRender(te, blockState, renderPass)) {
        if (renderPass == 0) {
          RenderSystem.disableBlend();
          RenderSystem.depthMask(true);
        } else {
          RenderSystem.enableBlend();
          RenderSystem.depthMask(false);
          RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        RenderUtil.bindBlockTexture();
        renderTileEntity(te, blockState, partialTicks, poseStack, renderer, light, overlayLight);
      }
    } else if (te == null) {
      renderItem();
    }
  }

  protected abstract void renderTileEntity(@Nonnull T te, BlockState blockState, float partialTicks,
                                           PoseStack poseStack, MultiBufferSource renderer, int light,
                                           int overlayLight);

  protected boolean  shouldRender(@Nonnull T te, @Nonnull BlockState blockState, int renderPass) {
    return true;
  }

  protected void renderItem() {
  }

}
