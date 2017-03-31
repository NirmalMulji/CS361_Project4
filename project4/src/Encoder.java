import java.io.*;
import java.util.*;

public class Encoder {
    public static double totalBits1 = 0;
    public static double totalBits2 = 0;
    public static double entropy1 = 0;
    public static int total = 0;
    public static int k = 0;

    public static void main(String[] args) throws IOException {
        int[] freq = new int[26];
        double[] prob1 = new double[26];

        String frequenciesFileName = args[0];
        int charsToGen = Integer.parseInt(args[1]);
        if(charsToGen % 2 != 0) {
            charsToGen++;
            k = charsToGen;
        } else {
            k = charsToGen;
        }

        Scanner scanner = new Scanner(new File(frequenciesFileName));

        int count = 0;
        while(scanner.hasNextInt()) {
            int nextNumber = scanner.nextInt();
            prob1[count] = nextNumber;
            freq[count] = nextNumber;
            total+=freq[count];
            count++;
        }

        for(int i = 0; i < prob1.length; i++) {
            prob1[i] = prob1[i] / total;
        }

        entropy1 = entropy(prob1);

        /**********************************************************************
         *
         * Single Symbol Alphabet
         *
         ***********************************************************************/

        char one = 'A';
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        for(int i = 0; i < freq.length; i++) {
            if(freq[i] != 0)
                map.put(Character.toString(one), freq[i]);
            one++;
        }

        Huffman app = new Huffman(map);

        Node finalTree = app.buildTree();

        app.displayQueue();

        System.out.println("Single symbol encoding: ");
        System.out.println();

        app.buildCodeMap(finalTree);

        System.out.println();

        outputToFile(prob1);

        Map<String, String> codeMap = app.codeMap;

        encode(codeMap);

        Map<String, String> reverseCodeMap = new HashMap<>();

        for(Map.Entry<String, String> value : codeMap.entrySet()) {
            reverseCodeMap.put(value.getValue(), value.getKey());
        }

        decode(reverseCodeMap);

        /**********************************************************************
         *
         * Two Symbol Alphabet
         *
         ***********************************************************************/

        LinkedHashMap<String, Integer> huffMap = new LinkedHashMap();
        char huffSymbol = 'A';
        char huffSymbol2 = 'A';
        for(int i = 0; i < freq.length; i++){
            for (int j = 0; j < freq.length; j++){
                if(freq[i] != 0 && freq[j] != 0) {
                    huffMap.put(Character.toString(huffSymbol) + Character.toString(huffSymbol2), freq[i] * freq[j]);
                }

                huffSymbol2++;
            }

            huffSymbol2 = 'A';
            huffSymbol++;
        }

        Huffman app2 = new Huffman(huffMap);

        Node finalTree2 = app2.buildTree();

        app2.displayQueue();

        System.out.println("Two symbol encoding: ");
        System.out.println();

        app2.buildCodeMap(finalTree2);

        System.out.println();

        Map<String, String> codeMap2 = app2.codeMap;

        encode2(codeMap2);

        Map<String, String> reverseCodeMap2 = new HashMap<>();

        for(Map.Entry<String, String> value : codeMap2.entrySet()) {
            reverseCodeMap2.put(value.getValue(), value.getKey());
        }

        decode2(reverseCodeMap2);

        printResults();
    }

    public static double entropy(double[] prob) {
        double logSum = 0;
        for(int i = 0; i < prob.length; i++) {
            if(prob[i] != 0)
                logSum += -(prob[i]) * (Math.log10(prob[i]) / Math.log10(2));
        }

        return logSum;
    }

    public static void outputToFile(double[] prob) throws IOException {
        double[] ranges = new double[26];
        for(int i = 0; i < 26; i++) {
            if(i != 0 && prob[i] != 0) {
                ranges[i] = prob[i] * 100 + ranges[i-1];
            } else {
                ranges[i] = prob[i] * 100;
            }
        }

        // Mapping from num to letter
        HashMap<Integer, Character> alphabet = new HashMap<Integer, Character>();
        char c = 'A';
        int l = 0;

        alphabet.put(l, c);
        for(int i = 0; i < 26; i++) {
            alphabet.put(l++, c++);
        }

        Writer output = new FileWriter("testText.txt");
        for(int i = 0; i < k; i++) {
            int k =  randInt();
            for (int j = 0; j < 26; j++) {
                if(k < ranges[j]) {
                    output.write(alphabet.get(j) + "");
                    break;
                }
            }
        }

        output.close();

    }

    public static int randInt() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

    public static void encode(Map<String, String> codeMap) throws IOException {
        // Now use codeMap to get encoding of characters
        Writer output = new FileWriter("testText.enc1");

        Reader reader = new InputStreamReader(new FileInputStream("testText.txt"));

        int c;
        while ((c = reader.read()) != -1) {
            String str = codeMap.get(Character.toString((char) c));
            totalBits1 += str.length();
            output.write(str + "");
        }

        output.close();
    }

    public static void decode(Map<String, String> reverseCodeMap) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream("testText.enc1"));
        Writer output = new FileWriter("testText.dec1");

        int bit;
        String string = "";

        while ((bit = reader.read()) != -1) {
            string += Character.toString((char) bit);
            if (reverseCodeMap.containsKey(string)) {
                output.write(reverseCodeMap.get(string));
                string = "";
            }

        }
        output.close();
    }

    public static void encode2(Map<String, String> codeMap) throws IOException {
        // Now use codeMap to get encoding of characters
        Writer output = new FileWriter("testText.enc2");
        Reader reader = new InputStreamReader(new FileInputStream("testText.txt"));

        int c;
        int d;
        while ((c = reader.read()) != -1 && (d = reader.read()) != -1) {
            String str = codeMap.get(Character.toString((char) c) + Character.toString((char) d));
            totalBits2 += str.length();
            output.write(str + "");
        }

        output.close();
    }

    public static void decode2(Map<String, String> reverseCodeMap) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream("testText.enc2"));
        Writer output = new FileWriter("testText.dec2");

        int bit;
        String string = "";

        while ((bit = reader.read()) != -1) {
            string += Character.toString((char) bit);
            if (reverseCodeMap.containsKey(string)) {
                output.write(reverseCodeMap.get(string));
                string = "";
            }
        }

        output.close();
    }

    public static void printResults(){
        double averageBits1 = totalBits1 / k;
        double averageBits2 = totalBits2 / k;

        System.out.println("-----------------------------------------------------");
        System.out.println("Summary of results: ");
        System.out.println();

        System.out.printf("Computed entropy of the language: %.3f\n", entropy1);

        System.out.println();

        System.out.printf("1-symbol actual average bits/symbol: %.3f\n", averageBits1);

        double percentageDiff1 = (averageBits1 - entropy1) / entropy1 * 100;
        System.out.printf("Percent difference 1-symbol vs theoretical: %%%.3f\n", percentageDiff1);

        System.out.println();

        System.out.printf("2-symbol actual average bits/symbol: %.3f\n", averageBits2);

        double percentageDiff2 = (averageBits2 - entropy1) / entropy1 * 100;
        System.out.printf("Percent difference 2-symbol vs theoretical: %%%.3f\n", percentageDiff2);

        System.out.println();

        double percentageDiff3 = Math.abs((averageBits1 - averageBits2) / averageBits2 * 100);
        System.out.printf("Percent difference 2-symbol vs 1-symbol: %%%.3f\n", percentageDiff3);
    }
}
