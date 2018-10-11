package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import com.updater.utils.asm.Harvester;
import com.updater.utils.asm.NullInsnNode;
import com.updater.utils.asm.Pattern;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 24/08/2013
 * Time: 01:09
 * To change this template use File | Settings | File Templates.
 */
public class Npc extends Analyser {
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
            if (node.superName.equals(getClassAnalyser("Actor").getNode().name) &&
                    !node.name.equals(getClassAnalyser("Player").getNode().name)) {
                return node;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if ((Modifier.isStatic(fn.access) || (Modifier.isFinal(fn.access)))) {
                continue;
            }
            addField("getComposite()", fn);
        }
    }
}
