package refactored.domain.internal_model;

public class InstructionData {
    private int opcode;
    private String textRepresentation;

    public InstructionData(int opcode, String textRepresentation) {
        this.opcode = opcode;
        this.textRepresentation = textRepresentation;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getTextRepresentation() {
        return textRepresentation;
    }
}
