package com.enderio.core.client.handlers;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class ClientHandler {

  private static int ticksElapsed;

  public static int getTicksElapsed() {
    return ticksElapsed;
  }

  @SubscribeEvent
  public static void onClientTick(@Nonnull TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      ticksElapsed++;
    }
  }

  private ClientHandler() {
  }

}
