package me.mythicalflame.netherreactor.utilities;

import net.kyori.adventure.key.Key;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public class KeyPersistentDataType implements PersistentDataType<byte[], Key>
{
    public static final KeyPersistentDataType INSTANCE = new KeyPersistentDataType();

    @Override
    @Nonnull
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    @Nonnull
    public Class<Key> getComplexType() {
        return Key.class;
    }

    @Override
    @Nonnull
    public byte[] toPrimitive(@Nonnull Key complex, @Nonnull PersistentDataAdapterContext context) {
        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES + (complex.namespace().length() + complex.value().length()) * Byte.BYTES);
        bb.putInt(complex.namespace().length());
        bb.put(complex.namespace().getBytes());
        bb.put(complex.value().getBytes());
        return bb.array();
    }

    @Override
    @Nonnull
    public Key fromPrimitive(@Nonnull byte[] primitive, @Nonnull PersistentDataAdapterContext context) {
        ByteBuffer bb = ByteBuffer.wrap(primitive);

        int firstLength = bb.getInt();
        byte[] firstBytes = new byte[firstLength];
        bb.get(firstBytes);
        String firstString = new String(firstBytes);

        int secondLength = bb.remaining();
        byte[] secondBytes = new byte[secondLength];
        bb.get(secondBytes);
        String secondString = new String(secondBytes);

        return Key.key(firstString, secondString);
    }
}
