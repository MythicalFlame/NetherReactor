package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.core.registries.AbstractInternalInterface;
import net.minecraft.SharedConstants;

public class InternalInterface_v1_21_8 implements AbstractInternalInterface
{
    @Override
    public int getServerProtocolVersion()
    {
        return SharedConstants.getProtocolVersion();
    }
}
