package io.cha0s.spawncontroller.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;

public class CommandStats extends CommandBase {

  @Override
  public String getCommandName() {
    return "spawncontrollerstats";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "mobstats [type]\nGet stats for mobs on the server.";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    if (args.length > 1) {
      throw new CommandException(getCommandUsage(sender));
    }

    boolean anyMobsCounted = false;
    EnumCreatureType specificCreatureType = 1 == args.length ? argToCreatureType(args[0]) : null;

    for (World world : DimensionManager.getWorlds()) {
      Map<EnumCreatureType, Integer> mobCounts = new HashMap<EnumCreatureType, Integer>();

      // Accumulate mob counts for the world.
      int total = 0;
      for (EnumCreatureType creatureType : EnumCreatureType.values()) {
        if (null != specificCreatureType && creatureType != specificCreatureType) {
          continue;
        }

        int creatureTypeCount = world.countEntities(creatureType, false);
        mobCounts.put(creatureType, creatureTypeCount);
        total += creatureTypeCount;
      }

      if (0 == total) {
        continue;
      }

      anyMobsCounted = true;

      sender.addChatMessage(new TextComponentString(world.provider.getDimensionType().getName() + " (" + world.provider.getDimension() + ")"));

      for (Map.Entry<EnumCreatureType, Integer> mobCount : mobCounts.entrySet()) {
        sender.addChatMessage(new TextComponentString("- " + creatureTypeToString(mobCount.getKey()) + ": " + mobCount.getValue()));
      }
    }

    if (!anyMobsCounted) {
      sender.addChatMessage(new TextComponentString("No mobs matched your criteria."));
    }
  }

  @Override
  public List<String> getCommandAliases() {
    return Arrays.asList("mobstats");
  }

  public static String creatureTypeToString(EnumCreatureType creatureType) {

    switch (creatureType) {
    case MONSTER:
      return "Hostile";
    case CREATURE:
      return "Animal";
    case AMBIENT:
      return "Ambient";
    case WATER_CREATURE:
      return "Water";
    default:
      return "?";
    }
  }

  public static EnumCreatureType argToCreatureType(String arg) throws CommandException {

    switch (arg.toLowerCase()) {
    case "hostile":
      return EnumCreatureType.MONSTER;
    case "animal":
      return EnumCreatureType.CREATURE;
    case "ambient":
      return EnumCreatureType.AMBIENT;
    case "water":
      return EnumCreatureType.WATER_CREATURE;
    default:
      throw new CommandException("[type] must be one of: hostile, animal, ambient, water");
    }
  }
}
