package com.updater.utils.analyse;

import com.updater.Updater;
import com.updater.utils.AnalyserTools;
import com.updater.utils.InsnSearcher;
import com.updater.utils.multiplier.Mul;
import com.updater.utils.analyse.container.Field;
import com.updater.utils.analyse.container.Method;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 23/08/13
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class Analyser extends AnalyserTools implements Opcodes {

    public ArrayList<Field> fields = new ArrayList<>();
    public ArrayList<Method> methods = new ArrayList<>();
    private ClassNode node;
    private long elapsed;
    private long start;
    public final String name = getClass().getSimpleName();
    protected final List<ClassNode> classes = Updater.classes;
    private boolean broken;

    protected Analyser() {
        start = System.currentTimeMillis();
    }

    protected abstract void finish();

    protected abstract int gettersTotal();

    protected abstract ClassNode find();

    protected abstract void getFields(ClassNode node);

    public void run() {
        node = find();
        if (node != null) {
            broken = false;
            try {
                getFields(getNode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            broken = true;
        finish();
        elapsed += (System.currentTimeMillis() - start);
    }

    public boolean isBroken() {
        return broken;
    }

    public ClassNode getNode() {
        return node;
    }

    public int getFoundFields() {
        return fieldArray().size();
    }

    public int getTotalFields() {
        return gettersTotal();
    }

    public Analyser getClassAnalyser(String name) {
        for (Analyser Analyser : Updater.analysers) {
            if (Analyser.name.equals(name)) {
                return Analyser;
            }
        }
        return null;
    }

    public long getElapsed() {
        return elapsed;
    }

    @Override
    public String toString() {
        String s = "";
        return "\n[- " + this.getClass().getSimpleName() + " identified as: " + node.name + " extends " + clean(node.superName) + " -]" + s;
    }

    public void print(boolean multipliers) {
        System.out.println(toString().concat(String.format("(%s/%s)", String.valueOf(getFoundFields()), String.valueOf(getTotalFields()))));
        for (Field f : fieldArray()) {
            f.print(multipliers);
        }
        for (Method m : methods) {
            m.print();
        }
    }

    public void addField(String name, FieldNode f, int multiplier, ClassNode subparent) {
        Field field = new Field(name, getNode(), f, multiplier, subparent);
        if (getField(name) == null)
            fields.add(field);
    }

    public void addField(String name, FieldNode f, int multiplier) {
        addField(name, f, multiplier, null);
    }

    public void addField(String name, FieldNode f, ClassNode subparent) {
        if (isInt(f)) {
            try {
                addField(name, f, Mul.get(findNode(subparent.name).name, f.name), subparent);
            } catch (Exception e) {
                addField(name, f, 1, subparent);
            }
        } else {
            addField(name, f, 1, subparent);
        }
    }

    public void addField(String name, FieldNode f) {
        if (isInt(f)) {
            try {
                addField(name, f, Mul.get(getNode().name, f.name));
            } catch (Exception e) {
                addField(name, f, 1);
            }
        } else {
            addField(name, f, 1);
        }
    }

    private void addMethod(String name, MethodNode mn, ClassNode parent, ClassNode subparent) {
        Method m = new Method(name, mn, parent, subparent);
        if (getMethod(name) == null)
            methods.add(m);
    }

    public void addMethod(String name, MethodNode mn, ClassNode subparent) {
        addMethod(name, mn, getNode(), subparent);
    }

    public void addMethod(String name, MethodNode mn) {
        addMethod(name, mn, null);
    }

    public ArrayList<Field> fieldArray() {
        return fields;
    }

    public Method getMethod(String name) {
        return getMethod(name, this);
    }

    public Method getMethod(String name, Analyser analyser) {
        for (Method m : analyser.methods) {
            if (m.name().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public Field getField(String name) {
        return getField(name, this);
    }

    public Field getField(String name, Analyser analyser) {
        for (Field f : analyser.fieldArray()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public boolean isDuplicate(FieldNode fn) {
        for (Field f : fieldArray()) {
            if (f.getField() == fn) {
                return true;
            }
        }
        return false;
    }

    public static AbstractInsnNode[] findPattern(final MethodNode mn, final AbstractInsnNode[] pattern) {
        final ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
        i:
        while (ainli.hasNext()) {
            AbstractInsnNode ain = ainli.next();
            final AbstractInsnNode[] fill = new AbstractInsnNode[pattern.length];
            for (int i = 0; i < pattern.length; i++) {
                if (ain.getOpcode() == Opcodes.GOTO) {
                    ain = mn.instructions.get(mn.instructions.indexOf(((JumpInsnNode) ain).label)).getNext();
                }
                fill[i] = ain;
                InsnSearcher insnSearcher = new InsnSearcher(pattern);
                if (insnSearcher.size() > 0)
                    //if (!match(ain, pattern[i]))
                    continue i;
                ain = ain.getNext();
            }
            return fill;
        }
        return null;
    }
}
