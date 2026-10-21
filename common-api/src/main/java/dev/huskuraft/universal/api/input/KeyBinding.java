package dev.huskuraft.universal.api.input;

import dev.huskuraft.universal.api.platform.ClientContentFactory;
import dev.huskuraft.universal.api.platform.PlatformReference;

public interface KeyBinding extends PlatformReference {

    static KeyBinding of(String name, String category, int code) {
        return ClientContentFactory.getInstance().newKeyBinding(name, category, code);
    }

    String getName();

    String getCategory();

    Key getDefaultKey();

    Key getKey();

    boolean consumeClick();

    boolean isDown();

    default boolean isKeyDown() {
        return getKey().isDown();
    }

    // Sets a keyboard key and saves the game options. KeyCodes.KEY_UNKNOWN removes the binding.
    default void setKeyCode(int code) {
        throw new UnsupportedOperationException("Rebinding keys is not supported on this platform");
    }

    // Sets a key from getKey or getDefaultKey and saves the game options. The key can be a mouse button.
    default void setKey(Key key) {
        throw new UnsupportedOperationException("Rebinding keys is not supported on this platform");
    }

}
