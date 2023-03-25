package com.enderio.core.common.network;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGhostSlot {

  int windowId;
  int slot;
  @Nonnull
  ItemStack stack = ItemStack.EMPTY;
  int realsize;

  public PacketGhostSlot() {
  }

  public PacketGhostSlot(FriendlyByteBuf buffer) {
    windowId = buffer.readInt();
    slot = buffer.readShort();
    stack = buffer.readItem();
    realsize = buffer.readInt();
  }

  public static PacketGhostSlot setGhostSlotContents(int slot, @Nonnull ItemStack stack, int realsize) {
    PacketGhostSlot msg = new PacketGhostSlot();
    msg.slot = slot;
    msg.stack = stack;
    msg.realsize = realsize;
    msg.windowId = Minecraft.getInstance().player.containerMenu.containerId;
    return msg;
  }

  public void write(FriendlyByteBuf buf) {
    buf.writeInt(windowId);
    buf.writeShort(slot);
    buf.writeItemStack(stack, false); // TODO: limitedTag here?
    buf.writeInt(realsize);
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    var openContainer = context.get().getSender().containerMenu;
    if (openContainer instanceof GhostSlot.IGhostSlotAware && openContainer.containerId == windowId) {
      ((GhostSlot.IGhostSlotAware) openContainer).setGhostSlotContents(slot, stack, realsize);
    }
    return false;
  }
}
