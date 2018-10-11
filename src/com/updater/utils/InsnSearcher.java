package com.updater.utils;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.List;

public class InsnSearcher {

    private final AbstractInsnNode[] instructions;
    private int index;

    public InsnSearcher(final InsnList instructions) {
        this.instructions = instructions.toArray();
        this.index = 0;
    }

    public InsnSearcher(final AbstractInsnNode[] insns) {
        this.instructions = insns;
        this.index = 0;
    }

    public AbstractInsnNode next() {
        if (instructions != null && index < instructions.length) {
            return instructions[index++];
        }
        return null;
    }

    public <T> T next(final Class<T> parent, final int opcode) {
        AbstractInsnNode node;
        while ((node = next()) != null) {
            if (node.getOpcode() == opcode && parent.isAssignableFrom(node.getClass())) {
                return parent.cast(node);
            }
        }
        return null;
    }

    public <T> T next(final Class<T> parent, final Condition condition) {
        AbstractInsnNode node;
        while ((node = next()) != null) {
            if (condition.equals(node) && parent.isAssignableFrom(node.getClass())) {
                return parent.cast(node);
            }
        }
        return null;
    }

    public <T> T next(final Class<T> parent, final int opcode, int skips) {
        T node = null;
        for (int i = 0; i <= skips; i++) {
            if ((node = next(parent, opcode)) == null) {
                return null;
            }
        }
        return node;
    }

    public <T> T next(final Class<T> parent, final Condition condition, int skips) {
        T node = null;
        for (int i = 0; i <= skips; i++) {
            if ((node = next(parent, condition)) == null) {
                return null;
            }
        }
        return node;
    }

    public AbstractInsnNode previous() {
        try {
            if (instructions != null && index > 0) {
                return instructions[--index];
            }
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public <T> T previous(final Class<T> parent, final int opcode) {
        AbstractInsnNode node;
        while ((node = previous()) != null) {
            if (node.getOpcode() == opcode && parent.isAssignableFrom(node.getClass())) {
                return parent.cast(node);
            }
        }
        return null;
    }

    public <T> T previous(final Class<T> parent, final Condition condition) {
        AbstractInsnNode node;
        while ((node = previous()) != null) {
            if (condition.equals(node) && parent.isAssignableFrom(node.getClass())) {
                return parent.cast(node);
            }
        }
        return null;
    }


    public <T> T previous(final Class<T> parent, final int opcode, int skips) {
        T node = null;
        for (int i = 0; i <= skips; i++) {
            if ((node = previous(parent, opcode)) == null) {
                return null;
            }
        }
        return node;
    }

    public <T> T previous(final Class<T> parent, final Condition condition, int skips) {
        T node = null;
        for (int i = 0; i <= skips; i++) {
            if ((node = previous(parent, condition)) == null) {
                return null;
            }
        }
        return node;
    }

    public boolean has(int opcode) {
        AbstractInsnNode node;
        while ((node = next()) != null) {
            if (node.getOpcode() == opcode) {
                return true;
            }
        }
        return false;
    }

    public <T> List<T> findAll(Class<T> type, int opcode) {
        List<T> foundInstructions = new ArrayList<T>();
        for (int i = 0; i < instructions.length; ++i) {
            if (type.isAssignableFrom(instructions[i].getClass()) && instructions[i].getOpcode() == opcode) {
                foundInstructions.add(type.cast(instructions[i]));
            }
        }
        return foundInstructions.size() > 0 ? foundInstructions : null;
    }

    public boolean hasNext() {
        return instructions != null && index < (instructions.length - 1);
    }

    public boolean hasPrevious() {
        return instructions != null && index > 0;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public AbstractInsnNode current() {
        return instructions[index];
    }

    public int size() {
        return instructions.length;
    }
}