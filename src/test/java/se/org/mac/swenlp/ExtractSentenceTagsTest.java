package se.org.mac.swenlp;

import java.io.File;
import java.io.IOException;
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
    void tagSentence_whenSentenceDefined_() {
        String testMening1 = "Det här är en exempelmening";
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags().setModelName(
                "swedish-pos-tagger-model");
        String taggedSentence = extractSentenceTags.tagSentence(testMening1);
        assertEquals("Det_DT här_AB är_VB en_DT exempelmening_NN ", taggedSentence);
    }

    @Test
    void analysis() {
        String text = "det är frukost på taket";
        ExtractSentenceTags extractSentenceTags = new ExtractSentenceTags();
        extractSentenceTags.analysisWithModel(text);
    }

}