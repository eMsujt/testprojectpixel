package com.skyblock.core.manager;

import com.skyblock.core.manager.NetworthManager.Item;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworthManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(NetworthManager.getInstance(), NetworthManager.getInstance());
    }

    @Test
    void getBaseValue_ZeroForUnknownItem() {
        assertEquals(0.0, NetworthManager.getInstance().getBaseValue("nw_unknown_item"));
    }

    @Test
    void calculateValue_BaseTimesCount() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_sword", 1000);
        Item item = Item.builder("nw_sword").count(3).build();
        assertEquals(3000.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_AddsEnchantsReforgeAndStars() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_blade", 1000);
        mgr.registerEnchantValue("nw_sharpness", 50);   // *6 = 300
        mgr.registerReforgeValue("nw_fabled", 400);
        // stars: 1000 * 0.05 * 4 = 200
        Item item = Item.builder("nw_blade")
                .enchant("nw_sharpness", 6)
                .reforge("nw_fabled")
                .stars(4)
                .build();
        assertEquals(1900.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_SoulboundIsZero() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_soul_accessory", 5000);
        Item item = Item.builder("nw_soul_accessory").soulbound(true).build();
        assertEquals(0.0, mgr.calculateValue(item));
    }

    @Test
    void calculateValue_UnknownEnchantContributesNothing() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_bow", 800);
        Item item = Item.builder("nw_bow").enchant("nw_mystery", 5).build();
        assertEquals(800.0, mgr.calculateValue(item));
    }

    @Test
    void calculateTotal_SumsItemsAndSkipsSoulbound() {
        NetworthManager mgr = NetworthManager.getInstance();
        mgr.registerBaseValue("nw_a", 100);
        mgr.registerBaseValue("nw_b", 250);
        List<Item> items = List.of(
                Item.builder("nw_a").count(2).build(),   // 200
                Item.builder("nw_b").build(),            // 250
                Item.builder("nw_a").soulbound(true).build() // 0
        );
        assertEquals(450.0, mgr.calculateTotal(items));
    }

    @Test
    void registerBaseValue_RejectsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> NetworthManager.getInstance().registerBaseValue("nw_neg", -1));
    }

    @Test
    void builder_RejectsInvalidCountStarsAndLevel() {
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").count(0));
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").stars(-1));
        assertThrows(IllegalArgumentException.class, () -> Item.builder("x").enchant("e", 0));
    }
}
