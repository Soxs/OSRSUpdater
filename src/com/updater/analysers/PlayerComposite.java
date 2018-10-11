package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 25/08/2013
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class PlayerComposite extends Analyser {
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
        for (final ClassNode node : classes) {
            if (!node.superName.equals("java/lang/Object") ||
                    !Modifier.isPublic(node.access))
                continue;
            if (getField("getPlayerComposite()", getClassAnalyser("Player")).getField().desc.equals(String.format("L%s;", node.name))) {
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
            if (fn.desc.equals("Z"))
                addField("isFemale()", fn);
            if (fn.desc.equals("[I"))
                addField("getEquipment()", fn);
        }
    }
}
