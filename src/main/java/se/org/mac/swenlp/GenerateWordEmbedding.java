package se.org.mac.swenlp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generates a word embeddings file
 */
public class GenerateWordEmbedding {

    private static final Logger logger = LoggerFactory.getLogger(GenerateWordEmbedding.class);

    private Word2Vec model;
    private String inputFile;
    private String inputClasspathResourceFile;
    private String outputFile;
    private int minSentenceCount = 5;
    private boolean flushOnWrite = true;


    public GenerateWordEmbedding setInputFile(String inputFile) {
        this.inputFile = inputFile;
        return this;
    }

    public GenerateWordEmbedding setInputClasspathResourceFile(String inputClasspathResourceFile) {
        this.inputClasspathResourceFile = inputClasspathResourceFile;
        return this;
    }

    public GenerateWordEmbedding setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public GenerateWordEmbedding flushOnWrite(boolean flushOnWrite) {
        this.flushOnWrite = flushOnWrite;
        return this;
    }

    /**
     * Set minWordFrequency
     *
     * @param minSentenceCount
     * @return
     */
    public GenerateWordEmbedding setMinSentenceCount(int minSentenceCount) {
        this.minSentenceCount = minSentenceCount;
        return this;
    }


    public void execute() throws IOException {
        String filePath = null;
        if (inputClasspathResourceFile != null) {
            filePath = new ClassPathResource(this.inputClasspathResourceFile).getFile()
                    .getAbsolutePath();
        } else {
            filePath = new File(this.inputFile).getAbsolutePath();
        }

        logger.info("Load & Vectorize Sentences....");
        // Definiera en SentencePreProcessor som tar bort onödiga tecken och gör om texten till
        // lowercase
        SentenceIterator iter = new BasicLineIterator(filePath);
        iter.setPreProcessor(
                (SentencePreProcessor) sentence -> sentence.replaceAll("[^a-zA-Z0-9 ]", "")
                        .toLowerCase());

        // Skapa en instans av DefaultTokenizerFactory
        // Konfigurera tokenizer med lämpliga inställningar, t.ex. att ta bort stoppord

        TokenizerFactory tokenizer = new DefaultTokenizerFactory();
        tokenizer.setTokenPreProcessor(new CommonPreprocessor());

        // Definiera inställningar för Word2Vec-modellen
        Word2Vec vec = new Word2Vec.Builder()

                .minWordFrequency(this.minSentenceCount).layerSize(100)
                .seed(42).windowSize(5).iterate(iter)
                //.iterations(5)
                .tokenizerFactory(tokenizer).build();

        // Träna modellen med träningsdata
        vec.fit();

        // Spara modellen till filen outputFile
        WordVectorSerializer.writeWordVectors(vec, outputFile);
        if (!flushOnWrite) {
            this.model = vec;
        }
    }

    public Collection<String> evaluateModel(String word) {
        if (this.model == null) {
            logger.warn("Model has been flushed.");
            return Collections.emptyList();
        }
        logger.info("Closest Words:");
        Collection<String> lst = this.model.wordsNearest(word, 10);
        return lst;
    }

}
