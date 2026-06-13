package com.skyblock.core.title;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TitleManager {

    private final Map<UUID, List<String>> titles = new HashMap<>();

    public List<String> getTitles(UUID playerId) {
        return Collections.unmodifiableList(titles.getOrDefault(playerId, Collections.emptyList()));
    }

    public void addTitle(UUID playerId, String title) {
        titles.computeIfAbsent(playerId, k -> new ArrayList<>()).add(title);
    }

    public boolean removeTitle(UUID playerId, String title) {
        List<String> list = titles.get(playerId);
        return list != null && list.remove(title);
    }

    public boolean hasTitle(UUID playerId, String title) {
        List<String> list = titles.get(playerId);
        return list != null && list.contains(title);
    }
}
