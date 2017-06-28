package io.cha0s.spawncontroller.event;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import io.cha0s.spawncontroller.config.SpawnControllerConfiguration;
import io.cha0s.spawncontroller.config.SpawnControllerConfiguration.MobCapEntityClass;
import io.cha0s.spawncontroller.util.MobStats;

public class HandlerEntity {
  
  final int DENIAL_INTERVAL = 2 * 20;
  int denialCaret = 1;
  
  public static Set<String> denialCache = new HashSet<String>();
  public static Map<Integer, MobStats> statsCache = new HashMap<Integer, MobStats>();
  
  @SubscribeEvent
  public void onServerTick(ServerTickEvent event) {
    if (Phase.END != event.phase) return;
    
    statsCache.clear();

    if (--denialCaret > 0) return;
    denialCaret = DENIAL_INTERVAL;
    
    denialCache.clear();
  }
  
  MobStats statsForWorld(World world) {
    int dimensionId = world.provider.getDimension();
    if (!statsCache.containsKey(dimensionId)) {
      statsCache.put(dimensionId, MobStats.forWorld(world));
    }
    return statsCache.get(dimensionId);
  }
  
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onCheckSpawn(CheckSpawn event) {
    final Entity entity = event.getEntity();
    final World world = event.getWorld();
    
    // Only for the server.
    if(world.isRemote) return;
    
    // Don't touch forced entities.
    if (entity.forceSpawn) return;
    
    // TorchMaster tag? TODO shouldn't be lazy...
    if (entity.getTags().contains("IsSpawnerMob")) return;
    
    // Add a tag so we can see that we've checked this entity.
    entity.addTag("SpawnControl");
    
    // Early out spawn check.
    List<String> entityKeys = SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity);
    for (String key : entityKeys) if (denialCache.contains(key)) {
      denyEntitySpawn(event);
      return;
    }
  }

  @SubscribeEvent
  public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    Entity entity = event.getEntity();
    World world = event.getWorld();
    
    // Only for the server.
    if(world.isRemote) return;
    
    // Only operate on entities that were just SpawnCheck'd.
    if (!entity.getTags().contains("SpawnControl")) return;
    entity.removeTag("SpawnControl");

    // Should we throttle this entity? Roll the dice...
    int probability = (int) Math.floor(Math.random() * 100);
    if (SpawnControllerConfiguration.highestProbabilityToThrottleEntity(entity) > probability) {
      denyEntitySpawn(event);
      return;
    }

    // Check mob cap?
    if (SpawnControllerConfiguration.entityHasMobCap(entity)) {
      MobStats stats = statsForWorld(world);
       
      // Divide by 17x17 chunks.
      double areas = stats.eligibleChunkCount / (17 * 17);
      
      boolean isUnderCap = true;
      
      List<String> entityKeys = SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity);
      for (String key : entityKeys) {
        if (!stats.mobCounts.containsKey(key)) continue;
        
        MobCapEntityClass mcec = SpawnControllerConfiguration.mobCapEntityClasses.get(key);
        if (null == mcec) continue;
        
        if (stats.mobCounts.get(key) >= mcec.cap * areas) {
          isUnderCap = false;
          denialCache.add(key);
          break;
        }
      }
      
      if (!isUnderCap) {
        denyEntitySpawn(event);
        return;
      }

      stats.incrementCountsForEntity(entity);        
    }
  }

  public void denyEntitySpawn(EntityEvent event) {
    final Entity entity = event.getEntity();
    final World world = entity.getEntityWorld();
    
    // Cleanup event.
    event.setResult(Result.DENY);
    if (event.isCancelable()) event.setCanceled(true);
    
    // Cleanup world.
    world.removeEntity(entity);
  }
}
