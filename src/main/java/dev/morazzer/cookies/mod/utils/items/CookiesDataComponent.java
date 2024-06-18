package dev.morazzer.cookies.mod.utils.items;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom data component that has no implementation.
 *
 * @param <Type> The type of the component.
 */
public record CookiesDataComponent<Type>(Identifier identifier) implements ComponentType<Type> {

    @Override
    public @NotNull Codec<Type> getCodec() {
        return FakeCodec.get();
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, Type> getPacketCodec() {
        return FakePacketCodec.get();
    }

    /**
     * A fake codec to have type safety while not implementing the real encoding/decoding.
     * @param <B> The type.
     * @param <V> The type.
     */
    public static class FakePacketCodec<B, V> implements PacketCodec<B, V> {
        @Override
        public V decode(B buf) {
            return null;
        }

        @Override
        public void encode(B buf, V value) {

        }

        public static <B, V> FakePacketCodec<B, V> get() {
            return new FakePacketCodec<>();
        }
    }

    /**
     * A fake codec to have type safety while not implementing the real encoding/decoding.
     * @param <T> The type.
     */
    public static class FakeCodec<T> implements Codec<T> {
        @Override
        public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
            return null;
        }

        @Override
        public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
            return null;
        }

        public static <T> FakeCodec<T> get() {
            return new FakeCodec<>();
        }
    }
}
