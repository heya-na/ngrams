import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
	
	//confusion Matrix
	static int[][] confusionMatrix  = new int[6][6];
	
	//The probability of each language in the training set
	static List<Double> pro_lang = new ArrayList<Double>();
		
	public static void printMatrix() throws IOException{
		 File file2 =new File("analysis-bigram.txt");		
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
				//System.out.print(confusionMatrix[i][j] + "\t");
			}
			bufferWritter2.newLine();
			//System.out.println();
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
		double unsmoothedprobability;
		double smoothedprobability;
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
	    String fullstr = "";
   	 	for(String k: hm.keySet()){
   	 		fullstr += k;
   	 	}
   	 	
   	 	//System.out.println(fullstr);
   	 	ArrayList<Character> unique = new ArrayList<Character>();
        for( int j = 0; j < fullstr.length(); j++){
       	 if( !unique.contains( fullstr.charAt( j ) ) )
                unique.add( fullstr.charAt( j ) );
        }	
        //System.out.println(unique.size());
	    while(i.hasNext()){
	    	 String key = i.next().getKey();	 
	    	 unsmoothedprobability = (double)hm.get(key) / sum;	    	         
	         smoothedprobability = (double)(hm.get(key)+ 0.1) / (sum + unique.size()*unique.size()*0.1);	        
	         ArrayList<Double> ls = new ArrayList<Double>();
	         ls.add(unsmoothedprobability); //unsmoothed
       	   	 ls.add(smoothedprobability); //smoothed
	         pro.put(key, ls);
	         //System.out.println(probability);
	         if(index <= 50){
	        	 bufferWritter.write(index + "\t" + key + "\t" + ls.get(0) + "\t\t" + ls.get(1));
		         bufferWritter.newLine();	   
		         index++;
	         } 
	    }	
	    bufferWritter.close();
	}
	
	public static void parse(String part, Map<String, Integer> hm){
		for(int i = 0; i < part.length() - 1; i++){
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
	
	private static HashMap sortByValues(Map<String, Integer> hm) { 
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
	
	//calculate one test sentence's possibility in the given language
		public static double testData(String s, Map<String, ArrayList<Double>> hm){
			double probability = 0.0;
			for(int i = 0; i<s.length() - 1; i++){
				char c1 = s.charAt(i);
				char c2 = s.charAt(i + 1);				
				if(Character.isLetter(c1) && Character.isLetter(c2)){
					String key = "" + c1 + c2;
					if(hm.containsKey(key) ){
						ArrayList<Double> ls = hm.get(key);
						probability += Math.log10(ls.get(0));
					}
				}
			}
			//System.out.println(probability);
			return probability;
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
		//BufferedReader in = new BufferedReader(new FileReader("training data.txt"));
	     
	     int[] totalLang = new int[6];
		 int total = 0;
			
		for(int i=0; i<6; i++){
			totalLang[i] = 0;
		}
			
		String line = "";
		     while ((line = in.readLine()) != null) {
		         String parts[] = line.split("\t");
		         parts[3] = parts[3].toLowerCase();
		         total++;
		         if(parts[2].equalsIgnoreCase("eu")){
		        	 parse(parts[3], hm_eu);
		        	 totalLang[0]++;
		         }	        	 
		         else if(parts[2].equalsIgnoreCase("ca")){
		        	 parse(parts[3], hm_ca);
		        	 totalLang[1]++;
		         } 
		         else if(parts[2].equalsIgnoreCase("gl")){
		        	 parse(parts[3], hm_gl);
		        	 totalLang[2]++;
		         } 
		         else if(parts[2].equalsIgnoreCase("es")){
		        	 parse(parts[3], hm_es);
		        	 totalLang[3]++;
		         }	 
		         else if(parts[2].equalsIgnoreCase("en")){
		        	 parse(parts[3], hm_en);
		        	 totalLang[4]++;
		         }	        	 
		         else if(parts[2].equalsIgnoreCase("pt")){
		        	 parse(parts[3], hm_pt);
		        	 totalLang[5]++;
		         }	        	 
		     }
		     
		     double prob = 0.0;
		     for(int i = 0; i<6; i++){
		    	 prob = Math.log10((double)totalLang[i] / total); 
		    	 //System.out.println(totalLang[i]);
		    	 pro_lang.add(prob);
		    	 //System.out.println(total);
		     }
	     //printHashMap(hm_eu);
	     hm_eu = sortByValues(hm_eu);
	     hm_ca = sortByValues(hm_ca);
	     hm_gl = sortByValues(hm_gl);
	     hm_es = sortByValues(hm_es);
	     hm_en = sortByValues(hm_en);
	     hm_pt = sortByValues(hm_pt);
	     
	     calculateProbability("Basque",hm_eu, pro_eu);
	     calculateProbability("Catalan",hm_ca, pro_ca);
	     calculateProbability("Galician",hm_gl, pro_gl);
	     calculateProbability("Spanish",hm_es, pro_es);
	     calculateProbability("English",hm_en, pro_en);
	     calculateProbability("Portuguese",hm_pt, pro_pt);
	     
	     in.close();
	     
	     File file =new File("results-bigram.txt");		

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
			for(int i=0; i<6; i++){
				rightEachLang[i] = 0;
				totalEachLang[i] = 0;
			}
			bufferWritter.write("Number\t\t\t" + "Result\t" + "Given Language");
			bufferWritter.newLine();
			total = 0;
		    while ((line = readTestFile.readLine()) != null) {
		         String parts[] = line.split("\t");	      	         
		         double max = 0.0;
		         String lang = "";
		         double temp = 0.0;
		         total++;		         
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
		         
		         max = testData(parts[3], pro_eu) + pro_lang.get(0);	         
		         lang = "eu";
		         
		         temp = testData(parts[3], pro_ca) + pro_lang.get(1);
		         if(max < temp){
		        	 max = temp;
		        	 lang = "ca";
		         } 
		         
		         temp = testData(parts[3], pro_gl) + pro_lang.get(2);
		         if(max < temp){
		        	 max = temp;
		        	 lang = "gl";
		         }
		         
		         temp = testData(parts[3], pro_es) + pro_lang.get(3);
		         if(max < temp){
		        	 max = temp;
		        	 lang = "es";
		         } 
		         
		         temp = testData(parts[3], pro_en) + pro_lang.get(4);
		         if(max < temp){
		        	 max = temp;
		        	 lang = "en";
		         } 
		         
		         temp = testData(parts[3], pro_pt) + pro_lang.get(5);
		         if(max < temp){
		        	 max = temp;
		        	 lang = "pt";
		         } 
		         bufferWritter.write(parts[0] + "\t" + parts[2] + "\t" + lang);
		         bufferWritter.newLine();
		        
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
		     System.out.println(result);
		     
		     File file2 =new File("analysis-bigram.txt");		
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
