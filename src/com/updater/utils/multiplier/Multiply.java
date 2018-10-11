package com.updater.utils.multiplier;

import com.updater.utils.ClassNode;
import com.updater.utils.InstructionSearcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 24/08/13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class Multiply implements Opcodes {

    public static int findMultiplier(ClassNode node, FieldInsnNode fieldInsnNode, int index, int ldc, int... Opcodes) {
        for (Object d : node.methods) {
            MethodNode mn = (MethodNode) d;
            InstructionSearcher searcher = new InstructionSearcher(mn.instructions, 0, Opcodes);
            if (searcher.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : searcher.getMatches()) {
                    LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNodes[ldc];
                    FieldInsnNode fn = (FieldInsnNode) abstractInsnNodes[index];
                    if (fn.name.equals(fieldInsnNode.name) && fn.owner.equals(fieldInsnNode.owner)) {
                        return Integer.parseInt(ldcInsnNode.cst.toString());
                    }
                }
            }
        }
        return -1;
    }
}
