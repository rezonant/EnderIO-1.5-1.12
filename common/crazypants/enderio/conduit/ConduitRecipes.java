package crazypants.enderio.conduit;

import static crazypants.enderio.ModObject.blockPainter;
import static crazypants.enderio.ModObject.itemConduitFacade;
import static crazypants.enderio.ModObject.itemLiquidConduit;
import static crazypants.enderio.ModObject.itemPowerConduit;
import static crazypants.enderio.ModObject.itemRedstoneConduit;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadePainterRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.Material;

public class ConduitRecipes {

  public static void addRecipes() {
	  
	  	//Crafting Components
	    ItemStack redstoneConduit = new ItemStack(itemRedstoneConduit.actualId, 1, 0);
	    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.CONDUIT_BINDER.ordinal());
	    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
	    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
	    ItemStack energeticIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ENERGETIC_ALLOY.ordinal());
	    ItemStack phasedIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_IRON.ordinal());
	    ItemStack redstoneAlloy = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.REDSTONE_ALLOY.ordinal());
	    

	    //Recipes
	    GameRegistry.addShapedRecipe(new ItemStack(itemConduitFacade.actualId, 1, 0), "bbb", "bbb", "bbb", 'b', conduitBinder);
	    GameRegistry.addShapedRecipe(new ItemStack(itemLiquidConduit.actualId, 8, 0), "bbb", "fgf", "bbb", 'b', conduitBinder, 'f', fusedQuartz, 'g', Block.glass);
	    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, 8, 0), "bbb", "#f#", "bbb", 'b', conduitBinder, 'f', fusedQuartz, '#', electricalSteel);
	    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, 8, 1), "bbb", "#f#", "bbb", 'b', conduitBinder, 'f', fusedQuartz, '#', energeticIron);
	    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, 8, 2), "bbb", "#f#", "bbb", 'b', conduitBinder, 'f', fusedQuartz, '#', phasedIron);
	    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId, 8, 0), "bbb", "#f#", "bbb", 'b', conduitBinder, 'f', fusedQuartz, '#', redstoneAlloy);
	    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId, 1, 1), "lbl", "bcb", "lbl", 'b', conduitBinder, 'c', redstoneConduit, 'l', Block.lever);
	    MachineRecipeRegistry.instance.registerRecipe(blockPainter.unlocalisedName, new FacadePainterRecipe());

  }

}
