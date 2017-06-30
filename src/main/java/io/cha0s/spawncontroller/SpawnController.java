package io.cha0s.spawncontroller;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import io.cha0s.spawncontroller.command.CommandReload;
import io.cha0s.spawncontroller.command.CommandStats;
import io.cha0s.spawncontroller.config.Configuration;

@Mod(modid = SpawnController.MODID, version = SpawnController.VERSION, acceptableRemoteVersions = SpawnController.ACCEPTABLEREMOTEVERSIONS)
public class SpawnController {
  public static final String MODID = "spawncontroller";
  public static final String VERSION = "1.0";
  public static final String ACCEPTABLEREMOTEVERSIONS = "*";

  @Instance
  private static SpawnController instance;

  @EventHandler
  public static void onPreInit(final FMLPreInitializationEvent event) {
    Configuration.Static.file = event.getSuggestedConfigurationFile();
    Configuration.applyConfiguration();
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    event.registerServerCommand(new CommandReload());
    event.registerServerCommand(new CommandStats());
  }
}
