package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class Cache extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 5;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            if (node.superName.equalsIgnoreCase("java/lang/Object") &&
                    !node.name.equals(getClassAnalyser("Queue").getNode().name)) {
                for (FieldNode fn : node.fields) {
                    if (fn.desc.equals(String.format("L%s;", getClassAnalyser("CacheableNode").getNode().name))) {
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
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("CacheableNode").getNode().name))) {
                addField("getEmptyCacheableNode()", fn);
            }
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("Queue").getNode().name))) {
                addField("getQueue()", fn);
            }
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("HashTable").getNode().name))) {
                addField("getHashTable()", fn);
            }
        }
        for (FieldNode fn : node.fields) {
            if (!Modifier.isStatic(fn.access)) {
                if (fn.desc.equals("I")) {
                    addField("getRemaining()", fn);
                }
            }
        }
        for (FieldNode fn : node.fields) {
            if (!Modifier.isStatic(fn.access)) {
                if (fn.desc.equals("I") && fn != getField("getRemaining()").getField()) {
                    addField("getSize()", fn);
                }
            }
        }
    }
}
