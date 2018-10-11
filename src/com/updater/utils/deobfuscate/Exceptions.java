package com.updater.utils.deobfuscate;

import com.updater.Updater;
import com.updater.utils.asm.Searcher;
import com.updater.utils.deobfuscate.container.Deobfuscator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * Created by zakariapacha on 10/10/2018.
 */
public class Exceptions extends Deobfuscator {

    private int Fixed = 0;

    @Override
    public boolean init() {
        System.out.println("Running exceptions removal...");
        return true;
    }

    @Override
    public void execute() {
        int Patterns[][] = new int[][]{
                {Opcodes.ILOAD, Opcodes.LDC, Searcher.IF, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Searcher.CONSTPUSH, Searcher.IF, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_0, Opcodes.IF_ICMPEQ, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_M1, Opcodes.IF_ICMPNE, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
                {Opcodes.ILOAD, Opcodes.ICONST_0, Opcodes.IF_ICMPGT, Opcodes.NEW, Opcodes.DUP, Opcodes.INVOKESPECIAL, Opcodes.ATHROW},
        };
        Fixed = 0;
        for (ClassNode Class : Updater.classes) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode Method : methodList) {
                Searcher Search = new Searcher(Method);
                for (int[] Pattern : Patterns) {
                    int L = Search.find(Pattern, 0);
                    int Count = 0;
                    int Check = 0;
                    out:
                    while (L != -1) {
                        ++Check;
                        if (Check > 100)
                            break out;
                        if (Method.instructions.get(L + 5) instanceof MethodInsnNode) {
                            LabelNode jmp = ((JumpInsnNode) Method.instructions.get(L + 2)).label;
                            Method.instructions.insertBefore(Method.instructions.get(L), new JumpInsnNode(Opcodes.GOTO, jmp));
                            for (int j = 0; j < Pattern.length; ++j)
                                Method.instructions.remove(Method.instructions.get(L + 1));
                            ++Count;
                            ++Fixed;
                            L = Search.find(Pattern, Count);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String conclusion() {
        if (Fixed == 0)
            completed = true;

        int f = Fixed;
        Fixed = 0;

        return String.format("Exceptions Remover: Removed %s exceptions.", f);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
