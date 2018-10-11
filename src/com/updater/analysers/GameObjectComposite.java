package com.updater.analysers;

import com.updater.Updater;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 28/08/2013
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class GameObjectComposite extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 4;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (!node.superName.equals(getClassAnalyser("CacheableNode").getNode().name))
                continue;
            for (MethodNode mn : node.methods) {
                if (!Modifier.isStatic(mn.access) &&
                        wildcard(String.format("(II[[IIIIL%s;I*)L%s;", getClassAnalyser("ProjectileComposite").getNode().name ,getClassAnalyser("Model").getNode().name), mn.desc))
                    return node;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access)) continue;
            if (fn.desc.equals("Ljava/lang/String;")) addField("getName()", fn);
            if (fn.desc.equals("[Ljava/lang/String;")) addField("getActions()", fn);
        }

        for (ClassNode cn : Updater.classes) {
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(I*)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, ISTORE, -1, ALOAD, GETFIELD);
                if (is.match()) {
                    for (AbstractInsnNode[] insnNodes : is.getMatches()) {
                        FieldInsnNode width = (FieldInsnNode) insnNodes[0];
                        FieldInsnNode height = (FieldInsnNode) insnNodes[5];
                        if (width.owner.equals(node.name) && width.desc.equals("I")) {
                            addField("getWidth()", insnToField(width));
                        }
                        if (height.owner.equals(node.name) && height.desc.equals("I")) {
                            addField("getHeight()", insnToField(height));
                        }
                    }
                }
            }
        }
    }
}
