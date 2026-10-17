package com.titammods.hephaestus_tools.materials.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record TraitEntry(Identifier id, int level) {

    private static final Codec<Identifier> ID_CODEC = Codec.STRING.comapFlatMap(
            raw -> TraitManager.resolve(raw)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Not a valid trait id: " + raw)),
            Identifier::toString);

    public static final Codec<TraitEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ID_CODEC.fieldOf("id").forGetter(TraitEntry::id),
                    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("level", 1).forGetter(TraitEntry::level)
            ).apply(instance, TraitEntry::new));

    public static final StreamCodec<ByteBuf, TraitEntry> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TraitEntry::id,
            ByteBufCodecs.VAR_INT, TraitEntry::level,
            TraitEntry::new
    );
}
