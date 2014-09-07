package com.kaleydra.kchat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.earth2me.essentials.User;
import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class PlayerChatData {
//	public static Set<Player> SPIES = new HashSet<Player>();
	
	Player player;
	Resident resident;
	ChatChannel channel = ChatChannel.GLOBAL;
	
	public PlayerChatData(Player player) throws NotRegisteredException{
		this.player = player;
		this.resident = TownyUniverse.getDataSource().getResident(player.getName());
	}

	public Player getPlayer(){
		return player;
	}
	public Resident getResident(){
		return resident;
	}
	
	public ChatChannel getChannel(){
		return channel;
	}
	
	public void setChannel(@Nonnull ChatChannel channel){
		this.channel = channel;
	}
	
	public Set<Player> getChannelRecipients(ChatChannel channel){
		Set<Player> recipients = new HashSet<Player>();
//		KChat.instance.getLogger().info("getChannelRecipients "+channel);
		
		switch(channel){
		case TOWN:
//			KChat.instance.getLogger().info("Town "+channel);
			try {
				Player p;
				for(Resident resi : resident.getTown().getResidents()){
					p = Bukkit.getPlayerExact(resi.getName());
//					KChat.instance.getLogger().info("TownChannel found: "+resi.getName()+ " for Player: "+p);
					if(p != null){
						recipients.add(p);
					}
				}
			} catch (NotRegisteredException e) {
				KChat.instance.getLogger().warning("Player "+player.getName()+" was in town channel without being in a town. Switching to global");
				channel = ChatChannel.GLOBAL;
			}
			break;
		default: // global
			for(Player player:Bukkit.getOnlinePlayers()){
				recipients.add(player);
			}
			break;
		}
		return recipients;		
	}
	
	public String getPlayerName(){
		String playerName = getPlayer().getCustomName();
		if(playerName == null || playerName.isEmpty()){
			playerName = getPlayer().getDisplayName();
			if(playerName == null || playerName.isEmpty()) playerName = getPlayer().getName();
		}
		String permPrefix = ChatColor.translateAlternateColorCodes('&', KChat.vaultChat.getPlayerPrefix(player));
		String permSuffix = ChatColor.translateAlternateColorCodes('&', KChat.vaultChat.getPlayerSuffix(player));
		
		return permPrefix+playerName+permSuffix;
	}
	
	/**
	 * @return the ranks
	 */
	public List<String> getRanks() {
		return getRanks(player);
	}
	/**
	 * @return the town
	 */
	public String getTown() {
		Town town;
		try {
			town = resident.getTown();
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
		if(town == null) return null;
		return town.getName();
	}
	/**
	 * @return the townRank
	 */
	public String getTownTitle() {
//		Town town;
//		try {
//			town = resident.getTown();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		if(town == null) return null;
		return resident.getTitle();
	}
	
	
	public ItemStack getInfoItem(){
		ItemStack item = new ItemStack(Material.STONE);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(player.getName());
		
		List<String> lore = new ArrayList<String>();
		
		List<String> ranks = getRanks();
		if(ranks != null && !ranks.isEmpty()) {
			lore.addAll(ranks);
		}
		String townLine = "";
		String title = getTownTitle();
		if(resident.isMayor()) {
			title = TownyFormatter.getNamePrefix(resident);
		} 
		if(title != null && !title.isEmpty()){
			townLine += ChatColor.GREEN+title.trim()+ChatColor.GRAY+" of ";
		} else if(getTown() != null && !getTown().isEmpty()) {
			townLine += ChatColor.GRAY+KChatConfig.getResidentTownPrefix();
		}
		title = getTown();
		if(title != null && !title.isEmpty()) 
			townLine += ChatColor.BLUE+formatTown(title);
		
		if(townLine.length() >= 1) {
			if(ranks != null && !ranks.isEmpty()) lore.add(ChatColor.RESET+""); // rank seperator
			lore.add(townLine);
		}
		
		if(lore != null && !lore.isEmpty()) itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public void sendMessage(String message){
//		player.sendMessage("direct: "+this.channel.name());
		sendMessage(message, this.channel);
	}

	public void sendMessage(String message, ChatChannel channel){
//		player.sendMessage("channel message: "+channel.name());
		FancyMessage fancyMessage = formatMessage(message, channel, false);
		if(fancyMessage == null) return;
		final Set<Player> channelRecipients = getChannelRecipients(channel);
		for(Player receiver:channelRecipients){
			fancyMessage.send(receiver);
		}
		if(channelRecipients.size() <= 1){
			getPlayer().sendMessage(ChatColor.RED+"Nobody can hear you right now...");
		}
		fancyMessage = formatMessage(message, channel, true);
		if(fancyMessage == null) return;
		for(Player onlinePlayer: Bukkit.getOnlinePlayers()){
			User essentialsUser = KChat.essentials.getUser(onlinePlayer);
			if(essentialsUser == null || !essentialsUser.isSocialSpyEnabled() ||
					channelRecipients.contains(onlinePlayer)) continue;
			fancyMessage.send(onlinePlayer);
		}
	}
	
	/**
	 * @param message
	 * @param channel
	 * @param spyMode
	 * @return null if <item> used and no item in hand
	 */
	public FancyMessage formatMessage(String message, ChatChannel channel, boolean spyMode){
//		player.sendMessage("formatted Message call: "+channel.name());

		FancyMessage fancy = new FancyMessage("");
		String namePrefix = "";
		
		// spymode tag
		if(spyMode) {
			fancy.then(KChatConfig.getSpyModePrefix());
		}
		
		// town chat tag/tooltip
		if(channel.equals(ChatChannel.TOWN)) {
			String town = getTown();
			fancy.then(KChatConfig.getTownChannelPrefix());
			fancy.suggest("/tc");
			try {
				if(town != null)fancy.tooltip("Town Chat for "+formatTown(resident.getTown().getName())); //TODO: formatting (_ = " ")
					
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
		}
		
		// Player name
		namePrefix += KChatConfig.getNamePrefix();
		
		fancy.then(namePrefix);
		fancy.then(getPlayerName())
				.suggest(String.format(KChatConfig.getNameClickSuggestion(), player.getName()))
		    	.itemTooltip(getInfoItem())
		    .then(KChatConfig.getNameSuffix());
		
		// <item> in chat
//		message = " "+message;
//		String[] splitted = message.split(KChatConfig.getItemReplaceText());
//		message = message.substring(1); // remove " " at front
//		
//		if(message.contains(KChatConfig.getItemReplaceText())){ // found <item>
//			if(player.hasPermission("kchat.senditem")){
//				if(player.getItemInHand() == null || player.getItemInHand().equals(Material.AIR)){
//					player.sendMessage("you don't have an item in your hand!");
//					return null;
//				}
//				player.sendMessage(splitted.length+""+player.getItemInHand());
//				for(int i=0; i<splitted.length; i++){
//					if(i == 0){
//						splitted[0] = splitted[0].substring(1);
//					}
//					fancy.then(splitted[i]);
//					fancy.then("[hover for item info]");
//					fancy.itemTooltip(player.getItemInHand());
//				}
//			} else {
//				player.sendMessage(ChatColor.RED+"You don't have permissions to use "+KChatConfig.getItemReplaceText()+"!");
//			}
//		} else {
//			player.sendMessage("no "+KChatConfig.getItemReplaceText());
			fancy.then(message);
//		}
		return fancy;
	}
	

	public static FancyMessage formatGuestMessage(Player player, String message){
		FancyMessage fancy = new FancyMessage("");
		String namePrefix = "";
		
		namePrefix += KChatConfig.getNamePrefix();
		
		fancy.then(namePrefix);
		fancy.then(player.getCustomName()).suggest(String.format(KChatConfig.getNameClickSuggestion(), player.getName()))
		    	.itemTooltip(getGuestItem(player))
		    .then(KChatConfig.getNameSuffix())
			.then(message);
		return fancy;
	}
	
	public static ItemStack getGuestItem(Player guest){
		ItemStack item = new ItemStack(Material.STONE);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(guest.getName());
		
		List<String> lore = new ArrayList<String>();
		
		List<String> ranks = getRanks(guest);
		if(ranks != null && !ranks.isEmpty()) {
			lore.addAll(ranks);
		}
		if(lore != null && !lore.isEmpty()) itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}
	

	/**
	 * @return the rank
	 */
	public static List<String> getRanks(Player player) {
		List<String> output = new ArrayList<String>();
		for(String group:KChat.vaultPermissions.getPlayerGroups(player)){
			String current = KChatConfig.getGroupRanks().get(group);
			if(current == null || current.isEmpty()) continue;
			output.add(current);
		}
		return output;
	}
	
	public static String formatTown(String townName){
		return townName.replaceAll("_", " ");
	}
}
