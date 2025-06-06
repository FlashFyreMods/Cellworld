package com.flashfyre.cellworld.cells;

import com.flashfyre.cellworld.Cellworld;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public interface SelectorType<P extends CellSelector> {
    MapCodec<P> codec();

    static <P extends CellSelector> SelectorType<P> register(String name, MapCodec<P> codec) {
        return Cellworld.SELECTOR_TYPES.register(name, () -> codec.codec());
    }
}
