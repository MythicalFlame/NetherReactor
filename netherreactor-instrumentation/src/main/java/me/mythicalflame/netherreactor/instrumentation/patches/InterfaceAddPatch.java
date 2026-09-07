package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Represents a patch that implements an interface.
 */
@NullMarked
public class InterfaceAddPatch extends Patch
{
    /**
     * The name of the interface to implement.
     */
    private final String INTERFACE_NAME;
    /**
     * A list of patches that add methods required by the interface.
     */
    private final MethodAddPatch[] SUBPATCHES;

    /**
     * Constructs a patch that implements an interface.
     *
     * @param className The full name of the class to patch.
     * @param interfaceName The full name of the interface to implement.
     * @param subpatches An array of methods to add to implement the interface.
     */
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
