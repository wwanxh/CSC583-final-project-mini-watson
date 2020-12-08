package edu.arizona.cs.engine;

import edu.arizona.cs.Preferences;
import edu.arizona.cs.parser.DataSetParser;
import edu.arizona.cs.parser.QuestionItem;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WatsonEngine {
    IndexSearcher searcher;
    StandardAnalyzer analyzer;
    public WatsonEngine() throws IOException {
        String indexPath = StringUtils.join(new String[]{System.getProperty("user.dir"), Preferences.INDEX_FILE_PATH}, "/");
        Directory indexDirectory = FSDirectory
                .open(Paths.get(indexPath));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(indexReader);

        analyzer = new StandardAnalyzer();
    }
    public  List<ResultClass> query(String queryExpr) throws ParseException, IOException {
        Query query = new QueryParser("Content", analyzer).parse(QueryParser.escape(queryExpr));
        TopDocs topDocs = searcher.search(query, 4);
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return searcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .map(ResultClass::new)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, ParseException {
        WatsonEngine we = new WatsonEngine();
        List<QuestionItem> questionItems = DataSetParser.GetQuestions();
        int cnt = 0;
        for(QuestionItem item : questionItems){
            List<ResultClass> rc = we.query(item.getCategory() + " " + item.getQuestion());
            if(rc.size() == 0){
                System.out.println("No match!");
            }else {
                System.out.println(item.getAnwer() + " vs. " + rc.get(0).DocName.get("docid"));
                if (item.getAnwer().trim().equals(rc.get(0).DocName.get("docid").trim())) {
                    cnt++;
                }
            }
        }
        System.out.println(cnt + " questions are matched!");
    }
}
