package com.enderio.core.common.util.stackable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IProducer {

  default @Nullable Block getBlock() {
    return null;
  };

  default @Nullable Item getItem() {
    return null;
  };

  default @Nonnull Block getBlockNN() {
    return NullHelper.notnull(NullHelper.notnull(getBlock(), "Block ", this, " is unexpectedly missing"), "Block ", this,
        " is unexpectedly missing");
  }

  default @Nonnull Item getItemNN() {
    return NullHelper.notnull(NullHelper.notnull(getItem(), "Item ", this, " is unexpectedly missing"), "Item ", this,
        " is unexpectedly missing");
  }

}
