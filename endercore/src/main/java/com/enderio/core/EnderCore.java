package com.enderio.core;

import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;
import com.enderio.core.client.ClientProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.enderio.core.common.CommonProxy;
import com.enderio.core.common.Lang;
import com.enderio.core.common.imc.IMCRegistry;
import com.enderio.core.common.network.EnderPacketHandler;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.Mod;

@Mod(EnderCore.MODID)
@Mod.EventBusSubscriber(modid = EnderCore.MODID)
public class EnderCore implements IEnderMod {

  private static final Logger LOGGER = LogManager.getLogger();

  public static final @Nonnull String MODID = "endercore";
  public static final @Nonnull String DOMAIN = MODID.toLowerCase(Locale.US);
  public static final @Nonnull String NAME = "EnderCore";
  public static final @Nonnull String BASE_PACKAGE = "com.enderio";
  public static final @Nonnull String VERSION = "@VERSION@";

  public static final @Nonnull Logger logger = NullHelper.notnull(LogManager.getLogger(NAME), "failed to aquire logger");
  public static final @Nonnull Lang lang = new Lang(MODID);

  public static EnderCore instance;

  public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

  private final @Nonnull Set<String> invisibleRequesters = Sets.newHashSet();

  public EnderCore() {
    instance = this;
    LOGGER.info("EnderCore is starting. This mod will never die!");
    //SimpleMixinLoader.loadMixinSources(this);
  }

  /**
   * Call this method BEFORE preinit (construction phase) to request that EnderCore start in invisible mode. This will disable ANY gameplay features unless the
   * user forcibly disables invisible mode in the config.
   */
  public void requestInvisibleMode() {
    final ModContainer activeModContainer = ModList.get().getModContainerById(modid()).get();
    if (activeModContainer != null) {
      invisibleRequesters.add(activeModContainer.getModId());
    } else {
      invisibleRequesters.add("null");
    }
  }

  public boolean invisibilityRequested() {
    return !invisibleRequesters.isEmpty();
  }

  public @Nonnull Set<String> getInvisibleRequsters() {
    return ImmutableSet.copyOf(invisibleRequesters);
  }

  @SubscribeEvent
  public void onInterModEnqueue(@Nonnull InterModEnqueueEvent event) {
    // During the InterModEnqueueEvent, use InterModComms#sendTo to send messages to different mods.
    proxy.onInterModEnqueue(event);
  }

  @SubscribeEvent
  public void onCommonSetup(@Nonnull FMLCommonSetupEvent event) {
    // FMLCommonSetupEvent is for actions that are common to both physical client and server, such as registering capabilities.

    Things.init(event);
    EnderPacketHandler.init();

    IMCRegistry.INSTANCE.init();
  }

  @SubscribeEvent
  public void clientSetup(FMLClientSetupEvent event) {
    //ClientCommandHandler.instance.registerCommand(CommandReloadConfigs.CLIENT);
  }

  public void dedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
    //((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager())
    //      .registerCommand(CommandReloadConfigs.SERVER);
  }

  @SubscribeEvent
  public void interModProcess(@Nonnull InterModProcessEvent event) {
    //Tweaks.loadLateTweaks();
  }

  @SubscribeEvent
  public void loadComplete(@Nonnull FMLLoadCompleteEvent event) {
    Things.init(event);
    //this.patchChunkExecutor();
  }

  private void patchChunkExecutor() {
//    ThreadPoolExecutor fixedChunkExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
//        new ThreadFactory() {
//          private AtomicInteger count = new AtomicInteger(1);
//
//          @Override
//          public Thread newThread(Runnable r) {
//            Thread thread = new Thread(r, "Chunk I/O Executor Thread-" + count.getAndIncrement());
//            thread.setDaemon(true);
//            return thread;
//          }
//        }) {
//
//      @Override
//      @SuppressWarnings({ "unchecked", "rawtypes" })
//      protected void afterExecute(Runnable r, Throwable t) {
//        if (t != null) {
//          try {
//            LOGGER.error("Unhandled exception loading chunk:", t);
//            Object queuedChunk = ObfuscationReflectionHelper.getPrivateValue((Class) r.getClass(), (Object) r, "chunkInfo");
//            Class cls = queuedChunk.getClass();
//            LOGGER.error(queuedChunk);
//            int x = (Integer) ObfuscationReflectionHelper.getPrivateValue(cls, queuedChunk, "x");
//            int z = (Integer) ObfuscationReflectionHelper.getPrivateValue(cls, queuedChunk, "z");
//            //LOGGER.error(CrashReportCategory.getCoordinateInfo(x << 4, 64, z << 4));
//          } catch (Throwable t2) {
//            LOGGER.error(t2);
//          }
//        }
//      }
//    };
//    try {
//      EnumHelper.setFailsafeFieldValue(ObfuscationReflectionHelper.findField(ChunkIOExecutor.class, "pool"), null, fixedChunkExecutor);
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
  }

  @Override
  public @Nonnull String modid() {
    return MODID;
  }

  @Override
  public @Nonnull String name() {
    return NAME;
  }

  @Override
  public @Nonnull String version() {
    return VERSION;
  }
}
