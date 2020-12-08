package edu.arizona.cs.training;

import edu.arizona.cs.Preferences;
import edu.arizona.cs.parser.DataSetParser;
import edu.arizona.cs.parser.PageItem;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ModelBuilder {

    public static void buildIndex() throws IOException {
        System.out.println("Building Index ...");
        List<PageItem> pages = null;
        try {
            pages = DataSetParser.GetPages();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Creating Lucene Environment ...");
        File file = new File(StringUtils.join(new String[]{System.getProperty("user.dir"), Preferences.INDEX_FILE_PATH}, "/"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = null;
        try {
            index = FSDirectory.open(file.toPath());
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter w = new IndexWriter(index, config);
            int cnt = 0;
            int progress = 0;
            for(PageItem page : pages){
                addDoc(w, page);
                cnt ++;
                int cur_progress = (int) ((double) cnt / (double) pages.size()) * 100;
                if(progress!=cur_progress){
                    System.out.println("\t%% Finished " + cur_progress + "%...");
                    progress = cur_progress;
                }

            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void addDoc(IndexWriter w, PageItem page) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("docid", page.getTitle(), Field.Store.YES));
        doc.add(new TextField("Content", page.getContent(), Field.Store.YES));
        w.addDocument(doc);
    }

    public static void main(String[] args) throws IOException {
        ModelBuilder.buildIndex();
    }
}
