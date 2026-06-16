/**
 * Canonical home of the consolidated SkyBlock core managers.
 *
 * <p>Each manager below is the single authoritative singleton for its feature
 * area. Use these types directly; do not create parallel copies in other
 * packages.</p>
 *
 * <ul>
 *   <li>{@link com.skyblock.core.manager.AccessoryManager} — accessory rarities, magical power, and tuning points.</li>
 *   <li>{@link com.skyblock.core.manager.BankManager} — per-player bank balance and interest tiers.</li>
 *   <li>{@link com.skyblock.core.manager.BazaarManager} — bazaar order book: instant buy/sell and buy/sell orders.</li>
 *   <li>{@link com.skyblock.core.manager.CalendarManager} — in-game season cycle and recurring scheduled events.</li>
 *   <li>{@link com.skyblock.core.manager.CollectionManager} — per-item collection tiers and unlock progress.</li>
 *   <li>{@link com.skyblock.core.manager.DungeonManager} — dungeon runs and class progression.</li>
 *   <li>{@link com.skyblock.core.manager.EconomyManager} — coin balances (purse and bank) with deposit/withdraw.</li>
 *   <li>{@link com.skyblock.core.manager.EnchantmentManager} — per-player enchantment levels and the enchant table.</li>
 *   <li>{@link com.skyblock.core.manager.EventManager} — server-wide bonus events.</li>
 *   <li>{@link com.skyblock.core.manager.FishingManager} — fishing progression, sea-creature spawn pools, and loot rolls.</li>
 *   <li>{@link com.skyblock.core.manager.GardenManager} — garden plots, crop milestones, and farming contests.</li>
 *   <li>{@link com.skyblock.core.manager.IslandManager} — per-player island creation and management.</li>
 *   <li>{@link com.skyblock.core.manager.MiningManager} — mining progression and speed bonuses.</li>
 *   <li>{@link com.skyblock.core.manager.MinionManager} — placed minion management.</li>
 *   <li>{@link com.skyblock.core.manager.MuseumManager} — museum donation flow and per-category completion.</li>
 *   <li>{@link com.skyblock.core.manager.PartyManager} — party invites, membership, and leader transfer.</li>
 *   <li>{@link com.skyblock.core.manager.PetManager} — pet collections, active pets, and XP curves.</li>
 *   <li>{@link com.skyblock.core.manager.QuestManager} — quest definitions and objective tracking.</li>
 *   <li>{@link com.skyblock.core.manager.ReforgeManager} — item reforges and reforge stones.</li>
 *   <li>{@link com.skyblock.core.manager.SackManager} — per-item sack capacity tiers, auto-pickup storage, and Sack of Sacks aggregation.</li>
 *   <li>{@link com.skyblock.core.manager.ShopManager} — NPC shops with buy/sell pricing.</li>
 *   <li>{@link com.skyblock.core.manager.SkillManager} — per-player skill XP and levels.</li>
 *   <li>{@link com.skyblock.core.manager.SlayerManager} — slayer quests, boss spawning, and tier escalation.</li>
 * </ul>
 *
 * <p>Two related managers live in their own feature sub-packages:
 * {@link com.skyblock.core.auction.manager.AuctionHouseManager} (BIN listings and
 * ascending-auction bidding) and
 * {@link com.skyblock.core.crafting.manager.CraftingManager} (custom recipes and
 * crafting history).</p>
 *
 * <p>Managers in this package are not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
package com.skyblock.core.manager;
