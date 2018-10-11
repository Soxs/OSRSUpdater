package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.*;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 23/08/13
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 */
public class Node extends Analyser {
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
            if (node.superName.equalsIgnoreCase("java/lang/Object")) {
                for (MethodNode mn : node.methods) {
                    InstructionSearcher searcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, IFNONNULL);
                    if (searcher.match()) {
                        if (node.fields.size() == 3 && node.name.contains("f")) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void getFields(ClassNode node) {
        String field = null;
        for (int i = 0; i < node.methods.size(); i++) {
            MethodNode mn = node.methods.get(i);
            if (field == null) {
                InstructionSearcher instructionSearch = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, IFNONNULL);
                if (instructionSearch.match()) {
                    for (AbstractInsnNode[] matches : instructionSearch.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) matches[1];
                        field = fieldInsnNode.name;
                        addField("getNext()", insnToField(fieldInsnNode, node));
                    }
                }
            }
        }
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals("J")) {
                addField("getHash()", fn);
            }
            if (!fn.name.equals(field) && fn.desc.contains(";")) {
                addField("getPrevious()", fn);
            }
        }
    }
}
