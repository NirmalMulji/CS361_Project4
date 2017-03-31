/**
 *  Huffman Encoding Tree
 *
 *  Author: Jayesh Chandrapal
 *  Version: 2.0
 */

import java.util.*;
import java.lang.*;

class Node implements Comparable<Node> {
    Character character;
    Integer frequency;
    Node left;
    Node right;

    Node(Character character, Integer frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Node otherNode) {
        return this.frequency.compareTo(otherNode.frequency);
    }

    @Override
    public String toString() {
        return "[ " + character + " : " + frequency + " ]";
    }
}

class Huffman
{
    Map<Character, Integer> characterMap; // character - frequency
    PriorityQueue<Node> tree;
    Map<Character, String> codeMap;      // character - codeword

    Huffman(LinkedHashMap<Character, Integer> map) {
        characterMap = map;

        tree = new PriorityQueue<Node>();
        codeMap = new HashMap<Character, String>();
    }

    public Node buildTree() {
        for(Map.Entry<Character, Integer> entry : characterMap.entrySet()) {
            tree.add(new Node(entry.getKey(), entry.getValue()));
        }

        while(tree.size() > 1) {
            Node node1 = tree.poll();
            Node node2 = tree.poll();
            Node newNode = new Node('*', node1.frequency + node2.frequency);
            newNode.left = node1;
            newNode.right = node2;
            tree.add(newNode);
        }

        // One remaining node is the root of final tree
        Node finalTree = tree.poll();

        System.out.println("\nHuffman Tree");
        displayTree(finalTree);
        return finalTree;
    }

    public void displayTree(Node root) {
        if(root != null)
            System.out.println(root);
        if(root.left != null)
            displayTree(root.left);
        if(root.right != null)
            displayTree(root.right);
    }

    public void buildCodeMap(Node root) {
        int[] arr = new int[characterMap.size()];
        int top = 0;

        System.out.println("\nEncoded Mapping");
        printCodes(root, arr, top);
    }

    private void printCodes(Node root, int[] arr, int top) {
        if(root.left != null) {
            arr[top] = 0;
            printCodes(root.left, arr, top + 1);
        }

        if(root.right != null) {
            arr[top] = 1;
            printCodes(root.right, arr, top + 1);
        }

        if(isLeaf(root)) {
            System.out.print(root.character + " : ");
            String code = printArr(arr, top);
            codeMap.put(root.character, code);
        }
    }

    private String printArr(int[] arr, int n) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < n; i++) {
            System.out.print(arr[i]);
            result.append(String.valueOf(arr[i]));
        }
        System.out.println();
        return result.toString();
    }

    private boolean isLeaf(Node root) {
        return (root.left == null) && (root.right == null);
    }

    public void displayQueue() {
        for(Node node : tree) {
            System.out.println(node);
        }
    }
}