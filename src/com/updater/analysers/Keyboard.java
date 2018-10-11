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
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public class Keyboard extends Analyser {
    @Override
    protected void finish() {

    }

    @Override
    protected int gettersTotal() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode node : classes) {
            for (MethodNode mn : node.methods) {
                if (mn.name.contains("key")) {
                    return node;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
