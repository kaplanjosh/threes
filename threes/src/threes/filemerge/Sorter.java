package threes.filemerge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

import threes.simulator.MergedResults;

public class Sorter {
	public TreeMap<String,MergedResults> fileOne;
	String fileName;
	
	public Sorter(String f) {
		fileName = f;
		fileOne = Parser.parseFile(f);
	}

	public void writeFile() {
		String fileOutName = fileName + "_sorted";
		try{
		    PrintWriter writer = new PrintWriter(fileOutName, "UTF-8");
			for (String key : fileOne.keySet()) { 
				MergedResults mr = fileOne.get(key);
				writer.println(key + ", " + mr);
			}		
		    writer.close();
		} catch (IOException e) {
		   System.out.println("oh SNAP with the file. " + e);
		}
		
	}
	
}
