package threes.filemerge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import threes.simulator.MergedResults;

public class Merger {
	public TreeMap<String,MergedResults> fileOne;
	public TreeMap<String,MergedResults> fileTwo;
	
	public Merger() {
	}
	
	public void loadFileOne(String file) {
		fileOne = Parser.parseFile(file);
	}
	public void loadFileTwo(String file) {
		fileTwo = Parser.parseFile(file);
	}
	
	public void mergeFiles() {
		for (String key : fileTwo.keySet()) { 
			if (fileOne.containsKey(key)) {
				MergedResults mrOne = fileOne.get(key);
				MergedResults mrTwo = fileTwo.get(key);
				mrOne.combineResults(mrTwo);
				fileTwo.remove(key);
			}
		}
	}
	
	public void writeFiles() {
		String fileName = "Merged_" + new Date();
		try{
		    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			for (String key : fileOne.keySet()) { 
				MergedResults mr = fileOne.get(key);
				writer.println(key + ", " + mr);
			}		
			for (String key : fileOne.keySet()) { 
				MergedResults mr = fileTwo.get(key);
				writer.println(key + ", " + mr);
			}		
		    writer.close();
		} catch (IOException e) {
		   System.out.println("oh SNAP with the file. " + e);
		}
		
	}
}
