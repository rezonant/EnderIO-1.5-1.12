//package com.enderio.core.client.sound;
//
//import javax.annotation.Nonnull;
//
//import net.minecraft.client.audio.ITickableSound;
//import net.minecraft.client.audio.PositionedSound;
//import net.minecraft.client.resources.sounds.Sound;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.sounds.SoundEvent;
//import net.minecraft.util.SoundCategory;
//
//public class BlockSound extends SoundEvent {
//
//  private boolean donePlaying = false;
//
//  public BlockSound(@Nonnull ResourceLocation p_i45103_1_) {
//    super(p_i45103_1_, SoundCategory.BLOCKS);
//  }
//
//  @Override
//  public boolean isDonePlaying() {
//    return this.donePlaying;
//  }
//
//  public BlockSound setDonePlaying(boolean donePlaying) {
//    this.donePlaying = donePlaying;
//    return this;
//  }
//
//  public BlockSound setVolume(float vol) {
//    this.volume = vol;
//    return this;
//  }
//
//  public BlockSound setPitch(float pitch) {
//    this.pitch = pitch;
//    return this;
//  }
//
//  public BlockSound setLocation(float x, float y, float z) {
//    this.xPosF = x;
//    this.yPosF = y;
//    this.zPosF = z;
//    return this;
//  }
//
//  public BlockSound setDoRepeat(boolean bool) {
//    this.repeat = bool;
//    return this;
//  }
//
//  @Override
//  public void update() {
//    ;
//  }
//}
