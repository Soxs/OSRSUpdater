package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 8/29/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WallObject extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 3;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            ClassNode region = getClassAnalyser("Region").getNode();
            for (final MethodNode mn : region.methods) {
                if (mn.desc.equals(String.format("(III)L%s;", cn.name))) {
                    return cn;
                }
            }
        }
        return null;
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("Renderable").getNode().name))) {
                addField("getRenderable()", fn);
            }
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("Renderable").getNode().name)) && (!getField("getRenderable()").getField().name.equals(fn.name))) {
                addField("getRenderable2()", fn);
            }
            if (fn.desc.equals("I") && (!Modifier.isStatic(fn.access)) && (Modifier.isPublic(fn.access))) {
                addField("getId()", fn);
            }
        }
    }
}
