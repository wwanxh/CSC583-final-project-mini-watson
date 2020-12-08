package edu.arizona.cs.parser;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public interface Item {

    public void lemmatization(StanfordCoreNLP pipeline);
    public void printout();
}
