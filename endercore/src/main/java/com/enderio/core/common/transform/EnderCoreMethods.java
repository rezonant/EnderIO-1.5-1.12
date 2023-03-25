//package com.enderio.core.common.transform;
//
//import javax.annotation.Nonnull;
//
//import com.enderio.core.common.event.ItemGUIRenderEvent;
//import com.enderio.core.common.interfaces.INotDestroyedInItemFrames;
//
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.monster.Creeper;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
//import net.minecraft.world.level.material.Material;
//import net.minecraft.world.phys.AABB;
//import net.minecraftforge.common.MinecraftForge;
//
//public class EnderCoreMethods {
//
//  // copied from ContainerFurnace, changes marked
//  public static @Nonnull ItemStack transferStackInSlot(@Nonnull FurnaceBlockEntity inv, @Nonnull Player playerIn, int index) {
//    ItemStack itemstack = ItemStack.EMPTY;
//    Slot slot = inv.get.get(index);
//
//    if (slot != null && slot.getHasStack()) {
//      ItemStack itemstack1 = slot.getStack();
//      itemstack = itemstack1.copy();
//
//      if (index == 2) {
//        if (!mergeItemStack(inv, itemstack1, 3, 39, true)) {
//          return ItemStack.EMPTY;
//        }
//
//        slot.setChanged();
//        //slot.onSlotChange(itemstack1, itemstack); <-- TODO significant?
//      } else if (index != 1 && index != 0) {
//        if (FurnaceBlockEntity.isFuel(itemstack1) && mergeItemStack(inv, itemstack1, 1, 2, false)) { // HL: added this case
//          // NOP - if we can move an item into the fuel slot, we're happy and done. Otherwise try to move it into the input slot, then stop moving it.
//        } else if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
//          if (!mergeItemStack(inv, itemstack1, 0, 1, false)) {
//            return ItemStack.EMPTY;
//          }
//        } else if (FurnaceBlockEntity.isFuel(itemstack1)) {
//          if (!mergeItemStack(inv, itemstack1, 1, 2, false)) {
//            return ItemStack.EMPTY;
//          }
//        } else if (index >= 3 && index < 30) {
//          if (!mergeItemStack(inv, itemstack1, 30, 39, false)) {
//            return ItemStack.EMPTY;
//          }
//        } else if (index >= 30 && index < 39 && !mergeItemStack(inv, itemstack1, 3, 30, false)) {
//          return ItemStack.EMPTY;
//        }
//      } else if (!mergeItemStack(inv, itemstack1, 3, 39, false)) {
//        return ItemStack.EMPTY;
//      }
//
//      if (itemstack1.isEmpty()) {
//        slot.set(ItemStack.EMPTY);
//      } else {
//        slot.setChanged();
//      }
//
//      if (itemstack1.getCount() == itemstack.getCount()) {
//        return ItemStack.EMPTY;
//      }
//
//      slot.onTake(playerIn, itemstack1);
//    }
//
//    return itemstack;
//  }
//
//  // copied from Container, unchanged
//  private static boolean mergeItemStack(@Nonnull Container inv, @Nonnull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
//    boolean flag = false;
//    int i = startIndex;
//
//    if (reverseDirection) {
//      i = endIndex - 1;
//    }
//
//    if (stack.isStackable()) {
//      while (!stack.isEmpty()) {
//        if (reverseDirection) {
//          if (i < startIndex) {
//            break;
//          }
//        } else if (i >= endIndex) {
//          break;
//        }
//
//        Slot slot = inv.inventorySlots.get(i);
//        ItemStack itemstack = slot.getStack();
//
//        if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata())
//            && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
//          int j = itemstack.getCount() + stack.getCount();
//          int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
//
//          if (j <= maxSize) {
//            stack.setCount(0);
//            itemstack.setCount(j);
//            slot.setChanged();
//            flag = true;
//          } else if (itemstack.getCount() < maxSize) {
//            stack.shrink(maxSize - itemstack.getCount());
//            itemstack.setCount(maxSize);
//            slot.setChanged();
//            flag = true;
//          }
//        }
//
//        if (reverseDirection) {
//          --i;
//        } else {
//          ++i;
//        }
//      }
//    }
//
//    if (!stack.isEmpty()) {
//      if (reverseDirection) {
//        i = endIndex - 1;
//      } else {
//        i = startIndex;
//      }
//
//      while (true) {
//        if (reverseDirection) {
//          if (i < startIndex) {
//            break;
//          }
//        } else if (i >= endIndex) {
//          break;
//        }
//
//        Slot slot1 = inv.inventorySlots.get(i);
//        ItemStack itemstack1 = slot1.getStack();
//
//        if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
//          if (stack.getCount() > slot1.getSlotStackLimit()) {
//            slot1.set(stack.splitStack(slot1.getSlotStackLimit()));
//          } else {
//            slot1.set(stack.splitStack(stack.getCount()));
//          }
//
//          slot1.setChanged();
//          flag = true;
//          break;
//        }
//
//        if (reverseDirection) {
//          --i;
//        } else {
//          ++i;
//        }
//      }
//    }
//
//    return flag;
//  }
//
//  public static void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
//    if (!stack.isEmpty()) {
//      if (stack.getItem() instanceof com.enderio.core.common.interfaces.IOverlayRenderAware) {
//        ((com.enderio.core.common.interfaces.IOverlayRenderAware) stack.getItem()).renderItemOverlayIntoGUI(stack, xPosition, yPosition);
//      }
//      MinecraftForge.EVENT_BUS.post(new ItemGUIRenderEvent.Post(stack, xPosition, yPosition));
//    }
//  }
//
//  public static void renderItemAndEffectIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
//    if (!stack.isEmpty()) {
//      if (stack.getItem() instanceof com.enderio.core.common.interfaces.IUnderlayRenderAware) {
//        ((com.enderio.core.common.interfaces.IUnderlayRenderAware) stack.getItem()).renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
//      }
//      MinecraftForge.EVENT_BUS.post(new ItemGUIRenderEvent.Pre(stack, xPosition, yPosition));
//    }
//  }
//
//  // Note: isRiding() and isInWater() are cheap getters, isInLava() is an expensive volumetric search
//  public static boolean isElytraFlying(@Nonnull LivingEntity entity) {
//    ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.CHEST);
//    if (itemstack.getItem() instanceof com.enderio.core.common.interfaces.IElytraFlyingProvider) {
//      return ((com.enderio.core.common.interfaces.IElytraFlyingProvider) itemstack.getItem()).isElytraFlying(entity, itemstack,
//          entity.isOnGround() || ((entity instanceof Player) && ((Player) entity).capabilities.isFlying) || entity.isPassenger() || entity.isInWater()
//              || isInLavaSafe(entity));
//    }
//    return false;
//  }
//
//  // non-chunkloading copy of Entity.isInLava()
//  public static boolean isInLavaSafe(@Nonnull Entity entity) {
//    return isMaterialInBBSafe(entity.level, entity.getBoundingBox().inflate(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D),
//        Material.LAVA);
//  }
//
//  // non-chunkloading copy of World.isMaterialInBB()
//  public static boolean isMaterialInBBSafe(@Nonnull Level world, @Nonnull AABB bb, @Nonnull Material materialIn) {
//    int i = MathHelper.floor(bb.minX);
//    int j = MathHelper.ceil(bb.maxX);
//    int k = MathHelper.floor(bb.minY);
//    int l = MathHelper.ceil(bb.maxY);
//    int i1 = MathHelper.floor(bb.minZ);
//    int j1 = MathHelper.ceil(bb.maxZ);
//    BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
//
//    for (int k1 = i; k1 < j; ++k1) {
//      for (int l1 = k; l1 < l; ++l1) {
//        for (int i2 = i1; i2 < j1; ++i2) {
//          blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
//          if (world.isBlockLoaded(blockpos$pooledmutableblockpos, false) && world.getBlockState(blockpos$pooledmutableblockpos).getMaterial() == materialIn) {
//            blockpos$pooledmutableblockpos.release();
//            return true;
//          }
//        }
//      }
//    }
//
//    blockpos$pooledmutableblockpos.release();
//    return false;
//  }
//
//  public static boolean isCreeperTarget(@Nonnull Creeper swellingCreeper, @Nonnull LivingEntity entitylivingbase) {
//    if (entitylivingbase instanceof com.enderio.core.common.interfaces.ICreeperTarget) {
//      return ((com.enderio.core.common.interfaces.ICreeperTarget) entitylivingbase).isCreeperTarget(swellingCreeper);
//    }
//    return true;
//  }
//
//  public static void processInitialInteract(Player player, InteractionHand hand) {
//    if (!player.level.isRemote && !player.isCreative()) {
//      ItemStack itemstack = player.getItemInHand(hand);
//      if (itemstack.getItem() instanceof INotDestroyedInItemFrames) {
//        // The calling code got stored getHeldItem() before calling us and will check that stack when determining if something happened to it
//        player.setItemInHand(hand, itemstack.copy());
//      }
//    }
//  }
//
//  @Deprecated
//  public interface ICreeperTarget extends com.enderio.core.common.interfaces.ICreeperTarget {
//  }
//
//  @Deprecated
//  public interface IElytraFlyingProvider extends com.enderio.core.common.interfaces.IElytraFlyingProvider {
//  }
//
//  @Deprecated
//  public interface IOverlayRenderAware extends com.enderio.core.common.interfaces.IOverlayRenderAware {
//  }
//
//  @Deprecated
//  public interface IUnderlayRenderAware extends com.enderio.core.common.interfaces.IUnderlayRenderAware {
//  }
//
//}
