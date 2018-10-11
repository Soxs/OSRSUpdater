package com.updater.utils.generate;

import com.updater.Updater;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.analyse.container.Field;
import com.updater.utils.analyse.container.Method;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 29/08/2013
 * Time: 19:46
 * To change this template use File | Settings | File Templates.
 */
public class Dump {

    private List<Analyser> analysers = new ArrayList<>();
    private FileWriter fw;
    private BufferedWriter bw;

    public Dump(List<Analyser> analysers) {
        this.analysers = analysers;
    }

    public void generate(File file, boolean print) {
        System.out.println("Dumping modscript.");
        String output = "";
        for (Analyser analyser : analysers) {
            boolean printedInterface = false;
            for (Field field : analyser.fieldArray()) {
                if (!printedInterface) {
                    output += String.format("interface %s com/osr/accessors/%s" + "\n", analyser.getNode().name, analyser.getClass().getSimpleName());
                    printedInterface = true;
                }
                String parent;
                try {
                    parent = field.getParent().name + " " + field.getSubParent().name;
                } catch (Exception e) {
                    if (field.getParent().name.equals("client")) {
                        parent = "client " + field.getParent().name;
                    } else
                        parent = field.getParent().name;
                }

                String access = "field";
                if (Modifier.isStatic(field.getField().access) &&
                        field.getParent().name.equals("client")) {
                    access = "staticField";
                }
                output += String.format(access + " %s %s %s %s %s %s" + "\n",
                        parent,
                        field.getField().name,
                        field.getName().replace("()", ""),
                        getAccessor(field.getField().desc),
                        field.getField().desc,
                        String.valueOf(field.getMultiplier()));

            }
            for (Method method : analyser.methods) {
                output += String.format("method %s %s %s %s %s %s %s" + "\n",
                        method.getParent().name,
                        method.getSubparent().name,
                        method.getMethod().name,
                        method.name().replace("()", ""),
                        getAccessor(method.getMethod().desc).replace(getParams(method.getMethod().desc), ""),
                        method.getMethod().desc.replace(getParams(method.getMethod().desc), ""),
                        getParams(method.getMethod().desc));
            }
        }
        if (print)
            try {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(output);
                bw.close();
                System.out.println("Dumped modscript.");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getParams(String raw) {
        return (String.format("(%s)", raw.split("\\)")[0].replace("(", "")));
    }

    public String getAccessor(String desc) {
        if (!desc.contains("/")) {
            for (Analyser a : Updater.analysers) {
                if (a.isBroken())
                    continue;
                ClassNode cn = a.getNode();
                if (desc.contains("L" + cn.name + ";")) {
                    String ret = (desc.replace(cn.name, "com/osr/accessors/" + a.getClass().getSimpleName()));
                    if (ret != null)
                        return ret;
                    else
                        return desc;
                }
            }
        }
        return desc;
    }

}
