package com.enderio.core.client.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.enderio.core.EnderCore;
import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpecialTooltipHandler {

  public interface ITooltipCallback extends IAdvancedTooltipProvider {
    boolean shouldHandleItem(@Nonnull ItemStack item);
  }

  private static final @Nonnull List<ITooltipCallback> callbacks = new ArrayList<>();

  public static void addCallback(@Nonnull ITooltipCallback callback) {
    callbacks.add(callback);
  }

  @SubscribeEvent
  public static void addTooltip(ItemTooltipEvent evt) {
    if (evt.getItemStack().isEmpty()) {
      return;
    }

    final boolean flag = showAdvancedTooltips();
    boolean doDurability = showDurability(evt.getFlags().isAdvanced());

    if (doDurability) {
      addDurabilityTooltip(getTooltip(evt), evt.getItemStack());
    }

    if (evt.getItemStack().getItem() instanceof IAdvancedTooltipProvider) {
      IAdvancedTooltipProvider ttp = (IAdvancedTooltipProvider) evt.getItemStack().getItem();
      addInformation(ttp, evt.getItemStack(), evt.getEntity(), getTooltip(evt), flag);
    } else if (evt.getItemStack().getItem() instanceof IResourceTooltipProvider) {
      addInformation((IResourceTooltipProvider) evt.getItemStack().getItem(), evt, flag);
    } else {
      Block blk = Block.byItem(evt.getItemStack().getItem());
      if (blk instanceof IAdvancedTooltipProvider) {
        addInformation((IAdvancedTooltipProvider) blk, evt.getItemStack(), evt.getEntity(), getTooltip(evt), flag);
      } else if (blk instanceof IResourceTooltipProvider) {
        addInformation((IResourceTooltipProvider) blk, evt, flag);
      }
    }

    for (ITooltipCallback callback : callbacks) {
      if (callback.shouldHandleItem(evt.getItemStack())) {
        addInformation(callback, evt.getItemStack(), evt.getEntity(), getTooltip(evt), flag);
      }
    }
  }

  public static boolean showDurability(boolean shiftDown) {
    return true; // TODO
//    return showDurabilityTooltips == 3 ? Minecraft.getInstance().options.advancedItemTooltips
//        : showDurabilityTooltips == 2 ? shiftDown : showDurabilityTooltips == 1;
  }

  public static NNList<Component> getAllTooltips(ItemStack stack) {
    NNList<Component> list = new NNList<>();

    if (stack.getItem() instanceof IAdvancedTooltipProvider) {
      IAdvancedTooltipProvider tt = (IAdvancedTooltipProvider) stack.getItem();
      tt.addCommonEntries(stack, Minecraft.getInstance().player, list, false);
      tt.addBasicEntries(stack, Minecraft.getInstance().player, list, false);
      tt.addDetailedEntries(stack, Minecraft.getInstance().player, list, false);
    } else if (stack.getItem() instanceof IResourceTooltipProvider) {
      String name = ((IResourceTooltipProvider) stack.getItem()).getUnlocalizedNameForTooltip(stack);
      addCommonTooltipFromResources(list, name);
      addBasicTooltipFromResources(list, name);
      addDetailedTooltipFromResources(list, name);
    } else {
      Block blk = Block.byItem(stack.getItem());
      if (blk instanceof IAdvancedTooltipProvider) {
        IAdvancedTooltipProvider tt = (IAdvancedTooltipProvider) blk;
        tt.addCommonEntries(stack, Minecraft.getInstance().player, list, false);
        tt.addBasicEntries(stack, Minecraft.getInstance().player, list, false);
        tt.addDetailedEntries(stack, Minecraft.getInstance().player, list, false);
      } else if (blk instanceof IResourceTooltipProvider) {
        IResourceTooltipProvider tt = (IResourceTooltipProvider) blk;
        String name = tt.getUnlocalizedNameForTooltip(stack);
        addCommonTooltipFromResources(list, name);
        addBasicTooltipFromResources(list, name);
        addDetailedTooltipFromResources(list, name);
      }
    }

    for (ITooltipCallback callback : callbacks) {
      if (callback.shouldHandleItem(stack)) {
        callback.addCommonEntries(stack, Minecraft.getInstance().player, list, false);
        callback.addBasicEntries(stack, Minecraft.getInstance().player, list, false);
        callback.addDetailedEntries(stack, Minecraft.getInstance().player, list, false);
      }
    }

    return list;
  }

  public static void addDurabilityTooltip(@Nonnull List<Component> toolTip, @Nonnull ItemStack itemStack) {
    if (!itemStack.isDamageableItem()) {
      return;
    }
    Item item = itemStack.getItem();
    if (item instanceof DiggerItem || item instanceof ArmorItem || item instanceof SwordItem || item instanceof BowItem
        || item instanceof ShearsItem) {
      toolTip.add(Component.literal(ItemUtil.getDurabilityString(itemStack)));
    }
  }

  public static void addInformation(@Nonnull IResourceTooltipProvider item, @Nonnull ItemTooltipEvent evt, boolean flag) {
    addInformation(item, evt.getItemStack(), evt.getEntity(), getTooltip(evt), flag);
  }

  private static @Nonnull List<Component> getTooltip(@Nonnull ItemTooltipEvent event) {
    List<Component> toolTip = event.getToolTip();
    if (toolTip == null) {
      throw new NullPointerException("How should we add a tooltip into a null list???");
    }
    return toolTip;
  }

  public static void addInformation(@Nonnull IResourceTooltipProvider tt, @Nonnull ItemStack itemstack, @Nullable Player entityplayer,
                                    @Nonnull List<Component> list, boolean flag) {
    String name = tt.getUnlocalizedNameForTooltip(itemstack);
    if (flag) {
      addCommonTooltipFromResources(list, name);
      addDetailedTooltipFromResources(list, name);
    } else {
      addBasicTooltipFromResources(list, name);
      addCommonTooltipFromResources(list, name);
      if (hasDetailedTooltip(tt, itemstack)) {
        addShowDetailsTooltip(list);
      }
    }
  }

  public static void addInformation(@Nonnull IAdvancedTooltipProvider tt, @Nonnull ItemStack itemstack, @Nullable Player entityplayer,
      @Nonnull List<Component> list, boolean flag) {
    tt.addCommonEntries(itemstack, entityplayer, list, false);
    if (flag) {
      tt.addDetailedEntries(itemstack, entityplayer, list, false);
    } else {
      tt.addBasicEntries(itemstack, entityplayer, list, false);
      if (hasDetailedTooltip(tt, itemstack, entityplayer, false)) {
        addShowDetailsTooltip(list);
      }
    }
  }

  private static final @Nonnull List<String> throwaway = new ArrayList<String>();

  private static boolean hasDetailedTooltip(@Nonnull IResourceTooltipProvider tt, @Nonnull ItemStack stack) {
    throwaway.clear();
    var name = Component.literal(tt.getUnlocalizedNameForTooltip(stack));
    // TODO
    //addDetailedTooltipFromResources(throwaway, name);
    return !throwaway.isEmpty();
  }

  private static boolean hasDetailedTooltip(@Nonnull IAdvancedTooltipProvider tt, @Nonnull ItemStack stack, @Nullable Player player, boolean flag) {
    throwaway.clear();

    // TODO
    //tt.addDetailedEntries(stack, player, throwaway, flag);
    return !throwaway.isEmpty();
  }

  public static void addShowDetailsTooltip(@Nonnull List<Component> list) {
    list.add(Component.literal(ChatFormatting.WHITE + "" + ChatFormatting.ITALIC + EnderCore.lang.localize("tooltip.showDetails")));
  }

  public static void addDetailedTooltipFromResources(@Nonnull List<Component> list, @Nonnull String unlocalizedName) {
    addMultilineTooltip(list, unlocalizedName, ".tooltip.detailed");
  }

  public static void addBasicTooltipFromResources(@Nonnull List<Component> list, @Nonnull String unlocalizedName) {
    addMultilineTooltip(list, unlocalizedName, ".tooltip.basic");
  }

  public static void addCommonTooltipFromResources(@Nonnull List<Component> list, @Nonnull String unlocalizedName) {
    addMultilineTooltip(list, unlocalizedName, ".tooltip.common");
  }

  public static void addMultilineTooltip(@Nonnull List<Component> list, @Nonnull String... keyParts) {
    String key = String.join("", keyParts);
    if (EnderCore.lang.canLocalizeExact(key + ".line1")) {
      // compat
      addTooltipFromResources(list, key + ".line");
    } else if (EnderCore.lang.canLocalizeExact(key)) {
      // TODO
      //list.addAll(Arrays.asList(EnderCore.lang.localizeListExact(key)));
    }
  }

  @Deprecated
  public static void addTooltipFromResources(@Nonnull List<Component> list, @Nullable /* for String.concat() */ String keyBase) {
    boolean done = false;
    int line = 1;
    while (!done) {
      String key = keyBase + line;
      String val = EnderCore.lang.localizeExact(key);
      if (val.trim().length() < 0 || val.equals(key) || line > 12) {
        done = true;
      } else {
        // TODO
//        list.add(val);
//        line++;
      }
    }
  }

  private static @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemstack) {
    String unlocalizedNameForTooltip = null;
    if (itemstack.getItem() instanceof IResourceTooltipProvider) {
      unlocalizedNameForTooltip = ((IResourceTooltipProvider) itemstack.getItem()).getUnlocalizedNameForTooltip(itemstack);
    }
    if (unlocalizedNameForTooltip == null) {
      unlocalizedNameForTooltip = itemstack.getItem().getName(itemstack).getString();
    }
    return unlocalizedNameForTooltip;
  }

  public static void addCommonTooltipFromResources(@Nonnull List<Component> list, @Nonnull ItemStack itemstack) {
    if (itemstack.isEmpty()) {
      return;
    }
    addCommonTooltipFromResources(list, getUnlocalizedNameForTooltip(itemstack));
  }

  public static void addDetailedTooltipFromResources(@Nonnull List<Component> list, @Nonnull ItemStack itemstack) {
    if (itemstack.isEmpty()) {
      return;
    }
    addDetailedTooltipFromResources(list, getUnlocalizedNameForTooltip(itemstack));
  }

  private SpecialTooltipHandler() {
  }

  public static boolean showAdvancedTooltips() {
    return true; // TODO
    //return KeyboardHandler.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
  }

}
