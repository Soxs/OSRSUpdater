package com.updater.utils.analyse.container;

import com.updater.utils.AnalyserTools;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 31/08/2013
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class Method extends AnalyserTools {

    private String name;
    private ClassNode parent;
    private ClassNode subparent;
    private MethodNode method;

    public Method(String name, MethodNode mn, ClassNode parent, ClassNode subparent) {
        this.name = name;
        this.method = mn;
        this.parent = parent;
        this.subparent = subparent;
    }

    public ClassNode getParent() {
        return this.parent;
    }

    public ClassNode getSubparent() {
        return this.subparent;
    }

    public MethodNode getMethod() {
        return this.method;
    }

    public String name() {
        return this.name;
    }

    public String desc() {
        return method.desc;
    }

    public String toString() {
        String owner = parent.name;
        if (subparent != null) {
            owner = getSubparent().name;
        }
        String out = String.format("\t[> Method '%s' identified as '", name).concat(
                String.format(owner + ".%s' -] (%s)", method.name, clean(method.desc)));
        return (out);
    }

    public void print() {
        System.out.println(toString());
    }

}
