package com.skyblock.core.npc;

/**
 * A Mojang skin texture/signature pair, as applied to a Citizens player NPC via
 * {@code SkinTrait.setSkinPersistent}. {@code texture} is the base64 textures
 * property value; {@code signature} is its base64 Yggdrasil signature (required
 * for the skin to render for other players in online mode).
 */
public record NpcSkin(String texture, String signature) {
}
