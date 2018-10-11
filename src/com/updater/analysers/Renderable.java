package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class Renderable extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            if (cn.superName.equals(getClassAnalyser("CacheableNode").getNode().name)) {
                if (Modifier.isAbstract(cn.access)) {
                    /*if (cn.fields.size() <= 10) {
                        return cn;
                    }*/
                    return cn;
/*
                    for (MethodNode mn : cn.methods) {
                        if (wildcard("(IIIIIIII*)V", mn.desc)) {
                            return cn;
                        }
                    }*/
                }
            }
        }
        return null;
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (!Modifier.isStatic(fn.access)) {
                if (fn.desc.equals("I")) {
                    addField("getModelHeight()", fn);
                }
            }
        }
    }
}
