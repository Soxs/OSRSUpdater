package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 22:39
 * To change this template use File | Settings | File Templates.
 */
public class HashTable extends Analyser {
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
        for (ClassNode node : classes) {
            for (final Object x : node.fields) {
                FieldNode fn = (FieldNode) x;
                if (node.superName.equalsIgnoreCase("java/lang/object")) {
                    if (fn.desc.equals(String.format("[L%s;", getClassAnalyser("Node").getNode().name))) {
                        return node;
                    }
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals(String.format("[L%s;", getClassAnalyser("Node").getNode().name))) {
                addField("getCache()", fn);
            }
        }
    }
}
