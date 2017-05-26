package threes.filemerge;
import java.io.*;
import java.util.*;

import threes.simulator.MergedResults;

public class Parser {
    public static TreeMap<String,MergedResults> parseFile(String fileName) {

        String line = "";
        String cvsSplitBy = ",";
        String serialized;
        int count;
        double score;
        double moves;
        
        TreeMap<String,MergedResults> retVal = new TreeMap<String,MergedResults>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] linefeed = line.split(cvsSplitBy);
                serialized = linefeed[0];
                count = Integer.parseInt(linefeed[1]);
                score = Double.parseDouble(linefeed[2]);
                moves = Double.parseDouble(linefeed[3]);

                MergedResults mr = new MergedResults(count,score,moves);
                retVal.put(serialized, mr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }	
}
