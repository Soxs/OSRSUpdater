package com.updater.analysers;

import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 30/08/2013
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
public class CollisionData extends Analyser {
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
        for (final ClassNode cn : classes) {
            if (!cn.superName.equals("java/lang/Object")) {
                continue;
            }
            for (MethodNode mn : cn.methods) {
                InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, ISUB, ISTORE, ALOAD, GETFIELD);
                if (is.match()) {
                    for (AbstractInsnNode[] ain : is.getMatches()) {
                        FieldInsnNode fin = (FieldInsnNode) ain[3];
                        if (fin.desc.equals("[[I")) {
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
        for (MethodNode mn : node.methods) {
            InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, ISUB, ISTORE, ALOAD, GETFIELD);
            if (is.match()) {
                for (AbstractInsnNode[] ain : is.getMatches()) {
                    FieldInsnNode fin = (FieldInsnNode) ain[3];
                    if (fin.desc.equals("[[I")) {
                        addField("getFlags()", insnToField(fin));
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, -1,
                    ISUB, ISTORE, ILOAD, ALOAD, GETFIELD);
            if (is.match()) {
                for (AbstractInsnNode[] ain : is.getMatches()) {
                    FieldInsnNode fin = (FieldInsnNode) ain[0];
                    if (fin.desc.equals("I")) {
                        addField("getOffsetX()", insnToField(fin));
                    }
                    FieldInsnNode fin2 = (FieldInsnNode) ain[7];
                    if (fin2.desc.equals("I")) {
                        addField("getOffsetY()", insnToField(fin2));
                    }
                }
            }
        }
    }
}
