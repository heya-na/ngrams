import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Uni {
	//create 6 hash maps for the 6 languages. Key is the character, value is its frequency
	static Map<Character, Integer> hm_eu = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_ca = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_gl = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_es = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_en = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_pt = new HashMap<Character, Integer>();
	
	//6 hash maps for the probabilities. Key is the character, value is its probabilities
	static Map<Character, ArrayList<Double>> pro_eu = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_ca = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_gl = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_es = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_en = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_pt = new HashMap<Character, ArrayList<Double>>();
	
	//calculate the total number of characters in the language
	public static int calculateSum(Map<Character, Integer> hm){
		int sum = 0;		
		Iterator<Map.Entry<Character, Integer>> i = hm.entrySet().iterator(); 
	     while(i.hasNext()){
	         Character key = i.next().getKey();	 
	         sum += hm.get(key);
	     }	     
	     System.out.println("sum: " + sum);
	     return sum;        
	}
	
	//calculate the probabilities of one character in the language
	public static void calculateProbability(String str, Map<Character, Integer> hm, 
			Map<Character, ArrayList<Double>> pro) throws IOException{		
		double probability;
		int sum = calculateSum(hm);
		Iterator<Map.Entry<Character, Integer>> i = hm.entrySet().iterator();
		
		File file =new File("unigramLM.txt");		
		//if file doesn't exists, then create it
		if(!file.exists()){
			file.createNewFile();
		}
		
		//true = append file
		FileWriter fileWritter = new FileWriter(file.getName(),true);
	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);     
	    bufferWritter.write(str);
	    bufferWritter.newLine();
	    
	    while(i.hasNext()){
	         Character key = i.next().getKey();	 
	         probability = (double)hm.get(key) / sum;
	         ArrayList<Double> ls = new ArrayList<Double>();
       	   	 ls.add(probability);
       	   	 ls.add(probability + 0.1);
	         pro.put(key, ls);
	         //System.out.println(probability);
	         bufferWritter.write(key + "\t" + ls.get(0) + "\t\t" + ls.get(1));
	         bufferWritter.newLine();	         
	    }	
	    bufferWritter.close();
	}

	//calculate one test sentence's possibility in the given language
	public static double testData(String s, Map<Character, ArrayList<Double>> hm){
		double probability = 0.0;
		for(int i = 0; i<s.length(); i++){
			char c = s.charAt(i);
			if(Character.isLetter(c)){
				if(hm.containsKey(c)){
					ArrayList<Double> ls = hm.get(c);
					probability += Math.log10(ls.get(0));
				}
			}
		}
		return probability;
	}
	
	public static void parse(String part, Map<Character, Integer> hm){
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
	
	private static HashMap sortByValues(Map<Character, Integer> hm) { 
	       List list = new LinkedList(hm.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	                  .compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	}
	
	public static void printHashMap(Map<Character, Integer> hm){
		Iterator<Map.Entry<Character, Integer>> i = hm.entrySet().iterator(); 
	     while(i.hasNext()){
	         Character key = i.next().getKey();
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
	      
	     hm_eu = sortByValues(hm_eu);
	     printHashMap(hm_eu);
//	     printHashMap(hm_ca);
//	     printHashMap(hm_gl);
//	     printHashMap(hm_es);
//	     printHashMap(hm_en);
//	     printHashMap(hm_pt);
	     calculateProbability("Basque",hm_eu, pro_eu);
//	     calculateProbability("Catalan",hm_ca, pro_ca);
//	     calculateProbability("Galician",hm_gl, pro_gl);
//	     calculateProbability("Spanish",hm_es, pro_es);
//	     calculateProbability("English",hm_en, pro_en);
//	     calculateProbability("Portuguese",hm_pt, pro_pt);
	     in.close();
	     
	     BufferedReader readTestFile = new BufferedReader(new FileReader("simple-testing-tweets_clean.txt"));
	     while ((line = in.readLine()) != null) {
	         String parts[] = line.split("\t");
	        
	     }
	}

}
