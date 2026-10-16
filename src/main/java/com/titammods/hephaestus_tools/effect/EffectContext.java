package com.titammods.hephaestus_tools.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record EffectContext(Level level, Player player, ItemStack tool, @Nullable LivingEntity target,
                            @Nullable BlockState state, @Nullable BlockPos pos) {

    public static EffectContext holding(Player player, ItemStack tool) {
        return new EffectContext(player.level(), player, tool, null, null, null);
    }

    public static EffectContext mining(Player player, ItemStack tool, BlockState state, @Nullable BlockPos pos) {
        return new EffectContext(player.level(), player, tool, null, state, pos);
    }

    public static EffectContext attacking(Player player, ItemStack tool, LivingEntity target) {
        return new EffectContext(player.level(), player, tool, target, null, null);
    }

    public float wear() {
        int max = tool.getMaxDamage();
        return max <= 0 ? 0f : (float) tool.getDamageValue() / max;
    }
}
