package com.updater;

import com.updater.analysers.*;
import com.updater.rsloader.Data;
import com.updater.rsloader.PageParser;
import com.updater.utils.ASMUtility;
import com.updater.utils.deobfuscate.DeadCode;
import com.updater.utils.deobfuscate.DummyRemover;
import com.updater.utils.deobfuscate.Exceptions;
import com.updater.utils.deobfuscate.OptimizeJumps;
import com.updater.utils.deobfuscate.container.Deobfuscator;
import com.updater.utils.multiplier.Mul;
import com.updater.utils.multiplier.MultiplierAnalyser;
import com.updater.utils.analyse.Analyser;
import com.updater.utils.generate.Dump;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 23/08/13
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class Updater {

    boolean showMultipliers = true;

    public static Map<String, String> clazzes = new HashMap<>();
    public static List<ClassNode> classes = new ArrayList<>();

    public static List<Analyser> analysers = new ArrayList<>();

    public static List<Deobfuscator> deobbers = new ArrayList<>();

    private PageParser parser;

    public void addAnalysers() {
        analysers.add(new Node());
        analysers.add(new CacheableNode());
        analysers.add(new HashTable());
        analysers.add(new LinkedList());
        analysers.add(new Queue());
        analysers.add(new Cache());
        analysers.add(new Renderable());
        analysers.add(new CollisionData());
        analysers.add(new Actor());
        analysers.add(new Projectile());
        analysers.add(new ProjectileComposite());
        analysers.add(new Item());
        analysers.add(new ItemComposite());
        analysers.add(new Model());
        analysers.add(new GameObject());
        analysers.add(new GameObjectComposite());
        analysers.add(new Player());
        analysers.add(new PlayerComposite());
        analysers.add(new Npc());
        analysers.add(new NpcComposite());
        analysers.add(new Widget());
        analysers.add(new WidgetNode());
        analysers.add(new Region());
        analysers.add(new WallObject());
        analysers.add(new FloorObject());
        analysers.add(new Tile());
        analysers.add(new Client());
        //analysers.add(new Socket());
        analysers.add(new Mouse());
        analysers.add(new Keyboard());
    }

    public void addDeobbers() {
        deobbers.add(new DummyRemover());
        deobbers.add(new DeadCode());
        deobbers.add(new OptimizeJumps());
        deobbers.add(new Exceptions());
    }

    public Updater() {
        System.out.println("Starting OSRS Updater.");
        boolean useFile = false;
        File f;

        long begin = System.currentTimeMillis();

        if (useFile) {
            f = new File("alora_client.jar");
            System.out.println(f.getAbsoluteFile());
            classes = ASMUtility.loadNodes(f);
            System.out.println("Finished loading " + classes.size() + " classes in " + (System.currentTimeMillis() - begin) + "ms from: " + f.getAbsolutePath());
        } else {
            parser = new PageParser(); //auto-download latest rs.
            classes = ASMUtility.downloadNodes(parser);
            System.out.println("Revision: " + Data.jarRevision);
            System.out.println("Finished loading " + classes.size() + " classes in " + (System.currentTimeMillis() - begin) + "ms from: " + Data.localJarLocation);
        }

        MultiplierAnalyser.findMultipliers(classes);
        Mul.decideMultipliers();
        System.out.println("Stored " + Mul.multipliers.size() + " multipliers");
        addDeobbers();
        addAnalysers();

        begin = System.currentTimeMillis();
        int foundField = 0;
        int totalField = 0;
        int foundMethod = 0;
        int foundClass = 0;

        for (Deobfuscator deobber : deobbers) {
            deobber.run();
        }

        for (Analyser analyser : analysers) {
            try {
                analyser.run();
                if (!analyser.isBroken()) {
                    foundClass++;
                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Analyser a : analysers) {
            foundField += a.getFoundFields();
            totalField += a.getTotalFields();
            foundMethod += a.methods.size();
            if (!a.isBroken())
                a.print(showMultipliers);
        }
        for (Analyser a : analysers) {
            if (a.isBroken())
                System.out.println("\n\t-> " + a.getClass().getSimpleName() + " is broken.");
        }
        long end = System.currentTimeMillis() - begin;
        System.out.println("\nIdentified " + foundField + "/" + totalField + " fields");
        System.out.println("Identified " + foundClass + "/" + analysers.size() + " classes");
        System.out.println("Also, identified " + foundMethod + " methods");
        System.out.println("Finished analyzing in " + /*elapsed*/ end + " ms");
        Dump modscript = new Dump(analysers);
        modscript.generate(new File("hooks.infi"), true);
    }

    public static void main(String... args) {
        new Updater();
    }
}
