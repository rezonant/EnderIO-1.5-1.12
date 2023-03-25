package com.enderio.core.client;

import javax.annotation.Nonnull;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderCoreModConflictException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final @Nonnull String[] msgs;

  public EnderCoreModConflictException(@Nonnull String[] msgs) {
    super(msgs[0]);
    this.msgs = msgs;
  }
}
