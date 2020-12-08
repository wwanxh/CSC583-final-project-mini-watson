package edu.arizona.cs.parser;

import edu.arizona.cs.Preferences;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class DataSetParser {
    private static StanfordCoreNLP pipeline = StanfordCoreNLPManager.buildPipeline();
    public static List<PageItem> GetPages() throws InterruptedException {
        System.out.println("---------- Loading Wiki Pages ----------");
        List<PageItem> res = new ArrayList<>();
        File[] files = new File(DataSetParser.class.getClassLoader().getResource(Preferences.TRAIN_DATA_SET_PATH).getFile()).listFiles();
        int cnt = 1;
        for(File file : files) {
            try (Scanner inputScanner = new Scanner(file)) {
                PageItem page = null;
                String subtitle = "main";
                StringBuilder temp = new StringBuilder();
                Matcher titleMatcher;
                Matcher substitleMatcher;
                while (inputScanner.hasNextLine()) {
                    String line = inputScanner.nextLine();
                    if (line.equals('\n')) {
                        continue;
                    } else if ((titleMatcher = Preferences.PAGE_TITLE_MATCHER.matcher(line)).matches()) {
                        if(page != null) {
                            page.getSubtitles().put(subtitle, temp.toString());
                            res.add(page);
                        }
                        page = new PageItem();
                        page.setTitle(titleMatcher.toMatchResult().group().replace('[', ' ').replace(']', ' ').trim());
                        subtitle = "main";
                        temp = new StringBuilder();
                    } else if ((substitleMatcher =Preferences.PAGE_SUBTITLE_MATCHER.matcher(line)).matches()) {
                        page.getSubtitles().put(subtitle, temp.toString());
                        subtitle = substitleMatcher.toMatchResult().group().replace('=', ' ').trim();
                        temp = new StringBuilder();
                    } else {
                        temp.append(line);
                        temp.append(" ");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("\t %% " + cnt + " / " + files.length + " has completed!");
            cnt ++;
        }
        System.out.println("---------- Wiki Pages are loaded! ----------");
        runLemmatization(res);
        return res;
    }

    public static List<QuestionItem> GetQuestions(){
        System.out.println("---------- Loading Questions ----------");
        List<QuestionItem> res = new ArrayList<>();
        File file = new File(DataSetParser.class.getClassLoader().getResource(Preferences.TEST_DATA_PATH).getFile());
        try (Scanner inputScanner = new Scanner(file)) {
            int cnt = 2;
            QuestionItem qi = null;
            while (inputScanner.hasNextLine()) {
                String line = inputScanner.nextLine();
                if(line.equals('\n') || cnt == -1){
                    cnt = 2;
                    continue;
                }
                switch (cnt){
                    case 2:
                        qi = new QuestionItem();
                        qi.setCategory(line.trim());
                        break;
                    case 1:
                        qi.setQuestion(line.trim());
                        break;
                    case 0:
                        qi.setAnwer(line.trim());
                        res.add(qi);
                        break;
                }
                cnt --;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(QuestionItem item : res){
            item.lemmatization(pipeline);
        }
        System.out.println("---------- Questions are loaded! ----------");
        return res;
    }

    private static void runLemmatization(List<PageItem> items) throws InterruptedException {
        System.out.println("Run lemmatization on all pages in MultiThreading mode --- " + Preferences.LEMMATIZATION_THREAD_NUMBER + " CPU CORES are used!");
        ParallelLemmatization[] threads = new ParallelLemmatization[Preferences.LEMMATIZATION_THREAD_NUMBER];
        for(int i = 0; i < Preferences.LEMMATIZATION_THREAD_NUMBER; i ++){
            threads[i] = new ParallelLemmatization(i, items);
            threads[i].start();
        }
        while(true){
            int cnt = 0;
            for(int i = 0; i < Preferences.LEMMATIZATION_THREAD_NUMBER; i ++){
                if(threads[i].isFinished()){
                    cnt ++;
                }
            }
            if(cnt == Preferences.LEMMATIZATION_THREAD_NUMBER){
                break;
            }
            System.out.println("Waiting for all threads finishing!");
            Thread.sleep(5000);
        }
        System.out.println("All threads are finished!");
    }

    static class ParallelLemmatization implements Runnable {
        private Thread t;
        private int threadName;
        private List<PageItem> items;
        private boolean finished;
        private int progress = 0;

        ParallelLemmatization( int name, List<PageItem> items) {
            this.threadName = name;
            this.items = items;
            System.out.println("Creating " +  threadName );
        }

        public void run() {
            System.out.println("Running Thread " +  threadName );
            this.finished = false;
            int totalTasks = items.size()/Preferences.LEMMATIZATION_THREAD_NUMBER;
            for(int i = 0; i < totalTasks; i ++){
                PageItem item = items.get(i * Preferences.LEMMATIZATION_THREAD_NUMBER + threadName);
                item.lemmatization(pipeline);
                int cur_progress = (int)((double)i/(double)totalTasks * 100.0);
                if(cur_progress != progress) {
                    System.out.println("Thread-" + threadName + " Progess: " + cur_progress +"%");
                    progress = cur_progress;
                }
            }
            this.finished = true;
            System.out.println("Thread " +  threadName + " exiting.");
        }

        public void start () throws InterruptedException {
            System.out.println("Starting " +  threadName );
            this.t = new Thread (this);
            this.t.start();
        }
        public boolean isFinished(){
            return this.finished;
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        StanfordCoreNLP pipeline = StanfordCoreNLPManager.buildPipeline();
//        List<QuestionItem> questions = DataSetParser.GetQuestions();
//        for(QuestionItem qi : questions) {
//            qi.lemmatization(pipeline);
//            qi.printout();
//        }

        List<PageItem> pages = DataSetParser.GetPages();
//        for(PageItem pi : pages) {
//            pi.lemmatization(pipeline);
//            pi.printout();
//        }
        System.out.println("DONE!");
    }

}
