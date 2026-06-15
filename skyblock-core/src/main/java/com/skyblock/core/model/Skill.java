package com.skyblock.core.model;

/** Canonical enum for every SkyBlock skill. */
public enum Skill {
    FARMING      ("Farming",       "farming",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMzI0NDAxNDZhNTVhYWE4NGIzOWEyMzc3NjJkN2EzMGIzY2JhN2Y0Y2U1YjYxZTZlYWJlMDRjZDI0MzgifX19"),
    MINING       ("Mining",        "mining",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YzNWYzOGM4OWZlZWI3Y2JiZDM2NWNiMzIxNzEyYTZmMzllMmJhNmZhMzA2NmZhYTFjYjgxZWM4MDZmYjgifX19"),
    COMBAT       ("Combat",        "combat",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U1YzJiMDI4NGE5YzJiYzU5NTNjMzljNGZlYzY5MDllODg4MmMwMzNmNzU0MGI4OWIwNzE2ZThhMTlkMTYifX19"),
    FORAGING     ("Foraging",      "foraging",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzY3M2UyYWE5NTU1NjkwMzBiZGYxOGIxMGMzOWE3YWI4YjViYmUxNTljNmFhMzM2ZjM0YWI4NTQ0ZDgwZjYifX19"),
    FISHING      ("Fishing",       "fishing",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3YzZhYTZiNTMwZDc4OTgzN2QxNmYyMjViZjlhYjFmMjVkMmUwOTg5YjljMjFiMjk0ZTM2MDNiNTM2ZmUifX19"),
    ENCHANTING   ("Enchanting",    "enchanting",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwYjMxYWU4ZjQ0NjBjMmY1OGI0NmU5ZjE2NWFkNGU1ODc4MzhlZGY0ZWRmNGE4MTZhYTc5YjMwZDk3ZTcifX19"),
    ALCHEMY      ("Alchemy",       "alchemy",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0YzRlZDYzMTU0YzVkNTc2N2Q0OGZkMTVhZmI4YTg0YTYwYTZjNWYwMTY2Y2ZhNzZlMzQ5OTA2ZTJiMmIifX19"),
    TAMING       ("Taming",        "taming",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1OWFjMTZiYjdjOWU5NGY3N2M0ZDdlNzZmOWYxMDJhODBmNTBhNjlhNGRkNGM5M2ZjMzIzMjc4ZmE3ZWFkNyJ9fX0="),
    CARPENTRY    ("Carpentry",     "carpentry",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkMmMxYjJlNGI4NmQ1M2M0ZGZjMWVhNTI3Zjk3NDRhYjRmYjhhYTM4YTFmMGU1N2ZkZTQyMzJiNmZiYzEifX19"),
    RUNECRAFTING ("Runecrafting",  "runecrafting",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q1NzExMzQxZTZiYWE0ZDllZTU0NTI5ZWUyN2Q4MzFiMzc4NTI1ZDgxZTE2MGRlY2Q0NTZlNzFhZjgxN2UyMTkifX19"),
    SOCIAL       ("Social",        "social",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzBhMmNjMjU4ZmZlMDc1Nzk5YzllMzI1MDBiNzRiZGNhZGYzOGM1MzRlMzdlMzUzZGE3MjE5ZTFhOWFhYTEifX19"),
    DUNGEONEERING("Dungeoneering", "dungeoneering", null);

    public final String displayName;
    private final String skillKey;
    /** Base64 skull texture, or {@code null} if no head skin is defined. */
    public final String texture;

    Skill(String displayName, String skillKey, String texture) {
        this.displayName = displayName;
        this.skillKey = skillKey;
        this.texture = texture;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Lowercase storage key (e.g. {@code "farming"}). */
    public String key() {
        return skillKey;
    }
}
