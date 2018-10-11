package com.updater.utils.deobfuscate;

import com.updater.Updater;
import com.updater.utils.deobfuscate.container.Deobfuscator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.Frame;


/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 05/10/2013
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
public class DeadCode extends Deobfuscator {

    public int cleaned = 0;

    @Override
    public boolean init() {
        System.out.println("Running dead code removal...");
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute() {
        for (ClassNode cn : Updater.classes) {
            removeDeadCode(cn);
            cleaned++;
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String conclusion() {
        completed = true;

        return String.format("Dead Code: cleaned %s classes.", String.valueOf(cleaned));  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeDeadCode(ClassNode cn){
        org.objectweb.asm.tree.analysis.Analyzer asmAnalyzer = new org.objectweb.asm.tree.analysis.Analyzer(new BasicInterpreter());

        for(MethodNode mn : cn.methods){
            try{
                asmAnalyzer.analyze(cn.name, mn);

                Frame[] analyzerFrames = asmAnalyzer.getFrames();
                AbstractInsnNode[] ains = mn.instructions.toArray();

                for(int i = 0; i < analyzerFrames.length; i++) {
                    if(analyzerFrames[i] == null && !(ains[i] instanceof LabelNode)) {
                        mn.instructions.remove(ains[i]);
                        System.out.println("Removing dead code..");
                    }
                }

            }catch (AnalyzerException e){
                e.printStackTrace();
            }
        }
    }

}
