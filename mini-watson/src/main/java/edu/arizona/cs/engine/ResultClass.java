package edu.arizona.cs.engine;
import org.apache.lucene.document.Document;

public class ResultClass {
    Document DocName;
    double docScore = 0;
    public ResultClass(Document doc){
        this.DocName = doc;
    }
}
