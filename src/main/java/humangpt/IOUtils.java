package humangpt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public final class IOUtils {

    private static final int NUMBER_OF_ARGS = 2;
    private static final int INPUT_FILE_INDEX = 0;
    private static final int OUTPUT_FILE_INDEX = 1;
    private static final int ERROR = 1;
    private static final String EXTENSION = ".txt";
    private static final String OUTPUT_FOLDER = "output";

    /**
     * Read the main arguments to perform all the checks
     * @param args args to check
     * @return {@link Args}
     */
    public static Args checkArgs(final String[] args) {
        if (args.length != NUMBER_OF_ARGS) {
            System.out.println("SICURO DI AVER MESSO TUTTI I PARAMETRI???");
            System.exit(ERROR);
        }
        final String inputFile = args[INPUT_FILE_INDEX];
        final String outputFile = args[OUTPUT_FILE_INDEX];
        if (inputFile.contains(".") || outputFile.contains(".")) {
            System.out.println("NON METTERE L'ESTENSIONE NEI NOMI DEI FILE!");
            System.exit(ERROR);
        }
        return new Args(inputFile, outputFile);
    }

    /**
     * Read file from "src/main/resources" project path
     * @param fileName name of the file to read
     * @param howToRead consumer that takes the scanner and use it to read the file
     */
    public static void readInput(final String fileName, final Consumer<Scanner> howToRead) {
        final InputStream inputStream = IOUtils.class.getClassLoader().getResourceAsStream(fileName + EXTENSION);
        if (inputStream == null) {
            System.out.println("HAI SBAGLIATO IL NOME DEL FILE DI INPUT, IDIOTA! :)");
            System.exit(ERROR);
        }

        try (final Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            scanner.useDelimiter("\n");
            howToRead.accept(scanner);
        }
    }

    /**
     * Write an output file into a specific subfolder of the working directory
     * @param fileName name of the file to write
     * @param lines lines to write
     * @throws IOException if an I/ O error occurs
     */
    public static void writeOutput(final String fileName, final List<String> lines) throws IOException {
        final Path path = Paths.get(OUTPUT_FOLDER, fileName + EXTENSION);

        // Create folder if it does not exist
        Files.createDirectories(path.getParent());

        Files.write(path, lines);
    }
}
