package com.updater.analysers;

import com.updater.Updater;
import com.updater.utils.EIS;
import com.updater.utils.InstructionSearcher;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.analyse.container.Field;
import com.updater.utils.asm.Harvester;
import com.updater.utils.asm.NullInsnNode;
import com.updater.utils.asm.Pattern;
import com.updater.utils.multiplier.MultiplierFinder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class Actor extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 19;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (node.superName.equals(getClassAnalyser("Renderable").getNode().name)) {
                if (Modifier.isAbstract(node.access)) {
                    return node;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void findCharHealth(final ClassNode cn) {
        for(final MethodNode mn : cn.methods) {
            if (Pattern.findInstruction(mn, new InsnNode(ATHROW))) {
                AbstractInsnNode[] pat = new AbstractInsnNode[] {
                        new InsnNode(IMUL),
                        new NullInsnNode(),
                        new NullInsnNode(),
                        new NullInsnNode(),
                        new InsnNode(IMUL),
                        new InsnNode(IDIV),
                        new VarInsnNode(ISTORE, 9000)
                };
                AbstractInsnNode[] ret = Pattern.findPattern(mn, pat);
                boolean max = true;
                String charName = this.getNode().name;
                Harvester harvester = new Harvester(charName);
                while (ret != null) {
                    AbstractInsnNode ain = ret[6];
                    for (int i = 0; i < 9; i++) {
                        ain = Pattern.getPrevious(ain);
                        harvester.scan(ain);
                        if (harvester.finished()) {
                            String n = max ? "getMaxHealth" : "getHealth";
                            addField(n, insnToField(harvester.getField()), harvester.getMultiplier());
                            max = false;
                            harvester = new Harvester(charName);
                        }
                    }
                    ret = Pattern.findPattern(mn, pat, ret[2]);
                }
            }
        }
    }

    private void findInteracting(MethodNode mn, String npc) {
        if (Pattern.findInstruction(mn, new InsnNode(ATHROW)) && mn.desc.startsWith("(L" + this.getNode().name)) {
            String npcClass = npc;
            AbstractInsnNode[] pat = new AbstractInsnNode[] {
                    new FieldInsnNode(GETSTATIC, null, null, "[L" + npcClass + ";"),
                    new NullInsnNode(),
                    new NullInsnNode(),
                    new NullInsnNode(),
                    new InsnNode(IMUL),
                    new InsnNode(AALOAD)
            };
            pat = Pattern.findPattern(mn, pat);
            if (pat != null) {
                Harvester harvester = new Harvester(this.getNode().name);
                AbstractInsnNode ain = pat[0];
                for (int i = 0; i < 3; i++) {
                    ain = Pattern.getNext(ain);
                    harvester.scan(ain);
                }
                addField("getInteractingIndex()", insnToField(harvester.getField()));
            }
        }
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access))
                continue;
            if (fn.desc.contains("String")) {
                addField("getSpokenText()", fn);
            }
            if (fn.desc.equals("Z")) {
                addField("isAnimating()", fn);
            }
        }
        getAnimation:
        for (final MethodNode mn : node.methods) {
            if (wildcard("(IIZ*)V", mn.desc)) {
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, LDC, PUTFIELD);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                        if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I") &&
                                !Modifier.isStatic(insnToField(fieldInsnNode).access)) {
                            addField("getAnimation()", insnToField(fieldInsnNode, node));
                            break getAnimation;
                        }
                    }
                }
            }
        }
        getSpeed:
        for (final MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ALOAD, GETFIELD, IMUL, ISTORE);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[1];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I") &&
                            !Modifier.isStatic(insnToField(fieldInsnNode).access)) {
                        addField("getSpeed()", insnToField(fieldInsnNode, node));
                        break getSpeed;
                    }
                }
            }
        }
        getGrid:
        for (ClassNode cn : classes)
            for (final MethodNode mn : cn.methods) {
                if (!wildcard(String.format("(L%s;II)V", node.name), mn.desc) || !Modifier.isStatic(mn.access) || !Modifier.isFinal(mn.access)) {
                    continue;
                }
                InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, -1, ALOAD, GETFIELD);
                if (instructionSearcher.match()) {
                    for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                        FieldInsnNode x = (FieldInsnNode) insnNodes[0];
                        FieldInsnNode y = (FieldInsnNode) insnNodes[3];
                        if (x.owner.equals(node.name) && x.desc.equals("I") &&
                                y.owner.equals(node.name) && y.desc.equals("I")) {
                            addField("getGridX()", insnToField(x, node));
                            addField("getGridY()", insnToField(y, node));
                            break getGrid;
                        }
                    }
                }
            }
        getQX:
        for (final MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, LDC, ALOAD, GETFIELD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("[I")) {
                        addField("getQueueX()", insnToField(fieldInsnNode, node));
                        break getQX;
                    }
                }
            }
        }
        getQY:
        for (final MethodNode mn : node.methods) {
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, ILOAD, ALOAD, GETFIELD, ILOAD);
            if (instructionSearcher.match()) {
                for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[2];
                    try {
                        if (insnToField(fieldInsnNode, node).equals(getField("getQueueX()").getField())) {
                            continue;
                        }
                    } catch (Exception e) {
                    }
                    if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("[I")) {
                        addField("getQueueY()", insnToField(fieldInsnNode, node));
                        break getQY;
                    }
                }
            }
        }
        for (final MethodNode mn : node.methods) {
            if (!wildcard("(III*)V", mn.desc) || !Modifier.isFinal(mn.access)) {
                continue;
            }
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, ILOAD, ILOAD, -1, -1);
            if(instructionSearcher.match()) {
                for(AbstractInsnNode[] ain : instructionSearcher.getMatches()) {
                    final FieldInsnNode fin1 = (FieldInsnNode) ain[0];
                    if(fin1.desc.equals("[I")) {
                        addField("getHitsplatsDamage()", insnToField(fin1));
                    }
                }
            }
            instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, ILOAD, ILOAD, -1, -1);
            if(instructionSearcher.match()) {
                for(AbstractInsnNode[] ain : instructionSearcher.getMatches()) {
                    final FieldInsnNode fin1 = (FieldInsnNode) ain[0];
                    if(fin1.desc.equals("[I") && !isDuplicate(insnToField(fin1))) {
                        addField("getHitsplatsTypes()", insnToField(fin1));
                    }
                }
            }
            instructionSearcher = new InstructionSearcher(mn.instructions, 0, GETFIELD, ILOAD, ILOAD, BIPUSH);
            if(instructionSearcher.match()) {
                for(AbstractInsnNode[] ain : instructionSearcher.getMatches()) {
                    final FieldInsnNode fin1 = (FieldInsnNode) ain[0];
                    if(fin1.desc.equals("[I")) {
                        addField("getHitsplatsTime()", insnToField(fin1));
                    }
                }
            }
        }
        for(final MethodNode mn : node.methods) {
            if(!wildcard("()V", mn.desc) || !Modifier.isFinal(mn.access)) {
                continue;
            }
            InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, PUTFIELD, -1, -1,
                    PUTFIELD);
            if(instructionSearcher.match()) {
                for(AbstractInsnNode[] ain : instructionSearcher.getMatches()) {
                    final FieldInsnNode fin1 = (FieldInsnNode) ain[0];
                    final FieldInsnNode fin2 = (FieldInsnNode) ain[3];
                    if(fin1.desc.equals("I")) {
                        addField("getQueueXPosition()", insnToField(fin1));
                    }
                    if(fin2.desc.equals("I")) {
                        addField("getQueueYPosition()", insnToField(fin2));
                    }
                }
            }
        }
        for (ClassNode cn : Updater.classes) {
            getOrientation:
            for (final MethodNode mn : cn.methods) {
                if (wildcard(String.format("(L%s;I*)V", getNode().name), mn.desc)) {
                    InstructionSearcher instructionSearcher = new InstructionSearcher(mn.instructions, 0, INVOKESTATIC, LDC, DMUL, D2I, SIPUSH, IAND, LDC, IMUL, PUTFIELD);
                    if (instructionSearcher.match()) {
                        for (AbstractInsnNode[] insnNodes : instructionSearcher.getMatches()) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNodes[8];
                            if (fieldInsnNode.owner.equals(node.name) && fieldInsnNode.desc.equals("I")) {
                                if (Modifier.isStatic(insnToField(fieldInsnNode, node).access)) {
                                    continue;
                                }
                                addField("getOrientation()", insnToField(fieldInsnNode, node), 331411347);
                                break getOrientation;
                            }
                        }
                    }
                }
            }
            for(final MethodNode mn : cn.methods) {
                findInteracting(mn, "l"); //need a better way
            }
            findCharHealth(cn);
            for(final MethodNode mn : cn.methods) {
                // if(!wildcard(String.format("(III*)V", node.name), mn.desc)) {
                //   continue;
                //}
                InstructionSearcher is = new InstructionSearcher(mn.instructions, 0, -1, ALOAD, GETFIELD, -1, GETSTATIC);
                if (is.match()) {
                    for (AbstractInsnNode[] insnNodes : is.getMatches()) {
                        final FieldInsnNode actor = (FieldInsnNode) insnNodes[2];
                        final FieldInsnNode game = (FieldInsnNode) insnNodes[4];
                        if (actor.owner.equals(node.name) && actor.desc.equals("I")) {
                            addField("getLoopCycle()", insnToField(actor));
                        }
                        if (game.owner.equals("client") && game.desc.equals("I")) {
                            addField("getGameCycle()", insnToField(game), findNode(game.owner));
                        }
                    }
                }
            }
        }
    }
}
