package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import crazypants.enderio.Log;

public class RecipeConfig {

  private boolean dumpItemRegistery = false;
  private boolean dumpOreDictionary = false;
  private boolean enabled = true;

  private Map<String, RecipeGroup> recipeGroups = new HashMap<String, RecipeConfig.RecipeGroup>();

  public RecipeConfig() {
  }

  public void merge(RecipeConfig userConfig) {

    if(userConfig.dumpItemRegistery) {
      dumpItemRegistery = true;
    }
    if(userConfig.dumpOreDictionary) {
      dumpOreDictionary = true;
    }

    for (RecipeGroup group : userConfig.getRecipeGroups().values()) {
      if(!group.enabled) {
        if(recipeGroups.remove(group.name) != null) {
          Log.info("Disabled core recipe group " + group.name + " due to user config.");
        }
      } else {
        RecipeGroup modifyGroup = recipeGroups.get(group.name);
        if(modifyGroup == null) {
          Log.info("Added user defined recipe group " + group.name);
          modifyGroup = new RecipeGroup(group.name);
          recipeGroups.put(group.name, modifyGroup);
        }
        for (RecipeElement recipe : group.recipes.values()) {
          if(recipe.isValid()) {
            if(modifyGroup.recipes.containsKey(recipe.name)) {
              Log.info("Replacing core recipe " + recipe.name + "  with user defined recipe.");
            } else {
              Log.info("Added user defined recipe " + recipe.name);
            }
            modifyGroup.addRecipe(recipe);
          } else {
            Log.info("Removed recipe " + recipe.name + " due to user config.");
            modifyGroup.recipes.remove(recipe.name);
          }
        }
      }
    }
  }

  public RecipeGroup createRecipeGroup(String name) {
    return new RecipeGroup(name);
  }

  public void addRecipeGroup(RecipeGroup group) {
    if(group.isNameValid()) {
      recipeGroups.put(group.getName(), group);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setRecipeGroups(Map<String, RecipeGroup> recipeGroups) {
    this.recipeGroups = recipeGroups;
  }

  public boolean isDumpItemRegistery() {
    return dumpItemRegistery;
  }

  public void setDumpItemRegistery(boolean dumpItemRegistery) {
    this.dumpItemRegistery = dumpItemRegistery;
  }

  public boolean isDumpOreDictionary() {
    return dumpOreDictionary;
  }

  public void setDumpOreDictionary(boolean dumpOreDictionary) {
    this.dumpOreDictionary = dumpOreDictionary;
  }

  public List<Recipe> getRecipes(boolean isRecipePerInput) {
    List<Recipe> result = new ArrayList<Recipe>(32);
    for (RecipeGroup rg : recipeGroups.values()) {
      if(rg.isEnabled() && rg.isValid()) {
        result.addAll(rg.createRecipes(isRecipePerInput));
      }
    }
    return result;
  }

  public List<Recipe> getRecipesForGroup(String group, boolean isRecipePerInput) {
    RecipeGroup grp = recipeGroups.get(group);
    if(grp == null) {
      return Collections.emptyList();
    }
    return grp.createRecipes(isRecipePerInput);
  }

  public Map<String, RecipeGroup> getRecipeGroups() {
    return recipeGroups;
  }

  public static class RecipeGroup {

    private final String name;

    private Map<String, RecipeElement> recipes = new HashMap<String, RecipeElement>();

    private boolean enabled = true;

    public RecipeGroup(String name) {
      if(name != null) {
        name = name.trim();
      }
      if(name.length() <= 0) {
        name = null;
      }
      this.name = name;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public RecipeElement createRecipe(String name) {
      return new RecipeElement(name);
    }

    public void addRecipe(RecipeElement recipe) {
      recipes.put(recipe.name, recipe);
    }

    public String getName() {
      return name;
    }

    public List<Recipe> createRecipes(boolean isRecipePerInput) {
      List<Recipe> result = new ArrayList<Recipe>(recipes.size());
      for (RecipeElement recipe : recipes.values()) {
        if(recipe.isValid()) {
          result.addAll(recipe.createRecipes(isRecipePerInput));
        }
      }
      return result;
    }

    public boolean isValid() {
      return isNameValid() && !recipes.isEmpty();
    }

    public boolean isNameValid() {
      return name != null;
    }

    @Override
    public String toString() {
      return "RecipeGroup [name=" + name + ", recipes=" + recipes + ", enabled=" + enabled + "]";
    }

  }

  public static class RecipeElement {

    private List<RecipeInput> inputs = new ArrayList<RecipeInput>();

    private List<RecipeOutput> outputs = new ArrayList<RecipeOutput>();

    private int energyRequired;

    private String name;

    private RecipeElement(String name) {
      this.name = name;
    }

    public void addInput(RecipeInput input) {
      inputs.add(input);
    }

    public void addInput(ItemStack stack, boolean useMetadata) {
      inputs.add(new RecipeInput(stack, useMetadata));
    }

    public void addOutput(RecipeOutput output) {
      outputs.add(output);
    }

    public List<Recipe> createRecipes(boolean isRecipePerInput) {

      RecipeOutput[] outputArr = outputs.toArray(new RecipeOutput[outputs.size()]);
      RecipeInput[] inputArr = inputs.toArray(new RecipeInput[inputs.size()]);
      List<Recipe> result = new ArrayList<Recipe>();
      if(isRecipePerInput) {
        for (RecipeInput input : inputs) {
          result.add(new Recipe(input, energyRequired, outputArr));
        }
      } else {
        for (RecipeOutput output : outputs) {
          result.add(new Recipe(output, energyRequired, inputArr));
        }
      }
      return result;
    }

    public boolean isValid() {
      return !inputs.isEmpty() && !outputs.isEmpty();
    }

    public float getEnergyRequired() {
      return energyRequired;
    }

    public void setEnergyRequired(int energyRequired) {
      this.energyRequired = energyRequired;
    }

    @Override
    public String toString() {
      return "Recipe [input=" + inputs + ", outputs=" + outputs + ", energyRequired=" + energyRequired + "]";
    }

  }

}
