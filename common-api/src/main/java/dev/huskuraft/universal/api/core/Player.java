package dev.huskuraft.universal.api.core;

import dev.huskuraft.universal.api.text.Text;

public interface Player extends Entity {

    PlayerProfile getProfile();

    Inventory getInventory();

    default boolean isOperator() {
        return getServer().isOperator(getProfile());
    }

    default boolean isSinglePlayerOwner() {
        return getServer().isSinglePlayerOwner(getProfile());
    }

    default ItemStack getItemStack(InteractionHand hand) {
        return switch (hand) {
            case MAIN -> getItemBySlot(EquipmentSlot.MAINHAND);
            case OFF -> getItemBySlot(EquipmentSlot.OFFHAND);
        };
    }

    default void setItemStack(InteractionHand hand, ItemStack itemStack) {
        switch (hand) {
            case MAIN -> getInventory().setSelectedItem(itemStack);
            case OFF -> getInventory().setOffhandItem(itemStack);
        }
    }

    default ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> getInventory().getSelectedItem();
            case OFFHAND -> getInventory().getOffhandItem();
            case FEET, LEGS, CHEST, HEAD -> getInventory().getArmorItems().get(slot.getIndex());
        };
    }

    void drop(ItemStack itemStack, boolean dropAround, boolean includeThrowerName);

    void sendMessage(Text message, boolean actionBar);

    default void sendMessage(String message, boolean actionBar) {
        sendMessage(Text.text(message), actionBar);
    }

    default void sendMessage(Text message) {
        sendMessage(message, false);
    }

    default void sendMessage(String message) {
        sendMessage(Text.text(message), false);
    }

    void swing(InteractionHand hand);

    void awardStat(Stat<?> stat, int increment);

    default void awardStat(Stat<?> stat) {
        awardStat(stat, 1);
    }

    void resetStat(Stat<?> stat);

    // Uses the permission mod if the server has one. If not, uses the operator level.
    default boolean hasPermission(String permission, int defaultPermissionLevel) {
        throw new UnsupportedOperationException("Permissions are not supported on this platform");
    }

    default float getHealth() {
        throw new UnsupportedOperationException("Health is not supported on this platform");
    }

    default float getAbsorptionAmount() {
        throw new UnsupportedOperationException("Absorption is not supported on this platform");
    }

    default boolean mayFly() {
        throw new UnsupportedOperationException("Flight is not supported on this platform");
    }

    default boolean isFlying() {
        throw new UnsupportedOperationException("Flight is not supported on this platform");
    }

    default float getFlyingSpeed() {
        throw new UnsupportedOperationException("Flight is not supported on this platform");
    }

    // Use on the server. The server sends the change to the client of the player.
    default void setFlight(boolean mayFly, boolean flying, float flyingSpeed) {
        throw new UnsupportedOperationException("Flight is not supported on this platform");
    }

    // Returns the respawn bed of the player, or null if there is no bed in the current world.
    // If loadChunk is false, a bed in an unloaded chunk is not examined and counts as present.
    default BlockPosition getRespawnBedPosition(boolean loadChunk) {
        throw new UnsupportedOperationException("Respawn beds are not supported on this platform");
    }

}
