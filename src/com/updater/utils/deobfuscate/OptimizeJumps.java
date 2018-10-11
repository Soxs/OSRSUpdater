package com.updater.utils.deobfuscate;

import com.updater.Updater;
import com.updater.utils.deobfuscate.container.Deobfuscator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 05/10/2013
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class OptimizeJumps extends Deobfuscator implements Opcodes {
    @Override
    public boolean init() {
        System.out.println("Running jump optimizer...");
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int cleaned = 0;

    @Override
    public void execute() {
        for (ClassNode cn : Updater.classes) {
            for (MethodNode mn : cn.methods) {
                optimizeJumps(mn);
                cleaned++;
            }
        }
    }

    @Override
    public String conclusion() {
        completed = true;
        return String.format("Jump Optimizer: cleaned %s methods.", cleaned);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void optimizeJumps(MethodNode mn){
        ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();

        while (iterator.hasNext()){
            AbstractInsnNode ain = iterator.next();

            if(ain instanceof JumpInsnNode){
                LabelNode lbl = ((JumpInsnNode) ain).label;
                AbstractInsnNode tgt;

                while (true){
                    tgt = lbl;
                    while (tgt != null && tgt.getOpcode() < 0){
                        tgt = tgt.getNext();
                    }
                    if(tgt != null && tgt.getOpcode() == GOTO){
                        //System.err.println("Optimizing jump insns..");
                        lbl = ((JumpInsnNode)tgt).label;
                    }else{
                        break;
                    }
                }

                ((JumpInsnNode) ain).label = lbl;

                if(ain.getOpcode() == GOTO && tgt != null){
                    int opcode = tgt.getOpcode();

                    if((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW){
                        //System.out.println("Optimizing jump instructions..");
                        mn.instructions.set(ain, tgt.clone(null));
                    }

                }

            }

        }
    }

}
