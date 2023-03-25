package com.enderio.core.client.handlers;

import java.util.Map;

import com.enderio.core.EnderCore;
import com.enderio.core.api.common.enchant.IAdvancedEnchant;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EnchantTooltipHandler {

  @SubscribeEvent
  public static void handleTooltip(ItemTooltipEvent event) {
    // TODO: config
//    if (!ConfigHandler.showEnchantmentTooltips) {
//      return;
//    }

    if (event.getItemStack().hasTag()) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());

      for (Enchantment enchant : enchantments.keySet()) {
        if (enchant instanceof IAdvancedEnchant) {
          for (int i = 0; i < event.getToolTip().size(); i++) {
            if (event.getToolTip().get(i).contains(Component.literal(enchant.getFullname(enchantments.get(enchant)).getString()))) {
              for (String s : ((IAdvancedEnchant) enchant).getTooltipDetails(event.getItemStack())) {
                event.getToolTip().add(i + 1, Component.literal(ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC + "  - " + s));
                i++;
              }
            }
          }
        }
      }
    }
  }

  private EnchantTooltipHandler() {
  }

}
