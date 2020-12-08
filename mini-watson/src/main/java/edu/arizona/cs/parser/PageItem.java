package edu.arizona.cs.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageItem implements Item{

    private String title;
    private Map<String, String> subtitles = new HashMap<>();

    public void lemmatization(StanfordCoreNLP pipeline){
        for(String key : subtitles.keySet()){
            CoreDocument document = pipeline.processToCoreDocument(subtitles.get(key));
            StringBuilder sb = new StringBuilder();
            for(CoreLabel c : document.tokens()){
                sb.append(c.lemma());
                sb.append(" ");
            }
            if(sb.length() > 0) sb.deleteCharAt(sb.length()-1);
            subtitles.put(key, sb.toString());
        }
    }

    public String getContent(){
        StringBuffer sb = new StringBuffer();
        for(String key : subtitles.keySet()){
            sb.append(subtitles.get(key));
            sb.append(" ");
        }
        return sb.toString();
    }

    public void printout(){
        System.out.println("----------");
        System.out.println("Title: " + title);
        for(String key : subtitles.keySet()){
            System.out.println("Subtitle: " + key);
            System.out.println("Content: " + subtitles.get(key));
        }
    }
}
