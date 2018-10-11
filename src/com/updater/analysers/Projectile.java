package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 24/08/13
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class Projectile extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 3;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (!node.superName.equals(getClassAnalyser("Renderable").getNode().name)) {
                continue;
            }
            if (node.name.equals(getClassAnalyser("Actor").getNode().name)) {
                continue;
            }
            if ((Modifier.isFinal(node.access))) {
                return node;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, GETFIELD, ARRAYLENGTH);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[1];
                    if (fieldInsnNode.owner.equals(node.name)) {
                        addField("getProjectileComposite()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, GETFIELD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("Z") && fieldInsnNode.owner.equals(node.name)) {
                        addField("isMoving()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            if (!wildcard("(*)V", mn.desc)) {
                continue;
            }
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, DUP, GETFIELD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[1];
                    if (fieldInsnNode.desc.equals("I") && fieldInsnNode.owner.equals(node.name)) {
                        addField("getDuration()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
    }
}
