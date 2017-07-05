package ocelot;

import java.util.Arrays;

/**
 *
 * @author ben
 */
public final class InterpMain {

    private final Opcode[] table = new Opcode[256];

    public void init() {
        for (Opcode op : Opcode.values()) {
            table[op.getOpcode()] = op;
        }
        // Sanity check
        int count = 0;
        for (int i = 0; i < 256; i++) {
            if (table[i] != null)
                count++;
        }
        final int numOpcodes = Opcode.values().length;
        if (count != numOpcodes) {
            throw new IllegalStateException("Opcode sanity check failed: " + count + " opcodes found, should be " + numOpcodes);
        }
    }

    public JVMValue execMethod(final byte[] instr) {
//         System.out.println(Arrays.toString(table));
        if (instr == null || instr.length == 0)
            return null;

        final EvaluationStack eval = new EvaluationStack();
        final LocalVars lvt = new LocalVars(eval);

        int current = 0;
        LOOP:
        while (true) {
            byte b = instr[current++];
            Opcode op = table[b & 0xff];
            if (op == null) {
                System.err.println("Unrecognised opcode byte: " + (b & 0xff) + " encountered. Stopping.");
                System.exit(1);
            }
            byte num = op.numParams();
            switch (op) {
                case ALOAD:
                    lvt.aload(instr[current++]);
                    break;
                case ALOAD_0:
                    lvt.aload((byte) 0);
                    break;
                case ASTORE:
                    lvt.astore(instr[current++]);
                    break;
                case DUP:
                    eval.dup();
                    break;
                case GOTO:
                    System.out.println(current + " += " + (instr[current] << 8) + " + " + instr[current + 1]);
                    current += 2 + ((int) instr[current] << 8) + (int) instr[current + 1];
                    break;
                case IADD:
                    eval.iadd();
                    break;
                case ICONST_0:
                    eval.iconst(0);
                    break;
                case ICONST_1:
                    eval.iconst(1);
                    break;
                case ICONST_2:
                    eval.iconst(2);
                    break;
                case ICONST_3:
                    eval.iconst(3);
                    break;
                case ICONST_M1:
                    eval.iconst(-1);
                    break;
                case IDIV:
                    eval.idiv();
                    break;
                case IINC:
                    lvt.iinc(instr[current++]);
                    break;
                case ILOAD:
                    lvt.iload(instr[current++]);
                    break;
                case IMUL:
                    eval.imul();
                    break;
                case IRETURN:
                    return eval.pop();
                case ISTORE:
                    lvt.istore(instr[current++]);
                    break;
                case ISUB:
                    eval.isub();
                    break;
                case NOP:
                    break;
                case POP:
                    eval.pop();
                    break;
                // Dummy implementation
                case GETSTATIC:
                case INVOKEVIRTUAL:
                case LDC:
                    System.out.print("Executing " + op + " with param bytes: ");
                    for (int i = current; i < current + num; i++) {
                        System.out.print(instr[i] + " ");
                    }
                    current += num;
                    System.out.println();
                    break;
                case RETURN:
                    return null;
                default:
                    System.err.println("Saw " + op + " - that can't happen. Stopping.");
                    System.exit(1);
            }
        }
    }

}
