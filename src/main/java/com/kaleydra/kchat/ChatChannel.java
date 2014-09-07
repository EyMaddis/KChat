package com.kaleydra.kchat;

public enum ChatChannel {
	GLOBAL,
	TOWN;

	public String getPrefix(){
		return KChatConfig.getChannelPrefixes().get(this.name());
	}
}
