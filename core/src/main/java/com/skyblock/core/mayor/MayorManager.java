package com.skyblock.core.mayor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorManager {

    private static final List<String> DEFAULT_CANDIDATES = Arrays.asList(
            "Jerry", "Aatrox", "Diana", "Finnegan", "Cole", "Foxy", "Marina", "Paul", "Scorpius"
    );

    private static final Map<String, List<String>> DEFAULT_PERKS;

    static {
        Map<String, List<String>> perks = new HashMap<>();
        perks.put("Jerry", Collections.unmodifiableList(Arrays.asList("Jerrypocalypse", "Jerry's Gifts", "Gift Hunt")));
        perks.put("Aatrox", Collections.unmodifiableList(Arrays.asList("Slayer XP Buff", "Slayer Quest Limit", "Slayer's Will", "Blood Thirst")));
        perks.put("Diana", Collections.unmodifiableList(Arrays.asList("Great Spook", "Mythological Ritual", "Lucky!")));
        perks.put("Finnegan", Collections.unmodifiableList(Arrays.asList("Cultivation", "Shining Armor", "Stead Fast", "Blooming Business")));
        perks.put("Cole", Collections.unmodifiableList(Arrays.asList("Prospection", "Mining Fiesta", "Molten Forge")));
        perks.put("Foxy", Collections.unmodifiableList(Arrays.asList("What the Dog Doin?", "Extra Pets", "Good Doggy")));
        perks.put("Marina", Collections.unmodifiableList(Arrays.asList("Fishing Festival", "Luck of the Sea", "Quiver", "Water Breathing")));
        perks.put("Paul", Collections.unmodifiableList(Arrays.asList("Marauder", "Goblin Raid", "Supply Drop", "Show Off")));
        perks.put("Scorpius", Collections.unmodifiableList(Arrays.asList("Bribe", "Scorched", "Plague")));
        DEFAULT_PERKS = Collections.unmodifiableMap(perks);
    }

    private String currentMayor = null;
    private final Map<String, List<String>> mayorPerks = new HashMap<>(DEFAULT_PERKS);
    private final List<String> candidates = new ArrayList<>(DEFAULT_CANDIDATES);
    private final List<String> electionHistory = new ArrayList<>();

    public String getCurrentMayor() {
        return currentMayor;
    }

    public void setCurrentMayor(String mayor) {
        this.currentMayor = mayor;
        if (mayor == null) {
            recordElectionEvent("Election ended: mayor cleared");
        } else {
            recordElectionEvent("Mayor elected: " + mayor);
        }
    }

    public List<String> getPerks(String mayor) {
        return mayorPerks.getOrDefault(mayor, Collections.emptyList());
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

    public void recordElectionEvent(String summary) {
        electionHistory.add(summary);
    }

    public List<String> getElectionHistory() {
        return Collections.unmodifiableList(electionHistory);
    }
}
