package com.enderio.core.common.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.Log;
import com.enderio.core.common.util.NullHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class BlockEntityCompoundTagMessage<T extends BlockEntity> extends BlockEntityMessage<T> {

  BlockEntity te;

  long pos;
  CompoundTag tags;

  boolean renderOnUpdate = false;

  public BlockEntityCompoundTagMessage() {

  }

  public BlockEntityCompoundTagMessage(BlockEntity te) {
    this.te = te;
    pos = te.getBlockPos().asLong();
    tags = te.serializeNBT();
  }

  @Override
  public void write(FriendlyByteBuf buffer) {
    super.write(buffer);
    NetworkUtil.writeCompoundTag(tags, buffer);
  }

  @Override
  public void read(FriendlyByteBuf buffer) {
    super.read(buffer);
    tags = NetworkUtil.readCompoundTag(buffer);
  }

  public @Nonnull BlockPos getPos() {
    return BlockPos.of(pos);
  }

// TODO
//  @Override
//  public IMessage onMessage(BlockEntityCompoundTagMessage msg, MessageContext ctx) {
//    te = handle(ctx.getServerHandler().player.world);
//    if (te != null && renderOnUpdate) {
//      BlockState bs = te.getLevel().getBlockState(msg.getPos());
//      te.getLevel().markAndNotifyBlock(msg.getPos(), te.getLevel().getChunkAt(te.getBlockPos()), bs, bs, 3);
//    }
//    return null;
//  }

  private @Nullable BlockEntity handle(Level world) {
    if (world == null) {
      Log.warn("PacketUtil.handleTileEntityPacket: TileEntity null world processing tile entity packet.");
      return null;
    }
    BlockEntity tileEntity = world.getBlockEntity(getPos());
    if (tileEntity == null) {
      Log.warn("PacketUtil.handleTileEntityPacket: TileEntity null when processing tile entity packet.");
      return null;
    }
    tileEntity.deserializeNBT(NullHelper.notnull(tags, "NetworkUtil.readNBTTagCompound()"));
    return tileEntity;
  }
}
