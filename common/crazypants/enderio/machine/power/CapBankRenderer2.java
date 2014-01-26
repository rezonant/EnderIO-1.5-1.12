package crazypants.enderio.machine.power;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.CustomCubeRenderer;

public class CapBankRenderer2 implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    CustomCubeRenderer.instance.renderBlock(world, block, x, y, z, EnderIO.blockAlloySmelter.getBlockTextureFromSide(3), false);
    return false;
  }

  @Override
  public boolean shouldRender3DInInventory() {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockCapacitorBank.renderId;
  }

}
