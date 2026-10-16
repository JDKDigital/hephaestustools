package com.titammods.hephaestus_tools.compat.jei;

import com.titammods.setup.ModRecipes;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

public final class CastingRecipeSync {

    private CastingRecipeSync() {}

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ModRecipes.CASTING_TABLE_TYPE.get());
    }
}
