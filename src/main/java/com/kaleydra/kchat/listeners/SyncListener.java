package com.kaleydra.kchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.kaleydra.kchat.KChat;
import com.kaleydra.kchat.PlayerChatData;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class SyncListener implements Listener {
	
	KChat plugin;
	
	public SyncListener(KChat plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLogin(PlayerLoginEvent event){
		try {
			PlayerChatData playerData = new PlayerChatData(event.getPlayer());
			if(playerData != null) KChat.playerData.put(event.getPlayer().getName(), playerData);
		} catch (NotRegisteredException e) {
//			e.printStackTrace();
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLogout(PlayerQuitEvent event){
		KChat.playerData.remove(event.getPlayer().getName());
	}

}
