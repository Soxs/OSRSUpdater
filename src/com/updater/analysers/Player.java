package com.updater.analysers;

import com.updater.Updater;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 24/08/2013
 * Time: 01:02
 * To change this template use File | Settings | File Templates.
 */
public class Player extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 7;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (node.superName.equals(getClassAnalyser("Actor").getNode().name)) {
                for (FieldNode fn : node.fields) {
                    if (fn.desc.contains("String")) {
                        return node;
                    }
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.access == 0 && fn.desc.contains("String")) {
                addField("getName()", fn);
            }
            if (fn.access == 0 & fn.desc.contains(getClassAnalyser("Model").getNode().name)) {
                addField("getModel()", fn);
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, ALOAD, ALOAD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[1];
                    addField("getPlayerComposite()", insnToField(fieldInsnNode, node));
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, IFNE);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[1];
                    if (fieldInsnNode.desc.equals("Z")) {
                        addField("isVisible()", insnToField(fieldInsnNode));
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ALOAD, ALOAD, -1, INVOKEVIRTUAL, -1, -1, PUTFIELD);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[6];
                    if (fieldInsnNode.desc.equals("I")) {
                        addField("getCombatLevel()", insnToField(fieldInsnNode));
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            if (!wildcard(String.format("(*)L%s;", getClassAnalyser("Model").getNode().name), mn.desc)) {
                continue;
            }
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, IF_ICMPGE);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("I")) {
                        addField("getSkullIcon()", insnToField(fieldInsnNode));
                    }
                }
            }
        }
        for (ClassNode cn : Updater.classes) {
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, GETSTATIC, -1, ALOAD, GETFIELD, -1, AALOAD);
                if (insn.match()) {
                    for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[3];
                        if (fieldInsnNode.desc.equals("I")) {
                            addField("getPrayerIcon()", insnToField(fieldInsnNode));
                        }
                    }
                }
            }
        }
    }
}
