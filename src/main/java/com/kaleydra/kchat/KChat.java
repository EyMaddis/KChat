package com.kaleydra.kchat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.ess3.api.IEssentials;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.kaleydra.kchat.commands.ChannelCommand;
import com.kaleydra.kchat.commands.KChatCommand;
import com.kaleydra.kchat.listeners.AsyncListener;
import com.kaleydra.kchat.listeners.SyncListener;
import com.palmergames.bukkit.towny.Towny;

public class KChat extends JavaPlugin {

	public static KChat instance;
	public static Map<String, PlayerChatData> playerData = new ConcurrentHashMap<String, PlayerChatData>();
	public static Permission vaultPermissions = null;
	public static IEssentials essentials;
	public static Chat vaultChat;
	
	public static Towny towny;
	
	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(new AsyncListener(this), this);
		getServer().getPluginManager().registerEvents(new SyncListener(this), this);
		setupPermissions();
		setupChat();
		
		towny = (Towny) getServer().getPluginManager().getPlugin("Towny");
		essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
		
		saveDefaultConfig();
		KChatConfig.load();
		
		
		getCommand("townchat").setExecutor(new ChannelCommand(this, ChatChannel.TOWN, ChatChannel.GLOBAL));
		getCommand("globalchat").setExecutor(new ChannelCommand(this, ChatChannel.GLOBAL, ChatChannel.TOWN));
		getCommand("kchat").setExecutor(new KChatCommand());
	}

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            vaultPermissions = permissionProvider.getProvider();
        }
        return (vaultPermissions != null);
    }
    

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
        return vaultChat != null;
    }
    
}
