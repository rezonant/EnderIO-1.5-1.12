package com.enderio.core.common.fluid;

import java.util.Locale;

import javax.annotation.Nonnull;
import com.enderio.core.common.util.NullHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.IFluidBlock;

public abstract class BlockFluidEnder extends Block implements IFluidBlock {

  private float fogColorRed = 1f;
  private float fogColorGreen = 1f;
  private float fogColorBlue = 1f;
  private final @Nonnull Material material;

  private Fluid fluid;

  @Override
  public Fluid getFluid() {
    return this.fluid;
  }

  protected BlockFluidEnder(BlockBehaviour.Properties properties, @Nonnull Fluid fluid, @Nonnull Material material, int fogColor) {
    super(properties);
    this.fluid = fluid;
    this.material = material;

    // darken fog color to fit the fog rendering
    float dim = 1;
    while (getFogColorRed() > .2f || getFogColorGreen() > .2f || getFogColorBlue() > .2f) {
      setFogColorRed((fogColor >> 16 & 255) / 255f * dim);
      setFogColorGreen((fogColor >> 8 & 255) / 255f * dim);
      setFogColorBlue((fogColor & 255) / 255f * dim);
      dim *= .9f;
    }

    //setNames(fluid);
  }

//  protected void setNames(Fluid fluid) {
//    setUnlocalizedName(NullHelper.notnullF(fluid.getUnlocalizedName(), "encountered fluid without a name"));
//    setRegistryName("block_fluid_" + fluid.getName().toLowerCase(Locale.ENGLISH));
//  }

  public float getFogColorRed() {
    return fogColorRed;
  }

  public void setFogColorRed(float fogColorRed) {
    this.fogColorRed = fogColorRed;
  }

  public float getFogColorGreen() {
    return fogColorGreen;
  }

  public void setFogColorGreen(float fogColorGreen) {
    this.fogColorGreen = fogColorGreen;
  }

  public float getFogColorBlue() {
    return fogColorBlue;
  }

  public void setFogColorBlue(float fogColorBlue) {
    this.fogColorBlue = fogColorBlue;
  }
//
//  @Override
//  public Boolean isEntityInsideMaterial(@Nonnull IBlockAccess world, @Nonnull BlockPos blockpos, @Nonnull BlockState iblockstate, @Nonnull Entity entity,
//                                        double yToTest, @Nonnull Material materialIn, boolean testingHead) {
//    if (materialIn == material || materialIn == this.blockMaterial) {
//      return Boolean.TRUE;
//    }
//    return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
//  }
//
//  @Override
//  public boolean canDisplace(IBlockAccess world, BlockPos pos) {
//    IBlockState bs = NullHelper.notnullF(world, "canDisplace() called without world")
//        .getBlockState(NullHelper.notnullF(pos, "canDisplace() called without pos"));
//    if (bs.getMaterial().isLiquid()) {
//      return false;
//    }
//    return super.canDisplace(world, pos);
//  }
//
//  @Override
//  public boolean displaceIfPossible(Level world, BlockPos pos) {
//    IBlockState bs = NullHelper.notnullF(world, "displaceIfPossible() called without world")
//        .getBlockState(NullHelper.notnullF(pos, "displaceIfPossible() called without pos"));
//    if (bs.getMaterial().isLiquid()) {
//      return false;
//    }
//    return super.displaceIfPossible(world, pos);
//  }
//
//  @Override
//  @OnlyIn(Dist.CLIENT)
//  public void getSubBlocks(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
//    if (tab != null) {
//      super.getSubBlocks(tab, list);
//    }
//  }
}
