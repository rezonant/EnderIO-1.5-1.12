//package com.enderio.core.client.handlers;
//
//import java.lang.reflect.Field;
//
//import javax.annotation.Nonnull;
//
//import com.enderio.core.EnderCore;
//import com.enderio.core.common.fluid.BlockFluidEnder;
//import com.enderio.core.common.util.NullHelper;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.entity.EntityRenderer;
//import net.minecraft.core.BlockPos;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffects;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
//
//@Handler(side = HandlerSide.CLIENT)
//@OnlyIn(Dist.CLIENT)
//public class FluidVisualsHandler {
//
//  private static final Field FfogColor1 = ObfuscationReflectionHelper.findField(EntityRenderer.class, "fogColor1", "field_78539_ae");
//  private static final Field FfogColor2 = ObfuscationReflectionHelper.findField(EntityRenderer.class, "fogColor2", "field_78535_ad");
//  private static final Field FbossColorModifier = ObfuscationReflectionHelper.findField(EntityRenderer.class, "bossColorModifier", "field_82831_U");
//  private static final Field FbossColorModifierPrev = ObfuscationReflectionHelper.findField(EntityRenderer.class, "bossColorModifierPrev", "field_82832_V");
//  private static final Field FcloudFog = ObfuscationReflectionHelper.findField(EntityRenderer.class, "cloudFog", "field_78500_U");
//
//  @SubscribeEvent
//  public static void onFOVModifier(@Nonnull EntityViewRenderEvent.FOVModifier event) {
//    if (event.getState() instanceof BlockFluidEnder) {
//      event.setFOV(event.getFOV() * 60.0F / 70.0F);
//    }
//  }
//
//  private static final @Nonnull ResourceLocation RES_UNDERFLUID_OVERLAY = new ResourceLocation(EnderCore.DOMAIN, "textures/misc/underfluid.png");
//
//  @SubscribeEvent
//  public static void onRenderBlockOverlay(RenderBlockScreenEffectEvent event) {
//    if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.WATER) {
//      final Player player = event.getPlayer();
//      // the event has the wrong BlockPos (entity center instead of eyes)
//      final BlockPos blockpos = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
//      final Block block = player.level.getBlockState(blockpos).getBlock();
//
//      if (block instanceof BlockFluidEnder) {
//        float fogColorRed = ((BlockFluidEnder) block).getFogColorRed();
//        float fogColorGreen = ((BlockFluidEnder) block).getFogColorGreen();
//        float fogColorBlue = ((BlockFluidEnder) block).getFogColorBlue();
//
//        Minecraft.getInstance().getTextureManager().bindForSetup(RES_UNDERFLUID_OVERLAY);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder vertexbuffer = tessellator.getBuffer();
//        float f = player.getBrightness();
//        GlStateManager.color(f * fogColorRed, f * fogColorGreen, f * fogColorBlue, 0.5F);
//        GlStateManager.enableBlend();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
//            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        GlStateManager.pushMatrix();
//        float f7 = -player.rotationYaw / 64.0F;
//        float f8 = player.rotationPitch / 64.0F;
//        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
//        vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
//        vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
//        vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
//        vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
//        tessellator.draw();
//        GlStateManager.popMatrix();
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.disableBlend();
//
//        event.setCanceled(true);
//      }
//    }
//  }
//
//  @SubscribeEvent
//  public static void onFogDensity(@Nonnull EntityViewRenderEvent.FogDensity event) throws IllegalArgumentException, IllegalAccessException {
//    if (event.getState().getBlock() instanceof BlockFluidEnder) {
//      final EntityRenderer renderer = event.getRenderer();
//      final Entity entity = event.getEntity();
//      final boolean cloudFog = FcloudFog.getBoolean(renderer);
//
//      // again the event is fired at a bad location...
//      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
//        return;
//      } else if (cloudFog) {
//        return;
//      }
//
//      GlStateManager.setFog(GlStateManager.FogMode.EXP);
//
//      if (entity instanceof EntityLivingBase) {
//        if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
//          event.setDensity(0.01F);
//        } else {
//          event.setDensity(0.1F - EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) * 0.03F);
//        }
//      } else {
//        event.setDensity(0.1F);
//      }
//    }
//  }
//
//  @SubscribeEvent
//  public static void onFogColor(EntityViewRenderEvent.FogColors event) throws IllegalArgumentException, IllegalAccessException {
//    if (event.getState().getBlock() instanceof BlockFluidEnder) {
//
//      float fogColorRed = ((BlockFluidEnder) event.getState().getBlock()).getFogColorRed();
//      float fogColorGreen = ((BlockFluidEnder) event.getState().getBlock()).getFogColorGreen();
//      float fogColorBlue = ((BlockFluidEnder) event.getState().getBlock()).getFogColorBlue();
//
//      // the following was copied as-is from net.minecraft.client.renderer.EntityRenderer.updateFogColor() because that %&!$ Forge event is fired after the
//      // complete fog color calculation is done
//
//      final EntityRenderer renderer = event.getRenderer();
//      final float fogColor1 = FfogColor1.getFloat(renderer);
//      final float fogColor2 = FfogColor2.getFloat(renderer);
//      final float partialTicks = (float) event.getRenderPartialTicks();
//      final Entity entity = event.getEntity();
//      final Level world = entity.getLevel();
//      final float bossColorModifier = FbossColorModifier.getFloat(renderer);
//      final float bossColorModifierPrev = FbossColorModifierPrev.getFloat(renderer);
//
//      float f12 = 0.0F;
//
//      if (entity instanceof EntityLivingBase) {
//        f12 = EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) * 0.2F;
//
//        if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
//          f12 = f12 * 0.3F + 0.6F;
//        }
//      }
//
//      fogColorRed += f12;
//      fogColorGreen += f12;
//      fogColorBlue += f12;
//
//      float f13 = fogColor2 + (fogColor1 - fogColor2) * partialTicks;
//      fogColorRed *= f13;
//      fogColorGreen *= f13;
//      fogColorBlue *= f13;
//      double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) * world.provider.getVoidFogYFactor();
//
//      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
//        int i = NullHelper.notnull(((EntityLivingBase) entity).getActivePotionEffect(MobEffects.BLINDNESS), "LOC9903481").getDuration();
//
//        if (i < 20) {
//          d1 *= 1.0F - i / 20.0F;
//        } else {
//          d1 = 0.0D;
//        }
//      }
//
//      if (d1 < 1.0D) {
//        if (d1 < 0.0D) {
//          d1 = 0.0D;
//        }
//
//        d1 = d1 * d1;
//        fogColorRed = (float) (fogColorRed * d1);
//        fogColorGreen = (float) (fogColorGreen * d1);
//        fogColorBlue = (float) (fogColorBlue * d1);
//      }
//
//      if (bossColorModifier > 0.0F) {
//        float f14 = bossColorModifierPrev + (bossColorModifier - bossColorModifierPrev) * partialTicks;
//        fogColorRed = fogColorRed * (1.0F - f14) + fogColorRed * 0.7F * f14;
//        fogColorGreen = fogColorGreen * (1.0F - f14) + fogColorGreen * 0.6F * f14;
//        fogColorBlue = fogColorBlue * (1.0F - f14) + fogColorBlue * 0.6F * f14;
//      }
//
//      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.NIGHT_VISION)) {
//        float f15 = getNightVisionBrightness((EntityLivingBase) entity, partialTicks);
//        float f6 = 1.0F / fogColorRed;
//
//        if (f6 > 1.0F / fogColorGreen) {
//          f6 = 1.0F / fogColorGreen;
//        }
//
//        if (f6 > 1.0F / fogColorBlue) {
//          f6 = 1.0F / fogColorBlue;
//        }
//
//        fogColorRed = fogColorRed * (1.0F - f15) + fogColorRed * f6 * f15;
//        fogColorGreen = fogColorGreen * (1.0F - f15) + fogColorGreen * f6 * f15;
//        fogColorBlue = fogColorBlue * (1.0F - f15) + fogColorBlue * f6 * f15;
//      }
//
//      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
//        float f16 = (fogColorRed * 30.0F + fogColorGreen * 59.0F + fogColorBlue * 11.0F) / 100.0F;
//        float f17 = (fogColorRed * 30.0F + fogColorGreen * 70.0F) / 100.0F;
//        float f7 = (fogColorRed * 30.0F + fogColorBlue * 70.0F) / 100.0F;
//        fogColorRed = f16;
//        fogColorGreen = f17;
//        fogColorBlue = f7;
//      }
//
//      event.setRed(fogColorRed);
//      event.setGreen(fogColorGreen);
//      event.setBlue(fogColorBlue);
//    }
//  }
//
//  private FluidVisualsHandler() {
//  }
//
//}
