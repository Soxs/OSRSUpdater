package com.updater.analysers;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.updater.Updater;
import com.updater.utils.EIS;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.asm.NullInsnNode;
import com.updater.utils.asm.Pattern;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 25/08/2013
 * Time: 00:14
 * To change this template use File | Settings | File Templates.
 */
public class Widget extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 26;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if(!node.superName.equals(getClassAnalyser("Node").getNode().name)) {
                continue;
            }
            int count = 0;
            for (FieldNode field : node.fields) {
                if (field.desc.equals("[Ljava/lang/Object;"))
                    count++;
            }
            if (count > 20)
                return node;
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (ClassNode cn : Updater.classes) {
            for (MethodNode mn : cn.methods) {
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, ALOAD, GETFIELD, SIPUSH);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("Ljava/lang/String;")) {
                            addField("getName()", insnToField(fieldInsnNode));
                            break;
                        }
                    }
                }
            }
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, IINC, ALOAD, GETFIELD, IFEQ);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("Z")) {
                            addField("isHidden()", insnToField(fieldInsnNode, node));
                            break;
                        }
                    }
                }
            }

            for(MethodNode mn : cn.methods) {
                if (!wildcard(String.format("([L%s;IIIIIIIII)V", node.name), mn.desc) && !Modifier.isFinal(mn.access)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, PUTFIELD, ALOAD, ILOAD, -1, -1,
                        PUTFIELD, ILOAD, -1, INVOKESTATIC);
                if(instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode stackSizes = (FieldInsnNode) insnNodes[5];
                        if(stackSizes.owner.equals(node.name) && stackSizes.desc.equals("I")) {
                            addField("getItemStackSize()", insnToField(stackSizes, node));
                        }
                    }
                }
                instructionSearcher = new InstructionSearcher(mn.instructions, 0, PUTFIELD, ALOAD, ILOAD, -1, -1, PUTFIELD, ILOAD, -1,
                        INVOKESTATIC);
                if(instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode itemId = (FieldInsnNode) insnNodes[0];
                        if(itemId.owner.equals(node.name) && itemId.desc.equals("I")) {
                            addField("getItemId()", insnToField(itemId, node));
                        }
                    }
                }
            }

            for (MethodNode mn : cn.methods) {
                if (!wildcard(String.format("(L%s;*)V", node.name), mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1, BASTORE);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                            addField("getStaticPosition()", insnToField(fieldInsnNode, node));
                            break;
                        }
                    }
                }
            }
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, ISUB, ILOAD, ALOAD, GETFIELD);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode scrollx = (FieldInsnNode) insnNodes[0];
                        FieldInsnNode scrolly = (FieldInsnNode) insnNodes[5];
                        if (scrollx.owner.equals(node.name) && scrollx.desc.equals("I")) {
                            addField("getScrollX()", insnToField(scrollx, node));
                        }
                        if (scrolly.owner.equals(node.name) && scrolly.desc.equals("I")) {
                            addField("getScrollY()", insnToField(scrolly, node));
                        }
                    }
                }
            }
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*)V", mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                            addField("getIndex()", insnToField(fieldInsnNode, node));
                            break;
                        }
                    }
                }
            }
            for (MethodNode mn : cn.methods) {
                if (!wildcard("(*)I", mn.desc)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, ILOAD, IALOAD, IADD, ISTORE);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[0];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("[I")) {
                            addField("getSlotStackSizes()", insnToField(fieldInsnNode, node));
                            break;
                        }
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, -1, -1, IAND);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[0];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                        addField("getId()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, DUP_X1, PUTFIELD, LDC, IMUL, PUTFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    FieldInsnNode getx = (FieldInsnNode) insnNodes[4];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.contains("I") &&
                            getx.owner.equals(node.name) && getx.desc.contains("I")) {
                        addField("getRelativeX()", insnToField(fieldInsnNode, node));
                        addField("getX()", insnToField(getx, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, DUP_X1, PUTFIELD, LDC, IMUL, PUTFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    FieldInsnNode gety = (FieldInsnNode) insnNodes[4];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.contains("I") &&
                            gety.owner.equals(node.name) && gety.desc.contains("I") &&
                            !fieldInsnNode.name.equals(getField("getRelativeX()").getField().name)) {
                        addField("getRelativeY()", insnToField(fieldInsnNode, node));
                        addField("getY()", insnToField(gety, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ILOAD, ALOAD, GETFIELD, ARRAYLENGTH, IF_ICMPGE);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("[Ljava/lang/String;")) {
                        addField("getActions()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1, -1, IF_ICMPNE);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                        addField("getParentId()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1, IFLE);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                        addField("getBorderThickness()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }

        for(MethodNode mn : node.methods) {
            final AbstractInsnNode[] pat = new AbstractInsnNode[] {
                    new VarInsnNode(ALOAD, 0),
                    new FieldInsnNode(GETFIELD, node.name, null, "I"),
                    new NullInsnNode(),
                    new NullInsnNode(),
                    new VarInsnNode(ISTORE, 3)
            };
            final AbstractInsnNode[] pattern = Pattern.findPattern(mn, pat);
            if(pattern != null) {
                final FieldInsnNode fin1 = (FieldInsnNode) pattern[1];
                addField("getTextureId()", insnToField(fin1), -131734905);
            }
        }
        for(MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, -1, -1, BIPUSH, IF_ICMPNE, ALOAD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                        addField("getType()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, LDC, INVOKEVIRTUAL, PUTFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("Ljava/lang/String;")) {
                        addField("getText()", insnToField(fieldInsnNode, node));
                        break;
                    }
                }
            }
        }
        for (MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, IMUL, LDC, ALOAD, GETFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode width = (FieldInsnNode) insnNodes[0];
                    FieldInsnNode height = (FieldInsnNode) insnNodes[4];
                    if (width.owner.equals(node.name) && width.desc.equals("I") &&
                            height.owner.equals(node.name) && height.desc.equals("I")) {
                        addField("getHeight()", insnToField(height));
                        addField("getWidth()", insnToField(width));
                        break;
                    }
                }
            }
        }

        for (MethodNode mn : node.methods) {
            EIS insn = new EIS(mn, "getfield");
            if (insn.found() > 0) {
                FieldInsnNode fin = (FieldInsnNode) insn.getNodesAt(0)[0];
                if (fin.desc.equals("[I") && insn.found() == 8) {
                    addField("getSlotContentIds()", insnToField(fin, node));
                }
            }
        }

        for (FieldNode fn : node.fields) {
            if (!Modifier.isStatic(fn.access)) {
                if (fn.desc.equals(String.format("L%s;", getNode().name))) {
                    addField("getParent()", fn);
                }
                if (fn.desc.equals(String.format("[L%s;", getNode().name))) {
                    addField("getChildren()", fn);
                }
                if (fn.desc.equals("[[I")) {
                    addField("getOpcodes()", fn);
                }
                /*if (fn.desc.equals("I") && !fn.equals(getField("getId()").getField())) {
                    addField("getHash()", fn);
                }*/
            }
        }
    }
}
