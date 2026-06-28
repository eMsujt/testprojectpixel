package com.skyblock.core.npc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * Skin textures for the functional NPCs. The baked-in defaults are the <b>real
 * Hypixel hub NPC skins</b> — the exact {@code textures}/{@code signature} pairs
 * the live server uses — so the Citizens player NPCs render identically to
 * Hypixel's Banker, Auction Master, Bazaar, Curator, Blacksmith and Pet Sitter.
 *
 * <p>Operators can override any skin, or add one for the NPCs that have no
 * Hypixel equivalent (Wardrobe, Guide), by dropping a {@code npc_skins.yml} in
 * the plugin's data folder:</p>
 * <pre>
 * banker:
 *   texture: "&lt;base64 textures value&gt;"
 *   signature: "&lt;base64 signature&gt;"
 * </pre>
 * Extract the values in-game with SkyHanni's {@code /shcopyentities} (or any NBT
 * tool) against the live NPC, then paste them here.
 */
public final class NpcSkins {

    private static final String FILE_NAME = "npc_skins.yml";

    /** NPC type → its skin. Seeded with the real Hypixel skins; overridable via npc_skins.yml. */
    private static final Map<FunctionalNpc, NpcSkin> SKINS = new EnumMap<>(FunctionalNpc.class);

    static {
        SKINS.put(FunctionalNpc.BANKER, new NpcSkin(
                "eyJ0aW1lc3RhbXAiOjE1NTA2ODA4Mjg5MjMsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZjFlOGY5ZjVjZWE3YmFmNTZkOWUwNzkxMDU3YTdiMjNlNzkzNTZlNzY4M2VkM2Y0NzYwZWFhNmZjNWRjNGIxIn19fQ==",
                "SkhUNSUjtfjFXHhfKO/Wsr0KYV96DzBjBlnzHbyvzHrY/xtHypc6qM8KB2TDPhNGlT3gNdjAyruf3rRaIeXZ9mpN1WdidPL4nYmGIDZRyxdMoEFuK20vHCg95gdg5sjVQyJmYjLzjAtOqBeZHfHiax8jTmuZjUEq94WiSzO5TkPNDwT9yu2hF51U4kvJKNIsdTsn6Y9Kkefx+mVSpd7UcsggmJ6uTEoP9aR9DeUwvaRA++1Ee5UyCURVFdIkGZrN52Ch63fbk9Gfr1XLThm6TYnUaIGatfrklW42KCkKhTuBNUeApAHiTd4lAApQJdqwRSMU4Z/L4THz0Kp64aHWOzqeY4ieW7PWxAS1f9grNRmM4wwlAKQEoyYW6YPpOhYCvHyxh9KlIix4g36sPj1xinmFuPKJMWwFSfMUZNQ/6D6QCejZcoY88ZL2bT3Q70jAl0vIqeS72dtlTjO33alTnkUIpxL7VWnRQSMWH1Q/LpcnLUkXTeJw07gX7C6oOH7nqmL6PTTrV+I5bZdgBYi9PDVj75iUBpWviODVIfQBr/Mzsbvv9KoDOttFjnXVX1l526whTbwnPyewq4rokqAuD5WXx22Rx6wAzQ/Z4SSNyV6oNm9RZWrcYIyvYXoj7sSgb3UsA9Qn+bmAoBMax0e43+Hy8QAn+vyzlqVgYTYruZM="));

        SKINS.put(FunctionalNpc.AUCTION_MASTER, new NpcSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTU5NzMwNTI3MzM5MiwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE5NzFkNzk2ZGM0ZGM5YWYzOGNjOTcwNjc5NjNkZTRlZDJiZjk5OTIyNDI1NzQxNmRmYjg0MTQ4OGZkOWUwZCIKICAgIH0KICB9Cn0=",
                "IHbiM8PpwXBFKB/x6Ue2msaxAg5uuAn7/4U8D1WIWFOy6vlTz7//aBAunwOpIAHwjI6wS+wMP+awFfcAQtL3CNnaQ6WWaUhPi+Vm2yfNDl7xOXxLKYy/soIBlPEHNteyaV7KEa22zG0a8H7cZ8UKBAdNvzWSMGeZmabBdDpboQAn3kuznmaqJh1Kij6HOvDo4fR5h87ihHy76+ljzGi62vl8ejKi37lwu2pOV+NmhEY37KSZbAtIN4s/UiYCqrwqJ8yP3lMMO7+iIjk8uyT5DJVgoc23bsw+sdDNJzNZ9OZNLvMhy/QvdE4UldIxY6Ikw2ZjP6k1Wb+oBgGDW25bAusvUKf2liPgvJtbcS2TpSanegSzreLfR9XThY9L1SHjja9CQbGeoRD4kmS6Vqi/oFZKDKhuGkWHgyJTcWm2+BGFrC183+ZfMt9JTu4g7GJfSJwL/5PrFpzBm2rbLNMmZP/zq5o0YZUSD0izdffVFoyaQ58oueE3DvZ1rnLuiuhBxGd+Ptc1xKM/sSmcdXIeAn+POCJvK3zb3I7adRCFAy432LzqRnLnLGzuufqvuyn506DdEOEgRaq4yc0VDR1IgmMAgdO/zE/pNdoR/p8LrVFRO5WQmxcXfCNwP888YbRt6t5a7/ExdSN39VYhtovnkPfO+SEsAVofw3wfBO0/FvE="));

        SKINS.put(FunctionalNpc.BAZAAR, new NpcSkin(
                "eyJ0aW1lc3RhbXAiOjE1NzMyMjM2NDc4NDcsInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyMzJlMzgyMDg5NzQyOTE1NzYxOWIwZWUwOTlmZWMwNjI4ZjYwMmZmZjEyYjY5NWRlNTRhZWYxMWQ5MjNhZDcifX19",
                "FDFrRosEo3GRN1QUX2XDIxxB9cWznwE1Nt6zoDCUL9Ya0sudiOMey3r0wL+qgKNItbDgeflDpTwlpA1JBWbfQWWVCRRQhsN6HWPAyTqFMXyy8skaR8UMgr6My8Xz6kcWIfv3g6toUe1sowoKDBXt9z3hn4j6qiARxMOb1nSSy1Cp19di4rYOIFa7Ibu5DNNKAo0bafPYA3Mexy1DYpkJ9FFO6wyW/3U30jPCTnbysZp6XJN0scnXQcoLeBw5wy0V/NI/C7TNJKhr7YWlZKqVKW8r1kyrGgkTvC1u1AWBj3PFV3KuIlhX+G7VUD8iCvz8hvwJVRJBPlsMT6CQ5sP0eCHs38YoN9kiHtO+gHElHzp0JctQXX/7eYXV1FCMGJ8ov+u9f9V/Xu9HEdjCxwdjrRS7I/FSy5/GuBOHY+G2YIVKzMsCTkOM+F52WWF+O6/mGTo6NAdgvJb0Wvvif6/edHbUucOp2OtH67XGD61p/ktg/DmHNoXvjDCD0ld1HLO24fZrdm/cuC85/VYrEb6m9NvFZZVIoLbjbwSFuZD7AyGvHiFVdBWa9Ps3IpxiKi8lroyW8D4VLEQteN/BoB2DHTvu+jEMFJK4W+X7MG0pPAQz5F+1JAaWufR6ZH6Jrx/r4+1gjZlWzV6tmv4OXQHtDnaY0HCRvB+srNfQ/c1UZt8="));

        SKINS.put(FunctionalNpc.MUSEUM, new NpcSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYzNTE2OTQwMTIwOSwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZmMjJiMDM0ZDM3MjFkMWZhZDcwODI4ZTM3NTkyNWJhNDFiNWY5NTI4NzNjZDlmYzZkYmJlMWU4MGJlN2JlYjUiCiAgICB9CiAgfQp9",
                "JQaG6dLgLgiEl6c/86bGPlXmXe8Lr2/NV1hPJOVGCHh037TQlcl9OyqOwhn2ofL4ITLS/HTnaX6GiIbDxVaeYPzD+M5hZDTZtgP10QmKLvQirhnRZ84YvyP+eOm7XB5BZj6IfE4yGLAO7ag85zjvUDpOivrna6KPrCMBodWsyafJ4dk8GLf3okyaVTV9y8O7/BwG8R2x2HSZ5nJ9/sFYYynRnx6tDpTN20OLdyZ9wlWOrahoFEQotgfMVhHNm85Ds2EU/Iyo8ngjPgl7shVtRqonD4qDNbS/i9ICNFzKTDqWGW82Wjmi0b5Hd7AG6Pzmlc0lXBwG5wIB53xJjHX5zpucj8rYdTQdV3F+a4uYyWVw0KLmxpapFTVtWhWdOUzK1Le8dKW8dvi8MnP4m0hn30Iad9BqMmg/ko1aGZhkvUv4IhjWSaYvSWMx16rJj8CZZzhjdLCRgyb1XPAZaHr26gZzgSiHnivb97gQIH4ML3MOjtbCGaLTirADSUovALDrlHB8RofFG3mQVzoTF7LSJIXKgpkRi000AJIabrBkW36SiJySTPpaEG/Flme/886bjtU0A31S2vjM19CjgXsXlvPRRbmS94pyLfoLYc23VlmZoaqrAdFDdMkXQYZ354VQeYJ0o82LCPNRoreLiZPa7AQtPrhh+tQVlz4olVqJB10="));

        SKINS.put(FunctionalNpc.BLACKSMITH, new NpcSkin(
                "eyJ0aW1lc3RhbXAiOjE1NTA2Nzg0NTQ3MTYsInByb2ZpbGVJZCI6ImEyZjgzNDU5NWM4OTRhMjdhZGQzMDQ5NzE2Y2E5MTBjIiwicHJvZmlsZU5hbWUiOiJiUHVuY2giLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FmM2Y3YTY5YzlhN2FkOTVlOTBmMjEyZGEzMDQ5OTBjOGJlMzZlOTMxOTdkYjg3NGMyYjBmZTA4MTA2ZTMyM2IifX19",
                "Sw5WHUwuYX2OxF/U1BHPpFdAJzwzzyzZ78bxQkdCQL1Jyd9RsIiQWKJSeTHsnhScpHxXEBtWKQ71YOci6HzwLiYZOh/M6KJ3kY+RtlRdh8EUEVj1BCoo8xvwAt28Piiqke3uDs9dWEvVBzs1PR9qxvMaarw3DT8sTgxIBU/xMmt41uDiCOgn6M/rL3waFsnPdN+v2tTHsl1aNz+hMLn4NuIbBprE90X0tsT/qlQzCBPHuwUV8elGb9xAfjJ0eRSlH1jxcfEJdb+YsPKwfqQ6btwkCOK2hdfm9/S6/Fd2KJsaDhH5twfykFyD1PEn4sCAlAisibKiOADQcg6lYN7d2eE95RVYhlqHTsb6g0aBUSk8Gj8OOABbBB0sEhGPmzTKJOAoyu9ZWgfzAty/ipGooyL/gWlGByTmWnmXf0ek6TUtkPpmLNd3Ik364+GDI+C8H14Dltc7axQXh0GsFmepUL2t/fz0fDtZlqfhwz3ei7q7fJ8S20l2O4y/GKDCItqMTkY80rOEQuysln13pV32z/8oZwl7a1rGSvSjLQwC+cpbhA8tATTVu/ovUd5Aev+PUb5vqrh4DmF4Br5cRxLCDHj6Q2CG5glXVTULhbmMLQE0YLVFvnik2xGNnlKIJ9GEdsat+PBHR7ToHasGr4JiS7n2uQykxTntgpqB9QNS4fI="));

        SKINS.put(FunctionalNpc.PET_SITTER, new NpcSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTU4ODcyMTU5ODM4OCwKICAicHJvZmlsZUlkIiA6ICI5ZThiODc0N2EzMTc0NGU2OGE0ODcxMzM0MDNiNGQzNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFdmlsRGN0ciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYWFlODEyNzc1NzA2YjU2NTk2NjgzNDg2YWJlYWY4NTdlYTFjMDY4Y2IzNGE4YmMxZGU5YTU3YzYzOGNmNDExIgogICAgfQogIH0KfQ==",
                "vPok0YXlbGxwdgMYI8WExOL1dfYlF47ISzPRMUr5g9nvtiHdTktB45gT7hTB45VwsAScDIgKOHl2fo/jlLCW01xtsHlczuvjurLvtyrQj8ipjfFr/RZTWUF1ZdXM2C5fRWa0etz4UQeQwcmKOnD+NUOKux3GJfhN7el7xYI2fp6nRDCqIcyAW/MIL7UhqdoRCUDIFgymYBQ04AVDIZ59nZjaqdBS+5oUQ8AkJkcZSThx0CpCdj24/xeyNlTgx9I0jwvIrf9AP+cc8hxrEIVhy9sMH9lHmIhbsdiGYlgMBFjfvdp9KOrFyWSjUyeqHstNXZ8C5v2ieWDy4u+FYtG485vylOQPo5CRitRdNBBbiMsL5rroVumLKroh0gyZGLlYHYAq0CmWST6wbizPpcE3tAAvYdiPCEMHpTYWiPAuKckBeSWaVL3t1MZuQoRM5aV8QeDGoe6QhYBsXVxrPDxRX05nZ5sDII8Uht6r8u0uk21ejvswnSGXH9Rpjoy85YKTDLtSzXYmzk/YmpfPUWB9YnXeARCJYO+kNN6QciDFTt+e5XtSKz4o9ejbvWu3Y74CXnWYcEs6Vqu/zztYfq47roCdTP/TVGD311pRxZvjS5PaQdEbrkLCFblOhGOcxg0IFbs/zlz8bemBdQZyJIzR3IwgIH1tcv4GEMs20rTrP9k="));
        // WARDROBE and GUIDE have no canonical Hypixel NPC — they keep the default
        // Citizens player model unless an operator sets them in npc_skins.yml.
    }

    private NpcSkins() {
    }

    /** Returns the configured skin for {@code type}, or {@code null} if none. */
    public static NpcSkin get(FunctionalNpc type) {
        return SKINS.get(type);
    }

    /**
     * Applies operator overrides from {@code npc_skins.yml} (each entry keyed by
     * NPC id with {@code texture} + {@code signature}). Missing/blank entries
     * leave the baked-in default in place. Safe to call when the file is absent.
     */
    public static void load(File dataFolder) {
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getKeys(false)) {
            FunctionalNpc npc = FunctionalNpc.byId(key);
            ConfigurationSection sec = cfg.getConfigurationSection(key);
            if (npc == null || sec == null) {
                continue;
            }
            String texture = sec.getString("texture");
            String signature = sec.getString("signature");
            if (texture != null && !texture.isBlank() && signature != null && !signature.isBlank()) {
                SKINS.put(npc, new NpcSkin(texture.trim(), signature.trim()));
                Bukkit.getLogger().info("[SkyBlock] Loaded custom skin for NPC '" + npc.id + "'.");
            }
        }
    }
}
