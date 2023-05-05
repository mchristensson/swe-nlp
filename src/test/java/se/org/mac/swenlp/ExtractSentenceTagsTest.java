package se.org.mac.swenlp;

import java.io.*;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtractSentenceTagsTest {
    private static final Logger logger = LoggerFactory.getLogger(ExtractSentenceTagsTest.class);

    @Test
    void trainFromConllu_whenPropertiesFilePresent_expectModelCreated() throws IOException {
        ExtractSentenceTags extractSentenceTags =
                new ExtractSentenceTags().setInputClasspathPropertiesFile(
                "swenlp.properties").setInputTrainData(
                "format=TSV,wordColumn=1,tagColumn=4," + "copyrighted/training/talbanken-stanford"
                        + "-train.conll");

        extractSentenceTags.trainFromConllu();
        File f = new File("swedish-pos-tagger-model");
        assertTrue(f.exists(), "Model file was not created");
        assertTrue(f.exists(), "Model properties file was not created");

    }

    @Test
    void applyModel_whenAllArgsPresent_expect() throws IOException {
        ExtractSentenceTags extractSentenceTags =
                new ExtractSentenceTags().setInputClasspathPropertiesFile(
                "swenlp.properties").setModelName("swedish-pos-tagger-model");
        extractSentenceTags.applyModel(
                new ModelFile("TSV", 1, 4, "copyrighted/training/talbanken-stanford-test.conll"));
    }

    @Test
    void extractModelFile_whenModelPresent_expectUnarchived() {
        try {
            FileInputStream fileInputStream = new FileInputStream("swedish-pos-tagger-model");
            GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
            logger.debug("Begin reading model file...");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(gzipInputStream))) {
                logger.debug(reader.readLine());
            }

            gzipInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void tagSentence_whenSentenceDefined_() {
        String testMening1 = "Det här är en exempelmening";
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags().setModelName(
                "swedish-pos-tagger-model");
        String taggedSentence = extractSentenceTags.tagSentence(testMening1);
        assertEquals("Det_DT här_AB är_VB en_DT exempelmening_NN ", taggedSentence);
    }

    @Test
    void analysisDepParsning() {
        String text = "det är frukost på taket";
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags();
        extractSentenceTags.setLanguage("Swedish");
        extractSentenceTags.performDepParsing(text);
    }

    @Test
    void trainForDepParsning() {
        SweNlpUtil.filterRowsInFile('#', "copyrighted/training/sv_talbanken-ud-dev.conllu",
                "copyrighted/training/sv_talbanken-ud-dev.filtered.conllu", false);
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags();

        extractSentenceTags.setInputClasspathPropertiesFile("swenlp.properties");
        extractSentenceTags.setInputDevData(
                "copyrighted/training/sv_talbanken-ud-dev.filtered.conllu");
        extractSentenceTags.setInputTrainData("copyrighted/training/sv_talbanken-ud-train.conllu");
        extractSentenceTags.setModelName("swedish-depparse-model");
        //  extractSentenceTags.setEmbeddingFile("swedish.word2vec.model.txt");
        extractSentenceTags.setEmbeddingSize(50);
        extractSentenceTags.setTrainingThreads(8);
        extractSentenceTags.setMaxIterations(10);
        extractSentenceTags.setLanguage("Swedish");

        extractSentenceTags.trainForDepParsning();

        File f = new File("swedish-depparse-model");
        assertTrue(f.exists(), "Model file was not created");
        assertTrue(f.exists(), "Model properties file was not created");
    }

    @Test
    void strip() {
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags();
        SweNlpUtil.filterRowsInFile('#', "copyrighted/training/sv_talbanken-ud-dev.conllu",
                "copyrighted/training/sv_talbanken-ud-dev.filtered.conllu", false);
    }
}