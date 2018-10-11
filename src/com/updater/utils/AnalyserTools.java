package com.updater.utils;

import com.updater.Updater;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.analyse.container.Field;
import com.updater.utils.wildcard.WildCard;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AnalyserTools extends WildCard {

    public void addClazz(String name, String owner) {
        Updater.clazzes.put(name, owner);
    }

    public String getClazz(String name) {
        return Updater.clazzes.get(name);
    }

    public ClassNode findNode(String name) {
        for (ClassNode cn : Updater.classes) {
            if (cn.name.equals(name)) {
                return cn;
            }
        }
        return null;
    }

    public FieldNode insnToField(FieldInsnNode insn) {
        for (org.objectweb.asm.tree.ClassNode cn : Updater.classes) {
            if (cn.name.equals(insn.owner)) {
                for (FieldNode f : cn.fields) {
                    if (f.name.equals(insn.name)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param insn
     * @param cn
     * @return Converted FieldInsnNode to corresponsive FieldNode
     */
    public FieldNode insnToField(FieldInsnNode insn, ClassNode cn) {
        for (FieldNode fn : cn.fields) {
            if (fn.name.equals(insn.name)) {
                return fn;
            }
        }
        return null;
    }

    /**
     * @param fn
     * @param cn
     * @return Converted FieldNode to corresponsive FieldInsnNode
     *         *Not tested.
     */
    public FieldInsnNode fieldToInsn(FieldNode fn, ClassNode cn) {
        return new FieldInsnNode(fn.access, cn.name, fn.name, fn.desc);
    }

    @Deprecated
    public Field getExternalField(String name, Analyser analyser) {
        for (Field f : analyser.fieldArray()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public String clean(String s) {
        return s.replace("java/awt/", "").replace("java/lang/", "");
    }

    public boolean isInt(FieldNode f) {
        try {
            return f.desc.equals("I");
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }


}
