package com.updater.utils.deobfuscate;

import com.updater.Updater;
import com.updater.utils.deobfuscate.container.Deobfuscator;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 05/10/2013
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class DummyRemover extends Deobfuscator {

    public int removed = 0;
    public static List<ClassNode> classes_temp = new ArrayList<>();

    public boolean init() {
        System.out.println("Running dummy method removal...");
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void execute() {
        for (ClassNode cn : Updater.classes) {
            nextCrystalMeth: for (int wat = 0; wat < cn.methods.size(); wat++) {
                final MethodNode meth = cn.methods.get(wat);
                if (isReferenced(meth)) {
                    continue nextCrystalMeth;
                }
                InsnList il = meth.instructions;
                for (int i = 0; i < il.size(); i++) {
                    if (il.get(i) instanceof MethodInsnNode) {
                        final MethodInsnNode min = (MethodInsnNode) il.get(i);
                        if (match(cn, meth, min) || isInheritedBySuper(cn, meth, min)) {
                            continue nextCrystalMeth;
                        }
                    }
                }
                if (!isNeeded(meth)) {
                    cn.methods.remove(meth);
                    removed++;
                }
            }
        }
    }

    public boolean isNeeded(MethodNode mn) {
        if (mn.name.toLowerCase().contains("mouse")) {
            return true;
        }
        return false;
    }

    public String conclusion() {
        //if (removed == 0)
        ///    completed = true;
        int r = removed;
        //removed = 0;
        completed = true;
        return String.format("Dummy Remover: Removed %s methods.", r);  //To change body of implemented methods use File | Settings | File Templates.
    }

    private final boolean match(ClassNode cn, MethodNode mn, MethodInsnNode methodInsnNode) {
        return cn.name.equals(methodInsnNode.owner) && mn.name.equals(methodInsnNode.name) && mn.desc.equals(methodInsnNode.desc);
    }

    private final boolean isInheritedByInterface(final ClassNode cn, final MethodNode mn, final MethodInsnNode min) {
        List<String> interfaces = cn.interfaces;

        for(final ClassNode node : Updater.classes) {
            for (String iface : interfaces) {
                if (iface.equals(node.name)) {
                    for (MethodNode superMethod : node.methods) {
                        if(superMethod.name.equals(mn.name) && superMethod.desc.equals(mn.desc) &&
                                superMethod.name.equals(min.name) && superMethod.desc.equals(min.desc)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private final boolean isInheritedBySuper(final ClassNode cn, final MethodNode mn, final MethodInsnNode min) {
        for(final ClassNode node : Updater.classes) {
            if(!node.name.equals(cn.superName)) {
                continue;
            }
            for(final MethodNode superMethod : node.methods) {
                if(superMethod.name.equals(mn.name) && superMethod.desc.equals(mn.desc) &&
                        superMethod.name.equals(min.name) && superMethod.desc.equals(min.desc)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean isReferenced(MethodNode method) {
        for (final ClassNode node : Updater.classes) {
            for (final MethodNode mn : node.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (!(ain instanceof MethodInsnNode)) {
                        continue;
                    }
                    MethodInsnNode min = (MethodInsnNode) ain;
                    if (method.name.equals(min.name) && method.desc.equals(min.desc) && node.name.equals(min.owner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
