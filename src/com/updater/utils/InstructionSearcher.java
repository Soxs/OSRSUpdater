package com.updater.utils;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 23/08/13
 * Time: 20:42
 * To change this template use File | Settings | File Templates.
 */
public class InstructionSearcher {
    /**
     * The instruction list to search inside
     */
    private InsnList nodes;

    /**
     * The opcodes to match for
     */
    private int[] opcodes;

    /**
     * The list of match arrays
     */
    private List<AbstractInsnNode[]> matches = new ArrayList<AbstractInsnNode[]>();

    /**
     * The highest number of opcodes which matched consecutively,
     * use this to debug where the pattern is breaking
     */
    private int highestBreakpoint = 0;

    /**
     * The opcode of the instruction at the highest breakpoint
     */
    private int highestBreakpointOpcode = -1;

    /**
     * The maximum, if above 0, goto instructions to follow.
     */
    private int maxJumps;

    /**
     * The number of jumps so far.
     */
    private int jumps = 0;

    /**
     * Constructs a new instruction searched
     *
     * @param nodes    The instruction list (methodnode.instructions)
     * @param maxJumps The maximum amount of jumps to follow (0 signifies not to follow goto instructions)
     * @param opcodes  The array of opcodes to match
     */
    public InstructionSearcher(InsnList nodes, int maxJumps, int... opcodes) {
        this.nodes = nodes;
        this.maxJumps = maxJumps;
        this.opcodes = new int[opcodes.length];
        for (int i = 0; i < opcodes.length; i++) {
            this.opcodes[i] = opcodes[i];
        }
        matches.add(new AbstractInsnNode[opcodes.length]);
    }

    private void clear() {
        for (int i = 0; i < matches.get(size() - 1).length; i++) {
            matches.get(size() - 1)[i] = null;
        }
    }

    /**
     * Matches for the opcodes in the instruction list.
     *
     * @return <b>true</b> if a pattern was found, <b>false</b> if not.
     */
    public boolean match() {
        int matchIndex = 0;
        boolean anyMatches = false;

        AbstractInsnNode currentNode = nodes.getFirst();
        while (currentNode != null) {
            if (matchIndex == opcodes.length) {
                //found a fully matching pattern, add a new index to the list and keep searching
                matches.add(new AbstractInsnNode[opcodes.length]);
                anyMatches = true;
                matchIndex = 0;
            }
            if (currentNode.getOpcode() == opcodes[matchIndex] || opcodes[matchIndex] == -1) {
                //match, proceed to keep matching
                matches.get(size() - 1)[matchIndex] = currentNode;
                matchIndex++;
            } else {
                //an instruction broke the pattern, reset
                if (matchIndex > highestBreakpoint) {
                    highestBreakpoint = matchIndex;
                    highestBreakpointOpcode = currentNode.getOpcode();
                }
                clear();
                matchIndex = 0;
            }
            if (matchIndex > 0 && currentNode instanceof JumpInsnNode && jumps <= maxJumps) {
                //if the current node is a goto instruction, set the next node as the jump target
                JumpInsnNode jumpInsn = (JumpInsnNode) currentNode;
                currentNode = jumpInsn.label.getNext();
                jumps++;
            } else {
                currentNode = currentNode.getNext();
            }
        }

        matches.remove(size() - 1);

        return anyMatches;
    }

    /**
     * Returns the array of matches respective to the opcodes they matched to
     * Note: The array will be empty unless <code>match()</code> returned <b>true</b>.
     *
     * @return
     */
    public List<AbstractInsnNode[]> getMatches() {
        return matches;
    }

    /**
     * Returns the highest index in the match array which
     * was reached whilst matching
     *
     * @return
     */
    public int getHighestBreakpoint() {
        return highestBreakpoint;
    }

    /**
     * Returns the opcode of the highest breakpoint index
     *
     * @return
     */
    public int getHighestBreakpointOpcode() {
        return highestBreakpointOpcode;
    }

    /**
     * Returns the size of the given match array
     *
     * @return
     */
    public int size() {
        return matches.size();
    }

}
