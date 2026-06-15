package com.skyblock.plugin.minion;

import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.MinionMenu} instead.
 */
@Deprecated
public class MinionMenu implements InventoryHolder {

    private final com.skyblock.core.menu.MinionMenu delegate;

    public MinionMenu(Minion minion) {
        MinionType coreType = MinionType.valueOf(minion.type.name());
        MinionTier coreTier = MinionTier.valueOf(minion.getTier().name());
        this.delegate = new com.skyblock.core.menu.MinionMenu(
                new MinionData(minion.id, minion.owner, coreType, coreTier));
    }

    public void open(Player player) {
        delegate.open(player);
    }

    @Override
    public Inventory getInventory() {
        return delegate.getInventory();
    }
}
