package com.kaleydra.kchat.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kaleydra.kchat.ChatChannel;
import com.kaleydra.kchat.KChat;
import com.kaleydra.kchat.PlayerChatData;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class ChannelCommand implements CommandExecutor {
	
	KChat plugin;
	ChatChannel baseChannel;
	ChatChannel inverseChannel;
	
	

	public ChannelCommand(KChat plugin, ChatChannel baseChannel, ChatChannel inverseChannel) {
		this.plugin = plugin;
		this.baseChannel = baseChannel;
		this.inverseChannel = inverseChannel;
	}



	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;
		PlayerChatData playerData = KChat.playerData.get(player.getName());
		

		if(playerData == null){
			plugin.getLogger().warning("Missing PlayerChatData for "+sender.getName());
			try {
				playerData = new PlayerChatData(player);
				KChat.playerData.put(sender.getName(), playerData);
			} catch (NotRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(baseChannel.equals(ChatChannel.TOWN)){
			boolean hasTown = false;
			if(playerData != null){
				try {
					Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
					Town town = resident.getTown();
					if(town != null) hasTown = true;
				} catch (NotRegisteredException e) {
					hasTown = false;
				}
			}
			if(!hasTown){
				player.sendMessage(ChatColor.RED+"You are not in a town!");
				return true;
			}
		}
		
		if(args.length <= 0){
			ChatChannel newChannel = baseChannel;
			if(playerData.getChannel().equals(baseChannel) && !baseChannel.equals(ChatChannel.GLOBAL)){ // /g multiple times should not switch to tc
				newChannel = inverseChannel;
			}
			playerData.setChannel(newChannel);
			player.sendMessage(ChatColor.RED+"Switched to "+ChatColor.DARK_GREEN+newChannel.name().toLowerCase()+ ChatColor.RED +" chat mode");
			return true;
		}
		
		String message = "";
		for(int i = 0; i < args.length; i++){
			if(i > 0) message += " ";
			message += args[i];
		}
		
		playerData.sendMessage(message, baseChannel);
		
		return true;
	}

}
