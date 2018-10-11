package com.updater.utils.analyse.container;

import com.updater.utils.AnalyserTools;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 23/08/2013
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class Field extends AnalyserTools {

    private String name;
    private ClassNode parent;
    private FieldNode field;
    private int multiplier = 1;
    private ClassNode subparent = null;

    public Field(String name, ClassNode node, FieldNode field, int multiplier, ClassNode subparent) {
        this.name = name;
        this.parent = node;
        this.field = field;
        this.multiplier = multiplier;
        this.subparent = subparent;
    }

    public String toString(boolean multipliers) {
        try {
            String owner = parent.name;
            if (subparent != null) {
                owner = getSubParent().name;
            }
            String out = String.format("\t[> '%s' identified as '", name).concat(String.format(owner + ".%s' -] (%s)", field.name, clean(field.desc)));
            if (field.desc.equals("I")) {
                int m = multiplier;
                if (!multipliers)
                    m = 69;
                out += String.format("\t[ * %s ]", String.valueOf(m));
            }
            return (out);
        } catch (Exception e) {

        }
        return "";
    }

    public void print(boolean multipliers) {
        System.out.println(toString(multipliers));
    }

    public ClassNode getParent() {
        return parent;
    }

    public ClassNode getSubParent() {
        return subparent;
    }

    public FieldNode getField() {
        return field;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getName() {
        return name;
    }


}
