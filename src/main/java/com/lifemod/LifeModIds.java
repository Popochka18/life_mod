package com.lifemod;

import net.minecraft.resources.Identifier;

/** Central helper for mod identifiers. */
public final class LifeModIds {
    public static final String MOD_ID = "life-mod";

    private LifeModIds() {
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
