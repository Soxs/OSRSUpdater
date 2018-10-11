package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 25/08/2013
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class Region extends Analyser {
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
        for (ClassNode n : classes) {
            int tripleI = 0;
            if (n.superName.contains("Object")) {
                for (FieldNode fn : n.fields) {
                    if (!Modifier.isStatic(fn.access) && !Modifier.isPublic(fn.access)) {
                        if (fn.desc.equals("[[I") || fn.desc.contains("[[[I"))
                            tripleI++;
                    }
                }
                if (tripleI == 4) {
                    return n;
                }
            }
            //for (MethodNode mn : n.methods) {
            //    if (mn.desc.equalsIgnoreCase(String.format("(IIIIL%s;II)V", getClassAnalyser("Renderable").getNode().name))) {
            //        return n;
            //    }
            //}
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (!Modifier.isStatic(fn.access) && fn.desc.equals(String.format("[L%s;", getClassAnalyser("GameObject").getNode().name))) {
                addField("getGameObjects()", fn);
            }
            if (Modifier.isStatic(fn.access) && fn.desc.equals(String.format("[L%s;", getClassAnalyser("GameObject").getNode().name))) {
                addField("getGameObjectCache()", fn);
            }
        }
    }
}
