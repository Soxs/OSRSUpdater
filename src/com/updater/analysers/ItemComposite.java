package com.updater.analysers;

import com.updater.utils.EIS;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 26/08/2013
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class ItemComposite extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 5;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            if (!cn.superName.equals(getClassAnalyser("CacheableNode").getNode().name))
                continue;
            for (MethodNode mn : cn.methods) {
                if(wildcard(String.format("(L%s;*)V", cn.name), mn.desc)) {
                    return cn;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (MethodNode mn : node.methods) {
            EIS insn = new EIS(mn, "putfield");
            if (insn.found() > 0) {
                FieldInsnNode fin = (FieldInsnNode) insn.getNodesAt(0)[0];
                if (fin.desc.equals("[Ljava/lang/String;")) {
                    addField("getActions()", insnToField(fin, node));
                }
            }
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, LDC, PUTFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] matches : instructionSearcher.getMatches()) {
                    FieldInsnNode fin = (FieldInsnNode) matches[2];
                    if (fin.desc.equals("I") && fin.owner.equals(node.name)) {
                        addField("getStackIds()", insnToField(fin));
                    }
                }
            }
        }
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access)) {
                continue;
            }
            if (fn.desc.equals("Ljava/lang/String;")) {
                addField("getName()", fn);
            }
            if (fn.desc.equals("Z")) {
                addField("isMember()", fn);
            }
            if(fn.desc.equals("[Ljava/lang/String;")) {
                addField("getActions()", fn);
            }
            if (fn.desc.equals("[Ljava/lang/String;") && (!fn.name.equals(getField("getActions()").getField().name))) {
                addField("getGroundActions()", fn);
            }
        }
    }
}
