package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 25/08/2013
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class NpcComposite extends Analyser {
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
        for (final ClassNode node : classes) {
            if (node.superName.equals(getClassAnalyser("CacheableNode").getNode().name)) {
                if (getField("getComposite()", getClassAnalyser("Npc")).getField().desc.equals("L" + node.name + ";")) {
                    return node;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access))
                continue;
            if (fn.desc.equalsIgnoreCase("Ljava/lang/String;"))
                addField("getName()", fn);
            if (fn.desc.equalsIgnoreCase("[Ljava/lang/String;"))
                addField("getActions()", fn);
        }

        getId:
        for (MethodNode mn : node.methods) {
            InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, -1, I2L);
            if (is.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : is.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("I") && !Modifier.isStatic(insnToField(fieldInsnNode).access)) {
                        addField("getId()", insnToField(fieldInsnNode, node));
                        break getId;
                    }
                }
            }
        }

        getModelIds:
        for (final MethodNode mn : node.methods) {
            if (!wildcard("(I)*", mn.desc))
                continue getModelIds;
            InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, GETFIELD);
            if (is.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : is.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[0];
                    if (fieldInsnNode.desc.equals("[I")) {
                        addField("getModelIds()", insnToField(fieldInsnNode));
                        break getModelIds;
                    }
                }
            }
        }
    }
}
