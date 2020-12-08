package edu.arizona.cs.parser;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class StanfordCoreNLPManager {

    public static StanfordCoreNLP buildPipeline(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,stopword");
//        props.setProperty("customAnnotatorClass.stopword", "intoxicant.analytics.corenlp.StopwordAnnotator");
        return new StanfordCoreNLP(props);
    }

}
