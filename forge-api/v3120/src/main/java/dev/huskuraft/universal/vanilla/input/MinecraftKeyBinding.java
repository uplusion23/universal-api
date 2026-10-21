package dev.huskuraft.universal.vanilla.input;

import com.mojang.blaze3d.platform.InputConstants;

import dev.huskuraft.universal.api.input.Key;
import dev.huskuraft.universal.api.input.KeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public record MinecraftKeyBinding(
        KeyMapping refs
) implements KeyBinding {

    @Override
    public String getName() {
        return refs.getName();
    }

    @Override
    public String getCategory() {
        return refs.getCategory();
    }

    @Override
    public Key getDefaultKey() {
        return new MinecraftKey(refs.getDefaultKey());
    }

    @Override
    public Key getKey() {
        return new MinecraftKey(refs.key);
    }

    @Override
    public boolean consumeClick() {
        return refs.consumeClick();
    }

    @Override
    public boolean isDown() {
        return refs.isDown();
    }

    @Override
    public void setKeyCode(int code) {
        setKey(InputConstants.Type.KEYSYM.getOrCreate(code));
    }

    @Override
    public void setKey(Key key) {
        setKey((InputConstants.Key) key.reference());
    }

    private void setKey(InputConstants.Key key) {
        refs.setKey(key);
        KeyMapping.resetMapping();
        Minecraft.getInstance().options.save();
    }

}
