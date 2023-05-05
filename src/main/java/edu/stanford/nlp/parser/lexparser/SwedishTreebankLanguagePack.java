package edu.stanford.nlp.parser.lexparser;

import edu.stanford.nlp.trees.AbstractTreebankLanguagePack;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.RightHeadFinder;

public class SwedishTreebankLanguagePack extends AbstractTreebankLanguagePack {

    private static final String encoding = "UTF-8";
    private static final String[] sentencePunctuationTags = new String[]{"$."};
    private static final String[] sentencePunctuationWords = new String[]{".", "!", "?"};
    private static final String[] punctuationTags = new String[]{"$.", "$,", "$*LRB*"};
    private static final String[] punctuationWords = new String[]{"-", ",", ";", ":", "!", "?",
            "/", ".", "...", "·", "'", "\"", "(", ")", "*LRB*", "*RRB*"};
    private static char[] annotationIntroducingChars = new char[]{'-', '%', '=', '|', '#', '^',
            '~'};
    private static String[] pennStartSymbols = new String[]{"ROOT"};

    @Override
    public String[] punctuationTags() {
        return punctuationTags;
    }

    @Override
    public String[] punctuationWords() {
        return this.sentencePunctuationWords;
    }

    @Override
    public String[] sentenceFinalPunctuationTags() {
        return this.sentencePunctuationTags;
    }

    @Override
    public String[] sentenceFinalPunctuationWords() {
        return this.punctuationWords;
    }

    @Override
    public String[] startSymbols() {
        return this.pennStartSymbols;
    }

    @Override
    public String treebankFileExtension() {
        return "tree";
    }

    @Override
    public HeadFinder headFinder() {
        return new RightHeadFinder();
    }

    @Override
    public HeadFinder typedDependencyHeadFinder() {
        return new RightHeadFinder();
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public char[] labelAnnotationIntroducingCharacters() {
        return annotationIntroducingChars;
    }
}
