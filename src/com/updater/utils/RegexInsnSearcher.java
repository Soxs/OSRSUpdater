package com.updater.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.reflect.Modifier.*;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 24/08/13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class RegexInsnSearcher {

    private static final Map<Integer, String> OPCODE_NAME_MAP;
    private static final Pattern[] NON_INSTRUCTION_CONST_PATTERNS = new Pattern[]{
            Pattern.compile("acc_.+"), Pattern.compile("t_.+"), Pattern.compile("v1_.+")
    };

    private InsnList insns;
    private Map<AbstractInsnNode, Integer> instrIndexMap;
    private int currentIndex = 0;
    private String mappedCode;

    public RegexInsnSearcher(InsnList insns) {
        this.insns = insns;
        reload();
    }

    public void reload() {
        StringBuffer buffer = new StringBuffer();
        instrIndexMap = new HashMap<AbstractInsnNode, Integer>();
        Iterator<AbstractInsnNode> iterator = insns.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn.getOpcode() < 0) {
                continue;
            }
            instrIndexMap.put(insn, buffer.length());
            buffer.append(OPCODE_NAME_MAP.get(insn.getOpcode())).append(" ");
        }
        mappedCode = buffer.toString();
    }

    private AbstractInsnNode getKey(Integer val) {
        for (Map.Entry<AbstractInsnNode, Integer> entry : instrIndexMap.entrySet()) {
            if (entry.getValue().equals(val)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private AbstractInsnNode[] getMatchFromRange(int start, int end) {
        AbstractInsnNode startInsn = getKey(start);
        int realEndIdx = -1;
        for (int x = end - 1; x >= start; --x) {
            if (mappedCode.charAt(x) == ' ') {
                realEndIdx = x + 1;
                break;
            }
        }
        AbstractInsnNode endInsn = getKey(realEndIdx);
        int startInsnIdx = 0;
        if (startInsn != null)
            startInsnIdx = insns.indexOf(startInsn);
        AbstractInsnNode[] match = new AbstractInsnNode[insns.indexOf(endInsn) - startInsnIdx + 1];
        for (int idx = 0; idx < match.length; ++idx) {
            match[idx] = insns.get(startInsnIdx + idx);
        }
        return match;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public List<AbstractInsnNode[]> search(String pattern, AbstractInsnNode from) {
        return search(Pattern.compile(pattern.toLowerCase()), from, null);
    }

    public List<AbstractInsnNode[]> search(String pattern, Constraint constraint) {
        return search(Pattern.compile(pattern.toLowerCase()), insns.getFirst(), constraint);
    }

    public List<AbstractInsnNode[]> search(String pattern) {
        return search(Pattern.compile(pattern.toLowerCase()), insns.getFirst());
    }

    public List<AbstractInsnNode[]> search(Pattern pattern, AbstractInsnNode from) {
        return search(pattern, from, null);
    }

    public List<AbstractInsnNode[]> search(Pattern pattern, Constraint constraint) {
        return search(pattern, insns.getFirst(), constraint);
    }

    public List<AbstractInsnNode[]> search(Pattern pattern) {
        return search(pattern, insns.getFirst());
    }

    public List<AbstractInsnNode[]> search(Pattern pattern, AbstractInsnNode from, Constraint constraint) {
        Matcher matcher = pattern.matcher(mappedCode);
        Integer ret = instrIndexMap.get(from);
        int startIdx = 0;
        if (ret != null)
            startIdx = ret.intValue();
        List<AbstractInsnNode[]> matches = new LinkedList<AbstractInsnNode[]>();
        while (matcher.find(startIdx)) {
            int start = matcher.start();
            int end = matcher.end();
            AbstractInsnNode[] match = getMatchFromRange(start, end);
            if (constraint == null || constraint.accept(match)) {
                matches.add(match);
            }
            startIdx = end;
        }
        currentIndex = startIdx;
        return matches;
    }

    public static interface Constraint {
        public boolean accept(AbstractInsnNode[] match);
    }

    static {
        OPCODE_NAME_MAP = new HashMap<Integer, String>();
        Class<?> opcodes = Opcodes.class;
        Field[] declaredFields = opcodes.getDeclaredFields();
        for (Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers) && field.getType() == Integer.TYPE) {
                try {
                    String name = field.getName().toLowerCase();
                    boolean failed = false;
                    for (Pattern pattern : NON_INSTRUCTION_CONST_PATTERNS) {
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find() && matcher.start() == 0) {
                            failed = true;
                            break;
                        }
                    }
                    if (failed) {
                        continue;
                    }
                    int constant = field.getInt(null);
                    OPCODE_NAME_MAP.put(constant, name);
                } catch (IllegalAccessException ex) {
                }
            }
        }
    }
}
