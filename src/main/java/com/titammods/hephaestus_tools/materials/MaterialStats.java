package com.titammods.hephaestus_tools.materials;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.materials.tier.BuiltInTiers;
import com.titammods.hephaestus_tools.materials.trait.TraitEntry;
import com.titammods.hephaestus_tools.tools.stat.HarvestTier;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public record MaterialStats(
        int durability,
        float miningSpeed,
        float attackDamage,
        float attackSpeed,
        Identifier tierId,
        int enchantability,
        float durabilityMult,
        float speedMult,
        float damageMult,
        float attackSpeedMult,
        String traitId,
        Optional<List<TraitEntry>> traits
) {
    public static final MaterialStats EMPTY = new MaterialStats(
            1, 1f, 1f, 1f, BuiltInTiers.WOOD, 0,
            0f, 0f, 0f, 0f, "", Optional.empty()
    );

    private static final Codec<Identifier> TIER_CODEC = Codec.either(Codec.INT, DefinitionId.CODEC).xmap(
            either -> either.map(BuiltInTiers::byPosition, id -> id),
            id -> {
                OptionalInt position = BuiltInTiers.position(id);
                return position.isPresent() ? Either.left(position.getAsInt()) : Either.right(id);
            });

    public static final Codec<MaterialStats> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("durability", 1).forGetter(MaterialStats::durability),
                    Codec.FLOAT.optionalFieldOf("mining_speed", 1f).forGetter(MaterialStats::miningSpeed),
                    Codec.FLOAT.optionalFieldOf("attack_damage", 1f).forGetter(MaterialStats::attackDamage),
                    Codec.FLOAT.optionalFieldOf("attack_speed", 1f).forGetter(MaterialStats::attackSpeed),
                    TIER_CODEC.optionalFieldOf("tier", BuiltInTiers.WOOD).forGetter(MaterialStats::tierId),
                    Codec.INT.optionalFieldOf("enchantability", 0).forGetter(MaterialStats::enchantability),
                    Codec.FLOAT.optionalFieldOf("durability_mult", 0f).forGetter(MaterialStats::durabilityMult),
                    Codec.FLOAT.optionalFieldOf("speed_mult", 0f).forGetter(MaterialStats::speedMult),
                    Codec.FLOAT.optionalFieldOf("damage_mult", 0f).forGetter(MaterialStats::damageMult),
                    Codec.FLOAT.optionalFieldOf("attack_speed_mult", 0f).forGetter(MaterialStats::attackSpeedMult),
                    Codec.STRING.optionalFieldOf("trait", "").forGetter(MaterialStats::traitId),
                    TraitEntry.CODEC.listOf().optionalFieldOf("traits").forGetter(MaterialStats::traits)
            ).apply(instance, MaterialStats::new)
    );

    private static final StreamCodec<ByteBuf, Optional<List<TraitEntry>>> TRAITS_STREAM_CODEC =
            ByteBufCodecs.optional(TraitEntry.STREAM_CODEC.apply(ByteBufCodecs.list()));

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialStats> STREAM_CODEC =
            StreamCodec.of(
                    (buf, s) -> {
                        buf.writeVarInt(s.durability);
                        buf.writeFloat(s.miningSpeed);
                        buf.writeFloat(s.attackDamage);
                        buf.writeFloat(s.attackSpeed);
                        buf.writeIdentifier(s.tierId);
                        buf.writeVarInt(s.enchantability);
                        buf.writeFloat(s.durabilityMult);
                        buf.writeFloat(s.speedMult);
                        buf.writeFloat(s.damageMult);
                        buf.writeFloat(s.attackSpeedMult);
                        buf.writeUtf(s.traitId != null ? s.traitId : "");
                        TRAITS_STREAM_CODEC.encode(buf, s.traits);
                    },
                    buf -> new MaterialStats(
                            buf.readVarInt(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readIdentifier(),
                            buf.readVarInt(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readFloat(),
                            buf.readUtf(),
                            TRAITS_STREAM_CODEC.decode(buf)
                    )
            );

    public HarvestTier tier() {
        return HarvestTier.of(tierId);
    }

    public boolean hasTrait() {
        return traitId != null && !traitId.isEmpty();
    }
}
