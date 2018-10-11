package com.updater.utils.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * Created by Kevin on 26/09/13.
 */
public class Harvester implements Opcodes {
    private int multiplier;
    private FieldInsnNode field;
    private String owner;

    public Harvester(String owner) {
        this.multiplier = 0;
        this.owner = owner;
    }

    public Harvester() {
        this.multiplier = 0;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public FieldInsnNode getField() {
        return field;
    }

    public void scan(AbstractInsnNode ain) {
        if (ain.getOpcode() == LDC && multiplier == 0) {
            multiplier = (int)((LdcInsnNode)ain).cst;
        } else if (ain instanceof FieldInsnNode && field == null) {
            if (((FieldInsnNode)ain).owner.equals(owner) ^ owner == null)
                field = (FieldInsnNode)ain;
        }
    }

    public boolean finished() {
        return multiplier != 0 && field != null;
    }
}
