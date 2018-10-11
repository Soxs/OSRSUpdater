package com.updater.utils;

import com.updater.Updater;
import com.updater.rsloader.PageParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ASMUtility implements Opcodes {

    private static final Map<String, Integer> OPCODE_MAP = new HashMap<>();
    private static final Map<String, Integer> TYPE_MAP = new HashMap<>();

    static {
        for (Field f : Opcodes.class.getDeclaredFields()) {
            putEntry(f, OPCODE_MAP);
        }
        for (Field f : AbstractInsnNode.class.getDeclaredFields()) {
            putEntry(f, TYPE_MAP);
        }
    }

    private static void putEntry(Field f, Map<String, Integer> map) {
        try {
            map.put(f.getName(), (Integer) f.get(null));
        } catch (IllegalAccessException e) {
            //control flow with catch ftw
        }
    }

    public static int getOpcode(final String opcode) {
        return OPCODE_MAP.get(opcode.toUpperCase());
    }

    public static int getType(final String type) {
        return TYPE_MAP.get(type.toUpperCase());
    }

    private static String searchMap(Map<String, Integer> map, int toFind) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == toFind) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getTypeName(final int type) {
        return searchMap(TYPE_MAP, type);
    }

    public static void dumpClasses(List<ClassNode> nodes, File file) {
        try {
            JarOutputStream output = new JarOutputStream(new FileOutputStream(file));
            for (ClassNode node : nodes) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                JarEntry entry = new JarEntry(node.name + ".class");
                output.putNextEntry(entry);
                output.write(writer.toByteArray());
            }
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getReturn(String desc) {
        switch (desc.charAt(0)) {
            case '[':
            case 'L':
                return ARETURN;
            case 'I':
            case 'Z':
                return IRETURN;
            case 'D':
                return DRETURN;
            case 'J':
                return LRETURN;
            case 'F':
                return FRETURN;
        }
        return ARETURN;
    }

    public static List<ClassNode> downloadNodes(PageParser p) {
        List<ClassNode> classes = new ArrayList<ClassNode>();
        HashMap<String, byte[]> clientFiles = p.getNodes();
        for (String s : clientFiles.keySet()) {
            if (s.endsWith(".class")) {
                byte[] bytes = clientFiles.get(s);
                //String name = s.substring(0, s.indexOf(".class"));
                ClassReader cr = new ClassReader(bytes);
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                classes.add(cn);
            }
        }
        return classes;
    }

    @SuppressWarnings("resource")
    public static List<ClassNode> loadNodes(final File file) {
        final List<ClassNode> nodes = new ArrayList<>();
        try {
            final JarFile jar = new JarFile(file);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry next = entries.nextElement();
                if (next.getName().endsWith(".class")) {
                    final ClassNode node = new ClassNode();
                    final ClassReader reader = new ClassReader(jar.getInputStream(next));
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    nodes.add(node);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }


    public static String stripDesc(final String desc) {
        return desc.split("L|;")[1];
    }

    public static boolean isDescStandard(final String desc) {
        final String[] standard = {"I", "Z", "F", "J", "S", "B", "D", "C"};
        return desc.contains("java") && desc.contains("/") || Arrays.binarySearch(standard, desc) >= 0 || desc.contains("[") && Arrays.binarySearch(standard, stripDesc(desc)) >= 0;
    }

    public static String getOpcodeName(int opcode) {
        return searchMap(OPCODE_MAP, opcode);
    }

    public static ClassNode getNode(String name) {
        for (ClassNode node : Updater.classes) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    public static MethodNode getMethod(ClassNode node, String name) {
        for (MethodNode mn : node.methods) {
            if (mn.name.equals(name)) {
                return mn;
            }
        }
        return null;
    }

    public static boolean descContains(String desc, String search) {
        String[] split = search.split(" ");
        for (String str : split) {
            if (desc.contains(str)) {
                desc = desc.replaceFirst(str, "");
                continue;
            }
            return false;
        }
        return true;
    }
}
