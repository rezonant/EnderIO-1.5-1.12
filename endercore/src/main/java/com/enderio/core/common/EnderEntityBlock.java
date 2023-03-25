package com.enderio.core.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class EnderEntityBlock<T extends BlockEntityBase> extends Block implements EntityBlock {

  protected final @Nullable Class<? extends T> teClass;

  protected EnderEntityBlock(@Nullable Class<? extends T> teClass) {
    this(teClass, Material.STONE, MaterialColor.RAW_IRON);
  }

  protected EnderEntityBlock(@Nullable Class<? extends T> teClass, @Nonnull Material mat) {
    this(teClass, mat, mat.getColor());
  }

  protected EnderEntityBlock(@Nullable Class<? extends T> teClass, @Nonnull Material mat, MaterialColor mapColor) {
    super(
            BlockBehaviour.Properties.of(mat, mapColor)
                    .strength(0.5f)
                    .sound(SoundType.METAL)
    );
    this.teClass = teClass;

    // NOTE: This is done using BlockTags.MINEABLE_WITH_PICKAXE and BlockTags.NEEDS_*_TOOL, which
    // would be done outside the Block class.

    //setHarvestLevel("pickaxe", 0);
  }

  @Override
  public PushReaction getPistonPushReaction(BlockState pState) {
    // Some mods coremod vanilla to ignore this condition, so let's try to enforce it.
    // If this doesn't work, we need code to blow up the block when it detects it was moved...
    return PushReaction.BLOCK;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    try {
      T te = teClass.newInstance();
      te.init();
      return te;
    } catch (Exception e) {
      throw new RuntimeException("Could not create tile entity for block " + getName() + " for class " + teClass, e);
    }
  }

  /* Subclass Helpers */

  @Override
  public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (pPlayer.isSteppingCarefully())
        return InteractionResult.PASS;

    BlockEntity te = getBlockEntity(pLevel, pPos);
    if (te instanceof ITankAccess) {
      if (FluidUtil.fillInternalTankFromPlayerHandItem(pLevel, pPos, pPlayer, pHand, (ITankAccess) te)) {
        return InteractionResult.SUCCESS;
      }
      if (FluidUtil.fillPlayerHandItemFromInternalTank(pLevel, pPos, pPlayer, pHand, (ITankAccess) te)) {
        return InteractionResult.SUCCESS;
      }
    }

    return openGui(pLevel, pPos, pPlayer, pHit.getDirection());
  }

  protected InteractionResult openGui(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Player entityPlayer, @Nonnull Direction side) {
    return InteractionResult.PASS;
  }

  @Override
  public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    final T te = getBlockEntity(level, pos);
    final ItemStack drop = getNBTDrop(level, pos, state, te);

    if (willHarvest) {
      if (drop != null) {
        var dropEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop);
        level.addFreshEntity(dropEntity);
      }
      return true;
    }
    
    return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  @Override
  public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
    return super.getDrops(pState, pBuilder);
  }

// TODO
//  /**
//   * override {@link #processPickBlock(BlockState, RayTraceResult, Level, BlockPos, Player, ItemStack)} instead if possible.
//   * <p>
//   *
//   * "Called when a player uses 'pick block'" (client-side) and by mods that don't know about getDrops() on the server...
//   */
//  @Override
//  public final @Nonnull ItemStack getPickBlock(@Nonnull BlockState state, @Nonnull RayTraceResult target, @Nonnull Level world, @Nonnull BlockPos pos,
//      @Nonnull Player player) {
//    if (player.world.isRemote && player.capabilities.isCreativeMode && GuiScreen.isCtrlKeyDown()) {
//      ItemStack nbtDrop = getNBTDrop(world, pos, state, 0, getBlockEntity(world, pos));
//      if (nbtDrop != null) {
//        return nbtDrop;
//      }
//    }
//    return processPickBlock(state, target, world, pos, player, super.getPickBlock(state, target, world, pos, player));
//  }
//
//  protected @Nonnull ItemStack processPickBlock(@Nonnull BlockState state, @Nonnull RayTraceResult target, @Nonnull Level world, @Nonnull BlockPos pos,
//      @Nonnull Player player, @Nonnull ItemStack pickBlock) {
//    return pickBlock;
//  }

  public @Nullable ItemStack getNBTDrop(@Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable T te) {
    ItemStack itemStack = new ItemStack(this, 1, new CompoundTag());
    processDrop(world, pos, te, itemStack);
    return itemStack;
  }

  protected final void processDrop(@Nonnull LevelAccessor world, @Nonnull BlockPos pos, @Nullable T te, @Nonnull ItemStack drop) {
    if (te != null) {
      te.writeCustomNBT(drop);
    }
  }

  @Override
  public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @org.jetbrains.annotations.Nullable LivingEntity pPlacer, ItemStack pStack) {
    T te = getBlockEntity(pLevel, pPos);
    te.readCustomNBT(pStack);
    onBlockPlaced(pLevel, pPos, pState, pPlacer, pStack, te);
  }

  public void onBlockPlaced(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack, @Nonnull T te) {
  }

  /**
   * Tries to load this block's TileEntity if it exists. Will create the TileEntity if it doesn't yet exist.
   * <p>
   * <strong>This will crash if used in any other thread than the main (client or server) thread!</strong>
   *
   */
  protected @Nullable T getBlockEntity(@Nonnull BlockGetter world, @Nonnull BlockPos pos) {
    final Class<? extends T> teClass2 = teClass;
    if (teClass2 != null) {
      BlockEntity te = world.getBlockEntity(pos);
      if (teClass2.isInstance(te)) {
        return teClass2.cast(te);
      }
    }
    return null;
  }

  /**
   * Tries to load this block's TileEntity if it exists. Will not create the TileEntity when used in a render
   * thread with the correct LevelAccessor.
   */
  protected @Nullable T safelyGetBlockEntity(@Nonnull BlockGetter world, @Nonnull BlockPos pos) {
    if (world instanceof Level) {
      final Class<? extends T> teClass2 = teClass;
      var te = ((Level)world).getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
      if (teClass2.isInstance(te)) {
        return teClass2.cast(te);
      }
      return null;
    } else {
      return getBlockEntity(world, pos);
    }
  }

  /**
   * Tries to load any block's TileEntity if it exists. Will not create the TileEntity when used in a render thread with the correct LevelAccessor. Will not
   * cause chunk loads.
   *
   */
  public static @Nullable BlockEntity safelyGetAnyBlockEntity(@Nonnull LevelAccessor world, @Nonnull BlockPos pos) {
    return safelyGetAnyBlockEntity(world, pos, BlockEntity.class);
  }

  /**
   * Tries to load any block's TileEntity if it exists. Will not create the TileEntity when used in a render thread with the correct LevelAccessor. Will not
   * cause chunk loads. Also works with interfaces as the class parameter.
   *
   */
  @SuppressWarnings("unchecked")
  public static @Nullable <Q> Q safelyGetAnyBlockEntity(@Nonnull LevelAccessor world, @Nonnull BlockPos pos, Class<Q> teClass) {
    BlockEntity te = null;
    if (world instanceof Level) {
      if (((Level) world).isLoaded(pos)) {
        te = ((Level) world).getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
      }
    } else {
      te = world.getBlockEntity(pos);
    }

    if (teClass == null) {
      return (Q) te;
    }

    if (teClass.isInstance(te)) {
      return teClass.cast(te);
    }
    return null;
  }

  /**
   * Tries to load any block's TileEntity if it exists. Not suitable for tasks outside the main thread. Also works with interfaces as the class parameter.
   *
   */
  @SuppressWarnings("unchecked")
  public static @Nullable <Q> Q getAnyTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockPos pos, Class<Q> teClass) {
    BlockEntity te = world.getBlockEntity(pos);
    if (teClass == null) {
      return (Q) te;
    }
    if (teClass.isInstance(te)) {
      return teClass.cast(te);
    }
    return null;
  }

  protected boolean shouldDoWorkThisTick(@Nonnull Level world, @Nonnull BlockPos pos, int interval) {
    T te = getBlockEntity(world, pos);
    if (te == null) {
      return world.getGameTime() % interval == 0;
    } else {
      return te.shouldDoWorkThisTick(interval);
    }
  }

  protected boolean shouldDoWorkThisTick(@Nonnull Level world, @Nonnull BlockPos pos, int interval, int offset) {
    T te = getBlockEntity(world, pos);
    if (te == null) {
      return (world.getGameTime() + offset) % interval == 0;
    } else {
      return te.shouldDoWorkThisTick(interval, offset);
    }
  }

  public Class<? extends T> getTeClass() {
    return teClass;
  }

  public void setShape(IShape<T> shape) {
    this.shape = shape;
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    if (shape != null) {
      T te = safelyGetBlockEntity(pLevel, pPos);
      if (te != null) {
        return shape.getBlockFaceShape(pLevel, pState, pPos, Direction.UP, te); // TODO: face/direction
      } else {
        return shape.getBlockFaceShape(pLevel, pState, pPos, Direction.UP); // TODO: face/direction
      }
    }
    return super.getShape(pState, pLevel, pPos, pContext);
  }

  private IShape<T> shape = null;

  public static interface IShape<T> {
    @Nonnull
    VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction face);

    default @Nonnull VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos,
        @Nonnull Direction face, @Nonnull T te) {
      return getBlockFaceShape(worldIn, state, pos, face);
    }
  }

  protected @Nonnull IShape<T> mkShape(@Nonnull VoxelShape allFaces) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction face) {
        return allFaces;
      }
    };
  }

  protected @Nonnull IShape<T> mkShape(@Nonnull VoxelShape upDown, @Nonnull VoxelShape allSides) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction face) {
        return face == Direction.UP || face == Direction.DOWN ? upDown : allSides;
      }
    };
  }

  protected @Nonnull IShape<T> mkShape(@Nonnull VoxelShape down, @Nonnull VoxelShape up, @Nonnull VoxelShape allSides) {
    return new IShape<T>() {
      @Override
      @Nonnull
      public VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction face) {
        return face == Direction.UP ? up : face == Direction.DOWN ? down : allSides;
      }
    };
  }

  protected @Nonnull IShape<T> mkShape(@Nonnull VoxelShape... faces) {
    return new IShape<T>() {
      @SuppressWarnings("null")
      @Override
      @Nonnull
      public VoxelShape getBlockFaceShape(@Nonnull BlockGetter worldIn, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Direction face) {
        return faces[face.ordinal()];
      }
    };
  }

}