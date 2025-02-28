package humangpt;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public final class Main {

    private static final Consumer<Scanner> HOW_TO_READ = scanner -> {

    };

    public static void main(String[] arguments) throws Exception {
        final Args args = IOUtils.checkArgs(arguments);
        IOUtils.readInput(args.getInputFile(), HOW_TO_READ);

        List<String> outputLines = new ArrayList<>();
        IOUtils.writeOutput(args.getOutputFile(), outputLines);
    }

}