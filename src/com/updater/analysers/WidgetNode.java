package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 15/09/2013
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public class WidgetNode extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            if (!cn.superName.equals(getClassAnalyser("Node").getNode().name)) {
                continue;
            }
            int i = 0;
            int b = 0;
            for (FieldNode fn : cn.fields) {
                if(fn.desc.equals("I") && !Modifier.isStatic(fn.access)) {
                    i++;
                }
                if(fn.desc.equals("Z") && !Modifier.isStatic(fn.access)) {
                    b++;
                }
                if(i == 2 && b == 1) return cn;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (ClassNode cn : classes) {
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*II)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                            addField("getId()", insnToField(fieldInsnNode));
                        }
                    }
                }
            }

        }
    }
}
