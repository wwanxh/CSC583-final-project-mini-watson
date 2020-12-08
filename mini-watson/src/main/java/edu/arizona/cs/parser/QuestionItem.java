package edu.arizona.cs.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionItem implements Item{
    private String category;
    private String question;
    private String anwer;

    public void printout(){
        System.out.println("----------");
        System.out.println("Category: " + category);
        System.out.println("Question: " + question);
        System.out.println("Answer: " + anwer);
    }

    public void lemmatization(StanfordCoreNLP pipeline){
        CoreDocument document = pipeline.processToCoreDocument(question);
        StringBuilder sb = new StringBuilder();
        for(CoreLabel c : document.tokens()){
            sb.append(c.lemma());
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length()-1);
        question = sb.toString();
    }
}
