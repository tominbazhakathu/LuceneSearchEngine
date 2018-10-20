package ie.tcd.studentprojects;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author Tomin Bijaimon Azhakathu
 */
public class MySearch {

    private int HITS_PER_PAGE = 1000;

    public static void main(String[] args) throws ParseException {

        FileRead fileIO = new FileRead();
        List<Map<String, String>> cranList = fileIO.parseCranQueries("dataDirectory");
        System.out.println("Parsing Finsihed!");

        System.out.println("Searching data...");
        MySearch searcher = new MySearch();
        searcher.searchCranQueries("indexFiles", cranList);
    }

    private void searchCranQueries(String indexFile, List<Map<String, String>> cranList) {
        indexClassic(cranList);
        indexLMD(cranList);
        indexBM25(cranList);
        System.out.println("Results Generated");

    }

    private void indexBM25(List<Map<String, String>> cranList) {

        try {
            Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/BM25"));

            DirectoryReader indexReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            indexSearcher.setSimilarity(new BM25Similarity());


            List<String> resFileContent = new ArrayList<String>();

            System.out.println("\nStandard English Index analyzer \nBM25 similarity\n1000 hits per page.");

            // Create directory if it does not exist
            File outputDir = new File("results/BM25");
            if (!outputDir.exists()) outputDir.mkdirs();


            resultDict = parseSearch(cranList,analyzer,indexSearcher,resFileContent);

            Files.write(Paths.get("results/BM25/results.txt"), resFileContent, Charset.forName("UTF-8"));
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
    }

    private void indexLMD(List<Map<String, String>> cranList) {
        try {
            Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/LMD"));

            DirectoryReader indexReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            indexSearcher.setSimilarity(new LMDirichletSimilarity());


            List<String> resFileContent = new ArrayList<String>();

            System.out.println("\nStandard English Index analyzer \nLMDirichlet similarity\n1000 hits per page.");


            resultDict = parseSearch(cranList,analyzer,indexSearcher,resFileContent);

            // Create directory if it does not exist
            File outputDir = new File("results/LMD");
            if (!outputDir.exists()) outputDir.mkdirs();

            Files.write(Paths.get("results/LMD/results.txt"), resFileContent, Charset.forName("UTF-8"));
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }

    }

    private void indexClassic(List<Map<String, String>> cranList) {
        try {
            Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
            Analyzer analyzer = null;

            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

            Directory directory = FSDirectory.open(Paths.get("indexFile/Classic"));

            DirectoryReader indexReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            indexSearcher.setSimilarity(new ClassicSimilarity());


            List<String> resFileContent = new ArrayList<String>();

            System.out.println("\nStandard English Index analyzer \nClassic similarity\n1000 hits per page.");

            resultDict = parseSearch(cranList,analyzer,indexSearcher,resFileContent);

            // Create directory if it does not exist
            File outputDir = new File("results/Classic");
            if (!outputDir.exists()) outputDir.mkdirs();

            Files.write(Paths.get("results/Classic/results.txt"), resFileContent, Charset.forName("UTF-8"));
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
    }

    private Map<String, List<String>> parseSearch(List<Map<String, String>> cranList, Analyzer analyzer, IndexSearcher indexSearcher, List<String> resFileContent) {
        Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
        try {
            for (int i = 0; i < cranList.size(); i++) {

                Map<String, String> cranQuery = cranList.get(i);
                MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                        new String[]{"Title", "Locations", "Authors", "Abstract"},
                        analyzer);
                Query query = queryParser.parse(cranQuery.get("Query"));

                // Searching For Query
                TopDocs topDocs = indexSearcher.search(query, HITS_PER_PAGE);
                ScoreDoc[] hits = topDocs.scoreDocs;

                // Results
                List<String> resultList = new ArrayList<String>();
//                System.out.println(hits.length + " hits Found.");
                for (int j = 0; j < hits.length; j++) {

                    int docId = hits[j].doc;
                    Document doc = indexSearcher.doc(docId);
                    resultList.add(doc.get("ID"));
                    resFileContent.add(cranQuery.get("QueryNo") + " 0 " + doc.get("ID") + " 0 " + hits[j].score + " STANDARD");
                }
                resultDict.put(Integer.toString(i + 1), resultList);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDict;
    }

}