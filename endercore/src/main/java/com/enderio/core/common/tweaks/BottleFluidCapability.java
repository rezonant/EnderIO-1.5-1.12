//package com.enderio.core.common.tweaks;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import com.enderio.core.EnderCore;
//import com.enderio.core.common.config.AbstractConfigHandler.RestartReqs;
//import com.enderio.core.common.config.ConfigHandler;
//
//import net.minecraft.core.Direction;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.alchemy.PotionUtils;
//import net.minecraft.world.item.alchemy.Potions;
//import net.minecraft.world.level.material.Fluids;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//import net.minecraftforge.fluids.capability.IFluidHandlerItem;
//import net.minecraftforge.fluids.capability.IFluidTankProperties;
//
//public class BottleFluidCapability implements IFluidHandlerItem, ICapabilityProvider {
//  final static int BOTTLE_SIZE = 250; // BOTTLE_SIZE;
//
//  private static final ResourceLocation KEY = new ResourceLocation(EnderCore.DOMAIN, "bottle");
//
//  private @Nonnull ItemStack container;
//
//  private BottleFluidCapability(@Nonnull ItemStack container) {
//    this.container = container;
//  }
//
//  private boolean isFull() {
//    return container.getItem() == Items.POTION && PotionUtils.getPotion(container) == Potions.WATER;
//  }
//
//  private void fill() {
//    container = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
//  }
//
//  private boolean isEmpty() {
//    return container.getItem() == Items.GLASS_BOTTLE;
//  }
//
//  private void empty() {
//    container = new ItemStack(Items.GLASS_BOTTLE);
//  }
//
//  @Override
//  public int fill(FluidStack resource, boolean doFill) {
//    if (container.getCount() != 1 || !isEmpty() || resource == null || resource.getFluid() != Fluids.WATER || resource.getAmount() < BOTTLE_SIZE) {
//      return 0;
//    } else {
//      if (doFill) {
//        fill();
//      }
//      return BOTTLE_SIZE;
//    }
//  }
//
//  @Override
//  @Nullable
//  public FluidStack drain(FluidStack resource, boolean doDrain) {
//    if (container.getCount() != 1 || !isFull() || resource == null || resource.getFluid() != Fluids.WATER || resource.getAmount() < BOTTLE_SIZE) {
//      return null;
//    } else {
//      if (doDrain) {
//        empty();
//      }
//      return new FluidStack(Fluids.WATER, BOTTLE_SIZE);
//    }
//  }
//
//  @Override
//  @Nullable
//  public FluidStack drain(int maxDrain, boolean doDrain) {
//    if (container.getCount() != 1 || !isFull() || maxDrain < BOTTLE_SIZE) {
//      return null;
//    } else {
//      if (doDrain) {
//        empty();
//      }
//      return new FluidStack(Fluids.WATER, BOTTLE_SIZE);
//    }
//  }
//
//  @Override
//  @Nonnull
//  public ItemStack getContainer() {
//    return container;
//  }
//
//  @Override
//  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
//    return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && (isEmpty() || isFull());
//  }
//
//  @Override
//  @Nullable
//  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
//    return hasCapability(capability, facing) ? CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this) : null;
//  }
//
//  public static class BottleTweak extends Tweak {
//
//    public BottleTweak() {
//      super("bottleFluidHandler", "If this tweak is enabled, vanilla bottles will act as fluid handlers for all automation.", RestartReqs.REQUIRES_WORLD_RESTART);
//    }
//
//    @Override
//    protected void load() {
//      MinecraftForge.EVENT_BUS.register(this);
//    }
//
//    @Override
//    protected void unload() {
//      MinecraftForge.EVENT_BUS.unregister(this);
//    }
//
//    @SubscribeEvent
//    public void attachCapabilities(@Nonnull AttachCapabilitiesEvent<ItemStack> evt) {
//      if (evt.getCapabilities().containsKey(KEY)) {
//        return;
//      }
//      final ItemStack stack = evt.getObject();
//      if (stack == null) {
//        return;
//      }
//      if (stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.POTION) {
//        BottleFluidCapability cap = new BottleFluidCapability(stack);
//        evt.addCapability(KEY, cap);
//      }
//    }
//  }
//}
