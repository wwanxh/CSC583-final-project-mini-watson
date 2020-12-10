package edu.arizona.cs.parser;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class StanfordCoreNLPManager {

    public static StanfordCoreNLP buildPipeline(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,cleanxml,ssplit,pos,lemma");
        return new StanfordCoreNLP(props);
    }

}
