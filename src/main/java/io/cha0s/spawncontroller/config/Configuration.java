package io.cha0s.spawncontroller.config;

import net.minecraftforge.common.config.Config;

import io.cha0s.spawncontroller.SpawnController;

@Config(modid = SpawnController.MODID)
public class Configuration {

  @Config.LangKey("config.sc:spawncontrol.Hostile")
  public static int Hostile = 70;

  @Config.LangKey("config.sc:spawncontrol.Animal")
  public static int Animal = 10;

  @Config.LangKey("config.sc:spawncontrol.Ambient")
  public static int Ambient = 15;

  @Config.LangKey("config.sc:spawncontrol.Water")
  public static int Water = 5;

}
