package io.cha0s.spawncontroller.config;

import java.io.File;

import net.minecraft.entity.EnumCreatureType;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;

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

  public static class Static {

    public static File file;

  }

  public static void applyConfiguration() {
    EnumCreatureType.MONSTER.maxNumberOfCreature = Configuration.Hostile;
    EnumCreatureType.CREATURE.maxNumberOfCreature = Configuration.Animal;
    EnumCreatureType.AMBIENT.maxNumberOfCreature = Configuration.Ambient;
    EnumCreatureType.WATER_CREATURE.maxNumberOfCreature = Configuration.Water;
  }

  public static void reloadFromDisk() {
    net.minecraftforge.common.config.Configuration configuration = new net.minecraftforge.common.config.Configuration(Static.file);

    // Mob cap.
    ConfigCategory category = configuration.getCategory("general");

    Configuration.Hostile = category.get("Hostile").getInt();
    Configuration.Animal = category.get("Animal").getInt();
    Configuration.Ambient = category.get("Ambient").getInt();
    Configuration.Water = category.get("Water").getInt();

    applyConfiguration();
  }

}
