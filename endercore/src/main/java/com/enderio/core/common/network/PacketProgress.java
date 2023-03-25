package com.enderio.core.common.network;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketProgress extends BlockEntityMessage<BlockEntity> {

  float progress;

  public PacketProgress() {
  }

  public PacketProgress(@Nonnull IProgressTile tile) {
    super(tile.getTileEntity());
    progress = tile.getProgress();
  }

  public PacketProgress(FriendlyByteBuf buffer) {
    super(buffer);
    progress = buffer.readFloat();
  }

  @Override
  public void write(FriendlyByteBuf buf) {
    buf.writeFloat(progress);
  }

  @Override
  public void onReceived(@Nonnull BlockEntity entity, @Nonnull Supplier<NetworkEvent.Context> context) {
    if (entity instanceof IProgressTile) {
      ((IProgressTile) entity).setProgress(progress);
    }
  }
}
