package com.lifemod.entity;

import net.minecraft.resources.Identifier;

/** Implemented by mobs whose renderer asks the entity for its texture (faction/profession/biome skins). */
public interface TexturedMob {
    Identifier getTexture();
}
