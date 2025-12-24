package me.mythicalflame.netherreactor.utilities;

import net.kyori.adventure.key.Key;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KeyPersistentDataType implements PersistentDataType<byte[], Key>
{
    public static final KeyPersistentDataType INSTANCE = new KeyPersistentDataType();
    private static final Charset ASCII = StandardCharsets.US_ASCII;

    @Override
    @Nonnull
    public Class<byte[]> getPrimitiveType()
    {
        return byte[].class;
    }

    @Override
    @Nonnull
    public Class<Key> getComplexType()
    {
        return Key.class;
    }

    @Override
    @Nonnull
    public byte[] toPrimitive(@Nonnull Key complex, @Nonnull PersistentDataAdapterContext context)
    {
        byte[] namespaceBytes = complex.namespace().getBytes(ASCII);
        byte[] valueBytes = complex.value().getBytes(ASCII);
        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES + (namespaceBytes.length + valueBytes.length) * Byte.BYTES);
        bb.putInt(namespaceBytes.length);
        bb.put(namespaceBytes);
        bb.put(valueBytes);
        return bb.array();
    }

    @Override
    @Nonnull
    public Key fromPrimitive(@Nonnull byte[] primitive, @Nonnull PersistentDataAdapterContext context)
    {
        ByteBuffer bb = ByteBuffer.wrap(primitive);

        int firstLength = bb.getInt();
        byte[] firstBytes = new byte[firstLength];
        bb.get(firstBytes);
        String firstString = new String(firstBytes, ASCII);

        int secondLength = bb.remaining();
        byte[] secondBytes = new byte[secondLength];
        bb.get(secondBytes);
        String secondString = new String(secondBytes, ASCII);

        return Key.key(firstString, secondString);
    }
}
