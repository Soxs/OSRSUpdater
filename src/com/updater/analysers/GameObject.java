package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 28/08/2013
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class GameObject extends Analyser {
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
            if (!node.superName.equals("java/lang/Object")) {
                continue;
            }
            boolean rend = false;
            int pubints = 0;
            int ints = 0;

            for (FieldNode fn : node.fields) {
                if (!Modifier.isStatic(fn.access) &&
                        fn.desc.equals(String.format("L%s;", getClassAnalyser("Renderable").getNode().name))) {
                    rend = true;
                }
                if (!Modifier.isStatic(fn.access) &&
                        fn.desc.equals("I")) {
                    if(fn.access==1)
                        pubints++;
                    else if(fn.access==0)
                        ints++;

                }
            }
            //if(pubints!=1 || ints!=12)
                //continue;
            if (rend) {
                return node;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access))
                continue;
            if (fn.desc.equals(String.format("L%s;", getClassAnalyser("Renderable").getNode().name))) {
                addField("getRenderable()", fn);
            }
            if (fn.desc.equals("I") && (!Modifier.isStatic(fn.access)) && (Modifier.isPublic(fn.access))) {
                addField("getId()", fn);
            }
        }
    }
}
