package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class CacheableNode extends Analyser {
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
        for (ClassNode cn : classes) {
            if (cn.superName.equals(getClassAnalyser("Node").getNode().name)) {
                if (cn.fields.size() < 4) {
                    for (final Object x : cn.fields) {
                        FieldNode fn = (FieldNode) x;
                        if (fn.desc.equals(String.format("L%s;", cn.name))) {
                            return cn;
                        }
                    }
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        label:
        for (MethodNode mn : node.methods) {
            InstructionSearcher insn = new InstructionSearcher(mn.instructions, 0, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFNONNULL);
            if (insn.match()) {
                for (AbstractInsnNode[] abstractInsnNodes : insn.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNodes[1];
                    if (fieldInsnNode.desc.equals(String.format("L%s;", node.name))) {
                        addField("getNext()", insnToField(fieldInsnNode, node));
                        break label;
                    }
                }
            }
        }
        for (FieldNode fn : node.fields) {
            if (!fn.name.equals(getField("getNext()").getField().name)) {
                addField("getPrevious()", fn);
                break;
            }
        }
    }
}
