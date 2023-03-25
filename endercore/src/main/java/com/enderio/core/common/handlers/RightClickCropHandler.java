package com.enderio.core.common.handlers;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber
public class RightClickCropHandler {

  public static interface IPlantInfo {
    @Nonnull
    ItemStack getSeed();

    @Nonnull
    BlockState getGrownState();

    @Nonnull
    BlockState getResetState();

    boolean init(@Nonnull String source);
  }

  public static class LegacyPlantInfo implements IPlantInfo {
    public String seed;
    public String block;
    public int meta = 7;
    public int resetMeta = 0;
    public boolean optional;

    private transient @Nonnull Things seedStack = new Things();
    private transient @Nonnull BlockState grownState = Blocks.AIR.defaultBlockState();
    private transient @Nonnull BlockState resetState = Blocks.AIR.defaultBlockState();

    public LegacyPlantInfo() { // for json de-serialization
    }

    public LegacyPlantInfo(String seed, String block, int meta, int resetMeta) {
      this.seed = seed;
      this.block = block;
      this.meta = meta;
      this.resetMeta = resetMeta;
    }

    @Override
    public boolean init(@Nonnull String source) {
      seedStack.add(seed);
      if (!seedStack.isValid()) {
        // some blocks and items share the same id but you cannot make an itemstack from the block.
        // if that is the case here, we can rescue this with a bit of bad code
        try {
          seedStack.add("item:" + seed);
        } catch (Exception e) {
        }
        if (!seedStack.isValid()) {
          if (optional) {
            return false;
          } else {
            throw new RuntimeException("invalid item specifier '" + seed + "' " + source);
          }
        }
      }
      String[] blockinfo = block.split(":");
      if (blockinfo.length != 2) {
        throw new RuntimeException("invalid block specifier '" + block + "' " + source);
      }
      Block mcblock = ForgeRegistries.BLOCKS
          .getValue(new ResourceLocation(NullHelper.notnullJ(blockinfo[0], "String.split()"), NullHelper.notnullJ(blockinfo[1], "String.split()")));
      if (mcblock == null) {
        if (optional) {
          return false;
        } else {
          throw new RuntimeException("invalid block specifier '" + block + "' " + source);
        }
      }
      if (mcblock instanceof BeetrootBlock) { // extends CropBlock, so it needs to be checked first
        meta = 3;
        resetMeta = 0;
        grownState = mcblock.defaultBlockState().setValue(BeetrootBlock.AGE, 3);
        resetState = mcblock.defaultBlockState().setValue(BeetrootBlock.AGE, 0);
      } else if (mcblock instanceof CropBlock) {
        meta = ((CropBlock) mcblock).getMaxAge();
        resetMeta = 0;
        grownState = mcblock.defaultBlockState().setValue(CropBlock.AGE, ((CropBlock) mcblock).getMaxAge());
        resetState = mcblock.defaultBlockState().setValue(CropBlock.AGE, 0);
      } else if (mcblock instanceof NetherWartBlock) {
        meta = 3;
        resetMeta = 0;
        grownState = mcblock.defaultBlockState().setValue(NetherWartBlock.AGE, 3);
        resetState = mcblock.defaultBlockState().setValue(NetherWartBlock.AGE, 0);
      } else {
        // TODO
//        grownState = mcblock.getStateFromMeta(meta);
//        resetState = mcblock.getStateFromMeta(resetMeta);
      }
      return true;
    }

    @Override
    @Nonnull
    public ItemStack getSeed() {
      return seedStack.getItemStack();
    }

    @Override
    @Nonnull
    public BlockState getGrownState() {
      return grownState;
    }

    @Override
    @Nonnull
    public BlockState getResetState() {
      return resetState;
    }
  }

  private List<IPlantInfo> plants = Lists.newArrayList();

  private IPlantInfo currentPlant = null;

  public static final RightClickCropHandler INSTANCE = new RightClickCropHandler();

  private RightClickCropHandler() {
  }

  public void addCrop(IPlantInfo info) {
    plants.add(info);
  }

  @SubscribeEvent
  public void handleCropRightClick(RightClickBlock event) {
    var enabled = true; // ConfigHandler.allowCropRC
    if (!enabled) {
      return;
    }

    if (event.getEntity().getMainHandItem().isEmpty() || !event.getEntity().isSteppingCarefully()) {
      BlockPos pos = event.getPos();
      BlockState blockState = event.getLevel().getBlockState(pos);
      for (IPlantInfo info : plants) {
        if (info.getGrownState() == blockState) {
          if (event.getLevel().isClientSide) {
            event.getEntity().swing(InteractionHand.MAIN_HAND);
          } else {
            currentPlant = info;
            event.getLevel().addFreshEntity(
                    new ItemEntity(event.getLevel(), pos.getX(), pos.getY(), pos.getZ(), new ItemStack(blockState.getBlock().asItem(), 1))
            );
            currentPlant = null;
            BlockState newBS = info.getResetState();
            event.getLevel().setBlock(pos, newBS, 3);
            event.setCanceled(true);
          }
          break;
        }
      }
    }
  }

  // TODO
//  @SubscribeEvent
//  public void onHarvestDrop(BlockDroppedItem event) {
//    if (currentPlant != null) {
//      for (int i = 0; i < event.getDrops().size(); i++) {
//        ItemStack stack = event.getDrops().get(i);
//        if (stack.getItem() == currentPlant.getSeed().getItem()
//            && (currentPlant.getSeed().getItemDamage() == OreDictionary.WILDCARD_VALUE || stack.getItemDamage() == currentPlant.getSeed().getItemDamage())) {
//          stack.shrink(1);
//          if (stack.isEmpty()) {
//            event.getDrops().remove(i);
//          }
//          break;
//        }
//      }
//    }
//  }
}
