package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import com.updater.utils.asm.Pattern;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 24/08/2013
 * Time: 22:43
 * To change this template use File | Settings | File Templates.
 */
public class Model extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 6;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (node.superName.equals(getClassAnalyser("Renderable").getNode().name)) {
                if (!node.name.equals(getClassAnalyser("Item").getNode().name)) {
                    if (!Modifier.isPublic(node.access))
                        continue;
                    for (MethodNode mn : node.methods) {
                        if (mn.desc.equalsIgnoreCase(String.format("(Z)L%s;", node.name))) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean hookVertices(final MethodNode mn) {
        if (!mn.desc.equals("(III)V"))
            return false;
        Set<String> arrLookups = new HashSet();
        ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == GETFIELD) {
                FieldInsnNode fin = (FieldInsnNode)ain;
                if (fin.desc.equals("[I"))
                    arrLookups.add(fin.name);
            }
            if (ain.getOpcode() == IDIV)
                return false;
        }
        if (arrLookups.size() != 3)
            return false;
        AbstractInsnNode[] pat = new AbstractInsnNode[] {
                new VarInsnNode(ALOAD, 0),
                new FieldInsnNode(GETFIELD, null, null, "[I"),
                new VarInsnNode(ILOAD, 9000),
                new InsnNode(DUP2),
                new InsnNode(IALOAD),
                new VarInsnNode(ILOAD, 9000),
                new InsnNode(IADD),
                new InsnNode(IASTORE)
        };
        AbstractInsnNode start = mn.instructions.getFirst();
        for (int i = 0; i < 3; i++) {
            AbstractInsnNode[] ret = Pattern.findPatternSnake(mn, pat, start);
            if (ret != null) {
                FieldInsnNode fin = (FieldInsnNode)ret[1];
                addField(String.format("getVertices%s()", "XYZ".charAt(i)), insnToField(fin));
                start = ret[7];
            } else
                return false;
        }
        return true;
    }

    private boolean hookIndices(final MethodNode mn) {
        if (!mn.desc.equals("(ZZI)V"))
            return false;
        AbstractInsnNode[] methodID = new AbstractInsnNode[] {
                new InsnNode(IALOAD),
                new IntInsnNode(BIPUSH, -2),
                new JumpInsnNode(-1, null)
        };
        AbstractInsnNode[] ret = Pattern.findPattern(mn, methodID);
        if (ret == null)
            return false;
        AbstractInsnNode start = mn.instructions.get(mn.instructions.indexOf(((JumpInsnNode) ret[2]).label));
        AbstractInsnNode[] pat = new AbstractInsnNode[] {
                new VarInsnNode(ALOAD, 0),
                new FieldInsnNode(GETFIELD, null, null, "[I"),
                new VarInsnNode(ILOAD, 9000),
                new InsnNode(IALOAD)
        };
        for (int i = 0; i < 3; i++) {
            ret = Pattern.findPattern(mn, pat, start);
            if (ret != null) {
                FieldInsnNode fin = (FieldInsnNode)ret[1];
                addField(String.format("getIndices%s()", "XYZ".charAt(i)), insnToField(fin));
                start = ret[3];
            } else
                return false;
        }
        return true;
    }

    @Override
    protected void getFields(ClassNode node) {
        for(final MethodNode mn : node.methods) {
            hookIndices(mn);
            hookVertices(mn);
        }
    }
}
