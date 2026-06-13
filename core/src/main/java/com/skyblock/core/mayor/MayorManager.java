package com.skyblock.core.mayor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorManager {

    private static final List<String> DEFAULT_CANDIDATES = Arrays.asList(
            "Finnegan", "Derpy", "Diana", "Jerry", "Foxy", "Paul", "Scorpius"
    );

    private String currentMayor = null;
    private final Map<String, List<String>> mayorPerks = new HashMap<>();
    private final List<String> candidates = new ArrayList<>(DEFAULT_CANDIDATES);

    public String getCurrentMayor() {
        return currentMayor;
    }

    public void setCurrentMayor(String mayor) {
        this.currentMayor = mayor;
    }

    public List<String> getPerks(String mayor) {
        return mayorPerks.getOrDefault(mayor, List.of());
    }

    public void setPerks(String mayor, List<String> perks) {
        mayorPerks.put(mayor, new ArrayList<>(perks));
    }

    public List<String> getCandidates() {
        return candidates;
    }

    public void addCandidate(String candidate) {
        if (!candidates.contains(candidate)) candidates.add(candidate);
    }

    public boolean removeCandidate(String candidate) {
        return candidates.remove(candidate);
    }
}
