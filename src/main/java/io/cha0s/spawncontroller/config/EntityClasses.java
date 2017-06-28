package io.cha0s.spawncontroller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

abstract public class EntityClasses<T> {
  
  private Map<String, T> specs = new HashMap<String, T>();
  
  private void clearCache() {
    specs.clear();
  }
  
  abstract protected T fromProperty(Property property, String key);
  
  public void fromConfig(ConfigCategory category) {
    clearCache();

    if (category.isEmpty()) {
      // Do initial config?
      return;
    }
    
    for (Map.Entry<String, Property> entry : category.entrySet()) {
      final String key = entry.getKey();
      specs.put(key, fromProperty(entry.getValue(), key));
    }
  }

  public List<String> keysForEntity(Entity entity) {
    List<String> result = new ArrayList<String>();
    
    // 4 types by default:
    String key;
    
    if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
      key = "Hostile";
    }
    else if (entity.isCreatureType(EnumCreatureType.CREATURE, false)) {
      key = "Animal";
    }
    else if (entity.isCreatureType(EnumCreatureType.AMBIENT, false)) {
      key = "Ambient";
    }
    else if (entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false)) {
      key = "Water";
    }
    else {
      key = "Ignored";
    }
    
    result.add(key);
    
    return result;
  }
  
  public T lookupForEntity(Entity entity) {
    List<String> keys = keysForEntity(entity);
    return specs.get(keys.get(0));
  }
  
  public T get(String key) { return specs.get(key); }
  public void put(String key, T value) { specs.put(key, value); }
}
