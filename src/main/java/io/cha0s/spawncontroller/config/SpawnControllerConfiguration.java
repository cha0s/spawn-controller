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
	
	public static int probabilityToThrottleEntity(Entity eventEntity) {
		ThrottleEntityClass tec = throttleEntityClasses.lookupForEntity(eventEntity);
		return tec == null ? 0 : tec.probability;
	}

	public static class MobCapEntityClass {
		public String originalKey = "";
		public int cap = 0;
	}
	public static class MobCapEntityClasses extends EntityClasses<MobCapEntityClass> {
		protected MobCapEntityClass fromProperty(Property property, String key) {
			MobCapEntityClass mcec = new MobCapEntityClass();
			mcec.cap = property.getInt();
			mcec.originalKey = key;
			return mcec;
		}
	}	
	public static MobCapEntityClasses mobCapEntityClasses = new MobCapEntityClasses();

	public static boolean entityHasMobCap(Entity eventEntity) {
		MobCapEntityClass mcec = mobCapEntityClasses.lookupForEntity(eventEntity);
		return mcec != null;
	}

	public static int mobCapForEntity(Entity eventEntity) {
		MobCapEntityClass mcec = mobCapEntityClasses.lookupForEntity(eventEntity);
		return mcec == null ? 0 : mcec.cap;
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
