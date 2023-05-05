package se.org.mac.swenlp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common utility methods.
 */
public final class SweNlpUtil {

    private static final Logger logger = LoggerFactory.getLogger(SweNlpUtil.class);

    /**
     * Default hidden constructor
     */
    private SweNlpUtil() {}

    /**
     * Extract properties from a {@link Properties} instance and extracts them to an array. To be
     * used as argument for a class main function.
     *
     * @param props Properties to extract, by key and value
     * @return an array contaning the property values, keys at even indeces (prefixed by dash
     * sign), values at odd indeces.
     */
    static String[] propertiesToArgs(Properties props) {
        ArrayList<String> argsList = new ArrayList<>();
        props.forEach((key, value) -> {
            argsList.add("-" + key);
            argsList.add(value != null ? (String) value : "");
        });
        String[] args = new String[argsList.size()];
        return argsList.toArray(args);
    }

    /**
     * Reads text as properties from a properties file.
     *
     * @param filename File to read from
     * @return Properties instance with the collected values
     */
    static Properties getProperties(String filename) {
        try (InputStream input = MaxentTaggerWrapper.class.getClassLoader()
                .getResourceAsStream(filename)) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("Could not load properties file");
            }
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a copy of the input file where the rows beginning with {@code startsWithExclusion}
     * is filtered out.
     *
     * @param startsWithExclusion  Character to trigger exclusion
     * @param inputFile            Input file
     * @param outputFile           Output file
     * @param appendToExistingFile when {@code false}, any existing output file will be replaced
     *                             (delete)
     */
    public static void filterRowsInFile(char startsWithExclusion, String inputFile,
                                        String outputFile, boolean appendToExistingFile) {
        String beginChar = String.valueOf(startsWithExclusion);
        try {
            File output = new File(outputFile);
            if (output.exists()) {
                output.delete();
            }
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            logger.debug("Stripping row beginning with ...");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileInputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(beginChar)) {
                        outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                        outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
