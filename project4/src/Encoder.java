import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.*;

public class Encoder {
    public static double totalBits1 = 0;
    public static double entropy1 = 0;
    public static double entropy2 = 0;

    public static void main(String[] args) throws IOException {
        int[] freq = new int[26];
        double[] prob1 = new double[26];

        Scanner scanner = new Scanner(new File("skrt.txt"));

        int total = 0;
        int count = 0;
        while(scanner.hasNextInt()) {
            int nextNumber = scanner.nextInt();
            prob1[count] = nextNumber;
            freq[count] = nextNumber;
            total += nextNumber;
            count++;
        }

        for(int i = 0; i < prob1.length; i ++) {
            prob1[i] = prob1[i] / total;
        }

        entropy1 = entropy(prob1);

        char one = 'A';
        LinkedHashMap<Character, Integer> map = new LinkedHashMap<Character, Integer>();
        for(int i = 0; i < freq.length; i++) {
            if(freq[i] != 0)
                map.put(one, freq[i]);
            one++;
        }


        Huffman app = new Huffman(map);

        Node finalTree = app.buildTree();

        app.displayQueue();

        app.buildCodeMap(finalTree);

        outputToFile(prob1);

        Map<Character, String> codeMap = app.codeMap;

        encode(codeMap);

        Map<String, Character> reverseCodeMap = new HashMap<>();

        for(Map.Entry<Character, String> value : codeMap.entrySet()) {
            reverseCodeMap.put(value.getValue(), value.getKey());
        }

        decode(reverseCodeMap);

        HashMap<Character, Integer> numberToSymbol = new HashMap<Character, Integer>();

        int huffSymbol = 1;
        LinkedHashMap<Character, Integer> huffMap = new LinkedHashMap();
        for(int i = 0; i < freq.length; i++){
            for (int j = 0; j < freq.length; j++){
                if(freq[i] != 0 && freq[j] != 0) {
                    char huffSymbol2 = (char) (huffSymbol + '0');
                    map.put(huffSymbol2, freq[i] * freq[j]);
                    System.out.println("Symbol: " + huffSymbol2 + " Freq: " + freq[i] * freq[j]);
                }

                huffSymbol++;
            }
        }

        Huffman app2 = new Huffman(huffMap);

        Node finalTree2 = app2.buildTree();

        app2.displayQueue();

        app2.buildCodeMap(finalTree2);

        Map<Character, String> codeMap2 = app2.codeMap;

        HashMap<Integer, String>  pairsMapping = new HashMap<Integer, String>();
        char first = 'A';
        char second = 'A';
        int huffSymbol2 = 1;
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                pairsMapping.put(huffSymbol2, "" + first + second);
                //System.out.println(huffSymbol2 + " : " + first + second);
                second += 1;
                huffSymbol2++;
            }
            second = 'A';
            first += 1;
        }

        encode(codeMap);

       // Map<String, Character> reverseCodeMap = new HashMap<>();

        for(Map.Entry<Character, String> value : codeMap.entrySet()) {
            reverseCodeMap.put(value.getValue(), value.getKey());
        }

        decode(reverseCodeMap);

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
        for(int i = 0; i < 10000; i++) {
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

    public static void encode(Map<Character, String> codeMap) throws IOException {
        // Now use codeMap to get encoding of characters
        Writer output = new FileWriter("testText.enc1");

        Reader reader = new InputStreamReader(new FileInputStream("testText.txt"));
        int c;

        while ((c = reader.read()) != -1) {
            String str = codeMap.get((char) c);
            totalBits1 += str.length();
            output.write(str + "");
        }
        output.close();
    }

    public static void decode(Map<String, Character> reverseCodeMap) throws IOException{
        Reader reader = new InputStreamReader(new FileInputStream("testText.enc1"));
        Writer output = new FileWriter("testText.dec1");

        int bit;
        String string = "";

        while ((bit = reader.read()) != -1) {
            string += (char) bit;
            if (reverseCodeMap.containsKey(string)) {
                output.write(reverseCodeMap.get(string));
                string = "";
            }

        }
        output.close();
    }

    public static void printResults(){
        double averageBits1 = totalBits1 / 10000;
        System.out.println("average bits: " + averageBits1);
        System.out.println("entropy1: " + entropy1);
    }
}
