package se.org.mac.swenlp;

/**
 * Defines an input file for training or testing.
 *
 * @param format     Type of syntax in input file @{@code i.e. TSV}
 * @param wordColumn index of word column beginning from ZERO
 * @param tagColumn  index of tag column beginning from ZERO
 * @param filePath   relative path to conll-file
 */
public record ModelFile(String format, int wordColumn, int tagColumn, String filePath) {
    @Override
    public String toString() {
        return "format=" + format + "," + "wordColumn=" + wordColumn + "," + "tagColumn=" + tagColumn + "," + filePath;
    }
}
