package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 8/29/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class FloorObject extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 2;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        ClassNode region = getClassAnalyser("Region").getNode();

        for (ClassNode cn : classes) {
            if (cn.name.equals(getClassAnalyser("WallObject").getNode().name)) {
                continue;
            }
            for (final MethodNode mn : region.methods) {
                InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, ILOAD, IFEQ, ALOAD, GETFIELD);
                if (is.match()) {
                    for (AbstractInsnNode[] ain : is.getMatches()) {
                        FieldInsnNode fin = (FieldInsnNode) ain[3];
                        if (fin.desc.equals(String.format("L%s;", cn.name))) {
                            return cn;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("Renderable").getNode().name))) {
                addField("getRenderable()", fn);
            }
            if (fn.desc.equals("I") && (!Modifier.isStatic(fn.access)) && (Modifier.isPublic(fn.access))) {
                addField("getId()", fn);
            }
        }
    }
}
