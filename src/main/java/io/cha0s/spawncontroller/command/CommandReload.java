package io.cha0s.spawncontroller.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.cha0s.spawncontroller.SpawnController;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandReload extends CommandBase {

	@Override
	public String getCommandName() {
		return "spawncontrollerreload";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "spawncontrollerreload";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		SpawnController.reloadConfig();
		sender.addChatMessage(new TextComponentString("SpawnController configuration reloaded!"));
	}
	
	@Override
	public List<String> getCommandAliases() {
		return new ArrayList<String>(Arrays.asList(
			"spawncontrollerreload",
			"screload"
		));
	}
}
