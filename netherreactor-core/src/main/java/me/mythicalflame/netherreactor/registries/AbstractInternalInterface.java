package me.mythicalflame.netherreactor.registries;

//For miscellaneous stuff
public interface AbstractInternalInterface
{
    int getServerProtocolVersion();
    void initRegistries();
    void nullRegistries() throws NoSuchFieldException, IllegalAccessException;
}
