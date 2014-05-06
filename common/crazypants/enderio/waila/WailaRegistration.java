package crazypants.enderio.waila;

import java.util.List;
import java.util.logging.Level;

import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.crusher.BlockCrusher;
import crazypants.enderio.machine.generator.BlockStirlingGenerator;
import crazypants.enderio.machine.hypercube.BlockHyperCube;
import crazypants.enderio.machine.light.BlockElectricLight;
import crazypants.enderio.machine.monitor.BlockPowerMonitor;
import crazypants.enderio.machine.painter.BlockPainter;
import crazypants.enderio.machine.power.BlockCapacitorBank;
import crazypants.enderio.machine.reservoir.BlockReservoir;
import crazypants.enderio.machine.solar.BlockSolarPanel;
import crazypants.enderio.material.BlockFusedQuartz;
import crazypants.enderio.teleport.BlockTravelAnchor;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class WailaRegistration {
	
	public static void register(IWailaRegistrar registrar) {
		registrar.addConfig("Ender IO (Official)", "enderio.official.enabled", "Enabled");
		registrar.addConfig("Ender IO (Official)", "enderio.official.facades.show", "Intelligent Facades");
		registrar.addConfig("Ender IO (Official)", "enderio.official.redstone", "Redstone States");
		registrar.addConfig("Ender IO (Official)", "enderio.official.hypercube.moreInfo", "Transceiver: More Info");
		registrar.addConfig("Ender IO (Official)", "enderio.official.capacitor.moreInfo", "Capacitor: More Info");
		registrar.addConfig("Ender IO (Official)", "enderio.official.showModules", "Block categories");
		registrar.addConfig("Ender IO (Official)", "enderio.official.crouchForMore", "Crouch for more info");
		registrar.addConfig("Ender IO (Official)", "enderio.official.alwaysShowMore", "Always show more info");

		registerProvider(registrar, WailaAlloySmelter.class, BlockAlloySmelter.class);
		registerProvider(registrar, WailaCapacitorBank.class, BlockCapacitorBank.class);
		registerProvider(registrar, WailaConduitBundle.class, BlockConduitBundle.class);
		registerProvider(registrar, WailaCrusher.class, BlockCrusher.class);
		registerProvider(registrar, WailaElectricLight.class, BlockElectricLight.class);
		registerProvider(registrar, WailaEnderIO.class, BlockEnderIO.class);
		registerProvider(registrar, WailaFusedQuartz.class, BlockFusedQuartz.class);
		registerProvider(registrar, WailaHyperCube.class, BlockHyperCube.class);
		registerProvider(registrar, WailaPainter.class, BlockPainter.class);
		registerProvider(registrar, WailaPowerMonitor.class, BlockPowerMonitor.class);
		registerProvider(registrar, WailaReservoir.class, BlockReservoir.class);
		registerProvider(registrar, WailaSolarPanel.class, BlockSolarPanel.class);
		registerProvider(registrar, WailaStirlingGenerator.class, BlockStirlingGenerator.class);
		registerProvider(registrar, WailaTravelAnchor.class, BlockTravelAnchor.class);
		
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerCapacitor(), TileCapacitorBank);
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
		//ModuleRegistrar.instance().registerSyncedNBTKey("*", TileCapacitorBank);
		//ModuleRegistrar.instance().registerSyncedNBTKey("*", TileTesseract);		
	}

	private static void registerProvider(IWailaRegistrar registrar, Class provider, Class block)
	{
		IWailaDataProvider instance;
		try {
			instance = (IWailaDataProvider)provider.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		registrar.registerBodyProvider(instance, block);
		registrar.registerHeadProvider(instance, block);
		registrar.registerTailProvider(instance, block);
	}
}
