package com.updater.analysers;

import com.updater.utils.EIS;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 29/08/2013
 * Time: 22:07
 * To change this template use File | Settings | File Templates.
 */
public class Tile extends Analyser {
    @Override
    protected void finish() {
        ClassNode cn = getClassAnalyser("Region").getNode();
        for (FieldNode fn : cn.fields) {
            if (!Modifier.isStatic(fn.access) && fn.desc.equals(String.format("[[[L%s;", getNode().name))) {
                getClassAnalyser("Region").addField("getTiles()", fn);
            }
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 6;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            if (!cn.superName.equals(getClassAnalyser("Node").getNode().name))
                continue;
            for (MethodNode mn : cn.methods) {
                if (!mn.name.equals("<init>"))
                    continue;
                if (mn.desc.equals("(III)V")) {
                    return cn;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals(String.format("[L%s;", getClassAnalyser("GameObject").getNode().name))) {
                addField("getGameObjects()", fn);
            }
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("FloorObject").getNode().name))) {
                addField("getFloorDecoration()", fn);
            }
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("WallObject").getNode().name))) {
                addField("getWallDecoration()", fn);
            }
        }
        for (final MethodNode mn : getNode().methods) {
            final EIS eis = new EIS(mn, "putfield ldc imul");
            if (eis.found() > 0) {
                final FieldInsnNode fin = (FieldInsnNode) eis.getNodesAt(0)[0];
                if (fin.desc.equals("I")) {
                    addField("getPlane()", insnToField(fin));
                }
            }
        }
        ClassNode reg = getClassAnalyser("Region").getNode();
        getX:
        for (MethodNode mn : reg.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, LDC, IMUL, ISTORE);
            if (insn.match()) {
                for (AbstractInsnNode[] matches : insn.getMatches()) {
                    FieldInsnNode fin = (FieldInsnNode) matches[1];
                    if (fin.desc.equals("I") && fin.owner.equals(node.name)) {
                        addField("getX()", insnToField(fin, node));
                        break getX;
                    }
                }
            }
        }
        getY:
        for (MethodNode mn : reg.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, ISTORE, ALOAD, GETFIELD, LDC, IMUL);
            if (insn.match()) {
                for (AbstractInsnNode[] matches : insn.getMatches()) {
                    FieldInsnNode fin = (FieldInsnNode) matches[2];
                    if (fin.desc.equals("I") && fin.owner.equals(node.name)) {
                        addField("getY()", insnToField(fin, node));
                        break getY;
                    }
                }
            }
        }
    }
}
