package com.enderio.core.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnderPacketHandler {
  private static SimpleChannel INSTANCE;
  private static int ID = 0;

  public static void init() {
    registerClientMessage(PacketProgress.class, PacketProgress::write, PacketProgress::new, PacketProgress::handle);
    registerServerMessage(PacketGhostSlot.class, PacketGhostSlot::write, PacketGhostSlot::new, PacketGhostSlot::handle);
  }

  protected static <T> void registerClientMessage(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                                  BiConsumer<T, Supplier<NetworkEvent.Context>> consumer) {
    INSTANCE.registerMessage(ID++, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }

  protected static <T> void registerServerMessage(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                                  BiConsumer<T, Supplier<NetworkEvent.Context>> consumer) {
    INSTANCE.registerMessage(ID++, type, encoder, decoder, consumer, Optional.of(NetworkDirection.PLAY_TO_SERVER));
  }

  public static <MSG> void sendToAllTracking(MSG message, BlockEntity te) {
    sendToAllTracking(message, te.getLevel(), te.getBlockPos());
  }

  public static <MSG> void sendToAllTracking(MSG packet, Level world, BlockPos pos) {
    // If we have a ServerWorld just directly figure out the ChunkPos so as to not require looking up the chunk
    // This provides a decent performance boost over using the packet distributor
    // -- Mekanism [https://github.com/mekanism/Mekanism/blob/0287e5fd48a02dd8fe0b7a474c766d6c3a8d3f01/src/main/java/mekanism/common/network/BasePacketHandler.java#L150]
    // TODO: Possibly not true by now

    if (world instanceof ServerLevel) {
      var vanillaPacket = INSTANCE.toVanillaPacket(packet, NetworkDirection.PLAY_TO_CLIENT);
      ((ServerLevel) world).getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)
              .forEach(e -> e.connection.send(vanillaPacket));
    } else{
      INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(pos.getX() >> 4, pos.getZ() >> 4)), packet);
    }
  }

  public static <T> void sendTo(T packet, ServerPlayer player) {
    INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
  }

  public static <T> void sendToServer(T packet) {
    INSTANCE.sendToServer(packet);
  }
}