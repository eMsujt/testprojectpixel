package com.skyblock.core.guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuildManager {

    private final Map<UUID, String> memberGuild = new HashMap<>();
    private final Map<String, List<UUID>> guildMembers = new HashMap<>();

    public void createGuild(String name, UUID owner) {
        if (guildMembers.containsKey(name)) throw new IllegalArgumentException("Guild already exists: " + name);
        guildMembers.put(name, new ArrayList<>(List.of(owner)));
        memberGuild.put(owner, name);
    }

    public void addMember(String guild, UUID player) {
        if (!guildMembers.containsKey(guild)) throw new IllegalArgumentException("Unknown guild: " + guild);
        guildMembers.get(guild).add(player);
        memberGuild.put(player, guild);
    }

    public boolean removeMember(UUID player) {
        String guild = memberGuild.remove(player);
        if (guild == null) return false;
        List<UUID> members = guildMembers.get(guild);
        if (members != null) members.remove(player);
        return true;
    }

    public String getGuild(UUID player) {
        return memberGuild.get(player);
    }

    public List<UUID> getMembers(String guild) {
        return guildMembers.getOrDefault(guild, List.of());
    }

    public boolean guildExists(String name) {
        return guildMembers.containsKey(name);
    }
}
