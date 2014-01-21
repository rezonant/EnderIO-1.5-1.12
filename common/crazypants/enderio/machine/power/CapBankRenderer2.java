package crazypants.enderio.machine.power;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.render.CustomCubeRenderer;

public class CapBankRenderer2 implements ISimpleBlockRenderingHandler {

  CustomCubeRenderer ccr = new CustomCubeRenderer();

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    //    renderer.setOverrideBlockTexture(EnderIO.blockCapacitorBank.getIcon(0, 0));
    //    renderer.renderStandardBlock(block, x, y, z);
    //    renderer.setOverrideBlockTexture(null);

    //    CubeRenderer.render(new BoundingBox(new BlockCoord(x, y, z)), EnderIO.blockCapacitorBank.getIcon(0, 0), true);    
    ccr.renderBlock(world, block, x, y, z);
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
