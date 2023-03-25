package com.enderio.core.common;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.network.EnderPacketHandler;
import com.enderio.core.common.network.PacketProgress;
import com.enderio.core.common.util.NullHelper;

import com.mojang.math.Vector3d;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class BlockEntityBase extends BlockEntity implements Tickable {

  private final int checkOffset = (int) (Math.random() * 20);
  protected final boolean isProgressTile = this instanceof IProgressTile;

  protected float lastProgressSent = -1;
  protected long lastProgressUpdate;
  private long lastUpdate = 0;

  public BlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
    super(pType, pPos, pBlockState);
  }

  @Override
  public final void tick() {
    // Note: Commented out checks are done in World for 1.12
    if (/* !hasLevel() || isInvalid() || !world.isLoaded(getBlockPos()) || */ level.getBlockEntity(getBlockPos()) != this
        || level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
      // we can get ticked after being removed from the world, ignore this
      return;
    }

    // TODO: config: ConfigHandler.allowExternalTickSpeedup
    if (level.getGameTime() != lastUpdate) {
      lastUpdate = level.getGameTime();
      doUpdate();
      sendProgressIf();
    }
  }

  public static int getProgressScaled(int scale, @Nonnull IProgressTile tile) {
    return (int) (tile.getProgress() * scale);
  }

  private final void sendProgressIf() {
    // this is only used for players that do not have the GUI open. They do not need a very fine resolution, as they only see the the machine being on or
    // off and get the sound restarted on progress==0
    if (isProgressTile && !level.isClientSide) {
      float progress = ((IProgressTile) this).getProgress();
      boolean send = //
          progress < lastProgressSent // always send progress if it goes down, e.g. machine goes inactive or new task starts
              || (lastProgressSent <= 0 && progress > 0) // always send progress if machine goes active
              || (lastUpdate - lastProgressUpdate) > 60 * 20; // also update every 60 seconds to avoid stale client status

      if (send) {
        EnderPacketHandler.sendToAllTracking(((IProgressTile) this).getProgressPacket(), this);
        lastProgressSent = progress;
        lastProgressUpdate = lastUpdate;
      }
    }
  }

  protected void doUpdate() {

  }

  public @Nonnull PacketProgress getProgressPacket() {
    return new PacketProgress((IProgressTile) this);
  }

  /**
   * SERVER: Called when being written to the save file.
   */
  @Override
  public final @Nonnull void saveAdditional(@Nonnull CompoundTag root) {
    super.saveAdditional(root);
    writeCustomNBT(NBTAction.SAVE, root);
  }

  /**
   * SERVER: Called when being read from the save file.
   */
  @Override
  public final void load(@Nonnull CompoundTag tag) {
    super.load(tag);
    readCustomNBT(NBTAction.SAVE, tag);
  }

  /**
   * Called when the chunk data is sent (client receiving chunks from server). Must have x/y/z tags.
   */
  @Override
  public final @Nonnull CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    writeCustomNBT(NBTAction.CLIENT, tag);
    if (isProgressTile) {
      // TODO: nicer way to do this? This is needed so players who enter a chunk get a correct progress.
      tag.putFloat("tileprogress", ((IProgressTile) this).getProgress());
    }
    return tag;
  }

  /**
   * CLIENT: Called when chunk data is received (client receiving chunks from server).
   */
  @Override
  public final void handleUpdateTag(@Nonnull CompoundTag tag) {
    super.handleUpdateTag(tag);
    readCustomNBT(NBTAction.CLIENT, tag);
    if (isProgressTile) {
      // TODO: nicer way to do this? This is needed so players who enter a chunk get a correct progress.
      ((IProgressTile) this).setProgress(tag.getFloat("tileprogress"));
    }
  }

  protected void writeCustomNBT(@Nonnull ItemStack stack) {
    final CompoundTag tag = new CompoundTag();
    writeCustomNBT(NBTAction.ITEM, tag);
    if (!tag.isEmpty()) {
      stack.setTag(tag);
    }
  }

  @Deprecated
  protected abstract void writeCustomNBT(@Nonnull NBTAction action, @Nonnull CompoundTag root);

  protected void readCustomNBT(@Nonnull ItemStack stack) {
    if (stack.hasTag()) {
      readCustomNBT(NBTAction.ITEM, NullHelper.notnullM(stack.getTag(), "tag compound vanished"));
    }
  }

  @Deprecated
  protected abstract void readCustomNBT(@Nonnull NBTAction action, @Nonnull CompoundTag root);

  public boolean canPlayerAccess(Player player) {
    return hasLevel() && !isRemoved() && player.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) <= 64D;
  }

  protected void updateBlock() {
    if (hasLevel() && level.isLoaded(getBlockPos())) {
      BlockState bs = level.getBlockState(getBlockPos());
      level.sendBlockUpdated(worldPosition, bs, bs, 3);
    }
  }

  protected boolean isPoweredRedstone() {
    return hasLevel() && level.isLoaded(getBlockPos()) ? level.getBestNeighborSignal(getBlockPos()) > 0 : false;
  }

  /**
   * Called directly after the TE is constructed. This is the place to call non-final methods.
   *
   * Note: This will not be called when the TE is loaded from the save. Hook into the nbt methods for that.
   */
  public void init() {
  }

  /**
   * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This is staggered so the work of different TEs
   * is stretched out over time.
   *
   * @see #shouldDoWorkThisTick(int, int) If you need to offset work ticks
   */
  protected boolean shouldDoWorkThisTick(int interval) {
    return shouldDoWorkThisTick(interval, 0);
  }

  /**
   * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This is staggered so the work of different TEs
   * is stretched out over time.
   *
   * If you have different work items in your TE, use this variant to stagger your work.
   */
  protected boolean shouldDoWorkThisTick(int interval, int offset) {
    return (level.getGameTime() + checkOffset + offset) % interval == 0;
  }

  /**
   * Called server-side when a GhostSlot is changed. Check that the given slot number really is a ghost slot before storing the given stack.
   *
   * @param slot
   *          The slot number that was given to the ghost slot
   * @param stack
   *          The stack that should be placed, null to clear
   * @param realsize
   */
  public void setGhostSlotContents(int slot, @Nonnull ItemStack stack, int realsize) {
  }

  @Override
  public void setChanged() {
    if (hasLevel() && level.isLoaded(getBlockPos())) {
      // we need the loaded check to make sure we don't trigger a chunk load while the chunk is loaded
      this.setChanged();
    }
  }

  /**
   * Sends an update packet to all players who have this TileEntity loaded. Needed because inventory changes are not synced in a timely manner unless the player
   * has the GUI open. And sometimes the rendering needs the inventory...
   */
  public void forceUpdatePlayers() {

    // TODO: not clear how to do this in 1.19
//    if (!(level instanceof ServerLevel)) {
//      return;
//    }
//
//    ServerLevel worldServer = (ServerLevel) level;
//    ChunkMap playerManager = worldServer.getChunkSource().chunkMap;
//    ClientboundBlockEntityDataPacket updatePacket = null;
//
//    int chunkX = worldPosition.getX() >> 4;
//    int chunkZ = worldPosition.getZ() >> 4;
//
//    for (Player playerObj : level.players()) {
//      if (playerObj instanceof ServerPlayer) {
//        ServerPlayer player = (ServerPlayer) playerObj;
//
//        player.trackChunk();
//        if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ)) {
//          if (updatePacket == null) {
//            updatePacket = getUpdatePacket();
//            if (updatePacket == null) {
//              return;
//            }
//          }
//          try {
//            player.connection.sendPacket(updatePacket);
//          } catch (Exception e) {
//          }
//        }
//      }
//    }
  }

//  @Override
//  protected void setWorldCreate(@Nonnull Level worldIn) {
//    // Forge gives us our World earlier than vanilla. No idea why it doesn't get put into #world but is ignored by default.
//    // Anyway, this is helpful while reading our nbt, so let's use it.
//    setLevel(worldIn);
//  }

}
