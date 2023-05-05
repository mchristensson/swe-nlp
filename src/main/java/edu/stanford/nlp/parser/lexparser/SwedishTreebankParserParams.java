package edu.stanford.nlp.parser.lexparser;

import java.util.List;
import edu.stanford.nlp.international.Language;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.international.negra.NegraPennLanguagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwedishTreebankParserParams extends AbstractTreebankParserParams {
    private static final long serialVersionUID = 9_999_524_678L;
    private static final Logger logger = LoggerFactory.getLogger(SwedishTreebankParserParams.class);

    public SwedishTreebankParserParams() {
        super(new SwedishTreebankLanguagePack());

        this.setInputEncoding("UTF-8");
        this.setOutputEncoding("UTF-8");
    }

    @Override
    public TreeReaderFactory treeReaderFactory() {
        return null;
    }

    @Override
    public HeadFinder headFinder() {
        return null;
    }

    @Override
    public HeadFinder typedDependencyHeadFinder() {
        return null;
    }

    @Override
    public TreeTransformer collinizer() {
        return null;
    }

    @Override
    public TreeTransformer collinizerEvalb() {
        return null;
    }

    @Override
    public String[] sisterSplitters() {
        return new String[0];
    }

    @Override
    public Tree transformTree(Tree tree, Tree tree1) {
        return null;
    }

    @Override
    public void display() {

    }

    @Override
    public List<? extends HasWord> defaultTestSentence() {
        String[] sent = new String[]{"Sådan", "en", "uppmuntran", "har", "Stefan", "Löfvén", "lång", "inte", "mer", "upplevd", "."};
        return SentenceUtils.toWordList(sent);
    }

    @Override
    public TreebankLanguagePack treebankLanguagePack() {
        logger.debug("Fetching Language pack (TLP)");
        return this.tlp;
    }
}
