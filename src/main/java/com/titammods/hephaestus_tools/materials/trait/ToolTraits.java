package com.titammods.hephaestus_tools.materials.trait;

import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.materials.MaterialManager;
import com.titammods.hephaestus_tools.materials.MaterialStats;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ToolTraits {

    public record TraitInstance(Identifier id, TraitDefinition definition, int level) {}

    private ToolTraits() {}

    public static List<TraitInstance> collect(List<MaterialId> materials) {
        List<TraitInstance> out = new ArrayList<>();
        if (materials == null) return out;
        Map<Identifier, Integer> levels = new LinkedHashMap<>();
        for (MaterialId material : materials) {
            for (Identifier trait : ofMaterial(material)) levels.merge(trait, 1, Integer::sum);
        }
        TraitManager traits = TraitManager.getInstance();
        levels.forEach((id, level) -> traits.get(id).ifPresent(definition ->
                out.add(new TraitInstance(id, definition, Math.min(level, definition.maxLevel())))));
        return out;
    }

    public static int fingerprint(List<MaterialId> materials) {
        int hash = 1;
        TraitManager traits = TraitManager.getInstance();
        for (TraitInstance trait : collect(materials)) {
            hash = 31 * hash + trait.id().hashCode();
            hash = 31 * hash + trait.level();
            hash = 31 * hash + traits.fingerprint(trait.id());
        }
        return hash;
    }

    public static List<Identifier> ofMaterial(MaterialId material) {
        MaterialStats head = MaterialManager.getInstance().getStatsForSlot(material, 0);
        if (head == null || !head.hasTrait()) return List.of();
        Optional<Identifier> id = TraitManager.resolve(head.traitId());
        if (id.isEmpty() || TraitManager.getInstance().get(id.get()).isEmpty()) return List.of();
        return List.of(id.get());
    }

    public static String nameKey(Identifier trait) {
        return trait.toLanguageKey("trait");
    }

    public static String descriptionKey(Identifier trait) {
        return trait.toLanguageKey("trait", "desc");
    }
}
