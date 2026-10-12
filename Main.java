import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
//NAME:Dantae Friley
//ASSIGNMENT:3
//LAB SECTION:Tuesday
//LECTURE SECTION:Mon, Wed, Fri

public class Main{

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<SentList> sentList = new ArrayList<SentList>();
        ArrayList<SentList> posList = new ArrayList<SentList>();
        ArrayList<SentList> negList = new ArrayList<SentList>();

        //load sentiment, positive words and negative words arraylists
        readSentimentList(sentList, posList, negList);

        //read review
        //load ArrayList wordList that will contain original review & pos & neg
        String outFileName = "reviews.txt";
        PrintWriter outFile = new PrintWriter(outFileName);

        // open input file adding review + number + ".txt" to review
        // if not able to open, print a message and continue
        // else process the file
        // if the file can be read properly, print the results
        for (int i = 1; i <= 8; i++){
            String inFileName = "review" + i + ".txt";
            ArrayList<Words> wordList = new ArrayList<Words>();

            boolean success = readReview(sentList, posList, negList, wordList, inFileName);
            if (success) {
                System.out.println("Successfully read review " + i);
                printReview(wordList, inFileName, outFile);
            } else {
                System.out.println("Failed to read review " + i);
            }
        }

        outFile.close();
    }

    //PRE:  accept the empty ArrayLists created in main
    //POST: the arrays are loaded with the proper words and information
    public static void readSentimentList(ArrayList<SentList> sentList,
                                         ArrayList<SentList> posList,
                                         ArrayList<SentList> negList){
        String csvFilePath = "sentiment.txt";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                  
                 //Create object SentList & add to sentList arraylist
 
                 //if word values are pos add to posList
                 //if word values are neg, add to neglist
                 String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String word = tokens[0].trim();
                
                 float tempValue = Float.parseFloat(tokens[1].trim()
                                                           .replace('–', '-')
                                                           .replace('—', '-')
                                                           .replaceAll("\\s+", ""));
                        
                // Create object SentList & add to sentList arraylist
                     SentList entry = new SentList(word, tempValue);
                     sentList.add(entry);
                     
                     // if word values are pos add to posList (> 1.25 per instructions)
                     if (tempValue > 1.25f) {
                         posList.add(entry);
                     } 
                     // if word values are neg, add to neglist (< -1.25 per instructions)
                     else if (tempValue < -1.25f) {
                         negList.add(entry);
            }
                 }
             }
        } catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
        }
    }

    //PRE: accept the word lists and file name to open
    //POST: read the file while the line is not null
    //      each word is edited (to lower case without punctuation)
    //      the sentiment value is accessed
    //      if the word is positive - update to a random word in the negative list and update the word value
    //      if the word is negative - update to a random positive word in the positive list & update the word value

    public static boolean readReview(ArrayList<SentList> sentList,
                                  ArrayList<SentList> posList,
                                  ArrayList<SentList> negList,
                                  ArrayList<Words> wordList,
                                  String fileName){

        String line;
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                 if (line.trim().isEmpty()) {
                    continue;
}

                String[] values = line.trim().split("\\s+");
                 
                 //for each word
                 //  strip off punctuation at the end of word set charWord to null or value of punctuation
                 //  save origWord, posWord, negWord
                 //  change to lower case  (set editWord)
                 //  look up word in sentList (set origValue, posVlue, negValue)
                 //  if positive enough, find new negword in negList & reset negWord & NegValue, reset negFlag
                 //  if negative enough, find new posword in posList & reset posWord & posValue, reset posFlag
                 for (String word : values) {
                    String charWord = "";
                    String strippedWord = word;
                    if (strippedWord.isEmpty()) continue;
                    
                    // Strip punctuation off the end of the word
                    while (strippedWord.length() > 0 && isPunctuation(strippedWord.charAt(strippedWord.length() - 1))) {
                        charWord = strippedWord.charAt(strippedWord.length() - 1) + charWord;
                        strippedWord = strippedWord.substring(0, strippedWord.length() - 1);
                    }

                    String origWord = word;
                    String posWord = origWord; // Default to original layout
                    String negWord = origWord; // Default to original layout
                    String editWord = strippedWord.toLowerCase();

                    double origValue = getSentiment(sentList, editWord);
                    double posValue = origValue;
                    double negValue = origValue;
                  
                    boolean posFlag = false;
                    boolean negFlag = false;

                    // Part 2b: If negative enough (< -1), swap with random positive word
                    if (origValue < -1.0 && !posList.isEmpty()) {
                        SentList randomPos = posList.get(random.nextInt(posList.size()));
                        posWord = randomPos.word + charWord;
                        posValue = randomPos.value;
                        posFlag = true;
                    }
                    
                    // Part 2: If positive enough (> 1), swap with random negative word
                    if (origValue > 1.0 && !negList.isEmpty()) {
                        SentList randomNeg = negList.get(random.nextInt(negList.size()));
                        negWord = randomNeg.word + charWord;
                        negValue = randomNeg.value;
                        negFlag = true;
                    }

                    // ADDED: Create the object and save it into your wordList collection!
                    wordList.add(new Words(origWord, posWord, negWord, origValue, posValue, negValue, posFlag, negFlag));
                }
            }
            return true;
        } 
        catch (IOException e) {
            System.out.println("Error reading the file: " + fileName);
            return false;
        }
    }

    //PRE:  accept the updated wordlist
    //POST: loop through word list, create a string that will be the original, positive & negative reviews
    //      print each review
    public static void printReview(ArrayList<Words> wordList, String inFile, PrintWriter outFile) {
        StringBuilder originalReview = new StringBuilder();
        StringBuilder positiveReview = new StringBuilder();
        StringBuilder negativeReview = new StringBuilder();
        double originalTotal = 0;
        double positiveTotal = 0;
        double negativeTotal = 0;

        ArrayList<Words> positiveUpdates = new ArrayList<>();
        ArrayList<Words> negativeUpdates = new ArrayList<>();

        for (Words w : wordList) {
            originalReview.append(w.origWord).append(" ");
            originalTotal += w.sentOrigValue;

            positiveReview.append(w.posWord).append(" ");
            positiveTotal += w.sentPosValue;
            // FIXED: Only log if the word was actually mutated/swapped
            if (!w.posWord.equals(w.origWord)) {
                positiveUpdates.add(w);
            }

            negativeReview.append(w.negWord).append(" ");
            negativeTotal += w.sentNegValue;
            // FIXED: Only log if the word was actually mutated/swapped
            if (!w.negWord.equals(w.origWord)) {
                negativeUpdates.add(w);
            }
        }
             // 1. Output Original Review details (80-character maximum line limits)
        outFile.println("PROCESSING FILE: " + inFile);
        outFile.println("FORMATTED REVIEW:");
        printWrappedLine(originalReview.toString().trim(), outFile);
        outFile.printf("ORIGINAL SENTIMENT: %.2f\n\n", originalTotal);

        // 2. Output Mutated Positive Review details
        outFile.println("MUTATED MORE POSITIVE REVIEW:");
        printWrappedLine(positiveReview.toString().trim(), outFile);
        outFile.printf("NEW POSITIVE SENTIMENT: %.2f\n", positiveTotal);
        outFile.println("WORDS UPDATED TO BE MORE POSITIVE:");
        if (positiveUpdates.isEmpty()) {
            outFile.println("No words were replaced.");
        } else {
            for (Words w : positiveUpdates) {
                outFile.printf("%-15s %-6.2f ->   %-15s %-6.2f\n", w.origWord, w.sentOrigValue, w.posWord, w.sentPosValue);
            }
        }
        outFile.println();

        // 3. Output Mutated Negative Review details
        outFile.println("MUTATED MORE NEGATIVE REVIEW:");
        printWrappedLine(negativeReview.toString().trim(), outFile);
        outFile.printf("NEW NEGATIVE SENTIMENT: %.2f\n", negativeTotal);
        outFile.println("WORDS UPDATED TO BE MORE NEGATIVE:");
        if (negativeUpdates.isEmpty()) {
            outFile.println("No words were replaced.");
        } else {
            for (Words w : negativeUpdates) {
                outFile.printf("%-15s %-6.2f ->   %-15s %-6.2f\n", w.origWord, w.sentOrigValue, w.negWord, w.sentNegValue);
            }
        }

        // Part 1c Requirement: Exactly 3 blank lines between review files printed
        outFile.println("\n\n\n");
    }

    //Method to break string paragraphs at 80 characters or fewer
    private static void printWrappedLine(String text, PrintWriter outFile) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > 80) {
                outFile.println(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }
        if (currentLine.length() > 0) {
            outFile.println(currentLine.toString().trim());
        }
    }    
    //PRE:  accept a character
    //POST: return true if this character is punctuation; false otherwise
    //      There may be a better way to write this!
    static boolean isPunctuation(char ch) {
        if (ch == '!' || ch == '\"' || ch == '#' || ch == '$' || ch == '%' || ch == '&' || ch == '\'' || ch == '(' || ch == ')' || ch == '*' || ch == '+' || ch == ',' || ch == '-' || ch == '.' || ch == '/' || ch == ':' || ch == ';' || ch == '<' || ch == '=' || ch == '>' || ch == '?' || ch == '@' || ch == '[' || ch == '\\' || ch == ']' || ch == '^' || ch == '`' || ch == '{' || ch == '|' || ch == '}')
          return true;
        return false;
    }

    //PRE:  accept the sentiment words list and a word to find
    //POST: return the value of the sentiment if found, 0 otherwise
    static double getSentiment (ArrayList<SentList> sentList, String eWord) {
        if (eWord == null) {
            return 0.0;
        }

        for (SentList entry : sentList) {
            // FIXED: Compare the word field inside the object to the target string
            if (entry.word.equalsIgnoreCase(eWord)) {
                return entry.value;
            }
        }
        
        // POST: Returns 0 if the word was not found in the list after checking everything
        return 0.0;
    }

    // Simple data holder for sentiment entries
    //Works cited: written with help from gemini
    static class SentList {
        String word;
        double value;

        public SentList(String word, double value) {
            this.word = word;
            this.value = value;
        }
    }

    // Simple data holder for processed review words
    //Works cited: written with help from gemini
    static class Words {
        String origWord;
        String posWord;
        String negWord;
        double sentOrigValue;
        double sentPosValue;
        double sentNegValue;
        boolean posFlag;
        boolean negFlag;

        public Words(String origWord, String posWord, String negWord,
                     double sentOrigValue, double sentPosValue, double sentNegValue,
                     boolean posFlag, boolean negFlag) {
            this.origWord = origWord;
            this.posWord = posWord;
            this.negWord = negWord;
            this.sentOrigValue = sentOrigValue;
            this.sentPosValue = sentPosValue;
            this.sentNegValue = sentNegValue;
            this.posFlag = posFlag;
            this.negFlag = negFlag;
        }
    }
}