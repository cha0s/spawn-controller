package io.cha0s.spawncontroller.config;

import net.minecraft.entity.Entity;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import io.cha0s.spawncontroller.config.EntityClasses;

public class SpawnControllerConfiguration {
  
  public static class ThrottleEntityClass {
    public int probability = 100;
    
  }
  public static class ThrottleEntityClasses extends EntityClasses<ThrottleEntityClass> {
    protected ThrottleEntityClass fromProperty(Property property, String key) {
      ThrottleEntityClass tec = new ThrottleEntityClass();
      tec.probability = property.getInt();
      return tec;
    }
  }
  public static ThrottleEntityClasses throttleEntityClasses = new ThrottleEntityClasses();
  
  public static int highestProbabilityToThrottleEntity(Entity entity) {
    int maxProbability = Integer.MIN_VALUE;
    for (String key : throttleEntityClasses.keysForEntity(entity)) {
      ThrottleEntityClass tec = throttleEntityClasses.get(key);
      int probability = tec == null ? 0 : tec.probability;
      if (probability > maxProbability) maxProbability = probability; 
    }
    return maxProbability;
  }

  public static class MobCapEntityClass {
    public int cap = 0;
  }
  public static class MobCapEntityClasses extends EntityClasses<MobCapEntityClass> {
    protected MobCapEntityClass fromProperty(Property property, String key) {
      MobCapEntityClass mcec = new MobCapEntityClass();
      mcec.cap = property.getInt();
      return mcec;
    }
  }  
  public static MobCapEntityClasses mobCapEntityClasses = new MobCapEntityClasses();

  public static boolean entityHasMobCap(Entity entity) {
    return mobCapEntityClasses.keysForEntity(entity).size() > 0;
  }

  public static Configuration forgeConfiguration;
  
  public static void fromForgeConfiguration(Configuration configuration) {
    forgeConfiguration = configuration;
    
    throttleEntityClasses.fromConfig(configuration.getCategory("throttle"));
    mobCapEntityClasses.fromConfig(configuration.getCategory("mobcap"));
  }

  public static void toForgeConfiguration(Configuration configuration) {
    
  }
}
