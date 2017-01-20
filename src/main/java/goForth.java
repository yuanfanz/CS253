import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Frank on 2017/1/17.
 */
public class goForth {

    //use global variables can help us update their value more easily
    public static Stack<Object> stack = new Stack<Object>();
    //use Object as data type can help us put very different data in it
    public static HashMap<String, Object> heap = new HashMap<String, Object>();

    public void readFile() throws IOException {
        //in the main function, we first push file path into stack
        String path = (String) stack.pop();
        //get file from FileInputStream function
        FileInputStream inputStream = new FileInputStream(path);
        //push every line into stack
        stack.push(IOUtils.toString(inputStream));
    }

    public void filterChars() {
        //for every line, turn into string and delete the redundant info
        String text = stack.pop().toString();
        //use regular expression to extract alphanumeric words
        String regEx = "[^A-Za-z0-9]";
        //also turn all into lower case
        stack.push(text.replaceAll(regEx, " ").toLowerCase());
    }

    public void scan() {
        String text = (String) stack.pop();
        //StringTokenizer can help with tokenization
        //in python, we can use nltk.word_tokenize() as well
        StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(text, "[ ]");
        //push every token into stack
        while (tokenizer.hasMoreElements()) {
            stack.push(tokenizer.nextToken());
        }
    }

    public void removeStopWords() throws IOException {
        //get stop words just like we get text from FileInputStream before
        FileInputStream inputStream = new FileInputStream("stop_words.txt");
        String[] stopWords = IOUtils.toString(inputStream).split(",");
        //now stack has the tokens from text we processed above
        //and we push stop words into stack and we can store them in heap
        stack.push(stopWords);
        //put all stop words into heap, it is a string array
        heap.put("stop_words", stack.pop());
        //create one new stack for storing words
        Queue<String> valid = new LinkedList<String>();
        heap.put("words", valid);
        //use while loop to remove stop words from texts
        while (!stack.empty()) {
            //first get string array from heap
            stopWords = (String[]) heap.get("stop_words");
            //like the last program, we use binary search in arrays to find match
            //we need a word, so the length is at least 2
            if ((stack.peek().toString().length() > 1) && Arrays.binarySearch(stopWords, stack.peek()) < 0) {  //if return -1, it means we find no match in stop words
                //get new stack from heap, which is a hashmap that stored by key "words"
                Queue<String> validWords = (Queue<String>) heap.get("words");
                //push valid word into new stack
                validWords.add(stack.pop().toString());
            } else {  //if we find stop words, just discard
                stack.pop();
            }
        }
        //push valid words back to stack from new stack
        while (!((Queue<String>) heap.get("words")).isEmpty()) {
            stack.push(((Queue<String>) heap.get("words")).poll());
        }

        //finish the process of removing stop words, discard redundant data
        heap.remove("stop_words");
        heap.remove("words");
    }

    public void frequencies() {
        //use heap to create a new entry, key is "word_freq" value is a hashmap storing words and its frequency
        heap.put("word_freq", new HashMap<String, Integer>());
        //make a hashmap to store frequency made from heap
        HashMap<String, Integer> word_freq = (HashMap<String, Integer>) heap.get("word_freq");
        //use a while loop to push every word in stack to count
        while (!stack.empty()) {
            if (word_freq.containsKey(stack.peek().toString())) { //if we already have this word
                stack.push(Integer.valueOf(word_freq.get(stack.peek().toString())));
                stack.push(1);
                Integer sum = (Integer) stack.pop() + (Integer) stack.pop();
                stack.push(sum);
            } else {
                stack.push(1);
            }
            Integer freq = (Integer) stack.pop();
            String word = stack.pop().toString();
            //put word and its frequency into hashmap
            word_freq.put(word, freq);
        }
        word_freq = (HashMap<String, Integer>) heap.get("word_freq");
        //push word and its frequency into stack by forming a new mapping relation with data type of Pair
        for (Map.Entry<String, Integer> entry : word_freq.entrySet()) {
            Pair newMap = new Pair(entry.getKey(), entry.getValue());
            stack.push(newMap);
        }
    }

    public void sort() {
        //use priority queue to sort the frequency
        PriorityQueue<Pair> priorityQueue = new PriorityQueue<Pair>(25, new Comparator<Pair>() {
            public int compare(Pair o1, Pair o2) {
                return o2.num - o1.num;
            }
        });
        while (!stack.empty()) {
            priorityQueue.add((Pair) stack.pop());
        }
        Queue<Pair> freq = new LinkedList<Pair>();
        while (!priorityQueue.isEmpty()) {
            freq.offer(priorityQueue.peek());
            priorityQueue.poll();
        }
        heap.put("freq", freq);
    }

    public static void main(String[] args) throws IOException {
        goForth test = new goForth();
        stack.push("pride-and-prejudice.txt");
        test.readFile();
        test.filterChars();
        test.scan();
        test.removeStopWords();
        test.frequencies();
        test.sort();
        int count = 25;
        Queue<Pair> freq = (Queue<Pair>) heap.get("freq");
        while (count > 0 && !freq.isEmpty()) {
            Pair tmp = freq.peek();
            System.out.println(tmp.s + " - " + tmp.num);
            freq.poll();
            count--;
        }
    }

    public class Pair{
        public String s = "";
        public int num = -1;

        public Pair(String s, int num) {
            this.s = s;
            this.num = num;
        }
    }
}
