package se.org.mac.swenlp;

import java.util.Properties;
import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ExtractSentenceTags {

    private static final Logger logger = LoggerFactory.getLogger(ExtractSentenceTags.class);
    private String inputClasspathPropertiesFile;
    private String modelName;
    private String inputTrainData;
    private String inputDevData;
    private String embeddingFile;
    private int embeddingSize;
    private int trainingThreads;
    private int maxIterations;
    private String language;

    /**
     * Builder setter for the property file to be used upon execution.
     *
     * @param inputClasspathPropertiesFile classpath property files
     * @return The builder instance
     */
    public ExtractSentenceTags setInputClasspathPropertiesFile(String inputClasspathPropertiesFile) {
        this.inputClasspathPropertiesFile = inputClasspathPropertiesFile;
        return this;
    }

    /**
     * Builder setter for the model name.
     *
     * @param modelName The model name.
     * @return The builder instance
     */
    public ExtractSentenceTags setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    /**
     * @param inputTrainData Path to the input data file {@code i.e. foo/bar-train.conll}
     * @return The builder instance
     */
    public ExtractSentenceTags setInputTrainData(String inputTrainData) {
        this.inputTrainData = inputTrainData;
        return this;
    }

    /**
     * @param inputDevData Path to the input dev data file {@code i.e. foo/bar-dev.conll}
     * @return The builder instance
     */
    public ExtractSentenceTags setInputDevData(String inputDevData) {
        this.inputDevData = inputDevData;
        return this;
    }

    public ExtractSentenceTags setEmbeddingFile(String embeddingFile) {
        this.embeddingFile = embeddingFile;
        return this;
    }

    public ExtractSentenceTags setEmbeddingSize(int embeddingSize) {
        this.embeddingSize = embeddingSize;
        return this;
    }

    public ExtractSentenceTags setTrainingThreads(int trainingThreads) {
        this.trainingThreads = trainingThreads;
        return this;
    }

    public ExtractSentenceTags setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public ExtractSentenceTags setLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * Trains the model using the specified file in the property {{@code trainFile}} from the
     * inputClasspathPropertiesFile.
     */
    public void trainFromConllu() {
        assert inputClasspathPropertiesFile != null;
        try {
            MaxentTagger.main(
                    new String[]{"-prop", inputClasspathPropertiesFile, "-trainFile",
                            inputTrainData});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Trains a new model for Dependency Parsing
     * See instructions at
     * <a href="https://github.com/klintan/corenlp-swedish-depparse-model">github.com/klintan</a>
     */
    public void trainForDepParsning() {


        Properties props = SweNlpUtil.getProperties(inputClasspathPropertiesFile);
        props.setProperty("trainFile", inputTrainData);
        props.setProperty("devFile", inputDevData);
        props.setProperty("model", modelName);
        props.setProperty("numPreComputed", "5"); //TODO: create property in this wrapper
        if (this.embeddingFile != null) {
            props.setProperty("-embedFile", this.embeddingFile);
        }
        if (this.maxIterations > -1) {
            props.setProperty("maxIter", String.valueOf(this.maxIterations));
        }
        if (this.embeddingSize > -1) {
            props.setProperty("embeddingSize", String.valueOf(this.embeddingSize));
        }
        if (this.trainingThreads > -1) {
            props.setProperty("trainingThreads", String.valueOf(this.trainingThreads));
        }
        if (!StringUtils.isBlank(this.language)) {
            props.setProperty("language", this.language);
        }
        String[] args = SweNlpUtil.propertiesToArgs(props);

        DependencyParser.main(args);
        //java -classpath ./stanford-corenlp-full/stanford-corenlp-3.8.0.jar edu.stanford.nlp
        // .parser.nndep.DependencyParser
        // -trainFile swedish-train.conllu
        // -devFile swedish-dev.conllu
        // -embedFile swedish.word2vec.model.txt
        // -embeddingSize 50
        // -model swedish.nndep.model.txt.gz
        // -trainingThreads 8
    }

    public void filterRowsInFile() {

    }


    /**
     * Applies a dataset onto the model
     *
     * @param inputTestData File defition with data to test
     */
    public void applyModel(ModelFile inputTestData) {
        try {
            MaxentTagger.main(
                    new String[]{"-prop", inputClasspathPropertiesFile, "-model", modelName,
                            "-testFile", inputTestData.toString()});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tags each word in a test block by its corresponding descriptor.
     *
     * @param sentence Input text to tag
     * @return Same sentence with each word's tag injected
     */
    public String tagSentence(String sentence) {
        MaxentTagger tagger = new MaxentTagger(modelName);
        return tagger.tagString(sentence);
    }

    /**
     * See {@code https://stanfordnlp.github.io/CoreNLP/pipeline.html}
     *
     * @param testText
     */
    public void performDepParsing(String testText) {

        //Usecase 2: Semantisk analys
        Properties props = SweNlpUtil.getProperties("swenlp.properties"); //TODO: Parametrize
        //props.setProperty("annotators", "tokenize, ssplit, pos, parse, ner, depparse, sentiment");
        props.setProperty("parse.debug", "true");
        props.setProperty("depparse.debug", "true");
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        props.setProperty("pos.model", "swedish-pos-tagger-model");  //TODO: Parametrize
        props.setProperty("depparse.model", "swedish-depparse-model"); //TODO: Parametrize
        if (!StringUtils.isBlank(this.language)) {
            props.setProperty("depparse.language", this.language);
        }
        //props.setProperty("parse.model", "swedish-pos-tagger-model");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create a document object
        CoreDocument document = pipeline.processToCoreDocument(testText);

        //Annotation document = new Annotation(testText);

        pipeline.annotate(document);

//^([A-Za-zåäö]*\s+[A-Za-zåäö])


        // Hämta alla meningar i dokumentet
        //List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterera genom varje mening och utför grammatisk analys
        //for (CoreMap sentence : sentences) {

        // Hämta trädstrukturen för meningen
        //  Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);


        logger.debug("jkg tree={}", document);
        //}
    }

    @Override
    public String toString() {
        return "ExtractSentenceTags{" + "inputClasspathPropertiesFile='" + inputClasspathPropertiesFile + '\'' + ", modelName='" + modelName + '\'' + ", inputTrainData='" + inputTrainData + '\'' + ", inputDevData='" + inputDevData + '\'' + ", embeddingFile='" + embeddingFile + '\'' + ", embeddingSize=" + embeddingSize + ", trainingThreads=" + trainingThreads + ", maxIterations=" + maxIterations + ", language='" + language + '\'' + '}';
    }
}
