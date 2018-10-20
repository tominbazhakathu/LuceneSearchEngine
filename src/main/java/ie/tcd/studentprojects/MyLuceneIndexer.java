package ie.tcd.studentprojects;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

class MyLuceneIndexer {

    public void createCranIndex(List<Map<String, String>> cranList) {


        indexClassic(cranList);
        indexLMD(cranList);
        indexBM25(cranList);


    }

    private void indexBM25(List<Map<String, String>> cranList) {

        try {
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/BM25"));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            config.setSimilarity(new BM25Similarity());


            IndexWriter iwriter = new IndexWriter(directory, config);

            for (int i = 0; i < cranList.size(); i++)
                addCranDocument(iwriter, cranList.get(i));

            iwriter.close();
            directory.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void indexLMD(List<Map<String, String>> cranList) {
        try {
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/LMD"));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            config.setSimilarity(new LMDirichletSimilarity());


            IndexWriter iwriter = new IndexWriter(directory, config);

            for (int i = 0; i < cranList.size(); i++)
                addCranDocument(iwriter, cranList.get(i));

            iwriter.close();
            directory.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void indexClassic(List<Map<String, String>> cranList) {
        try {
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/Classic"));
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            config.setSimilarity(new ClassicSimilarity());


            IndexWriter iwriter = new IndexWriter(directory, config);

            for (int i = 0; i < cranList.size(); i++)
                addCranDocument(iwriter, cranList.get(i));

            iwriter.close();
            directory.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCranDocument(IndexWriter iwriter, Map<String, String> cranDict) throws IOException {

        Document document = new Document();
        document.add(new StringField("ID", cranDict.get("ID"), Field.Store.YES));
        document.add(new TextField("Title", cranDict.get("Title"), Field.Store.YES));
        document.add(new TextField("Locations", cranDict.get("Locations"), Field.Store.YES));
        document.add(new TextField("Authors", cranDict.get("Authors"), Field.Store.YES));
        document.add(new TextField("Abstract", cranDict.get("Abstract"), Field.Store.YES));
        iwriter.addDocument(document);
    }

    public static void main(String[] args) {

        System.out.println("Parsing Started");
        FileRead fileIO = new FileRead();
        List<Map<String, String>> cranList = fileIO.cranParser("dataDirectory");
        System.out.println("Parsing Finished!");

        fileIO.deleteDir(new File("indexFile"));

        System.out.println("Indexing data...");
        MyLuceneIndexer indexer = new MyLuceneIndexer();
        indexer.createCranIndex(cranList);
        System.out.println("Indexing Finished");
    }
}
