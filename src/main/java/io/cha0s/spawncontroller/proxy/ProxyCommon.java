package io.cha0s.spawncontroller.proxy;

import java.io.File;

import io.cha0s.spawncontroller.config.SpawnControllerConfiguration;
import io.cha0s.spawncontroller.event.HandlerEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon implements IProxyCommon {
  
  private HandlerEntity handlerEntity;
  
  public ProxyCommon() {
    handlerEntity = new HandlerEntity();
  }
  
  @Override
  public void registerEventHandlers() {
    MinecraftForge.EVENT_BUS.register(handlerEntity);
  }
  
  public void loadConfig(File configFile) {
    SpawnControllerConfiguration.fromForgeConfiguration(new Configuration(configFile));
  }
}
