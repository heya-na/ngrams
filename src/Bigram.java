import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Bigram {
	//create 6 hash maps for the 6 languages. Key is the character, value is its frequency
	static Map<String, Integer> hm_eu = new HashMap<String, Integer>();
	static Map<String, Integer> hm_ca = new HashMap<String, Integer>();
	static Map<String, Integer> hm_gl = new HashMap<String, Integer>();
	static Map<String, Integer> hm_es = new HashMap<String, Integer>();
	static Map<String, Integer> hm_en = new HashMap<String, Integer>();
	static Map<String, Integer> hm_pt = new HashMap<String, Integer>();
	
	//6 hash maps for the probabilities. Key is the character, value is its probabilities
	static Map<String, ArrayList<Double>> pro_eu = new HashMap<String, ArrayList<Double>>();
	static Map<String, ArrayList<Double>> pro_ca = new HashMap<String, ArrayList<Double>>();
	static Map<String, ArrayList<Double>> pro_gl = new HashMap<String, ArrayList<Double>>();
	static Map<String, ArrayList<Double>> pro_es = new HashMap<String, ArrayList<Double>>();
	static Map<String, ArrayList<Double>> pro_en = new HashMap<String, ArrayList<Double>>();
	static Map<String, ArrayList<Double>> pro_pt = new HashMap<String, ArrayList<Double>>();
	
	//calculate the total number of characters in the language
	public static int calculateSum(Map<String, Integer> hm){
		int sum = 0;		
		Iterator<Map.Entry<String, Integer>> i = hm.entrySet().iterator(); 
	     while(i.hasNext()){
	    	 String key = i.next().getKey();	 
	         sum += hm.get(key);
	     }	     
	     System.out.println("sum: " + sum);
	     return sum;        
	}
	
	//calculate the probabilities of one character in the language
	public static void calculateProbability(String str, Map<String, Integer> hm, 
			Map<String, ArrayList<Double>> pro) throws IOException{		
		double probability;
		int sum = calculateSum(hm);
		Iterator<Map.Entry<String, Integer>> i = hm.entrySet().iterator();		
		File file =new File("bigramLM.txt");		
		
		//if file doesn't exists, then create it
		if(!file.exists()){
			file.createNewFile();
		}		
		//true = append file
		FileWriter fileWritter = new FileWriter(file.getName(),true);
	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);     
	    bufferWritter.write(str);
	    bufferWritter.newLine();
	    int index = 1;
	    while(i.hasNext()){
	    	 String key = i.next().getKey();	 
	         ArrayList<Double> ls = new ArrayList<Double>();
	         probability = (double)hm.get(key) / sum;
       	   	 ls.add(probability);
       	   	 ls.add(probability + 0.1);
	         pro.put(key, ls);
	         //System.out.println(probability);
	         if(index <= 50){
	        	 bufferWritter.write(index + ". " + key + "\t" + ls.get(0) + "\t\t" + ls.get(1));
		         bufferWritter.newLine();	   
		         index++;
	         } 
	    }	
	    bufferWritter.close();
	}
		
	
	public static void parseBigram(String part, Map<Character, Integer> hm){
		for(int i = 0; i < part.length(); i++){
       	 char c = part.charAt(i);
	 		 if(Character.isLetter(c)){
	 			Integer val = hm.get(new Character(c));
 	        	   if(val != null){
 	        		   hm.put(c, new Integer(val + 1));
 	        	   }else{
 	        		   hm.put(c,1);
 	        	   }
	 		 } 			  	        	           	  
       }
	}
	
	public static void parse(String part, Map<String, Integer> hm){
		for(int i = 0; i < part.length()-1; i++){
       	 	char c1 = part.charAt(i);
       	 	char c2 = part.charAt(i + 1);
	 		 if(Character.isLetter(c1) && Character.isLetter(c2)){
	 			String s = "" + c1 + c2;
	 			Integer val = hm.get(new String(s));
 	        	if(val != null){
 	        		hm.put(s, new Integer(val + 1));
 	        	}else{
 	        		hm.put(s,1);
 	        	}
	 		 } 			  	        	           	  
       }
	}
	
	public static void printHashMap(Map<String, Integer> hm){
		Iterator<Map.Entry<String, Integer>> i = hm.entrySet().iterator(); 
	     while(i.hasNext()){
	    	 String key = i.next().getKey();
	         System.out.println(key+": "+hm.get(key));
	     }
	     System.out.println("-------------------------------------------");
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("simple-training-tweets_clean.txt"));
	    String line = "";
	     while ((line = in.readLine()) != null) {
	         String parts[] = line.split("\t");
	         if(parts[2].equalsIgnoreCase("eu"))
	        	 parse(parts[3], hm_eu);
	         else if(parts[2].equalsIgnoreCase("ca"))
	        	 parse(parts[3], hm_ca);
	         else if(parts[2].equalsIgnoreCase("gl"))
	        	 parse(parts[3], hm_gl);
	         else if(parts[2].equalsIgnoreCase("es"))
	        	 parse(parts[3], hm_es);
	         else if(parts[2].equalsIgnoreCase("en"))
	        	 parse(parts[3], hm_en);
	         else if(parts[2].equalsIgnoreCase("pt"))
	        	 parse(parts[3], hm_pt);
	     }
	     //printHashMap(hm_eu);
	     
	     calculateProbability("Basque",hm_eu, pro_eu);
	     calculateProbability("Catalan",hm_ca, pro_ca);
	     calculateProbability("Galician",hm_gl, pro_gl);
	     calculateProbability("Spanish",hm_es, pro_es);
	     calculateProbability("English",hm_en, pro_en);
	     calculateProbability("Portuguese",hm_pt, pro_pt);
	     
	     in.close();

	}

}
