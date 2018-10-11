package com.updater.utils.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by Kevin on 26/09/13.
 */
public class Pattern implements Opcodes {

    // Since ASM counts labels as insns, we need to do this
    public static AbstractInsnNode getNext(AbstractInsnNode a) {
        AbstractInsnNode next = a.getNext();
        if (next == null)
            return null;
        if (next.getOpcode() == -1)
            next = next.getNext();
        return next;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode a) {
        AbstractInsnNode prev = a.getPrevious();
        if (prev == null)
            return null;
        if (prev.getOpcode() == -1)
            prev = prev.getPrevious();
        return prev;
    }


    public static boolean isPrimitive(String descriptor) {
        return "BCDFIJSZ".contains(descriptor.replace("[", "").replace("L", "").replace(";", ""));
    }

    // Eventually ran into Jagex having some junk parameters,
    // so I needed some flexibility in checking method descriptors
    public static boolean descContains(String desc, String[] a) {
        String d = desc;
        for (String s : a) {
            int l = d.length();
            d = d.replace(s, "");
            if (d.length() == l)
                return false;
        }
        return true;
    }

    public static boolean isConstructorDescriptor(org.objectweb.asm.tree.ClassNode classNode, String desc) {
        ListIterator<MethodNode> mnli = classNode.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.name.equals("<init>"))
                if (mn.desc.contains(desc))
                    return true;
        }
        return false;
    }

    public static ArrayList<MethodNode> methodsFromDescriptor(String descriptor, ClassNode classNode) {
        ArrayList<MethodNode> ret = new ArrayList<>();
        ListIterator<MethodNode> mnli = classNode.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            if (mn.desc.endsWith(descriptor))
                ret.add(mn);
        }
        return ret;
    }

    public static int countSIPUSH(MethodNode methodNode, int operand) {
        int count = 0;
        ListIterator<AbstractInsnNode> ainli = methodNode.instructions.iterator();
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == Opcodes.SIPUSH) {
                IntInsnNode iin = (IntInsnNode) ain;
                if (iin.operand == operand)
                    count++;
            }
        }
        return count;
    }

    public static int countInstructions(MethodNode methodNode, int opcode) {
        int count = 0;
        ListIterator<AbstractInsnNode> ainli = methodNode.instructions.iterator();
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == opcode) {
                count++;
            }
        }
        return count;
    }

    public static void scrollTo(ListIterator<AbstractInsnNode> ainli, int opcode) {
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == opcode)
                break;
        }
    }

    public static AbstractInsnNode scrollFind(ListIterator<AbstractInsnNode> ainli, AbstractInsnNode arg) {
        if (!ainli.hasNext())
            return null;
        AbstractInsnNode ain = ainli.next();
        while (ainli.hasNext()) {
            if (match(ain, arg))
                break;
            ain = ainli.next();
        }
        return ain;
    }

    public static int countInstructions(ListIterator<AbstractInsnNode> ainli, int opcode, int limit) {
        int count = 0;
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (ain.getOpcode() == opcode)
                count++;
            if (ain.getOpcode() == limit)
                break;
        }
        return count;
    }

    public static int countInstructions(ListIterator<AbstractInsnNode> ainli, AbstractInsnNode ainz) {
        int count = 0;
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (match(ain, ainz))
                count++;
        }
        return count;
    }

    public static int countInstructionStop(MethodNode mn, AbstractInsnNode instruction, AbstractInsnNode stop) {
        int count = 0;
        ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            if (match(ain, instruction))
                count++;
            if (match(ain, stop))
                break;
        }
        return count;
    }

    public static boolean findInstruction(MethodNode mn, AbstractInsnNode ain) {
        ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
        while (ainli.hasNext()) {
            AbstractInsnNode next = ainli.next();
            if (match(next, ain))
                return true;
        }
        return false;
    }

    public static AbstractInsnNode[] findPattern(MethodNode mn, AbstractInsnNode[] pattern) {
        ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
        i:
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            AbstractInsnNode[] fill = new AbstractInsnNode[pattern.length];
            for (int i = 0; i < pattern.length; i++) {
                if (ain == null)
                    continue i;
                if (ain.getOpcode() == Opcodes.GOTO) {
                    try {
                        ain = mn.instructions.get(mn.instructions.indexOf(((JumpInsnNode) ain).label)).getNext();
                    } catch (Exception e) {
                        continue i;
                    }
                }
                if (ain.getOpcode() == -1)
                    ain = ain.getNext();
                fill[i] = ain;
                if (!match(ain, pattern[i]))
                    continue i;
                ain = getNext(ain);
            }
            return fill;
        }
        return null;
    }

    public static AbstractInsnNode[] findPattern(MethodNode mn, AbstractInsnNode[] pattern, AbstractInsnNode start) {
        if (start == null)
            return null;
        AbstractInsnNode ain = start;
        i:
        while (ain.getNext() != null) {
            ain = ain.getNext();
            AbstractInsnNode[] fill = new AbstractInsnNode[pattern.length];
            AbstractInsnNode next = mn.instructions.get(mn.instructions.indexOf(ain)); // I'm trying to cheat mutability ffs
            for (int i = 0; i < pattern.length; i++) {
                if (next == null)
                    continue i;
                if (next.getOpcode() == Opcodes.GOTO) {
                    next = mn.instructions.get(mn.instructions.indexOf(((JumpInsnNode) next).label)).getNext();
                }
                if (next.getOpcode() == -1)
                    next = next.getNext();
                fill[i] = next;
                if (!match(next, pattern[i]))  {
                    continue i;
                }
                next = next.getNext();
            }
            return fill;
        }
        return null;
    }

    public static AbstractInsnNode[] findPatternNaive(MethodNode mn, AbstractInsnNode[] pattern, AbstractInsnNode start) {
        if (start == null)
            return null;
        AbstractInsnNode ain = start;
        i:
        while (ain.getNext() != null) {
            ain = ain.getNext();
            AbstractInsnNode[] fill = new AbstractInsnNode[pattern.length];
            AbstractInsnNode next = mn.instructions.get(mn.instructions.indexOf(ain));
            for (int i = 0; i < pattern.length; i++) {
                if (next == null)
                    continue i;
                if (next.getOpcode() == -1)
                    next = next.getNext();
                fill[i] = next;
                if (!match(next, pattern[i])) {
                    continue i;
                }
                next = next.getNext();
            }
            return fill;
        }
        return null;
    }

    // Snakes through labels
    public static AbstractInsnNode[] findPatternSnake(MethodNode mn, AbstractInsnNode[] pattern, AbstractInsnNode start) {
        LabelNode jumpMarker = null;
        if (start == null)
            return null;
        AbstractInsnNode ain = start;
        i:
        while (ain.getNext() != null) {
            AbstractInsnNode[] fill = new AbstractInsnNode[pattern.length];
            AbstractInsnNode next = mn.instructions.get(mn.instructions.indexOf(ain));
            for (int i = 0; i < pattern.length; i++) {
                if (next == null)
                    return null;
                if (next.getOpcode() == Opcodes.GOTO) {
                    JumpInsnNode jin = (JumpInsnNode) next;
                    if (jumpMarker == null)
                        jumpMarker = jin.label;
                    else if (jin.label.equals(jumpMarker)) // If we've already done this jump, get out
                        return null;
                    next = mn.instructions.get(mn.instructions.indexOf(((JumpInsnNode) next).label)).getNext();
                }
                if (next.getOpcode() == -1) // We don't want labels interfering
                    next = next.getNext();
                fill[i] = next;
                if (i == 0) {
                    ain = getNext(next);
                    if (ain == null)
                        return null;
                }
                if (!match(next, pattern[i])) {
                    continue i;
                }
                next = next.getNext();
            }
            return fill;
        }
        return null;
    }

    public static boolean match(AbstractInsnNode haystack, AbstractInsnNode needle) {
        if (needle instanceof NullInsnNode)
            return true;
        if (needle instanceof MethodInsnNode)
            return match(haystack, (MethodInsnNode) needle);
        else if (needle instanceof FieldInsnNode)
            return match(haystack, (FieldInsnNode) needle);
        else if (needle instanceof IntInsnNode)
            return match(haystack, (IntInsnNode) needle);
        else if (needle instanceof TypeInsnNode)
            return match(haystack, (TypeInsnNode) needle);
        else if (needle instanceof VarInsnNode)
            return match(haystack, (VarInsnNode) needle);
        else if (needle instanceof LdcInsnNode)
            return match(haystack, (LdcInsnNode) needle);
        else if (needle instanceof JumpInsnNode)
            return haystack instanceof JumpInsnNode;
        return haystack.getOpcode() == needle.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, MethodInsnNode b) {
        if (!(a instanceof MethodInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        MethodInsnNode c = (MethodInsnNode) a;
        if (b.desc != null)
            if (!b.desc.equals(c.desc))
                return false;
        if (b.owner != null)
            if (!b.owner.equals(c.owner))
                return false;
        if (b.name != null)
            if (!b.name.equals(c.name))
                return false;
        return c.getOpcode() == b.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, FieldInsnNode b) {
        if (!(a instanceof FieldInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        FieldInsnNode c = (FieldInsnNode) a;
        if (b.desc != null)
            if (!b.desc.equals(c.desc))
                return false;
        if (b.owner != null)
            if (!b.owner.equals(c.owner))
                return false;
        if (b.name != null)
            if (!b.name.equals(c.name))
                return false;
        return c.getOpcode() == b.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, IntInsnNode b) {
        if (!(a instanceof IntInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        IntInsnNode c = (IntInsnNode) a;
        if (b.operand != 9000)
            if (c.operand != b.operand)
                return false;
        return c.getOpcode() == b.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, TypeInsnNode b) {
        if (!(a instanceof TypeInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        TypeInsnNode c = (TypeInsnNode) a;
        if (c.desc != null)
            if (!c.desc.equals(b.desc))
                return false;
        return c.getOpcode() == b.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, VarInsnNode b) {
        if (!(a instanceof VarInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        VarInsnNode c = (VarInsnNode) a;
        if (b.var != 9000)
            if (c.var != b.var)
                return false;
        return c.getOpcode() == b.getOpcode();
    }

    public static boolean match(AbstractInsnNode a, LdcInsnNode b) {
        if (!(a instanceof LdcInsnNode))
            return false;
        if (b.getOpcode() == 0)
            return true;
        LdcInsnNode c = (LdcInsnNode) a;
        if (b.cst != null)
            if (!c.cst.equals(b.cst))
                return false;
        return c.getOpcode() == b.getOpcode();
    }
}

