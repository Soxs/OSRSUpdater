package com.updater.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;

public class AsmUtil {
    public static Map<String, ClassNode> getClassNodes(final JarFile jar) {
        final Map<String, ClassNode> classes = new HashMap<>();
        final Enumeration<?> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = (JarEntry) entries.nextElement();
            final String name = entry.getName();
            if (name.endsWith(".class")) {
                InputStream input;
                try {
                    input = jar.getInputStream(entry);
                } catch (final java.io.IOException e) {
                    continue;
                }
                if (input != null) {
                    ClassNode node;
                    try {
                        final ByteArrayOutputStream output = new ByteArrayOutputStream();
                        final byte[] buffer = new byte[2048];
                        int read;
                        while (input.available() > 0 && (read = input.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, read);
                        }
                        node = ClassNode.createNode(output.toByteArray());
                    } catch (java.io.IOException e) {
                        continue;
                    }
                    classes.put(node.name, node);
                }
            }
        }
        return classes;
    }

    public static MethodNode getMethod(ClassNode cn, String name) {
        for (MethodNode mn : cn.getMethodNodes()) {
            if (mn.name.equals(name)) {
                return mn;
            }
        }
        return null;
    }

    @SuppressWarnings("resource")
    public static void dump(final String jarName, final Map<String, ClassNode> nodes) {

        final File jar = new File(jarName + ".jar");
        try {
            final JarOutputStream output = new JarOutputStream(new FileOutputStream(jar));
            for (final Map.Entry<String, ClassNode> entry : nodes.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                output.write(entry.getValue().getBytes());
                output.closeEntry();
            }
            output.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FieldNode getFieldNode(final ClassNode cn, final String name) {
        for (final FieldNode fn : cn.getFieldNodes()) {
            if (fn.name.equals(name)) {
                return fn;
            }
        }
        return null;
    }


    public static int getReturnFor(final String desc) {
        if (desc.startsWith("[")) {
            return Opcodes.ARETURN;
        } else if (desc.equals("D")) {
            return Opcodes.DRETURN;
        } else if (desc.equals("F")) {
            return Opcodes.FRETURN;
        } else if (desc.equals("J")) {
            return Opcodes.LRETURN;
        }
        final String[] i = {"I", "Z"};
        for (final String s : i) {
            if (s.equals(desc)) {
                return Opcodes.IRETURN;
            }
        }
        return Opcodes.ARETURN;
    }

    public static FieldNode getFieldNode(final ClassNode cn, final FieldInsnNode fin) {
        for (final FieldNode fn : cn.getFieldNodes()) {
            if (fn.equals(fin)) {
                return fn;
            }
        }
        return null;
    }

    public static class OUtils {
        /**
         * A class full of Opcode utilities and the such.
         */
        private static Map<String, Integer> OPCODE_MAP = new HashMap<>();

        static {
            boolean advance = false;
            for (final Field f : Opcodes.class.getFields()) {
                if (!advance) {
                    if (!f.getName().equals("NOP")) {
                        continue;
                    }
                    advance = true;
                    try {
                        OPCODE_MAP.put(f.getName(), (Integer) f.get(null));
                    } catch (final IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } else {
                    try {
                        OPCODE_MAP.put(f.getName(), (Integer) f.get(null));
                    } catch (final IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public static String getReturn(String desc) {
            int off = 0;
            String[] str = desc.split("");
            while (!str[off++].equals(")"))
                ;
            String s = "";
            for (int i = off; i < str.length; i++) {
                if (str[i].equals("L")) {
                    s += "L;";
                    break;
                }
                s += str[i];
            }
            return s;
        }

        public static String[] getDesc(String desc) {
            String[] str = desc.split("");
            ArrayList<String> hii = new ArrayList<String>();
            for (int i = 1; i < str.length; i++) {
                if (str[i].equals("[")) {
                    int off = i;
                    String build = "";
                    while (str[off++].equals("["))
                        if (str[off].equals("[")) build += str[off];
                    build += "[";
                    i += off - i - 1;
                    if (str[i].equals("Z")) {
                        build += "Z";
                    } else if (str[i].equals("L")) {
                        int offi = i;
                        String buildi = str[offi];
                        while (!str[offi++].equals(";"))
                            ;
                        buildi += ";";
                        i += offi - i - 1;
                        build += buildi;
                    } else if (str[i].equals(")")) {
                        break;
                    } else {
                        build += "?";
                    }
                    hii.add(build);
                } else if (str[i].equals("Z")) {
                    hii.add("Z");
                } else if (str[i].equals("L")) {
                    int off = i;
                    String build = str[off];
                    while (!str[off++].equals(";"))
                        ;
                    build += ";";
                    i += off - i - 1;
                    hii.add(build);
                } else if (str[i].equals(")")) {
                    break;
                } else {
                    hii.add("?");
                }
            }
            hii.remove(0);
            return hii.toArray(new String[hii.size()]);
        }

        public static boolean classContainsFieldWithDescription(ClassNode cn, String desc) {
            for (FieldNode fn : (List<FieldNode>) cn.fields) {
                if (fn.desc.equals(desc)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean methodContainsParameter(final MethodNode mn, final String parameterDesc) {
            for (final Object parameter : mn.localVariables) {
                if (parameter.toString().equals(parameterDesc)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isDescStandard(final String desc) {
            final String[] keywords = {"I", "Z", "F", "J", "S", "B", "D"};
            return desc.contains("java") && desc.contains("/") || Arrays.binarySearch(keywords, desc) >= 0 || desc.contains("[") && Arrays.binarySearch(keywords, stripDesc(desc)) >= 0;
        }

        public static boolean isFieldStandard(final FieldNode field) {
            return isDescStandard(field.desc);
        }

        public static String stripDesc(String desc) {
            if (desc.startsWith("L")) {
                desc = desc.substring(1);
            }
            return desc.replaceAll("\\[L", "").replaceAll("\\[", "").replaceAll(";", "");
        }

        public static int getOpcode(String opstring) {
            try {
                return OPCODE_MAP.get(opstring.toUpperCase());
            } catch (final Exception e) {
                return -1;
            }
        }

        public static String getOpcodeName(int i) {
            for (Map.Entry<String, Integer> e : OPCODE_MAP.entrySet()) {
                if (e.getValue() == i) {
                    return e.getKey();
                }
            }
            return "IDK";
        }

        public static Integer[] getMatchingOpcodes(String opstring) {
            opstring = StringUtil.replaceNonAlphanumeric(opstring);
            opstring = opstring.toUpperCase();
            final List<Integer> opcodes = new ArrayList<Integer>();
            for (final Map.Entry<String, Integer> entry : OPCODE_MAP.entrySet()) {
                final String key = entry.getKey();
                if (!key.contains(opstring) || !Pattern.compile(".*" + opstring).matcher(key).matches()) {
                    continue;
                }
                opcodes.add(entry.getValue());
            }
            return opcodes.toArray(new Integer[opcodes.size()]);
        }

        public static Integer[] getOpcodesFrom(final String pattern) {
            final String[] instructions = pattern.split(" ");
            final List<Integer> opcodes = new ArrayList<Integer>();
            for (final String instruction : instructions) {
                final String[] current = instruction.split("\\|");
                for (final String curr : current) {
                    if (curr.equals("*")) {
                        opcodes.add(-1);
                    } else {
                        if (StringUtil.containsNonAlphanumeric(curr)) {
                            Collections.addAll(opcodes, getMatchingOpcodes(curr));
                        } else {
                            opcodes.add(getOpcode(curr));
                        }
                    }
                }
            }
            return opcodes.toArray(new Integer[opcodes.size()]);
        }

        public static int getReturnFor(final String desc) {
            if (desc.startsWith("[")) {
                return Opcodes.ARETURN;
            } else if (desc.equals("D")) {
                return Opcodes.DRETURN;
            } else if (desc.equals("F")) {
                return Opcodes.FRETURN;
            } else if (desc.equals("J")) {
                return Opcodes.LRETURN;
            }
            final String[] i = {"I", "Z"};
            for (final String s : i) {
                if (s.equals(desc)) {
                    return Opcodes.IRETURN;
                }
            }
            return Opcodes.ARETURN;
        }

        public static FieldNode getField(final ClassNode cn, final FieldInsnNode fin) {
            for (final FieldNode fn : (List<FieldNode>) cn.fields) {
                if (!fn.name.equals(fin.name)) {
                    continue;
                }
                return fn;
            }
            return null;
        }

        public static FieldNode getField(final ClassNode cn, final String field) {
            for (final FieldNode fn : (List<FieldNode>) cn.fields) {
                if (!fn.name.equals(field)) {
                    continue;
                }
                return fn;
            }
            return null;
        }

        public static MethodNode getMethod(final ClassNode cn, final String method) {
            for (final MethodNode mn : (List<MethodNode>) cn.methods) {
                if (!mn.name.equals(method)) {
                    continue;
                }
                return mn;
            }
            return null;
        }

        public static Map<String, ClassNode> getClassNodes(final JarFile jar) {
            final Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
            final Enumeration<?> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = (JarEntry) entries.nextElement();
                final String name = entry.getName();
                if (name.endsWith(".class")) {
                    InputStream input;
                    try {
                        input = jar.getInputStream(entry);
                    } catch (final IOException e) {
                        continue;
                    }
                    if (input != null) {
                        ClassNode node;
                        try {
                            node = ClassNode.createNode(IOHelper.read(input));
                        } catch (IOException e) {
                            continue;
                        }
                        classes.put(node.name, node);
                    }
                }
            }
            return classes;
        }

        @SuppressWarnings("resource")
        public static void dump(final String jarName, final Map<String, ClassNode> nodes) {

            final File jar = new File(jarName + ".jar");
            try {
                final JarOutputStream output = new JarOutputStream(new FileOutputStream(jar));
                for (final Map.Entry<String, ClassNode> entry : nodes.entrySet()) {
                    output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                    output.write(entry.getValue().getBytes());
                    output.closeEntry();
                }
                output.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static String[] splitDesc(String desc) {
            String[] str = desc.split("");
            ArrayList<String> hii = new ArrayList<String>();
            for (int i = 1; i < str.length; i++) {
                if (str[i].equals("[")) {
                    int off = i;
                    String build = "";
                    while (str[off++].equals("["))
                        if (str[off].equals("[")) build += str[off];
                    i += off - i - 1;
                    if (str[i].equals("L")) {
                        int offi = i;
                        String buildi = str[offi];
                        while (!str[offi++].equals(";")) {
                            buildi += str[offi];
                        }
                        i += offi - i - 1;
                        build += buildi;
                    } else if (str[i].equals(")")) {
                        break;
                    } else {
                        build += str[i];
                    }
                    hii.add(build);
                } else if (str[i].equals("L")) {
                    int off = i;
                    String build = str[off];
                    while (!str[off++].equals(";")) {
                        build += str[off];
                    }
                    i += off - i - 1;
                    hii.add(build);
                } else if (str[i].equals(")")) {
                    break;
                } else {
                    hii.add(str[i]);
                }
            }
            hii.remove(0);
            return hii.toArray(new String[hii.size()]);
        }
    }
}