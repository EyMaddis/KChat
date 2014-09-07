package com.kaleydra.kchat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class KChatConfig {
	private static boolean isLoaded = false;

//	/**
//	 * <town rank, name in chat>
//	 */
//	static Map<String,String> townRanks = new ConcurrentHashMap<String,String>();
	
	

	/**
	 * <permission group, name in chat>
	 */
	static private Map<String,String> groupRanks = new ConcurrentHashMap<String,String>();
	/**
	 * <channel, prefix in chat>
	 */
	static private Map<String,String> channelPrefixes = new ConcurrentHashMap<String,String>();
	static private String namePrefix;
	static private String nameSuffix;
	static private String spyModePrefix;
	static private String townChannelPrefix;
	static private String itemReplaceText;
	static private String residentTownPrefix;
	static private String nameClickSuggestion;
	
	
	/**
	 * @return the groupRanks
	 */
	public static Map<String, String> getGroupRanks() {
		if(!isLoaded) load();
		return groupRanks;
	}



	/**
	 * @return the channelPrefixes
	 */
	public static Map<String, String> getChannelPrefixes() {
		if(!isLoaded) load();
		return channelPrefixes;
	}



	/**
	 * @return the namePrefix
	 */
	public static String getNamePrefix() {
		if(!isLoaded) load();
		return namePrefix;
	}



	/**
	 * @return the nameSuffix
	 */
	public static String getNameSuffix() {
		if(!isLoaded) load();
		return nameSuffix;
	}
	
	public static String getSpyModePrefix() {
		if(!isLoaded) load();
		return spyModePrefix;
	}
	
	public static String getTownChannelPrefix() {
		if(!isLoaded) load();
		return townChannelPrefix;
	}
	
	public static String getItemReplaceText() {
		if(!isLoaded) load();
		return itemReplaceText;
	}

	public static String getResidentTownPrefix() {
		if(!isLoaded) load();
		return residentTownPrefix;
	}
	
	public static String getNameClickSuggestion() {
		if(!isLoaded) load();
		return nameClickSuggestion;
	}



	public static void load(){
		FileConfiguration config = KChat.instance.getConfig();
		groupRanks = loadStringMap(config, "groupRanks");
//		townRanks = loadStringMap(config, "townRanks");
		channelPrefixes = loadStringMap(config, "channelPrefixes");
		namePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("namePrefix"));
		nameSuffix = ChatColor.translateAlternateColorCodes('&', config.getString("nameSuffix"));
		spyModePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("spyModePrefix"));
		townChannelPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("townChannelPrefix"));
		residentTownPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("residentTownPrefix"));
		itemReplaceText = config.getString("itemReplaceText");
		nameClickSuggestion = config.getString("nameClickSuggestion");
		isLoaded = true;
	}
	
	
	
	private static Map<String, String> loadStringMap(FileConfiguration config, String configKey){
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		ConfigurationSection section = config.getConfigurationSection(configKey);		
		for(String key: section.getKeys(false)){
			map.put(key, ChatColor.translateAlternateColorCodes('&', section.getString(key)));
		}
		return map;
	}
}
