package com.titammods.hephaestus_tools.table;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;

public record ToolUpgrade(Identifier id, String nameKey, Set<ToolRole> roles,
                          int maxLevel, Item costItem, int baseCost, Identifier icon) {

    public int costFor(int targetLevel) {
        int lvl = Math.max(1, targetLevel);
        return baseCost * (1 << (lvl - 1));
    }

    public List<ItemStack> extraCosts(int targetLevel) {
        if (maxLevel < 10) return List.of();
        return switch (targetLevel) {
            case 6 -> List.of(new ItemStack(Items.NETHER_STAR, 5));
            case 7 -> List.of(new ItemStack(Items.TORCHFLOWER));
            case 8 -> List.of(new ItemStack(Items.DRIED_GHAST));
            case 9 -> List.of(new ItemStack(Items.PALE_OAK_LOG, 64));
            case 10 -> List.of(new ItemStack(Items.MACE), new ItemStack(Items.DRAGON_EGG));
            default -> List.of();
        };
    }
}