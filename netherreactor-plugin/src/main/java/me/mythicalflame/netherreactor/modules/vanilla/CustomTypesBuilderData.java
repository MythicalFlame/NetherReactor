package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.mappings.TypesBuilderData;

public class CustomTypesBuilderData extends TypesBuilderData
{
    private final int id;

    public CustomTypesBuilderData(ResourceLocation name, int id)
    {
        super(name, new int[]{});
        this.id = id;
    }

    @Override
    public int getId(ClientVersion version)
    {
        return this.id;
    }
}
