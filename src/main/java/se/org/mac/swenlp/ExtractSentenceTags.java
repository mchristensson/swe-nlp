package se.org.mac.swenlp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceOptions;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
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
     * It is possible to train the Shift-Reduce Parser for languages other than English. An
     * appropriate HeadFinder needs to be provided. This and other options are handled by
     * specifying the -tlpp flag, which lets you choose the class for a TreebankLangParserParams.
     * A language appropriate tagger is also required.
     * <p>
     * For example, here is a command used to train a Chinese model. The options not already
     * explained are explained in the next section.
     *
     * <pre>
     * java -mx10g edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser
     * -trainTreebank /u/nlp/data/chinese/ctb7/train.mrg
     * -devTreebank /u/nlp/data/chinese/ctb7/dev_small.mrg
     * -preTag -taggerSerializedFile /u/nlp/data/pos-tagger/distrib/chinese-nodistsim.tagger
     * -serializedPath chinese.ser.gz
     * -tlpp edu.stanford.nlp.parser.lexparser.ChineseTreebankParserParams
     * -trainingThreads 4
     * -batchSize 12
     * -trainingIterations 100
     * -stalledIterationLimit 20
     * </pre>
     * ‑trainingMethod	See below.
     * ‑beamSize	Size of the beam to use when running beam search. 4 is already sufficient to
     * greatly increase accuracy without affecting speed too badly.
     * ‑trainBeamSize	Size of the beam to use when training.
     * ‑trainingThreads	Training can be run in parallel. This is done by training on multiple
     * trees simultaneously.
     * ‑batchSize	How many trees to batch together when training. This allows training in
     * parallel to get repeatable results, as each of the trees are scored using the weights at
     * the start of the training batch, and then all updates are applied at once.
     * ‑trainingIterations	The maximum number of iterations to train. Defaults to 40.
     * ‑stalledIterationLimit	The heuristic for ending training before -trainingIterations
     * iterations is to stop when the current dev set score has not improved for this many
     * iterations. Defaults to 20.
     * ‑averagedModels	When the perceptron has finished training, in general, the model with the
     * best score on the dev set is kept. This flag averages together the best K models and uses
     * that as the final model instead. Defaults to 8. This has the potential to greatly increase
     * the amount of memory needed, so can be set to a lower number if memory is a barrier.
     * ‑featureFrequencyCutoff	If a feature is not seen this many times when training, it is
     * removed from the final model. This can eliminate rarely seen features without impacting
     * overall accuracy too much. It is especially useful in the case of model training using a
     * beam (or oracle, if that method is ever made to work), as that training method results in
     * many features that were only seen once and don't really have much impact on the final model.
     * ‑saveIntermediateModels	By default, training does not save the intermediate models any
     * more, since they basically don't do anything. Use this flag to turn it back on.
     * ‑featureFactory	The feature factory class to use.
     */
    /*
    public void trainSrParser() {
        ShiftReduceParser.main(
                new String[]{
                        "-trainTreebank", inputTrainData.toString(),
                        "-devTreebank", "copyrighted/training/sv-train.mrg",
                        "-preTag", "",
                        "-taggerSerializedFile", modelName,
                        "-serializedPath", "swedish.ser.gz",
                        //"-tlpp edu.stanford.nlp.parser.lexparser.ChineseTreebankParserParams",
                        "-trainingThreads","4",
                        "-batchSize","12",
                        "-trainingIterations","100",
                        "-stalledIterationLimit","20"

                });
    }

     */

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
    public void analysisWithModel(String testText) {

        //Usecase 2: Semantisk analys
        Properties props = getProperties("swenlp.properties");
        //props.setProperty("annotators", "tokenize, ssplit, pos, parse, ner, depparse, sentiment");
        props.setProperty("parse.debug", "true");
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        props.setProperty("pos.model", "swedish-pos-tagger-model");
        props.setProperty("depparse.model", "swedish-pos-tagger-model");
        //props.setProperty("parse.model", "swedish-pos-tagger-model");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // create a document object
        CoreDocument document = pipeline.processToCoreDocument(testText);

        //Annotation document = new Annotation(testText);

        pipeline.annotate(document);


        // Hämta alla meningar i dokumentet
        //List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterera genom varje mening och utför grammatisk analys
        //for (CoreMap sentence : sentences) {

        // Hämta trädstrukturen för meningen
        //  Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);


        logger.debug("jkg tree={}", document);
        //}
    }

    private static Properties getProperties(String filename) {
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
}
