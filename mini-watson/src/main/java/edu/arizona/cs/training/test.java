//package edu.arizona.cs.training;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.File;
//import java.io.FileReader;
//import java.io.BufferedReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.store.RAMDirectory;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.StopAnalyzer;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//import org.apache.lucene.index.TermFreqVector;
//
//public class mIndexer extends Thread {
//
//    private File ifile;
//    private static IndexWriter writer;
//
//    public mIndexer(File f) {
//        ifile = f.getAbsoluteFile();
//    }
//
//    public static void main(String args[]) throws Exception {
//        System.out.println("here...");
//
//        String indexDir;
//        String dataDir;
//        if (args.length != 2) {
//            dataDir = new String("/home/omid/Ranking/docs/");
//            indexDir = new String("/home/omid/Ranking/indexes/");
//        }
//        else {
//            dataDir = args[0];
//            indexDir = args[1];
//        }
//
//        long start = System.currentTimeMillis();
//
//        Directory dir = FSDirectory.open(new File(indexDir));
//        writer = new IndexWriter(dir,
//                new StopAnalyzer(Version.LUCENE_34, new File("/home/omid/Desktop/stopwords.txt")),
//                true,
//                IndexWriter.MaxFieldLength.UNLIMITED);
//        int numIndexed = 0;
//        try {
//            numIndexed = index(dataDir, new TextFilesFilter());
//        } finally {
//            long end = System.currentTimeMillis();
//            System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
//            writer.optimize();
//            System.out.println("Optimization took place in " + (System.currentTimeMillis() - end) + " milliseconds");
//            writer.close();
//        }
//        System.out.println("Enjoy your day/night");
//    }
//
//    public static int index(String dataDir, FileFilter filter) throws Exception {
//        File[] dires = new File(dataDir).listFiles();
//        for (File d: dires) {
//            if (d.isDirectory()) {
//                File[] files = new File(d.getAbsolutePath()).listFiles();
//                for (File f: files) {
//                    if (!f.isDirectory() &&
//                            !f.isHidden() &&
//                            f.exists() &&
//                            f.canRead() &&
//                            (filter == null || filter.accept(f))) {
//                        Thread t = new mIndexer(f);
//                        t.start();
//                        t.join();
//                    }
//                }
//            }
//        }
//        return writer.numDocs();
//    }
//
//    private static class TextFilesFilter implements FileFilter {
//        public boolean accept(File path) {
//            return path.getName().toLowerCase().endsWith(".txt");
//        }
//    }
//
//    protected Document getDocument() throws Exception {
//        Document doc = new Document();
//        if (ifile.exists()) {
//            doc.add(new Field("contents", new FileReader(ifile), Field.TermVector.YES));
//            doc.add(new Field("path", ifile.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
//            String cat = "WIR";
//            cat = ifile.getAbsolutePath().substring(0, ifile.getAbsolutePath().length()-ifile.getName().length()-1);
//            cat = cat.substring(cat.lastIndexOf('/')+1, cat.length());
//            //doc.add(new Field("category", cat.subSequence(0, cat.length()), Field.Store.YES));
//            //System.out.println(cat.subSequence(0, cat.length()));
//        }
//        return doc;
//    }
//
//    public void run() {
//        try {
//            System.out.println("Indexing " + ifile.getAbsolutePath());
//            Document doc = getDocument();
//            writer.addDocument(doc);
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//
//    }
//}