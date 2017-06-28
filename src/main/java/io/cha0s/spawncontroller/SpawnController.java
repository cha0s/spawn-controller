package io.cha0s.spawncontroller;

import net.minecraft.entity.EnumCreatureType;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import io.cha0s.spawncontroller.config.Configuration;

@Mod(
  modid = SpawnController.MODID,
  version = SpawnController.VERSION,
  acceptableRemoteVersions = SpawnController.ACCEPTABLEREMOTEVERSIONS
)
public class SpawnController {
  public static final String MODID = "spawncontroller";
  public static final String VERSION = "1.0";
  public static final String ACCEPTABLEREMOTEVERSIONS = "*";
    
  @Instance
  private static SpawnController instance;

  @EventHandler
  public static void onPreInit(final FMLPreInitializationEvent event) {
    EnumCreatureType.MONSTER.maxNumberOfCreature = Configuration.Hostile;
    EnumCreatureType.CREATURE.maxNumberOfCreature = Configuration.Animal;
    EnumCreatureType.AMBIENT.maxNumberOfCreature = Configuration.Ambient;
    EnumCreatureType.WATER_CREATURE.maxNumberOfCreature = Configuration.Water;
  }
}
