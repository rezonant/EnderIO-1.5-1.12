package crazypants.enderio.machine.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRecipeInput extends RecipeInput {

  private int oreId;

  public OreDictionaryRecipeInput(ItemStack itemStack, int oreId) {
    super(itemStack);
    this.oreId = oreId;
  }

  @Override
  public boolean isInput(ItemStack test) {
    if(test == null) {
      return false;
    }
    //System.out.println("OreDictionaryRecipeInput.isInput: ");
    return OreDictionary.getOreID(test) == oreId;
  }

  @Override
  public ItemStack[] getEquivelentInputs() {
    System.out.println("OreDictionaryRecipeInput.getEquivelentInputs: !@@@@@@@@@@!!@*(*(*)(#*$)@(*(@)(*#@$)(@#*$)@(*#$)@(#*$)@(#*$)(@#*$)@(#*$#@)*(");
    ArrayList<ItemStack> res = OreDictionary.getOres(oreId);
    if(res == null || res.isEmpty()) {
      System.out.println("OreDictionaryRecipeInput.getEquivelentInputs: No equivs");
      return null;
    }
    System.out.println("OreDictionaryRecipeInput.getEquivelentInputs: " + res);
    return res.toArray(new ItemStack[res.size()]);
  }

}
