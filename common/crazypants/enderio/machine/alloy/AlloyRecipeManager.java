package crazypants.enderio.machine.alloy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.Config;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeConfig;
import crazypants.enderio.machine.recipe.RecipeConfigParser;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.util.Util;

public class AlloyRecipeManager {

  private static final String CORE_FILE_NAME = "AlloySmelterRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "AlloySmelterRecipes_User.xml";

  static final AlloyRecipeManager instance = new AlloyRecipeManager();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  private final List<IAlloyRecipe> recipes = new ArrayList<IAlloyRecipe>();

  public AlloyRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME);
    if(config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for Alloy Smelter.");
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new AlloyMachineRecipe());
    //vanilla alloy furnace recipes    
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new VanillaSmeltingRecipe());
  }

  public void addCustumRecipes(String xmlDef) {
    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(xmlDef);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    if(config == null) {
      Log.error("Could process custom XML");
      return;
    }
    processConfig(config);
  }

  public List<IAlloyRecipe> getRecipes() {
    return recipes;
  }

  private void processConfig(RecipeConfig config) {
    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }

    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Added " + newRecipes.size() + " Alloy Smelter recipes from config.");
    for (Recipe rec : newRecipes) {
      addRecipe(new BasicAlloyRecipe(rec));
    }

  }

  public void addRecipe(IAlloyRecipe recipe) {
    if(recipe == null) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    IRecipe rec = getRecipeForInputs(recipe.getInputStacks());
    if(rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + recipe);
      return;
    }
    recipes.add(recipe);
  }

  IRecipe getRecipeForInputs(ItemStack[] inputs) {
    for (IAlloyRecipe rec : recipes) {
      if(rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null || input.item == null) {
      return false;
    }
    for (IAlloyRecipe recipe : recipes) {
      for (RecipeInput ri : recipe.getInputs()) {
        if(ri.isInput(input.item)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidRecipeComponents(ItemStack[] inputs) {
    if(inputs == null || inputs.length == 0) {
      return false;
    }
    for (IAlloyRecipe recipe : recipes) {
      if(recipe.isValidRecipeComponents(inputs)) {
        return true;
      }
    }
    return false;
  }

  public float getExperianceForOutput(ItemStack output) {
    for (IAlloyRecipe recipe : recipes) {
      if(recipe.getOutput().itemID == output.itemID && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }

}
