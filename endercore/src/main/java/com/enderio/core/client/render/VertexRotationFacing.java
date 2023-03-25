package com.enderio.core.client.render;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector3d;
import net.minecraft.core.Direction;

public class VertexRotationFacing extends VertexRotation {

  private static final double ROTATION_AMOUNT = Math.PI / 2;

  private final @Nonnull Direction defaultDir;

  public VertexRotationFacing(@Nonnull Direction defaultDir) {
    super(0, new Vector3d(0, 0.5, 0), new Vector3d(0, 0, 0));
    this.defaultDir = defaultDir;
  }

  public void setRotation(@Nonnull Direction dir) {
    if (dir == defaultDir) {
      setAngle(0);
    } else if (dir == defaultDir.getOpposite()) {
      setAngle(ROTATION_AMOUNT * 2);
    } else if (dir == defaultDir.getCounterClockWise(Direction.Axis.Y)) {
      setAngle(ROTATION_AMOUNT);
    } else {
      setAngle(ROTATION_AMOUNT * 3);
    }
  }

  public Direction rotate(@Nonnull Direction dir) {
    if (dir.getStepY() != 0) {
      return dir;
    }
    if (getAngle() == ROTATION_AMOUNT) {
      return dir.getCounterClockWise(Direction.Axis.Y);
    }
    if (getAngle() == ROTATION_AMOUNT * 2) {
      return dir.getOpposite();
    }
    if (getAngle() == ROTATION_AMOUNT * 3) {
      return dir.getClockWise(Direction.Axis.Y);
    }
    return dir;
  }
}
