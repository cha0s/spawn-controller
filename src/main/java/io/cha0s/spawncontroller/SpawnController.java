package io.cha0s.spawncontroller;

import java.io.File;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import io.cha0s.spawncontroller.command.CommandReload;
import io.cha0s.spawncontroller.command.CommandStats;

@Mod(
	modid = SpawnController.MODID,
	version = SpawnController.VERSION,
	acceptableRemoteVersions = SpawnController.ACCEPTABLEREMOTEVERSIONS
)
public class SpawnController
{
    public static final String MODID = "spawncontroller";
    public static final String VERSION = "1.0";
    public static final String ACCEPTABLEREMOTEVERSIONS = "*";
    
	@Instance
	private static SpawnController instance;

	@SidedProxy(
		clientSide="io.cha0s.spawncontroller.proxy.ProxyClient",
		serverSide="io.cha0s.spawncontroller.proxy.ProxyServer"
	)
	private static io.cha0s.spawncontroller.proxy.IProxyCommon proxy;
	
	private static File suggestedConfigurationFile;
	
	public static void reloadConfig() {
		proxy.loadConfig(suggestedConfigurationFile);
	}

	@EventHandler
	public static void onPreInit(final FMLPreInitializationEvent event){
		final ProgressBar bar = ProgressManager.push("SpawnController PreInitialization", 2, true);

		bar.step("Registering event handlers");
		proxy.registerEventHandlers();
		
		// Loading config.
		bar.step("Loading config");
		suggestedConfigurationFile = event.getSuggestedConfigurationFile();
		reloadConfig();

		ProgressManager.pop(bar);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandReload());
		event.registerServerCommand(new CommandStats());
	}	
}
