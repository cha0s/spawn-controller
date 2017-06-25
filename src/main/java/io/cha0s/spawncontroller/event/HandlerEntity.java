package io.cha0s.spawncontroller.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import io.cha0s.spawncontroller.config.SpawnControllerConfiguration;

public class HandlerEntity {
	
	private static final int ELIGIBILITY_INTERVAL = 20;
	
	private static int ticksRemaining = ELIGIBILITY_INTERVAL;
	
	public static class MobCapCache {
		public Map<String, Integer> entityCounts = new HashMap<String, Integer>();
	}
	
	public static Map<Integer, MobCapCache> mobCapCache = new HashMap<Integer, MobCapCache>();
	
	public static List<Chunk> eligibleChunks = new ArrayList<Chunk>();
	
	public static Map<String, Integer> entityCountsWithinEligibleSpawnChunks(List<Chunk> eligibleSpawnChunks) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		
		List<EntityLivingBase> livingEntities = new ArrayList<EntityLivingBase>();
		
		for (Chunk chunk : eligibleSpawnChunks) {
			
			// Get all entities within bounding box.
			double x = chunk.xPosition << 4;
			double z = chunk.zPosition << 4;
			
			chunk.getEntitiesOfTypeWithinAAAB(
				EntityLivingBase.class,
				new AxisAlignedBB(x, 0, z, x + 15, 255, z + 15),
				livingEntities,
				null
			);
		}
		
		for (EntityLivingBase entity : livingEntities) {
			if (entity instanceof EntityPlayer) continue;
			
			for (String key : SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity)) {
				if (!count.containsKey(key)) count.put(key, 0);
				count.put(key, count.get(key) + 1);
			}
		}
		
		return count;
	}
	
	public static List<Chunk> eligibleSpawnChunksUncached(World world) {
		Map<BlockPos, Chunk> uniqueChunks = new HashMap<BlockPos, Chunk>();
		
		for (EntityPlayerMP entity : world.getMinecraftServer().getPlayerList().getPlayerList()) {
			if (entity.getEntityWorld() != world) continue;
			
			final BlockPos originalPosition = entity.getPosition();

			// Eligible spawn chunks are 17x17 chunks centered on player. 
			IntStream.range(-8, 9).forEach(cz -> {
				IntStream.range(-8, 9).forEach(cx -> {
					
					final Chunk chunk = world.getChunkFromBlockCoords(originalPosition.add(
						cx << 4, 0, cz << 4
					));
					
					uniqueChunks.put(
						new BlockPos(chunk.xPosition, 0, chunk.zPosition),
						chunk
					);
							
					return;
				});  
			});  
		}
		
		return new ArrayList<Chunk>(uniqueChunks.values());
	}
	
	public static List<Chunk> eligibleSpawnChunks(World world) {
		if (0 == eligibleChunks.size()) {
			eligibleChunks.addAll(eligibleSpawnChunksUncached(world));
		}
		return eligibleChunks;
	}
	
	
	public static Map<String, Integer> entityCountsWithinWorldUncached(World world) {
		return entityCountsWithinEligibleSpawnChunks(eligibleSpawnChunks(world));
	}
	
	public static Map<String, Integer> entityCountsWithinWorld(World world) {
		int dimensionId = world.provider.getDimension();

		if (!mobCapCache.containsKey(dimensionId)) {
			mobCapCache.put(dimensionId, new MobCapCache());
			mobCapCache.get(dimensionId).entityCounts = entityCountsWithinWorldUncached(world);
		}
		
		return mobCapCache.get(dimensionId).entityCounts;
	}
	
	public static int mobCountForEntity(Entity entity) {
   		int mobcount = 0;
   		for (String key : SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity)) {
   			Map<String, Integer> entityCounts = entityCountsWithinWorld(entity.getEntityWorld());
   			int keyCount = entityCounts.containsKey(key) ? entityCounts.get(key) : 0;
   			if (keyCount > mobcount) mobcount = keyCount;
   		}
   		
   		return mobcount;
	}
	
	public void increaseEntityCount(World world, Entity entity, int increaseBy) {
		int dimensionId = world.provider.getDimension();
		MobCapCache mobCapCacheForEntity = mobCapCache.get(dimensionId);
   		for (String key : SpawnControllerConfiguration.mobCapEntityClasses.keysForEntity(entity)) {
   			Map<String, Integer> entityCounts = mobCapCacheForEntity.entityCounts;
   			if (!entityCounts.containsKey(key)) entityCounts.put(key, 0);
   			entityCounts.put(key, entityCounts.get(key) + increaseBy);
   		}
	}

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
    	World world = event.world;
    	
    	// Only for the server.
    	if(world.isRemote) return;
    	
    	// Only every [interval] ticks.
    	if (--ticksRemaining > 0) return;
    	ticksRemaining = ELIGIBILITY_INTERVAL;
    	
    	eligibleChunks.clear();
    	mobCapCache.clear();
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    	final Entity entity = event.getEntity();
    	final World world = event.getWorld();
    	
    	// Only for the server.
    	if(world.isRemote) return;
    	
    	// Should we throttle this entity? Roll the dice...
    	int probability = (int) Math.floor(Math.random() * 100);
       	if (SpawnControllerConfiguration.probabilityToThrottleEntity(entity) > probability) {
       		// Throttled.
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
    	}
       	
    	// Check mob cap?
       	if (SpawnControllerConfiguration.entityHasMobCap(entity)) {
       		
       		// Divide by 17x17 chunks.
       		double areas = eligibleChunks.size() / 289;
       		int mobcap = SpawnControllerConfiguration.mobCapForEntity(entity);
       		
       		if (mobCountForEntity(entity) >= (mobcap * areas)) {
       			// Nooope.
    			event.setResult(Result.DENY);
    			event.setCanceled(true);
    			return;
       		}
       		
        	// Update the cache.
        	increaseEntityCount(world, entity, 1);
       	}
    }    
}
