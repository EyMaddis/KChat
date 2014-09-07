package com.kaleydra.kchat.listeners;


import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.earth2me.essentials.User;
import com.kaleydra.kchat.KChat;
import com.kaleydra.kchat.PlayerChatData;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class AsyncListener implements Listener {

	KChat plugin;
	
	public AsyncListener(KChat plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true, priority=EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event){
		event.setCancelled(true);
		
		String message = event.getMessage();
		Player sender = event.getPlayer();
		
		
		User essentialsUser = KChat.essentials.getUser(sender);
		if(essentialsUser != null && essentialsUser.isMuted()){
			sender.sendMessage(ChatColor.RED+"You can't talk! You are muted!");
			return;
		}
		
		PlayerChatData playerData = KChat.playerData.get(sender.getName());
		
		if(playerData == null){
//			plugin.getLogger().warning("Missing PlayerChatData for "+sender.getName());
			try {
				playerData = new PlayerChatData(sender);
				KChat.playerData.put(sender.getName(), playerData);
			} catch (NotRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(playerData != null) {
			plugin.getLogger().info("("+playerData.getChannel().name()+") "+sender.getName()+": "+message);
			playerData.sendMessage(message);
		} else { // guest without town
			plugin.getLogger().info("(Guest Global) "+sender.getName()+": "+message);
			FancyMessage fancyMessage = PlayerChatData.formatGuestMessage(sender, message);
			if(fancyMessage != null) 
				for(Player receiver: event.getRecipients()) fancyMessage.send(receiver);
		}
	}
	
}
