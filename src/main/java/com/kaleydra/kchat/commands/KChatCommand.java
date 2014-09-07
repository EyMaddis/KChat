package com.kaleydra.kchat.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.kaleydra.kchat.KChat;
import com.kaleydra.kchat.KChatConfig;

public class KChatCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("kchat.admin")) return false;
		
		if(args.length <= 0){
			sender.sendMessage(ChatColor.GREEN+KChat.instance.getName()+" version: "+KChat.instance.getDescription().getVersion());
			return true;
		}
		if(args[0].equalsIgnoreCase("reload")){
			KChat.instance.reloadConfig();
			KChatConfig.load();
			sender.sendMessage(ChatColor.GREEN+"KChat config reloaded!");
			return true;
		}
		
		return false;
	}

}
