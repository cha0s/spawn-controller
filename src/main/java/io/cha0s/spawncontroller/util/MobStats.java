package io.cha0s.spawncontroller.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import io.cha0s.spawncontroller.config.SpawnControllerConfiguration;

public class MobStats {
  
  public Map<String, Integer> mobCounts = new HashMap<String, Integer>();
  public int eligibleChunkCount;
  
  public MobStats(Map<String, Integer> mobCounts, int eligibleChunkCount) {
    this.mobCounts = mobCounts;
    this.eligibleChunkCount = eligibleChunkCount;
  }
  
  public void incrementCountsForEntity(Entity entity) {
    for (String key : SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity)) {
      if (!mobCounts.containsKey(key)) mobCounts.put(key, 0);
      mobCounts.put(key, mobCounts.get(key) + 1);
    }
  }
  
  public static MobStats forWorld(World world) {
    Map<String, Integer> mobCounts = new HashMap<String, Integer>();
    Set<Long> uniqueChunks = new HashSet<Long>();
    
    // Players in this world.
    for (EntityPlayerMP entity : world.getMinecraftServer().getPlayerList().getPlayerList()) {
      if (entity.getEntityWorld() != world) continue;
      
      // Translate to chunk position.
      final BlockPos pos = entity.getPosition();
      int ex = pos.getX() >> 4;
      int ez = pos.getZ() >> 4;

      // Eligible spawn chunks are 17x17 chunks centered on player.
      for (int cz = -8; cz < 9; ++cz) {
        for (int cx = -8; cx < 9; ++cx) {
          if (!uniqueChunks.add(ChunkPos.asLong(cx + ex, cz + ez))) continue;
          
          // Get living entities.
          ClassInheritanceMultiMap<Entity>[] entityLists = world.getChunkFromChunkCoords(cx + ex, cz + ez).getEntityLists();
          for (int i = 0; i < entityLists.length; ++i) {
            for (EntityLiving elb : entityLists[i].getByClass(EntityLiving.class)) {

              // Counts per key.
              for (String key : SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(elb)) {
                if (!mobCounts.containsKey(key)) mobCounts.put(key, 0);
                mobCounts.put(key, mobCounts.get(key) + 1);
              }
            }
          }
        }
      }
    }
    
    return new MobStats(mobCounts, uniqueChunks.size());
  }
}
