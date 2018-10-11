package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 26/08/2013
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class ProjectileComposite extends Analyser {
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
        for (final ClassNode node : classes) {
            if (node.superName.equals(getClassAnalyser("CacheableNode").getNode().name)) {
                if (getField("getProjectileComposite()", getClassAnalyser("Projectile")).getField().desc.equals("L" + node.name + ";")) {
                    return node;
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (fn.desc.equals("Z")) {
                addField("isMoving()", fn);
            }
        }
    }
}
