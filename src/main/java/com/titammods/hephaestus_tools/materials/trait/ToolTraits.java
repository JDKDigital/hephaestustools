package com.titammods.hephaestus_tools.materials.trait;

import com.titammods.hephaestus_tools.materials.Material;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.materials.MaterialManager;
import com.titammods.hephaestus_tools.materials.MaterialStats;
import com.titammods.hephaestus_tools.tools.helper.ToolBuildHandler;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ToolTraits {

    public record TraitInstance(Identifier id, TraitDefinition definition, int level) {}

    private ToolTraits() {}

    public static List<TraitInstance> collect(ItemStack tool) {
        return collect(tool.getItem(), ToolStack.getMaterials(tool));
    }

    public static List<TraitInstance> collect(Item tool, List<MaterialId> materials) {
        List<TraitInstance> out = new ArrayList<>();
        if (materials == null) return out;
        List<Item> layoutParts = ToolBuildHandler.getToolParts(tool);
        Map<Identifier, Integer> levels = new LinkedHashMap<>();
        for (int i = 0; i < materials.size(); i++) {
            for (TraitEntry trait : ofSlot(materials.get(i), ToolBuildHandler.partRole(layoutParts, i))) {
                levels.merge(trait.id(), trait.level(), Integer::sum);
            }
        }
        TraitManager traits = TraitManager.getInstance();
        levels.forEach((id, level) -> traits.get(id).ifPresent(definition ->
                out.add(new TraitInstance(id, definition, Math.min(level, definition.maxLevel())))));
        return out;
    }

    public static int fingerprint(Item tool, List<MaterialId> materials) {
        int hash = 1;
        TraitManager traits = TraitManager.getInstance();
        for (TraitInstance trait : collect(tool, materials)) {
            hash = 31 * hash + trait.id().hashCode();
            hash = 31 * hash + trait.level();
            hash = 31 * hash + traits.fingerprint(trait.id());
        }
        return hash;
    }

    public static List<TraitEntry> ofSlot(MaterialId material, int role) {
        Material mat = MaterialManager.getInstance().getMaterial(material);
        if (mat == null) return List.of();
        List<TraitEntry> head = declared(mat.headStats()).orElse(List.of());
        return switch (role) {
            case 0 -> head;
            case 2 -> declared(mat.bindingStats()).orElse(head);
            default -> declared(mat.handleStats()).orElse(head);
        };
    }

    private static Optional<List<TraitEntry>> declared(MaterialStats stats) {
        if (stats.traits().isPresent()) return stats.traits();
        if (!stats.hasTrait()) return Optional.empty();
        return Optional.of(TraitManager.resolve(stats.traitId())
                .map(id -> List.of(new TraitEntry(id, 1)))
                .orElse(List.of()));
    }

    public static List<Identifier> ofMaterial(MaterialId material) {
        TraitManager traits = TraitManager.getInstance();
        List<Identifier> out = new ArrayList<>();
        for (TraitEntry trait : ofSlot(material, 0)) {
            if (traits.get(trait.id()).isPresent() && !out.contains(trait.id())) out.add(trait.id());
        }
        return out;
    }

    public static String nameKey(Identifier trait) {
        return trait.toLanguageKey("trait");
    }

    public static String descriptionKey(Identifier trait) {
        return trait.toLanguageKey("trait", "desc");
    }
}
