package com.updater.utils.multiplier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by Kevin on 26/09/13.
 */
public class MultiplierFinder {
    private HashMap<Integer, Integer> counts = new HashMap();

    private String name;

    public MultiplierFinder(FieldNode fieldNode) {
        this.name = fieldNode.name;
    }

    public int getMultiplier() {
        if (counts.size() < 1)
            return -1;
        int index = 0;
        int c = (int)counts.values().toArray()[0];
        for (int z = 0; z < counts.values().size(); z++) {
            if ((int)counts.values().toArray()[z] > c) {
                c = (int)counts.values().toArray()[z];
                index = z;
            }
        }
        return (int)counts.keySet().toArray()[index];
    }

    public void record(ClassNode cn) {
        ListIterator<MethodNode> mnli = cn.methods.listIterator();
        while (mnli.hasNext()) {
            MethodNode mn = mnli.next();
            ListIterator<AbstractInsnNode> ainli = mn.instructions.iterator();
            while (ainli.hasNext()) {
                AbstractInsnNode ain = ainli.next();
                if (ain.getOpcode() == Opcodes.IMUL) {
                    if (ainli.previousIndex() - 3 < 0)
                        continue;
                    ainli.previous();
                    // it can either be ldc var mul or var ldc mul, need to check both although
                    // Jagex's obber seems to like putting the ldc last
                    AbstractInsnNode p1 = ainli.previous();    //->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    AbstractInsnNode p2 = ainli.previous();                                                      //   V
                    if (p1.getOpcode() == Opcodes.GETFIELD) {                                                    //   V
                        if (((FieldInsnNode)p1).name.equals(name)) {                                             //   V
                            if (p2.getOpcode() == Opcodes.LDC) {                                                 //   V
                                int mul = (int)((LdcInsnNode)p2).cst;                                            //   V
                                if (counts.containsKey(mul))                                                     //   V
                                    counts.put((int)((LdcInsnNode)p2).cst, counts.get(mul) + 1);                 //   V
                                else                                                                             //   V
                                    counts.put((int)((LdcInsnNode)p2).cst, 1);                                   //   V
                            }                                                                                    //   V
                        }                                                                                        //   V
                    } else if (p1.getOpcode() == Opcodes.LDC) {                                                  //   V
                        if (p2.getOpcode() == Opcodes.GETFIELD) {                                                //   V
                            if (((FieldInsnNode)p2).name.equals(name)) {                                         //   V
                                int mul = (int)((LdcInsnNode)p1).cst;                                            //   V
                                if (counts.containsKey(mul))                                                     //   V
                                    counts.put((int)((LdcInsnNode)p1).cst, counts.get(mul) + 1);                 //   V
                                else                                                                             //   V
                                    counts.put((int)((LdcInsnNode)p1).cst, 1);                                   //   V
                            }                                                                                    //   V
                        }                                                                                        //   V
                    }                                                                                            //   V
                    ainli.next();  //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-
                    ainli.next();
                    ainli.next();
                }
            }
        }
    }
}
