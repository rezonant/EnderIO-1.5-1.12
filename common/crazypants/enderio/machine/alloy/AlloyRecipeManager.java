package crazypants.enderio.machine.alloy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.io.IOUtils;

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
    File coreFile = new File(Config.configDirectory, CORE_FILE_NAME);

    String defaultVals = null;
    try {
      defaultVals = readRecipes(coreFile, CORE_FILE_NAME, true);
    } catch (IOException e) {
      Log.error("Could not load default Alloy Smelter recipes from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return;
    }

    if(!coreFile.exists()) {
      Log.error("Could not load default Alloy Smelter recipes from " + coreFile + " as the file does not exist.");
      return;
    }

    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(defaultVals);
    } catch (Exception e) {
      Log.error("Error parsing " + CORE_FILE_NAME);
      return;
    }

    File userFile = new File(Config.configDirectory, CUSTOM_FILE_NAME);
    String userConfigStr = null;
    try {
      userConfigStr = readRecipes(userFile, CUSTOM_FILE_NAME, false);
      RecipeConfig userConfig = RecipeConfigParser.parse(userConfigStr);
      config.merge(userConfig);
    } catch (Exception e) {
      Log.error("Could not load user defined Alloy Smelter recipes.");
      e.printStackTrace();
    }

    processConfig(config);

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new AlloyMachineRecipe());
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

  private String readRecipes(File copyTo, String fileName, boolean replaceIfExists) throws IOException {
    if(!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }

    InputStream in = getClass().getResourceAsStream("/assets/enderio/config/" + fileName);
    if(in == null) {
      Log.error("Could load default SAG Mill recipes.");
      throw new IOException("Could not resource /assets/enderio/config/" + fileName + " form classpath. ");
    }
    String output = readStream(in);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(copyTo, false));
      writer.write(output.toString());
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return output.toString();

  }

  private String readStream(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder output = new StringBuilder();
    try {
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        output.append("\n");
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return output.toString();
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

  public List<IAlloyRecipe> getRecipes() {
    return recipes;
  }

}
