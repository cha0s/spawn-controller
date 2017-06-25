package io.cha0s.spawncontroller.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;

import io.cha0s.spawncontroller.event.HandlerEntity;

public class CommandStats extends CommandBase {

  @Override
  public String getCommandName() {
    return "spawncontrollerstats";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "spawncontrollerstats";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    for (World world : DimensionManager.getWorlds()) {
      sender.addChatMessage(new TextComponentString("Stats for dimension: " + world.provider.getDimension()));
      sender.addChatMessage(new TextComponentString("  Eligible chunks: " + HandlerEntity.eligibleSpawnChunks(world).size()));
      for (Map.Entry<String, Integer> countEntry : HandlerEntity.entityCountsWithinWorld(world).entrySet()) {
        sender.addChatMessage(new TextComponentString("  " + countEntry.getKey() + ": " + countEntry.getValue()));
      }
      
    }
  }
  
  @Override
  public List<String> getCommandAliases() {
    return new ArrayList<String>(Arrays.asList(
      "spawncontrollerstats",
      "scstats"
    ));
  }
}
