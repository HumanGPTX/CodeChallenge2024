package humangpt;

public class Args {

    private final String inputFile;
    private final String outputFile;

    public Args(final String inputFile, final String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
