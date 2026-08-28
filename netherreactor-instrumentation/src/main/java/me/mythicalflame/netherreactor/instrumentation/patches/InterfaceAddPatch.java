package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;

import java.io.IOException;

public class InterfaceAddPatch extends Patch
{
    private final String INTERFACE_NAME;
    private final MethodAddPatch[] SUBPATCHES;

    public InterfaceAddPatch(String className, String interfaceName, MethodAddPatch[] subpatches)
    {
        super(className);
        this.INTERFACE_NAME = interfaceName;
        this.SUBPATCHES = subpatches;
    }

    @Override
    public void apply(CtClass cc) throws NotFoundException, IOException, CannotCompileException
    {
        CtClass inter = Patcher.getCtClass(INTERFACE_NAME);
        cc.addInterface(inter);
        for (MethodAddPatch subpatch : SUBPATCHES)
        {
            subpatch.apply(cc);
        }
    }
}
