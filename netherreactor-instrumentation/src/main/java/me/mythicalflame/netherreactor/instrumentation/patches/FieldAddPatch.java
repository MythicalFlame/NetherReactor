package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * Represents a patch that adds a field.
 */
@NullMarked
public class FieldAddPatch extends Patch
{
    /**
     * The modifiers of the field.
     */
    private final int[] MODIFIERS;
    /**
     * The name of the class of the type of the field.
     */
    private final String FIELD_TYPE;
    /**
     * The name of the field.
     */
    private final String FIELD_NAME;
    /**
     * The initial value of the field.
     */
    private final String INITIALIZER;

    /**
     * Constructs a patch that adds a field.
     *
     * @param className The full name of the class to patch.
     * @param modifiers The modifiers of the field, as an array consisting of values from java.lang.reflect.Modifier.
     * @param fieldType The full name of the class of the type of this field.
     * @param fieldName The name of the field.
     * @param initializer The initial value of the field.
     */
    public FieldAddPatch(String className, int[] modifiers, String fieldType, String fieldName, String initializer)
    {
        super(className);
        this.MODIFIERS = modifiers;
        this.FIELD_TYPE = fieldType;
        this.FIELD_NAME = fieldName;
        this.INITIALIZER = initializer;
    }

    @Override
    public void apply(CtClass cc) throws NotFoundException, IOException, CannotCompileException
    {
        CtClass fieldType = Patcher.getCtClass(FIELD_TYPE);
        CtField field = new CtField(fieldType, FIELD_NAME, cc);
        int modifier = 0;
        for (int mod : MODIFIERS)
        {
            modifier |= mod;
        }
        field.setModifiers(modifier);
        cc.addField(field, INITIALIZER);
    }
}
