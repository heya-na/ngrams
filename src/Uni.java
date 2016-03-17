import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Uni {
	//create one hashmap for each language. Key is the character, value is its frequency.
	static Map<Character, Integer> hm_eu = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_ca = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_gl = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_es = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_en = new HashMap<Character, Integer>();
	static Map<Character, Integer> hm_pt = new HashMap<Character, Integer>();
	
	//create one hashmap for each language. Key is the character, value is an arrayList which contains its
	//smoothed and unsmoothed probability
	static Map<Character, ArrayList<Double>> pro_eu = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_ca = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_gl = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_es = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_en = new HashMap<Character, ArrayList<Double>>();
	static Map<Character, ArrayList<Double>> pro_pt = new HashMap<Character, ArrayList<Double>>();
	
	//confusion Matrix
	static int[][] confusionMatrix  = new int[6][6];
	
	public static void printMatrix() throws IOException{
		 File file2 =new File("analysis-unigram.txt");		
		 //if file doesn't exists, then create it
	     if(!file2.exists()){
	    	 file2.createNewFile();
		 }    				
	    //true = append file
		FileWriter fileWritter2 = new FileWriter(file2.getName(),true);
		BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);     	
		bufferWritter2.write("The confusion matrix of languages(in the order of eu ca gl es en pt):");
		bufferWritter2.newLine();
		
		for(int i = 0; i<6; i++){
			for(int j = 0; j<6; j++){
				bufferWritter2.write(confusionMatrix[i][j] + "\t");
				System.out.print(confusionMatrix[i][j] + "\t");
			}
			bufferWritter2.newLine();
			System.out.println();
		}
		bufferWritter2.close();
	}
	
	public static void matrix(String s1, String s2){
		int i = m(s1);
		 if(s2.equalsIgnoreCase("eu"))
			 confusionMatrix[i][0]++;
		 else if(s2.equalsIgnoreCase("ca"))
			 confusionMatrix[i][1]++;
		 else if(s2.equalsIgnoreCase("gl"))
			 confusionMatrix[i][2]++;
		 else if(s2.equalsIgnoreCase("es"))
			 confusionMatrix[i][3]++;
		 else if(s2.equalsIgnoreCase("en"))
			 confusionMatrix[i][4]++;
		 else if(s2.equalsIgnoreCase("pt"))
			 confusionMatrix[i][5]++;
	}
	
	public static int m(String s1){
		 int i = 0;
		 if(s1.equalsIgnoreCase("eu"))
			 i = 0;
		 else if(s1.equalsIgnoreCase("ca"))
			 i = 1;
		 else if(s1.equalsIgnoreCase("gl"))
			 i = 2;
		 else if(s1.equalsIgnoreCase("es"))
			 i = 3;
		 else if(s1.equalsIgnoreCase("en"))
			 i = 4;
		 else if(s1.equalsIgnoreCase("pt"))
			 i = 5;
		 return i;
	}
	
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
		double unsmoothedprobability;
		double smoothedprobability;
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
	         unsmoothedprobability = (double)hm.get(key) / sum;
	         smoothedprobability = (double)(hm.get(key)+ 0.1) / (sum + hm.size()*0.1);
	         ArrayList<Double> ls = new ArrayList<Double>();
       	   	 ls.add(unsmoothedprobability); //unsmoothed
       	   	 ls.add(smoothedprobability); //smoothed
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
	       // Defined Custom Comparator
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	                  .compareTo(((Map.Entry) (o1)).getValue());
	            }
	       });
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	}
	
	public static void printHashMap(Map<Character, Integer> hm){
		System.out.println("-------------------Map----------------------");
		Iterator<Map.Entry<Character, Integer>> i = hm.entrySet().iterator(); 
	     while(i.hasNext()){
	         Character key = i.next().getKey();
	         System.out.println(key+": "+hm.get(key));
	     }
	    System.out.println("-------------------------------------------");
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("simple-training-tweets_clean.txt"));
		//BufferedReader in = new BufferedReader(new FileReader("training data.txt"));
	    String line = "";
	     while ((line = in.readLine()) != null) {
	         String parts[] = line.split("\t");
	         parts[3] = parts[3].toLowerCase();
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
	     hm_ca = sortByValues(hm_ca);
	     hm_gl = sortByValues(hm_gl);
	     hm_es = sortByValues(hm_es);
	     hm_en = sortByValues(hm_en);
	     hm_pt = sortByValues(hm_pt);
//	     printHashMap(hm_eu);
//	     printHashMap(hm_ca);
//	     printHashMap(hm_gl);
//	     printHashMap(hm_es);
//	     printHashMap(hm_en);
//	     printHashMap(hm_pt);
	     calculateProbability("Basque",hm_eu, pro_eu);
	     calculateProbability("Catalan",hm_ca, pro_ca);
	     calculateProbability("Galician",hm_gl, pro_gl);
	     calculateProbability("Spanish",hm_es, pro_es);
	     calculateProbability("English",hm_en, pro_en);
	     calculateProbability("Portuguese",hm_pt, pro_pt);
	     in.close();
	     
	     File file =new File("results-unigram.txt");		

	   //if file doesn't exists, then create it
	     if(!file.exists()){
			file.createNewFile();
		}
			
		//true = append file
		FileWriter fileWritter = new FileWriter(file.getName(),true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);     
	   		
		BufferedReader readTestFile = new BufferedReader(new FileReader("simple-testing-tweets_clean.txt"));
		//BufferedReader readTestFile = new BufferedReader(new FileReader("testing data.txt"));
		int right = 0;
		int[] rightEachLang = new int[6];
		int[] totalEachLang = new int[6];
		int total = 0;
		
		for(int i=0; i<6; i++){
			rightEachLang[i] = 0;
			totalEachLang[i] = 0;
		}
		
	     while ((line = readTestFile.readLine()) != null) {
	         String parts[] = line.split("\t");	         
	         double max = 0.0;
	         String lang = "";
	         double temp = 0.0;
	         
	         if(parts[2].equalsIgnoreCase("eu"))
	        	 totalEachLang[0]++;
	         else if(parts[2].equalsIgnoreCase("ca"))
	        	 totalEachLang[1]++;
	         else if(parts[2].equalsIgnoreCase("gl"))
	        	 totalEachLang[2]++;
	         else if(parts[2].equalsIgnoreCase("es"))
	        	 totalEachLang[3]++;
	         else if(parts[2].equalsIgnoreCase("en"))
	        	 totalEachLang[4]++;
	         else if(parts[2].equalsIgnoreCase("pt"))
	        	 totalEachLang[5]++;
	         
	         max = testData(parts[3], pro_eu);
	         lang = "eu";
	         
	         temp = testData(parts[3], pro_ca);
	         if(max < temp){
	        	 max = temp;
	        	 lang = "ca";
	         } 
	         
	         temp = testData(parts[3], pro_gl);
	         if(max < temp){
	        	 max = temp;
	        	 lang = "gl";
	         }
	         
	         temp = testData(parts[3], pro_es);
	         if(max < temp){
	        	 max = temp;
	        	 lang = "es";
	         } 
	         
	         temp = testData(parts[3], pro_en);
	         if(max < temp){
	        	 max = temp;
	        	 lang = "en";
	         } 
	         
	         temp = testData(parts[3], pro_pt);
	         if(max < temp){
	        	 max = temp;
	        	 lang = "pt";
	         } 
	         
	         bufferWritter.write(parts[0] + "\t" + parts[2] + "\t" + lang);
	         bufferWritter.newLine();
	         total++;
	         if(lang.equalsIgnoreCase(parts[2])){
	        	 right++;	
	        	 if(lang.equalsIgnoreCase("eu"))
		        	 rightEachLang[0]++;
		         else if(lang.equalsIgnoreCase("ca"))
		        	 rightEachLang[1]++;
		         else if(lang.equalsIgnoreCase("gl"))
		        	 rightEachLang[2]++;
		         else if(lang.equalsIgnoreCase("es"))
		        	 rightEachLang[3]++;
		         else if(lang.equalsIgnoreCase("en"))
		        	 rightEachLang[4]++;
		         else if(lang.equalsIgnoreCase("pt"))
		        	 rightEachLang[5]++;
	         }
	         matrix(parts[2], lang);
	     }
	     double result = (double)right / total;
	     //System.out.println(result);
	     
	     File file2 =new File("analysis-unigram.txt");		
		 //if file doesn't exists, then create it
	     if(!file2.exists()){
	    	 file2.createNewFile();
		 }    				
	    //true = append file
		FileWriter fileWritter2 = new FileWriter(file2.getName(),true);
		BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);     	
		bufferWritter2.write("The accuracy of each language(in the order of eu ca gl es en pt):");
		bufferWritter2.newLine();
		bufferWritter2.write("right\t" + "total\t" + "accuracy");
		bufferWritter2.newLine();
	    for(int i = 0; i<6; i++){
	    	//System.out.println((double)rightEachLang[i]/totalEachLang[i]);
	    	bufferWritter2.write(rightEachLang[i]+ "\t" + totalEachLang[i] + "\t" +
	    			Double.toString((double)rightEachLang[i]/totalEachLang[i]));
	    	bufferWritter2.newLine();
	    }
	     
	    bufferWritter.close();
	    readTestFile.close();
	    bufferWritter2.close();
	    printMatrix();
	}

}
