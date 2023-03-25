package com.enderio.core.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import com.enderio.core.EnderCore;
import com.enderio.core.common.vecmath.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityUtil {

  private static final Random rand = new Random();

  @Deprecated
  public static void setEntityVelocity(Entity entity, double velX, double velY, double velZ) {
    entity.setDeltaMovement(velX, velY, velZ);
  }

  public static @Nonnull FireworkRocketEntity getRandomFirework(@Nonnull Level world) {
    return getRandomFirework(world, new BlockPos(0, 0, 0));
  }

  public static @Nonnull FireworkRocketEntity getRandomFirework(@Nonnull Level world, @Nonnull BlockPos pos) {
    ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);
    firework.setTag(new CompoundTag());
    CompoundTag expl = new CompoundTag();
    expl.putBoolean("Flicker", true);
    expl.putBoolean("Trail", true);

    int[] colors = new int[rand.nextInt(8) + 1];
    for (int i = 0; i < colors.length; i++) {
      colors[i] = DyeColor.values()[rand.nextInt(16)].getId();
    }
    expl.putIntArray("Colors", colors);
    byte type = (byte) (rand.nextInt(3) + 1);
    type = type == 3 ? 4 : type;
    expl.putByte("Type", type);


    ListTag explosions = new ListTag();
    explosions.add(expl);

    CompoundTag fireworkTag = new CompoundTag();
    fireworkTag.put("Explosions", explosions);
    fireworkTag.putByte("Flight", (byte) 1);
    firework.addTagElement("Fireworks", fireworkTag);

    FireworkRocketEntity e = new FireworkRocketEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, firework);
    return e;
  }

  public static void spawnFirework(@Nonnull Level level, @Nonnull BlockPos block) {
    spawnFirework(level, block, 0);
  }

  public static void spawnFirework(@Nonnull Level level, @Nonnull BlockPos pos, int range) {
    BlockPos spawnPos = pos;

    // don't bother if there's no randomness at all
    if (range > 0) {
      spawnPos = new BlockPos(moveRandomly(spawnPos.getX(), range), spawnPos.getY(), moveRandomly(spawnPos.getZ(), range));
      BlockState bs = level.getBlockState(spawnPos);

      // TODO seems bad
//      int tries = -1;
//      while (!level.isEmptyBlock(new BlockPos(spawnPos)) && !bs.canBeReplaced(BlockPlaceContext.at(new BlockPlaceContext(), spawnPos)) {
//        tries++;
//        if (tries > 100) {
//          return;
//        }
//      }
    }

    var firework = getRandomFirework(level, spawnPos);
    level.addFreshEntity(firework);
  }

  private static double moveRandomly(double base, double range) {
    return base + 0.5 + rand.nextDouble() * range - (range / 2);
  }

  public static @Nonnull String getDisplayNameForEntity(@Nonnull String mobName) {
    return EnderCore.lang.localizeExact("entity." + mobName + ".name");
  }

  public static @Nonnull NNList<ResourceLocation> getAllRegisteredMobNames() {
    NNList<ResourceLocation> result = new NNList<ResourceLocation>();
    for (ResourceLocation entityName : ForgeRegistries.ENTITY_TYPES.getKeys()) {
      var entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityName);
      final Class<? extends Entity> clazz = entityType.getBaseClass();
      if (clazz != null && LivingEntity.class.isAssignableFrom(clazz)) {
        result.add(entityName);
      }
    }
    return result;
  }

  public static boolean isRegisteredMob(ResourceLocation entityName) {
    if (entityName != null) {
      var entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityName);
      final Class<? extends Entity> clazz = entityType.getBaseClass();
      return clazz != null && LivingEntity.class.isAssignableFrom(clazz);
    }
    return false;
  }

  private EntityUtil() {
  }

  public static Vector3d getEntityPosition(@Nonnull Entity ent) {
    return new Vector3d(ent.getX(), ent.getY(), ent.getZ());
  }

  public static List<AABB> getCollidingBlockGeometry(@Nonnull Level world, @Nonnull Entity entity) {
    AABB entityBounds = entity.getBoundingBox();
    ArrayList<AABB> collidingBoundingBoxes = new ArrayList<AABB>();
    int minX = (int)entityBounds.minX;
    int minY = (int)entityBounds.minY;
    int minZ = (int)entityBounds.minZ;
    int maxX = (int)entityBounds.maxX + 1;
    int maxY = (int)entityBounds.maxY + 1;
    int maxZ = (int)entityBounds.maxZ + 1;
    for (int x = minX; x < maxX; x++) {
      for (int z = minZ; z < maxZ; z++) {
        for (int y = minY; y < maxY; y++) {
          BlockPos pos = new BlockPos(x, y, z);
          collidingBoundingBoxes.add(world.getBlockState(pos).getShape(world, pos).bounds());
        }
      }
    }
    return collidingBoundingBoxes;
  }

  public static void spawnItemInWorldWithRandomMotion(@Nonnull Level world, @Nonnull ItemStack item, int x, int y, int z) {
    if (!item.isEmpty()) {
      spawnItemInWorldWithRandomMotion(world, item, x + 0.5, y + 0.5, z + 0.5);
    }
  }

  public static void spawnItemInWorldWithRandomMotion(@Nonnull Level world, @Nonnull ItemStack item, double x, double y, double z) {
    if (!item.isEmpty()) {
      spawnItemInWorldWithRandomMotion(new ItemEntity(world, x, y, z, item));
    }
  }

  public static void spawnItemInWorldWithRandomMotion(@Nonnull ItemEntity entity) {
    entity.setDefaultPickUpDelay();

    float f = (entity.level.random.nextFloat() * 0.1f) - 0.05f;
    float f1 = (entity.level.random.nextFloat() * 0.1f) - 0.05f;
    float f2 = (entity.level.random.nextFloat() * 0.1f) - 0.05f;

    entity.push(f, f1, f2);
    entity.level.addFreshEntity(entity);
  }
}
