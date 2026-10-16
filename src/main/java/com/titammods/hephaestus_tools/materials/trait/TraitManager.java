package com.titammods.hephaestus_tools.materials.trait;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class TraitManager {

    private static final TraitManager INSTANCE = new TraitManager();

    private final Map<Identifier, TraitDefinition> traits = new LinkedHashMap<>(BuiltInTraits.definitions());

    private TraitManager() {}

    public static TraitManager getInstance() {
        return INSTANCE;
    }

    public Optional<TraitDefinition> get(Identifier id) {
        return Optional.ofNullable(traits.get(id));
    }

    public Map<Identifier, TraitDefinition> all() {
        return Collections.unmodifiableMap(traits);
    }

    public static Optional<Identifier> resolve(String raw) {
        if (raw == null || raw.isEmpty()) return Optional.empty();
        if (raw.indexOf(':') < 0) return Optional.ofNullable(Identifier.tryBuild(HephaestusTools.MOD_ID, raw));
        return Optional.ofNullable(Identifier.tryParse(raw));
    }
}
