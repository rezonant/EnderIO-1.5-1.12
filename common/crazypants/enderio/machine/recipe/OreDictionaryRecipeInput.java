package crazypants.enderio.machine.recipe;

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

}
