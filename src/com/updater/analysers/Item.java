package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 24/08/2013
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class Item extends Analyser {
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
        for (ClassNode node : classes) {
            if (!node.superName.equals(getClassAnalyser("Renderable").getNode().name)) {
                continue;
            }
            if (node.name.equals(getClassAnalyser("Actor").getNode().name)) {
                continue;
            }
            if (node.name.equals(getClassAnalyser("Projectile").getNode().name)) {
                continue;
            }
            if ((Modifier.isFinal(node.access))) {
                return node;
            }

        }
        return null;
    }

    @Override
    protected void getFields(ClassNode node) {
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, Opcodes.GETFIELD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("I") && fieldInsnNode.owner.equals(node.name)) {
                        addField("getId()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, Opcodes.GETFIELD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("I") && fieldInsnNode.owner.equals(node.name) &&
                            !insnToField(fieldInsnNode, getNode()).equals(getField("getId()").getField())) {
                        addField("getStackSize()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
    }
}
