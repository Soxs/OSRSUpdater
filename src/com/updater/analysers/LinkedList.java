package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 24/08/2013
 * Time: 00:13
 * To change this template use File | Settings | File Templates.
 */
public class LinkedList extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 2;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            for (MethodNode mn : node.methods) {
                if (mn.desc.equals("(L" + getClassAnalyser("Node").getNode().name + ";)V")) {
                    return node;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (!Modifier.isPublic(fn.access)) {
                addField("getCurrent()", fn);
            } else {
                addField("getHead()", fn);
            }
        }
    }
}
