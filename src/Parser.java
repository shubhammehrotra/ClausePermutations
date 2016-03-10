
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

class Parser {

	/** This example shows a few more ways of providing input to a parser.
	 *
	 *  Usage: ParserDemo2 [grammar [textFile]]
	 */
	static String pID;
	static String sLabel;
	static int textCount = 0;
	static int globalCount = 0;
	static String globalString;
	static CsvWriter csvOutput;
	final static String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	static String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
	static LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
	static TreebankLanguagePack tlp = lp.getOp().langpack();
	static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	static int count = 0;
	static String outputFile = "userVoiceAllClause.csv";
	static boolean alreadyExists = new File(outputFile).exists();
	
	public static void main(String[] args) throws IOException {



		Iterable<List<? extends HasWord>> sentences;
		if (args.length > 1) {
			System.out.println("helloooo");
			DocumentPreprocessor dp = new DocumentPreprocessor(args[1]);
			List<List<? extends HasWord>> tmp =
					new ArrayList<>();
			for (List<HasWord> sentence : dp) {
				tmp.add(sentence);
			}
			sentences = tmp;
		} else {
			String sent2 = ("I hate apple products they are expensive");
			sent2 = sent2.toLowerCase();
			// Use the default tokenizer for this TreebankLanguagePack
			Tokenizer<? extends HasWord> toke =
					tlp.getTokenizerFactory().getTokenizer(new StringReader(sent2));
			List<? extends HasWord> sentence2 = toke.tokenize();


			List<List<? extends HasWord>> tmp =
					new ArrayList<>();
			//tmp.add(sentence);
			tmp.add(sentence2);
			//tmp.add(sentence3);
			sentences = tmp;
		}
		try {
			
			CsvReader products = new CsvReader("uservoiceAll.csv");

			products.readHeaders();

			while (products.readRecord())
			{	
				globalCount++;
				count = 0;
				String productID = products.get("id");
				pID = productID;
				String reviewText = products.get("sentence");
				String label = products.get("label");
				sLabel = label;
				//System.out.println(reviewText);
				for (List<? extends HasWord> sentence : sentences) {
					Tree parse = lp.parse(sentence);
					//System.out.println(parse.);
					//parse.indentedListPrint();
					//System.out.println(parse.pennString());

					//parse.pennPrint();
					//System.out.println("heloo!!!!@#$%^%$#@");
					GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
					List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
					//System.out.println(tdl);
					/*System.out.println("HIhihiu");

		      System.out.println("The words of the sentence:");
		      for (Label lab : parse.yield()) {
		        if (lab instanceof CoreLabel) {
		          System.out.println(((CoreLabel) lab).toString(CoreLabel.OutputFormat.VALUE_MAP));
		        } else {
		          System.out.println(lab);
		        }
		      }
		      System.out.println();
		      System.out.println(parse.taggedYield());
		      System.out.println();
		    }*/

					// This method turns the String into a single sentence using the
					// default tokenizer for the TreebankLanguagePack.
					String sent3 = reviewText;
					//String sent3 = "i dont like apple computers they are expensive";
					sent3 = sent3.toLowerCase();
					int slen = 0;
					//lp.parse(sent3).pennPrint();
					//String check = lp.parse(sent3).pennString();
					//String y1 = lp.parse(sent3).deepCopy(tree1, 5);
					String br = lp.parse(sent3).pennString().replaceAll("[\r\n]+", "").replaceAll(" ", "");
					//System.out.println(br);
					String[] parts = br.split("\\(SBAR");
					if (parts.length == 1) parts = br.split("S\\(");
					for (int i = 0; i < parts.length; i++) {
						//textCount++;
						parts[i] = parts[i].replaceAll("\\$", "");
						parts[i] = parts[i].replaceAll("[A-Z]", "");
						parts[i] = parts[i].replaceAll("[(]", " ");
						parts[i] = parts[i].replaceAll("[)]", " ");
						parts[i] = parts[i].trim();
						parts[i] = parts[i].replaceAll("[ ]+", " ");
						slen += parts[i].length();
						parts[i] = parts[i] + " ";
						//System.out.println(parts[i]);
					} //System.out.println(parts.length); 
					if (parts.length <= 4) {
						//language(parts.length, parts,"");
					}
					else {
						String outputFile = "usersGreaterThan4.csv";
						System.out.println(pID + " hi ");
						// before we open the file check to see if it already exists
						boolean alreadyExists = new File(outputFile).exists();
							
						try {
							// use FileWriter constructor that specifies open for appending
							CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
							csvOutput.write(pID);
							csvOutput.write(reviewText);
							csvOutput.write(sLabel);
							csvOutput.endRecord();
							csvOutput.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					/*String [] addition = new String[100];
					int ind = 0, flag1 = 0;
					String temp;
					int letLen = (int) Math.pow(2, parts.length);
					for (int i = 0; i < letLen; i++) {
						temp = "";
						for (int j = 0; j < parts.length; j++) {
							int a = (int) Math.pow((int)2, (int)j);
							if ((i & a) != 0) {
								temp += parts[j];
							}
						} if (temp != "") addition[ind++] = temp;
					} for (int i = 0; i < ind; i++) {
						for (int j = 0; j < parts.length; j++) {
							if (addition[i] == parts[j]) {
								flag1 = 0;
								break;
							}
							else flag1 = 1;
						}
						if (flag1 == 1 && !addition[i].equals(""))System.out.println(addition[i]);
					}*/
					//System.out.println(count);
					//System.out.println(lp.parse(sent3).pennString().replaceAll("[\r\n]+", "").replaceAll(" ", ""));//System.out.println("trueee thattttttt");
					//System.out.println(lp.parse(sent3).pennString().replaceAll("[\r\n]+", "").replaceAll(" ", "").replaceAll("[^a-z]", ""));
					//System.out.println(lp.parse(sent3).pennString().replaceAll("[\r\n]+", "").replaceAll(" ", "").indexOf("VP(VBP"));//System.out.println("trueee thattttttt");
					//else System.out.println("noooo");
				}

				// perform program logic here
				//System.out.println(/*productID + ":" + productName + ":" + */supplierID);
			}

			products.close();
			//csvOutput.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
		
	}
	private static void language(final int n, final String[] syllables, final String currentWord) throws IOException { // example of N = 3
		
		///if (globalCount == 0)
		final CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile,true), ',');
		/*if (!alreadyExists)
		{
			csvOutput.write("id");
			csvOutput.write("sentence");
			csvOutput.write("label");
			csvOutput.endRecord();
		}*/ 

		if (n == 0) {
			csvOutput.write(pID + "_" + count);
			csvOutput.write(currentWord);
			csvOutput.write(sLabel);
			//System.out.println("hi");
			csvOutput.endRecord();
			csvOutput.close();
			count++;
			globalString = currentWord;
			System.out.println(pID);
		} else {
			for (int i = 0; i < syllables.length; i++) {
				if (!currentWord.contains(syllables[i]))
					language(n - 1, syllables, currentWord + syllables[i]);
			}
		}
	}

	private Parser() {} // static methods only

}
