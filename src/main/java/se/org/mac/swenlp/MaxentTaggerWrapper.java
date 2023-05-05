package se.org.mac.swenlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MaxentTaggerWrapper {

    private static final Logger logger = LoggerFactory.getLogger(MaxentTaggerWrapper.class);



    public static void main(String[] args) throws Exception {

        //Usecase 2: Semantisk analys
        Properties props = getProperties("swenlp.properties");
        //props.setProperty("annotators", "tokenize, ssplit, pos, parse, ner, depparse, sentiment");
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        props.setProperty("pos.model", "swedish-pos-tagger-model");
        props.setProperty("parse.model", "swedish-pos-tagger-model");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String text = "det är frukost på taket";
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Hämta alla meningar i dokumentet
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterera genom varje mening och utför grammatisk analys
        for (CoreMap sentence : sentences) {

            // Hämta trädstrukturen för meningen
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            // Hämta semantisk graf för meningen
            SemanticGraph dependencies =
            sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
            /*
            SemanticGraph dependencies = sentence.get(
                    SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
*/
            // Gör något med grammatisk analysresultaten, t.ex. skriv ut dem
            System.out.println("Mening: " + sentence.toString());
            if (tree != null) {
                System.out.println("Trädstruktur: " + tree.toString());
                System.out.println("Semantisk graf: " + dependencies.toString());
            }

        }

        System.exit(0);

    }


    private static Properties getProperties(String filename) {
        try (InputStream input = MaxentTaggerWrapper.class.getClassLoader().getResourceAsStream(filename)) {
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
