package com.enderio.core.common.handlers;

import com.enderio.core.common.fluid.BlockFluidEnder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FluidSpawnHandler {

  @SubscribeEvent
  public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn evt) {
    if (evt.getResult() != Event.Result.DENY
        && SpawnPlacements.getPlacementType(evt.getEntity().getType()) == SpawnPlacements.Type.IN_WATER
        && evt.getLevel().getBlockState(new BlockPos(evt.getEntity().getPosition(0))).getBlock() instanceof BlockFluidEnder) {
      evt.setResult(Event.Result.DENY);
    }
    return;
  }

  private FluidSpawnHandler() {
  }

}
