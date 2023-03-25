package com.enderio.core.common.imc;

import java.util.List;
import java.util.stream.Collectors;

import com.enderio.core.common.imc.handlers.IMCRightClickCrop;
import com.google.common.collect.Lists;

import net.minecraftforge.fml.InterModComms;

public class IMCRegistry {
  public interface IIMC {
    String getKey();

    void act(InterModComms.IMCMessage msg);
  }

  public static abstract class IMCBase implements IIMC {
    private String key;

    public IMCBase(String key) {
      this.key = key;
    }

    @Override
    public String getKey() {
      return key;
    }
  }

  public static final IMCRegistry INSTANCE = new IMCRegistry();

  private List<IIMC> handlers = Lists.newArrayList();

  private IMCRegistry() {
  }

  public void addIMCHandler(IIMC handler) {
    handlers.add(handler);
  }

  public void handleMessages(String modId) {

    for (IIMC handler : handlers) {

      InterModComms.getMessages(modId, s -> true).forEach(msg -> {
        if (msg.method().equals(handler.getKey())) {
          handler.act(msg);
        }
      });
    }
  }

  public void init() {
    addIMCHandler(new IMCRightClickCrop());
  }
}
