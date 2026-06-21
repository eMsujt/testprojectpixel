package com.skyblock.core.ability;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An item ability parsed straight from real Hypixel lore — e.g.
 * {@code §6Ability: Instant Transmission  §e§lRIGHT CLICK} with a following
 * {@code §8Mana Cost: §350} line. Lets 1:1 items drive their own abilities without a
 * hardcoded per-item registry.
 */
public final class LoreAbility {

    /** How the ability is triggered. */
    public enum Trigger { RIGHT_CLICK, SNEAK_RIGHT_CLICK }

    private static final Pattern ABILITY =
            Pattern.compile("^Ability: (.+?)\\s+(SNEAK RIGHT CLICK|RIGHT CLICK)$");
    private static final Pattern MANA = Pattern.compile("Mana Cost: ([0-9,]+)");
    private static final Pattern BLOCKS = Pattern.compile("([0-9]+) blocks");

    public final String name;
    public final Trigger trigger;
    public final int manaCost;
    /** A numeric magnitude pulled from the description (e.g. teleport blocks); 0 if none. */
    public final int magnitude;

    public LoreAbility(String name, Trigger trigger, int manaCost, int magnitude) {
        this.name = name;
        this.trigger = trigger;
        this.manaCost = manaCost;
        this.magnitude = magnitude;
    }

    /** Parses every right-click ability declared in an item's lore, in order. */
    public static List<LoreAbility> parse(List<String> lore) {
        List<LoreAbility> out = new ArrayList<>();
        if (lore == null) return out;

        String name = null;
        Trigger trigger = null;
        int mana = 0;
        int magnitude = 0;

        for (String raw : lore) {
            if (raw == null) continue;
            String line = ChatColor.stripColor(raw).trim();

            Matcher a = ABILITY.matcher(line);
            if (a.matches()) {
                if (name != null) out.add(new LoreAbility(name, trigger, mana, magnitude));
                name = a.group(1).trim();
                trigger = a.group(2).equals("SNEAK RIGHT CLICK")
                        ? Trigger.SNEAK_RIGHT_CLICK : Trigger.RIGHT_CLICK;
                mana = 0;
                magnitude = 0;
                continue;
            }
            if (name == null) continue;

            Matcher mc = MANA.matcher(line);
            if (mc.find()) {
                try {
                    mana = Integer.parseInt(mc.group(1).replace(",", ""));
                } catch (NumberFormatException ignored) {
                }
            }
            Matcher bl = BLOCKS.matcher(line);
            if (bl.find()) {
                try {
                    magnitude = Integer.parseInt(bl.group(1));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (name != null) out.add(new LoreAbility(name, trigger, mana, magnitude));
        return out;
    }
}
