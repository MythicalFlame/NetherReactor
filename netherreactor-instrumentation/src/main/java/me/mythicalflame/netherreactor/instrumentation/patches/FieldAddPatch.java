package me.mythicalflame.netherreactor.instrumentation.patches;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import me.mythicalflame.netherreactor.instrumentation.Patcher;

import java.io.IOException;

public class FieldAddPatch extends Patch
{
    private final int[] MODIFIERS;
    private final String FIELD_TYPE;
    private final String FIELD_NAME;
    private final String INITIALIZER;

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
