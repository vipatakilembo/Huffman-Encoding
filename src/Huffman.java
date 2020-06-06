import com.sun.deploy.util.StringUtils;
import com.sun.javaws.IconUtil;

import java.io.*;
import java.util.*;

public class Huffman {


    /**
     * Initialize global variables you create
     */


    public Huffman() {
        //TODO
    }

    public static HuffmanNode treeRoot = null;
    /**
     * Produces the output frequency.txt
     *
     * @param input - File containing the message
     * @throws Exception
     */
    public static void frequency(String input) throws Exception {
        //TODO

        HashMap<Character,Integer> frequencies = new HashMap<>();

        Scanner scanner = new Scanner(new File(input));
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Character> characters = new ArrayList<>();
        int lineCounter = 0;
        while(scanner.hasNextLine()){
            if(lineCounter > 0){
                if(characters.contains('N')){
                    int total = frequencies.get('N');
                    frequencies.replace('N', total+1);
                }else{
                    characters.add('N');
                    frequencies.put('N',1);
                }
            }
            String line = scanner.nextLine();
            lines.add(line);
            for(int i = 0; i < line.length(); i++){
                char currentChar = line.charAt(i);
                if(currentChar == ' '){
                    currentChar  = 'S';
                }else if (currentChar == '\n'){
                    currentChar = 'N';
                }
               if(characters.contains(currentChar)){
                   int total = frequencies.get(currentChar);
                  frequencies.replace(currentChar, total+1);
               }else{
                   characters.add(currentChar);
                   frequencies.put(currentChar,1);
               }
            }
            lineCounter++;
        }

        FileWriter fileWriter = new FileWriter("frequency.txt");
        for(int i =0; i < characters.size(); i++){
            fileWriter.write(characters.get(i) + " "+ frequencies.get(characters.get(i))+"\n");
        }

        fileWriter.close();
    }

    public static void main(String[] args){
        try {
            frequency("LocalTests/inputFiles/testMessage2.txt");
            buildTree("frequency.txt");
            encode("codes.txt","LocalTests/inputFiles/testMessage2.txt");
            decode("tree.txt","encode.bin");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Scanner scanner = new Scanner(new File("LocalTests/inputFiles/encode2.bin"));
            String expected = scanner.nextLine();
            scanner = new Scanner(new File("encode.bin"));
            String actual = scanner.nextLine();
            System.out.println(actual.length() + " " + expected.length());

            for(int i = 0; i< actual.length(); i++){
//                System.out.println(actual.charAt(i));
                if(actual.charAt(i) == expected.charAt(i)){
                    System.out.println(actual.charAt(i)+ " " + expected.charAt(i));
                }else{
                    System.out.println("Mismatch at " + i +"->"+actual.charAt(i)+"/"+expected.charAt(i));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    
    

    /**
     * Produces the output codes.txt and tree.txt
     *
     * @param freqFile - File containing the frequencies
     * @throws Exception
     */
    public static void buildTree(String freqFile) throws Exception {
        /** use the queue below **/
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
        ArrayList<HuffmanNode> tree =  new ArrayList<>();
        ArrayList<Huffman> codes = new ArrayList<>();

        //TODO
        Scanner scanner = new Scanner(new File(freqFile));
        while(scanner.hasNextLine()){
            String[] current = scanner.nextLine().split(" ");
            queue.add(new HuffmanNode(current[0].charAt(0),Integer.parseInt(current[1])));
        }
        HuffmanNode root = null;
        while(queue.size()>1){
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            HuffmanNode frequency;
            if(right != null){
                frequency = new HuffmanNode('F',left.frequency + right.frequency);
            }else{
                frequency = new HuffmanNode('F',left.frequency);
            }

            frequency.left = left;
            frequency.right = right;
            root = frequency;
            queue.add(frequency);
            tree.add(root);
        }

        File codeFile  = new File("codes.txt");
        File treeFile  = new File("tree.txt");
        if(codeFile.exists()){
            codeFile.delete();
        }
        if(treeFile.exists()){
            treeFile.delete();
        }
        treeRoot = root;
        printTree(root);
        printNodes(root,"");

    }




    public static void printTree(HuffmanNode root){
        if(root == null){
            return;
        }
        if(doNot(root)){
            return;
        }

        String output = "";
        if(root.left != null){
            if(root.left.letter == 'F'){
                output+=root.left.frequency+"-";
            }else{
                output+=root.left.letter+"-";
            }
        }

        if(root.letter == 'F'){
            output+=root.frequency;
        }else{
            output+=root.letter;
        }

        if(root.right != null){
            if(root.right.letter == 'F'){
                output+= "-"+root.right.frequency;
            }else{
                output+="-"+root.right.letter;
            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("tree.txt", true);
            fileWriter.write(output+"\n");
            fileWriter.close();
        }catch (IOException e ){
            e.printStackTrace();
        }
        printTree(root.left);
        printTree(root.right);

    }

    public static boolean doNot(HuffmanNode root){
        return (root.letter!= 'F' && root.left == null && root.right == null);
    }

    public static String getValue(HuffmanNode node){
        if (node.letter == 'F'){
            return node.frequency+"";
        }else{
            return node.letter+"";
        }
    }

    public static void printNodes(HuffmanNode root, String S){
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter("codes.txt",true);
            if(root.left == null || root.right == null && Character.isLetter(root.letter)){
//                System.out.println(root.frequency);
                fileWriter.write(root.letter+ " "+ S+"\n");
                fileWriter.close();
                return;
            }

            printNodes(root.left, S+"0");
            printNodes(root.right, S+"1");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    /**
     * Produces the output encode.bin
     *
     * @param code - File containing the bit codes
     * @param message -  File containing the message
     * @throws Exception
     */
    public static void encode(String code, String message) throws Exception {
        //TODO
        Scanner scanner = new Scanner(new File(code));
        HashMap<Character,String> codes = new HashMap<>();
        while(scanner.hasNextLine()){
            String[] current = scanner.nextLine().split(" ");
            codes.put(current[0].charAt(0),current[1]);
        }

//int x = 0;
      String encoded = "";
        scanner = new Scanner(new File(message));
        char currentChar;
        while(scanner.hasNextLine()){
            String current = scanner.nextLine();
            current = current.replaceAll(" ","S");
            current = current.replaceAll("\n","N");
            for(int i = 0; i < current.length(); i++){
                currentChar = current.charAt(i);
                encoded+=codes.get(currentChar);
            }
            if(scanner.hasNextLine()){
                encoded+=codes.get('N');
            }
        }

        File file = new File("encode.bin");
        if(file.exists()){
            file.delete();
        }

        FileWriter fileWriter = new FileWriter(new File("encode.bin"));
        fileWriter.write(encoded+"\n");
        fileWriter.close();
    }



    /**
     * Produces the output decode.txt
     *
     * tree - File containing the Huffman tree
     * encode - - File containing the encoded message
     * @throws Exception
     */

    public static int traverse(HuffmanNode root, int index, String encoded) throws IOException {
        if(root == null){
            return index;
        }

        if(root.left == null && root.right == null){
            FileWriter fileWriter = new FileWriter("decode.txt",true);
            if(root.letter == 'S'){
                fileWriter.write(" ");
            }else if(root.letter == 'N'){
                fileWriter.write('\n');
            }else{
                fileWriter.write(root.letter);
            }
            fileWriter.close();
//            System.out.print(root.letter);
            return index;
        }

        index++;
        if(index < encoded.length()){
            if(encoded.charAt(index) == '0'){
                index = traverse(root.left,index,encoded);
            }else{
                index = traverse(root.right,index,encoded);
            }
        }

        return index;
    }

    static boolean isInt(String input){
        for(int i = 0; i < input.length(); i++){
            if(!Character.isDigit(input.charAt(i))){
                return false;
            }
        }

        return true;
    }

    static HuffmanNode setValues(int index, String[] nodes){
        if(isInt(nodes[index])){
            return new HuffmanNode('F', Integer.parseInt(nodes[index]));
        }else{
            return new HuffmanNode(nodes[index].charAt(0),0);
        }
    }

    public static void decode(String tree, String encode) throws Exception {
        //TODO
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
        HuffmanNode root = null;
        Scanner scanner = new Scanner(new File(encode));
        String encoded = scanner.nextLine();
        scanner = new Scanner(new File(tree));
        while(scanner.hasNextLine()){
            String[] nodes = scanner.nextLine().split("-");
            HuffmanNode cRoot,left,right;
            cRoot = setValues(1,nodes);
            left = setValues(0,nodes);
            right = setValues(2,nodes);
            cRoot.left = left;
            cRoot.right = right;

            queue.add(cRoot);
            HuffmanNode current = queue.poll();
            root = buildTree(cRoot,current);
            queue.add(current);
            queue.add(root);


        }
        root = queue.poll();
        File file = new File("decode.txt");
        if(file.exists()){
            file.delete();
        }
        int index = 0;
        for(int i = 0;i < encoded.length(); i++){
           i =  traverse(root, i, encoded);
        }
//        while(index < encoded.length()-2) {
//            index += traverse(root, index, encoded);
//        }
        FileWriter fileWriter = new FileWriter("decode.txt", true);
        fileWriter.write("\n");
        fileWriter.close();
    }

    public static HuffmanNode buildTree(HuffmanNode node, HuffmanNode root){

        if(root == null){
            return null;
        }

        if(root.letter == 'F'  && node.letter == 'F'){
            if(root.frequency == node.frequency){
                root.left = node.left;
                root.right = node.right;
                return root;
            }
        }else if(root.letter == node.letter){
            root.left = node.left;
            root.right = node.right;
            return root;
        }

        if(root.left != null){
            root = buildTree(node,root.left);

        }
        if(root.right != null){
            root = buildTree(node,root.right);
        }
        return root;
    }





    /**
     * Auxiliary class for Huffman
     *
     */
    static class HuffmanNode implements Comparable<HuffmanNode> {
        int frequency;
        int index;
        char letter;
        HuffmanNode left;
        HuffmanNode right;
        HuffmanNode prev;

        public HuffmanNode(char letter,int frequency){
            this.letter = letter;
            this.frequency = frequency;
        }

        /**
         * Uses frequency to determine the nodes order in the queue
         * Note: DO NOT MODIFY THIS FUNCTION
         *
         * @param node of type HuffmanNode
         * @return frequency of key node subtracted by frequency of node from parameter
         */
        @Override
        public int compareTo(HuffmanNode node) {
            return frequency - node.frequency;
        }

    }


}