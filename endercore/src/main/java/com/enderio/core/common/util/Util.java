package com.enderio.core.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.vecmath.Vector3d;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class Util {

  public static @Nullable Block getBlockFromItemId(@Nonnull ItemStack itemId) {
    Item item = itemId.getItem();
    if (item instanceof BlockItem) {
      return ((BlockItem) item).getBlock();
    }
    return null;
  }

  public static @Nonnull ItemStack consumeItem(@Nonnull ItemStack stack) {
    if (stack.getItem() instanceof PotionItem) {
      if (stack.getCount() == 1) {
        return new ItemStack(Items.GLASS_BOTTLE);
      } else {
        stack.split(1);
        return stack;
      }
    }
    if (stack.getCount() == 1) {
      if (stack.getItem().hasCraftingRemainingItem(stack)) {
        return stack.getItem().getCraftingRemainingItem(stack);
      } else {
        return ItemStack.EMPTY;
      }
    } else {
      stack.split(1);
      return stack;
    }
  }

  public static void giveExperience(@Nonnull Player thePlayer, float experience) {
    int intExp = (int) experience;
    float fractional = experience - intExp;
    if (fractional > 0.0F) {
      if ((float) Math.random() < fractional) {
        ++intExp;
      }
    }
    while (intExp > 0) {
      int j = ExperienceOrb.getExperienceValue(intExp);
      intExp -= j;
      var orb = new ExperienceOrb(thePlayer.level, thePlayer.getX(), thePlayer.getY() + 0.5D, thePlayer.getZ() + 0.5D, j);
      thePlayer.level.addFreshEntity(orb);
    }
  }

  public static ItemEntity createDrop(@Nonnull Level world, @Nonnull ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
    if (stack.isEmpty()) {
      return null;
    }
    if (doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      ItemEntity entityitem = new ItemEntity(world, x + d, y + d1, z + d2, stack);
      entityitem.setDefaultPickUpDelay();
      return entityitem;
    } else {
      ItemEntity entityitem = new ItemEntity(world, x, y, z, stack);

      // TODO
//      entityitem.motionX = 0;
//      entityitem.motionY = 0;
//      entityitem.motionZ = 0;
      entityitem.setNoPickUpDelay();
      return entityitem;
    }
  }

  public static void dropItems(@Nonnull Level world, @Nonnull ItemStack stack, @Nonnull BlockPos pos, boolean doRandomSpread) {
    dropItems(world, stack, pos.getX(), pos.getY(), pos.getZ(), doRandomSpread);
  }

  public static void dropItems(@Nonnull Level world, @Nonnull ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
    if (stack.isEmpty()) {
      return;
    }

    ItemEntity itemEntity = createEntityItem(world, stack, x, y, z, doRandomSpread);
    world.addFreshEntity(itemEntity);
  }

  public static ItemEntity createEntityItem(@Nonnull Level world, @Nonnull ItemStack stack, double x, double y, double z) {
    return createEntityItem(world, stack, x, y, z, true);
  }

  public static @Nonnull ItemEntity createEntityItem(@Nonnull Level world, @Nonnull ItemStack stack, double x, double y, double z, boolean doRandomSpread) {
    ItemEntity entityitem;
    if (doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      entityitem = new ItemEntity(world, x + d, y + d1, z + d2, stack);
      entityitem.setDefaultPickUpDelay();
    } else {
      entityitem = new ItemEntity(world, x, y, z, stack);

      // TODO
//      entityitem.motionX = 0;
//      entityitem.motionY = 0;
//      entityitem.motionZ = 0;
      entityitem.setNoPickUpDelay();
    }
    return entityitem;
  }

  public static void dropItems(@Nonnull Level world, @Nonnull ItemStack stack, int x, int y, int z, boolean doRandomSpread) {
    if (stack.isEmpty()) {
      return;
    }

    if (doRandomSpread) {
      float f1 = 0.7F;
      double d = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d1 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      double d2 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
      ItemEntity itemEntity = new ItemEntity(world, x + d, y + d1, z + d2, stack);
      itemEntity.setDefaultPickUpDelay();
      world.addFreshEntity(itemEntity);
    } else {
      ItemEntity itemEntity = new ItemEntity(world, x + 0.5, y + 0.5, z + 0.5, stack);
      // TODO
//      itemEntity.motionX = 0;
//      itemEntity.motionY = 0;
//      itemEntity.motionZ = 0;
      itemEntity.setNoPickUpDelay();
      world.addFreshEntity(itemEntity);
    }
  }

  public static void dropItems(@Nonnull Level world, ItemStack[] inventory, int x, int y, int z, boolean doRandomSpread) {
    if (inventory == null) {
      return;
    }
    for (ItemStack stack : inventory) {
      if (!stack.isEmpty()) {
        dropItems(world, stack.copy(), x, y, z, doRandomSpread);
      }
    }
  }

  public static void dropItems(@Nonnull Level world, @Nonnull IItemHandler inventory, int x, int y, int z, boolean doRandomSpread) {
    for (int l = 0; l < inventory.getSlots(); ++l) {
      ItemStack items = inventory.getStackInSlot(l);

      if (!items.isEmpty()) {
        dropItems(world, items.copy(), x, y, z, doRandomSpread);
      }
    }
  }

  public static boolean dumpModObjects(@Nonnull File file) {

    StringBuilder sb = new StringBuilder();
    for (Object key : ForgeRegistries.BLOCKS.getKeys()) {
      if (key != null) {
        sb.append(key.toString());
        sb.append("\n");
      }
    }
    for (Object key : ForgeRegistries.ITEMS.getKeys()) {
      if (key != null) {
        sb.append(key.toString());
        sb.append("\n");
      }
    }

    try {
      Files.write(sb, file, Charsets.UTF_8);
      return true;
    } catch (IOException e) {
      Log.warn("Error dumping ore dictionary entries: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  public static @Nonnull com.mojang.math.Vector3d getEyePosition(@Nonnull Player player) {
    return new com.mojang.math.Vector3d(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
  }

  public static @Nonnull Vector3d getEyePositionEio(@Nonnull Player player) {
    return new Vector3d(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
  }

  public static @Nonnull Vector3d getLookVecEio(@Nonnull Player player) {
    var lv = player.getLookAngle();
    return new Vector3d(lv.x, lv.y, lv.z);
  }

  // Code adapted from World.rayTraceBlocks to return all
  // collided blocks
//  public static @Nonnull List<BlockHitResult> raytraceAll(
//          @Nonnull Level world, @Nonnull com.mojang.math.Vector3d startVector,
//          @Nonnull com.mojang.math.Vector3d endVec, boolean includeLiquids) {
//    boolean ignoreBlockWithoutBoundingBox = true;
//    Vec3d startVec = startVector;
//
//    List<RayTraceResult> result = new ArrayList<RayTraceResult>();
//
//    if (!Double.isNaN(startVec.x) && !Double.isNaN(startVec.y) && !Double.isNaN(startVec.z)) {
//      if (!Double.isNaN(endVec.x) && !Double.isNaN(endVec.y) && !Double.isNaN(endVec.z)) {
//        int i = MathHelper.floor(endVec.x);
//        int j = MathHelper.floor(endVec.y);
//        int k = MathHelper.floor(endVec.z);
//        int l = MathHelper.floor(startVec.x);
//        int i1 = MathHelper.floor(startVec.y);
//        int j1 = MathHelper.floor(startVec.z);
//        BlockPos blockpos = new BlockPos(l, i1, j1);
//        IBlockState iblockstate = world.getBlockState(blockpos);
//        Block block = iblockstate.getBlock();
//
//        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB)
//            && block.canCollideCheck(iblockstate, includeLiquids)) {
//          @Nonnull
//          RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, startVec, endVec);
//          result.add(raytraceresult);
//        }
//
//        int k1 = 200;
//
//        while (k1-- >= 0) {
//          if (Double.isNaN(startVec.x) || Double.isNaN(startVec.y) || Double.isNaN(startVec.z)) {
//            return new ArrayList<RayTraceResult>();
//          }
//
//          if (l == i && i1 == j && j1 == k) {
//            return result;
//          }
//
//          boolean flag2 = true;
//          boolean flag = true;
//          boolean flag1 = true;
//          double d0 = 999.0D;
//          double d1 = 999.0D;
//          double d2 = 999.0D;
//
//          if (i > l) {
//            d0 = l + 1.0D;
//          } else if (i < l) {
//            d0 = l + 0.0D;
//          } else {
//            flag2 = false;
//          }
//
//          if (j > i1) {
//            d1 = i1 + 1.0D;
//          } else if (j < i1) {
//            d1 = i1 + 0.0D;
//          } else {
//            flag = false;
//          }
//
//          if (k > j1) {
//            d2 = j1 + 1.0D;
//          } else if (k < j1) {
//            d2 = j1 + 0.0D;
//          } else {
//            flag1 = false;
//          }
//
//          double d3 = 999.0D;
//          double d4 = 999.0D;
//          double d5 = 999.0D;
//          double d6 = endVec.x - startVec.x;
//          double d7 = endVec.y - startVec.y;
//          double d8 = endVec.z - startVec.z;
//
//          if (flag2) {
//            d3 = (d0 - startVec.x) / d6;
//          }
//
//          if (flag) {
//            d4 = (d1 - startVec.y) / d7;
//          }
//
//          if (flag1) {
//            d5 = (d2 - startVec.z) / d8;
//          }
//
//          if (d3 == -0.0D) {
//            d3 = -1.0E-4D;
//          }
//
//          if (d4 == -0.0D) {
//            d4 = -1.0E-4D;
//          }
//
//          if (d5 == -0.0D) {
//            d5 = -1.0E-4D;
//          }
//
//          Direction enumfacing;
//
//          if (d3 < d4 && d3 < d5) {
//            enumfacing = i > l ? Direction.WEST : Direction.EAST;
//            startVec = new Vec3d(d0, startVec.y + d7 * d3, startVec.z + d8 * d3);
//          } else if (d4 < d5) {
//            enumfacing = j > i1 ? Direction.DOWN : Direction.UP;
//            startVec = new Vec3d(startVec.x + d6 * d4, d1, startVec.z + d8 * d4);
//          } else {
//            enumfacing = k > j1 ? Direction.NORTH : Direction.SOUTH;
//            startVec = new Vec3d(startVec.x + d6 * d5, startVec.y + d7 * d5, d2);
//          }
//
//          l = MathHelper.floor(startVec.x) - (enumfacing == Direction.EAST ? 1 : 0);
//          i1 = MathHelper.floor(startVec.y) - (enumfacing == Direction.UP ? 1 : 0);
//          j1 = MathHelper.floor(startVec.z) - (enumfacing == Direction.SOUTH ? 1 : 0);
//          blockpos = new BlockPos(l, i1, j1);
//          IBlockState iblockstate1 = world.getBlockState(blockpos);
//          Block block1 = iblockstate1.getBlock();
//
//          if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL
//              || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
//            if (block1.canCollideCheck(iblockstate1, includeLiquids)) {
//              @Nonnull
//              RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, startVec, endVec);
//              result.add(raytraceresult1);
//            }
//          }
//        }
//
//        return result;
//      } else {
//        return result;
//      }
//    } else {
//      return result;
//    }
//  }

  public static @Nullable Direction getDirFromOffset(int xOff, int yOff, int zOff) {
    if (xOff != 0 && yOff == 0 && zOff == 0) {
      return xOff < 0 ? Direction.WEST : Direction.EAST;
    }
    if (zOff != 0 && yOff == 0 && xOff == 0) {
      return zOff < 0 ? Direction.NORTH : Direction.SOUTH;
    }
    if (yOff != 0 && xOff == 0 && zOff == 0) {
      return yOff < 0 ? Direction.DOWN : Direction.UP;
    }
    return null;
  }

  public static @Nonnull Direction getFacingFromEntity(@Nonnull LivingEntity entity) {
    int heading = ((int)(entity.getRotationVector().y * 4.0F / 360.0F + 0.5D)) & 3;
    switch (heading) {
    case 0:
      return Direction.NORTH;
    case 1:
      return Direction.EAST;
    case 2:
      return Direction.SOUTH;
    case 3:
    default:
      return Direction.WEST;
    }

  }

  public static int getProgressScaled(int scale, @Nonnull IProgressTile tile) {
    return (int) (tile.getProgress() * scale);
  }

  public static void writeFacingToNBT(@Nonnull CompoundTag nbtRoot, @Nonnull String name, @Nonnull Direction dir) {
    short val = -1;
    val = (short) dir.ordinal();
    nbtRoot.putShort(name, val);
  }

  public static @Nullable Direction readFacingFromNBT(@Nonnull CompoundTag nbtRoot, @Nonnull String name) {
    short val = -1;
    if (nbtRoot.contains(name)) {
      val = nbtRoot.getShort(name);
    }
    if (val > 0) {
      return Direction.values()[val];
    }
    return null;
  }

}
