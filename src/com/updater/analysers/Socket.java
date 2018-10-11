package com.updater.analysers;

import com.updater.utils.analyse.Analyser;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 12/09/2013
 * Time: 19:52
 * To change this template use File | Settings | File Templates.
 */
public class Socket extends Analyser {
    @Override
    protected void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int gettersTotal() {
        return 4;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected ClassNode find() {
        for (ClassNode cn : classes) {
            boolean finalize = false; boolean run = false;
            if (!cn.superName.equals("java/lang/Object")) {
                continue;
            }
            for (MethodNode mn : cn.methods) {
                if (mn.name.equals("run")) {
                    run = true;
                }
                if (mn.name.equals("finalize")) {
                    finalize = true;
                }
            }
            if (run && finalize)
                return cn;
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void getFields(ClassNode node) {
        for (FieldNode fn : node.fields) {
            if (Modifier.isStatic(fn.access))
                continue;
            switch (fn.desc) {
                case "[B":
                    addField("getBuffer()", fn);
                    break;
                case "Ljava/net/Socket;":
                    addField("getSocket()", fn);
                    break;
                case "Ljava/io/OutputStream;":
                    addField("getOutputStream()", fn);
                    break;
                case "Ljava/io/InputStream;":
                    addField("getInputStream()", fn);
                    break;
            }
        }
    }
}
