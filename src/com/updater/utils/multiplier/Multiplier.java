package com.updater.utils.multiplier;

import org.objectweb.asm.Opcodes;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 24/08/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class Multiplier implements Opcodes {

    private enum MULTIPLIER_TYPE {
        INTEGER, DOUBLE, FLOAT, LONG
    }

    private int i = -1;
    private double d = -1;
    private float f = -1;
    private long l = -1;

    public Multiplier(final int i) {
        this.i = i;
    }

    public Multiplier(final double d) {
        this.d = d;
    }

    public Multiplier(final float f) {
        this.f = f;
    }

    public Multiplier(final long l) {
        this.l = l;
    }

    private MULTIPLIER_TYPE getType() {
        return i != -1 ? MULTIPLIER_TYPE.INTEGER : d != -1 ? MULTIPLIER_TYPE.DOUBLE : f != -1 ? MULTIPLIER_TYPE.FLOAT : MULTIPLIER_TYPE.LONG;
    }

    public Object getMultiple() {
        final MULTIPLIER_TYPE type = getType();
        if (type == MULTIPLIER_TYPE.INTEGER) {
            return i;
        } else if (type == MULTIPLIER_TYPE.DOUBLE) {
            return d;
        } else if (type == MULTIPLIER_TYPE.FLOAT) {
            return f;
        } else {
            return l;
        }
    }

    public int getMUL() {
        final MULTIPLIER_TYPE type = getType();
        if (type == MULTIPLIER_TYPE.INTEGER) {
            return IMUL;
        } else if (type == MULTIPLIER_TYPE.DOUBLE) {
            return DMUL;
        } else if (type == MULTIPLIER_TYPE.FLOAT) {
            return FMUL;
        } else {
            return LMUL;
        }
    }

    public int getRETURN() {
        final MULTIPLIER_TYPE type = getType();
        if (type == MULTIPLIER_TYPE.INTEGER) {
            return IRETURN;
        } else if (type == MULTIPLIER_TYPE.DOUBLE) {
            return DRETURN;
        } else if (type == MULTIPLIER_TYPE.FLOAT) {
            return FRETURN;
        } else {
            return LRETURN;
        }
    }
}
