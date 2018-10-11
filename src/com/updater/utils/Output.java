package com.updater.utils;

import com.updater.Updater;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.analyse.container.Field;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 25/08/2013
 * Time: 01:14
 * To change this template use File | Settings | File Templates.
 */
public class Output {

    public static void printAllHooks() {
        for (Analyser a : Updater.analysers) {
            for (Field f : a.fieldArray()) {
                String sub;
                if (f.getSubParent() == null) {
                    sub = "null";
                } else {
                    sub = f.getSubParent().name;
                }
                System.out.println(String.format("%s\n\tidentified as\t%s\tinside\t%s\t(%s)\t[%s]\n", f.getName(), f.getField().name, f.getParent().name, sub, f.getMultiplier()));
            }
        }
    }

}
