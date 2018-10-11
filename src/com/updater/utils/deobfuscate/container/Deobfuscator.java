package com.updater.utils.deobfuscate.container;

/**
 * Created with IntelliJ IDEA.
 * User: Zak
 * Date: 05/10/2013
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class Deobfuscator {

    public boolean completed = false;

    public abstract boolean init();

    public abstract void execute();

    public abstract String conclusion();

    public void run() {
        if (init()) {
            while (!completed) {
                execute();
                System.out.println(String.format("Deobfuscator: %s", conclusion()));
            }
        }
    }

}
