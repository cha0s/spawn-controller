package io.cha0s.spawncontroller.proxy;

import java.io.File;

public interface IProxyCommon {

	public void registerEventHandlers();
	
	public void loadConfig(File configFile);
	
}
