package com.enderio.core.client.render;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.EnderCore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IconUtil {
  public static @Nonnull IconUtil instance = new IconUtil();

  public @Nonnull TextureAtlasSprite whiteTexture;
  public @Nonnull TextureAtlasSprite blankTexture;
  public @Nonnull TextureAtlasSprite errorTexture;

  private boolean doneInit = false;

  @SuppressWarnings("null")
  private IconUtil() {
  }

  public void init() {
    if (doneInit) {
      return;
    }
    doneInit = true;
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {
    event.addSprite(new ResourceLocation(EnderCore.MODID, "white"));
    event.addSprite(new ResourceLocation(EnderCore.MODID, "error"));
    event.addSprite(new ResourceLocation(EnderCore.MODID, "blank"));
  }

  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Post event) {
    var atlas = event.getAtlas();
    whiteTexture = atlas.getSprite(new ResourceLocation(EnderCore.MODID, "white"));
    errorTexture = atlas.getSprite(new ResourceLocation(EnderCore.MODID, "error"));
    blankTexture = atlas.getSprite(new ResourceLocation(EnderCore.MODID, "blank"));
  }
}
