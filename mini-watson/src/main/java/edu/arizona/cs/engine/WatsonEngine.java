package edu.arizona.cs.engine;

import edu.arizona.cs.Preferences;
import edu.arizona.cs.parser.DataSetParser;
import edu.arizona.cs.parser.QuestionItem;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class WatsonEngine {
    IndexSearcher searcher;
    Analyzer analyzer;
    public WatsonEngine() throws IOException {
        String indexPath = StringUtils.join(new String[]{System.getProperty("user.dir"), Preferences.INDEX_FILE_PATH}, "/");
        Directory indexDirectory = FSDirectory
                .open(Paths.get(indexPath));
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(indexReader);
//        searcher.setSimilarity(new ClassicSimilarity());
        analyzer = new StandardAnalyzer();
    }
    public List<ResultClass> query(String queryExpr, int hitPerPage) throws ParseException, IOException {
        Query query = new QueryParser("Content", analyzer).parse(QueryParser.escape(queryExpr));
        TopDocs topDocs = searcher.search(query, hitPerPage);
        List<ResultClass> res = new ArrayList<>();
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            res.add(new ResultClass(searcher.doc(scoreDoc.doc), scoreDoc.score));
        }
        return res;
    }

    public ResultClass[] queryAllQuestions(List<QuestionItem> questionItems, int hitPerPage, boolean languageModelApplied, boolean verbose) throws IOException, ParseException {
        int cnt = 0;
        ResultClass[] res = new ResultClass[questionItems.size()];
        for(QuestionItem item : questionItems){
            if(verbose) System.out.println("-----------");
            if(verbose) System.out.println("Category:\t" + item.getCategory());
            if(verbose) System.out.println("Question:\t" + item.getQuestion());
            if(verbose) System.out.println("Answer:\t" + item.getAnwer());
            List<ResultClass> rc = null;
            if(!languageModelApplied)
                rc = query(item.getCategory() + " " + item.getQuestion(), hitPerPage);
            else
                rc = queryWithLanguageModel(item.getCategory() + " " + item.getQuestion(), hitPerPage);
            if(rc.size() == 0){
                if(verbose) System.out.println("Prediction:\tNo match!");
            }else {
                for(int i = 0; i < rc.size(); i ++) {
                    if(verbose) System.out.println("--> Prediction #" + i + ":\t" + rc.get(i).DocName.get("docid"));
                    if(verbose) System.out.println("--> \t\tScore:\t" + rc.get(i).docScore);
                    if (item.getAnwer().contains(rc.get(i).DocName.get("docid").trim())) {
                        rc.get(i).docScore = (float)i + 1f;
                        res[cnt] = rc.get(i);
                        cnt++;
                        if(verbose) System.out.println("--> \t\tResult:\tMATCHED! ================");
                    }
                }
            }
        }
        System.out.println(cnt + " questions are matched!");
        return res;
    }

    public List<ResultClass> queryWithLanguageModel(String queryExpr, int hitPerPage) throws ParseException, IOException {
        Query query = new QueryParser("Content", analyzer).parse(QueryParser.escape(queryExpr));
        TopDocs topDocs = searcher.search(query, hitPerPage);
        return reRanking(queryExpr, topDocs);
    }

    private List<ResultClass>  reRanking(String queryExpr, TopDocs topDocs) throws IOException {
        List<ResultClass> res = new ArrayList<>();
        String[] queryArr = queryExpr.split(" ");
        Map<Integer, List<String>> docMap = new HashMap<>();
        for(ScoreDoc scoreDoc:topDocs.scoreDocs){
            List<String> content = Arrays.asList(searcher.doc(scoreDoc.doc).get("Content").split(" "));
            docMap.put(scoreDoc.doc, content);
        }
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            int docID = scoreDoc.doc;
            Document doc = searcher.doc(docID);
            List<String> tokens = docMap.get(scoreDoc.doc);
            double score = 1;
            for(String c : queryArr){
                int num = tokens.size();
                int tf = Collections.frequency(tokens, c);
                double total = 0, occurances = 0;
                for(int docid : docMap.keySet()){
                    occurances += Collections.frequency(docMap.get(docid), c);
                    total += docMap.get(docid).size();
                }
                double PtMc = occurances / total;
                double param = (tf + 0.5 * PtMc) / (num + 0.5);
                score *= param;
            }
            res.add(new ResultClass(doc, score));
        }
        Collections.sort(res, new Comparator<ResultClass>() {
            @Override
            public int compare(ResultClass o1, ResultClass o2) {
                return new Double(o2.docScore).compareTo(o1.docScore);
            }
        });
        return res;
    }
    public void Pat1(int hitPerPage, boolean languageModelApplied) throws IOException, ParseException {
        List<QuestionItem> questionItems = DataSetParser.GetQuestions();
        int cnt = 0;
        for(QuestionItem item : questionItems){
            List<ResultClass> rc = null;
            if(!languageModelApplied)
                rc = query(item.getCategory() + " " + item.getQuestion(), hitPerPage);
            else
                rc = queryWithLanguageModel(item.getCategory() + " " + item.getQuestion(), hitPerPage);
            if(item.getAnwer().contains(rc.get(0).DocName.get("docid").trim())){
                cnt ++;
            }
        }
        System.out.println("-----------");
        System.out.println("%% P@1: " + cnt + " / " + questionItems.size());
    }

    public void MRRBenchMark(int hitPrePage, boolean languageModelApplied) throws IOException, ParseException {
        List<QuestionItem> questionItems = DataSetParser.GetQuestions();
        ResultClass[] result_arr = queryAllQuestions(questionItems, hitPrePage, languageModelApplied, false);
        double res = 0.0;
        for(int i = 0; i < result_arr.length; i ++){
            if(result_arr[i] == null) continue;
            res += 1.0/result_arr[i].docScore;
        }
        res /= (double)questionItems.size();
        System.out.println("MRR: " + res);
    }

    public static void main(String[] args) throws IOException, ParseException {
        WatsonEngine we = new WatsonEngine();
//        List<QuestionItem> questionItems = DataSetParser.GetQuestions();
//        we.queryAllQuestions(questionItems, 10, true,true);
        we.Pat1(1, false);
        we.MRRBenchMark(100, false);
    }
}
