package com.enderio.core.common.handlers;

import com.enderio.core.EnderCore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class JoinMessageHandler {

  @SubscribeEvent
  public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    boolean enabled = true; // ConfigHandler.invisibleMode == 1 && ConfigHandler.instance().showInvisibleWarning()
    if (enabled) {
      String unlocBase = "chat.invis";
      String warnBase = unlocBase + ".warn.";
      String reasonBase = unlocBase + ".reason.";
      String reason = String.format(EnderCore.lang.localize(reasonBase + (EnderCore.instance.invisibilityRequested() ? "1" : "2"),
          EnderCore.instance.getInvisibleRequsters()));
      String text1 = String.format(EnderCore.lang.localize(warnBase + "1"), ChatFormatting.RED, ChatFormatting.WHITE, reason);
      String text2 = String.format(EnderCore.lang.localize(warnBase + "2"), ChatFormatting.BOLD, ChatFormatting.WHITE);
      String text3 = EnderCore.lang.localize(warnBase + "3");
      String text4 = EnderCore.lang.localize(warnBase + "4");

      event.getEntity().sendSystemMessage(Component.literal(text1));
      event.getEntity().sendSystemMessage(Component.literal(text2));
      if(EnderCore.instance.invisibilityRequested()) {
        event.getEntity().sendSystemMessage(Component.literal(text3));
      }
      event.getEntity().sendSystemMessage(Component.literal(text4));
    }
  }
}
