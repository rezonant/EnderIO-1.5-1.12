package com.enderio.core.api.common.enchant;

import javax.annotation.Nonnull;
import com.enderio.core.EnderCore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Allows your enchants to have some flavor or description text underneath them
 */
public interface IAdvancedEnchant {
  /**
   * Get the detail for this itemstack
   *
   * @param stack
   * @return a list of <code>String</code>s to be bulleted under the enchantment
   */
  default @Nonnull String[] getTooltipDetails(@Nonnull ItemStack stack) {
    final String unloc = "description." + ((Enchantment) this).getFullname(stack.getEnchantmentLevel((Enchantment)this));
    final String loc = EnderCore.lang.localizeExact(unloc);
    return unloc.equals(loc) ? new String[0] : EnderCore.lang.splitList(loc);
  }

}
