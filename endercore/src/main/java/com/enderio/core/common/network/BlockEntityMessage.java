package com.enderio.core.common.network;

import java.lang.reflect.TypeVariable;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.enderio.core.EnderCore;
import com.google.common.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class BlockEntityMessage<T extends BlockEntity> {

  private BlockPos pos;

  protected BlockEntityMessage() {
  }

  protected BlockEntityMessage(@Nonnull T tile) {
    pos = tile.getBlockPos();
  }

  public BlockEntityMessage(FriendlyByteBuf buffer) {
    this.read(buffer);
  }

  public void read(FriendlyByteBuf buf) {
    pos = BlockPos.of(buf.readLong());
  }

  public void write(FriendlyByteBuf buf) {
    buf.writeLong(pos.asLong());
    write(buf);
  }

  public @Nonnull BlockPos getPos() {
    return this.pos;
  }

  @SuppressWarnings("unchecked")
  protected T getBlockEntity(Level worldObj) {
    // Sanity check, and prevent malicious packets from loading chunks
    if (worldObj == null || !worldObj.isLoaded(getPos())) {
      return null;
    }
    BlockEntity te = worldObj.getBlockEntity(getPos());
    if (te == null) {
      return null;
    }
    @SuppressWarnings("rawtypes")
    final Class<? extends BlockEntityMessage> ourClass = getClass();
    @SuppressWarnings("rawtypes")
    final TypeVariable<Class<BlockEntityMessage>>[] typeParameters = BlockEntityMessage.class.getTypeParameters();
    if (typeParameters.length > 0) {
      @SuppressWarnings("rawtypes")
      final TypeVariable<Class<BlockEntityMessage>> typeParam0 = typeParameters[0];
      if (typeParam0 != null) {
        TypeToken<?> teType = TypeToken.of(ourClass).resolveType(typeParam0);
        final Class<? extends BlockEntity> teClass = te.getClass();
        if (teType.isSupertypeOf(teClass)) {
          return (T) te;
        }
      }
    }
    return null;
  }

  public void onReceived(@Nonnull T te, @Nonnull Supplier<NetworkEvent.Context> context) {
  }

  protected @Nonnull Level getLevel(Supplier<NetworkEvent.Context> context) {
    if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
      return context.get().getSender().level;
    } else {
      var clientLevel = EnderCore.proxy.getClientWorld();
      if (clientLevel == null) {
        throw new NullPointerException("Recieved network packet outside any world!");
      }
      return clientLevel;
    }
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      if (context.get() != null) {
        T te = getBlockEntity(getLevel(context));
        if (te != null) {
          onReceived(te, context);
        }
      }
    });
    return true;
  }
}
