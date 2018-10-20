package ie.tcd.studentprojects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * File Read and Querying
 */

public class FileRead {

    /**
     * Parse the Documents
     * Get Results As List of Mapped Values
     */
    public List<Map<String, String>> cranParser(String cranDirect) {

        List<Map<String, String>> cranList = new ArrayList<Map<String, String>>();
        try {

            if (!(new File(cranDirect).exists() && new File(cranDirect).isDirectory())) cranDirect = "dataDirectory/";
            System.out.println(cranDirect);
            File file = new File(cranDirect + "/cran.all.1400");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Map<String, String> cranRead = new HashMap<String, String>();

            String line;
            String nextParseStringParseString = ".I";
            int lineNumber = 0;

            String id = "";
            String title = "";
            String authors = "";
            String locations = "";
            String fileAbstract = "";

            while ((line = bufferedReader.readLine()) != null) {

                lineNumber++;

                String[] wordsInLine = line.split("\\s+");
                switch (wordsInLine[0]) {

                    case ".I":
                        if (nextParseStringParseString != ".I")
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        assert (Integer.parseInt(wordsInLine[1]) - 1) == cranList.size();
                        if (lineNumber > 1) {

                            cranRead.put("ID", id);
                            cranRead.put("Abstract", fileAbstract);
                            cranList.add(cranRead);
                            cranRead = new HashMap<String, String>();
                        }
                        id = wordsInLine[1];
                        fileAbstract = "";
                        nextParseStringParseString = ".T";
                        break;

                    case ".T":
                        if (nextParseStringParseString != ".T")
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        nextParseStringParseString = ".A";
                        break;

                    case ".A":
                        if (nextParseStringParseString != ".A") {

                            if (nextParseStringParseString == ".I") break;
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        }
                        cranRead.put("Title", title);
                        title = "";
                        nextParseStringParseString = ".B";
                        break;

                    case ".B":
                        if (nextParseStringParseString != ".B") {

                            if (nextParseStringParseString == ".I") break;
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        }
                        cranRead.put("Authors", authors);
                        authors = "";
                        nextParseStringParseString = ".W";
                        break;

                    case ".W":
                        if (nextParseStringParseString != ".W") {

                            if (nextParseStringParseString == ".I") break;
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        }
                        cranRead.put("Locations", locations);
                        locations = "";
                        nextParseStringParseString = ".I";
                        break;

                    default:
                        switch (nextParseStringParseString) {
                            case ".A":
                                title += line + " ";
                                break;

                            case ".B":
                                authors += line + " ";
                                break;

                            case ".W":
                                locations += line + " ";
                                break;

                            case ".I":
                                fileAbstract += line + " ";
                                break;

                            default:
                                System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        }
                }
            }

            cranRead.put("ID", id);
            cranRead.put("Abstract", fileAbstract);
            cranList.add(cranRead);

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return cranList;
    }


    public List<Map<String, String>> parseCranQueries(String cranDirect) {

        List<Map<String, String>> cranQueryList = new ArrayList<Map<String, String>>();
        try {

            if (!(new File(cranDirect).exists() && new File(cranDirect).isDirectory())) cranDirect = "dataDirectory";


            File file = new File(cranDirect + "/cran.qry");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            Map<String, String> cranQueryDict = new HashMap<String, String>();

            String line;
            String nextParseStringParseString = ".I";
            int lineNumber = 0;

            String id = "";
            int queryNo = 0;
            String query = "";

            while ((line = bufferedReader.readLine()) != null) {

                lineNumber++;

                line = line.replace("?", "");
                String[] wordsInLine = line.split("\\s+");
                switch (wordsInLine[0]) {


                    case ".I":
                        if (nextParseStringParseString != ".I")
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        if (lineNumber > 1) {

                            cranQueryDict.put("ID", id);
                            cranQueryDict.put("QueryNo", Integer.toString(queryNo));
                            cranQueryDict.put("Query", query);
                            cranQueryList.add(cranQueryDict);
                            cranQueryDict = new HashMap<String, String>();
                        }
                        id = wordsInLine[1];
                        queryNo++;
                        query = "";
                        nextParseStringParseString = ".W";
                        break;

                    case ".W":
                        if (nextParseStringParseString != ".W")
                            System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        nextParseStringParseString = ".I";
                        break;

                    default:
                        switch (nextParseStringParseString) {

                            case ".I":
                                query += line + " ";
                                break;

                            default:
                                System.out.println("Parsing Error Detected on  " + Integer.toString(lineNumber));
                        }
                }
            }

            cranQueryDict.put("ID", id);
            cranQueryDict.put("QueryNo", Integer.toString(queryNo));
            cranQueryDict.put("Query", query);
            cranQueryList.add(cranQueryDict);

            fileReader.close();
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
        return cranQueryList;
    }

    public void deleteDir(File file) {

        File[] contents = file.listFiles();
        if (contents != null) {

            for (File f : contents) {

                deleteDir(f);
            }
        }
        file.delete();
    }
}
