package com.skyblock.core.chat;

/** Available chat channels players can switch between. */
public enum ChatChannel {
    GLOBAL,
    PARTY,
    GUILD,
    TRADE;

    /** Display name shown to players. */
    public String displayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
