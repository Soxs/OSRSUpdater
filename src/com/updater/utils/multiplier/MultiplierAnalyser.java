package com.updater.utils.multiplier;

import com.updater.utils.RegexInsnSearcher;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 24/08/13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class MultiplierAnalyser implements Opcodes {

    private static final String[] PATTERNS = {
            "(ldc|getstatic|aload) (aload|getfield|getstatic|ldc|invokevirtual) (imul|putstatic|getfield|ldc) (imul)?",
            "(ldc|getstatic|aload) (aload|getfield|getstatic|ldc|invokevirtual) (getfield|ldc) imul putstatic"
    };

    @SuppressWarnings("unchecked")
    public static void findMultipliers(List<org.objectweb.asm.tree.ClassNode> classes2) {
        final Iterator<org.objectweb.asm.tree.ClassNode> classes = classes2.iterator();
        while (classes.hasNext()) {
            org.objectweb.asm.tree.ClassNode cn = classes.next();
            List<MethodNode> methods = (List<MethodNode>) cn.methods;
            for (MethodNode mn : methods) {
                RegexInsnSearcher searcher = new RegexInsnSearcher(mn.instructions);
                for (String pattern : PATTERNS) {
                    List<AbstractInsnNode[]> matches = searcher.search(pattern);
                    for (AbstractInsnNode[] match : matches) {
                        Integer value = null, refHash = null;
                        for (AbstractInsnNode insn : match) {
                            if (insn.getOpcode() == LDC) {
                                try {
                                    value = (Integer) ((LdcInsnNode) insn).cst;
                                } catch (ClassCastException cce) {
                                    break;
                                }
                            }
                            if (insn.getOpcode() == GETFIELD || insn.getOpcode() == GETSTATIC) {
                                FieldInsnNode fieldInsn = ((FieldInsnNode) insn);
                                refHash = Mul.getHash(fieldInsn.owner, fieldInsn.name);
                            }
                            if (insn.getOpcode() == PUTSTATIC) {
                                FieldInsnNode fieldInsn = ((FieldInsnNode) insn);
                                refHash = Mul.getHash(fieldInsn.owner, fieldInsn.name);
                            }
                        }
                        if (refHash != null && value != null) {
                            Mul.put(refHash, value);
                        }
                    }
                }
            }
        }
    }
}
