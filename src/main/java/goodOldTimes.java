/**
 * Created by yuanfanz on 17/1/15.
 */


import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * CONSTRAINTS:
 * <p>
 * 1. Very small amount of primary memory,
 * typically orders of magnitude smaller than the data that needs to be processed/generated.
 * <p>
 * 2. No identifiers – i.e. no variable names or tagged memory addresses.
 * All we have is memory that is addressable with numbers.
 */

public class goodOldTimes {

    // Utility for handling the intermediate 'secondary memory'
    public File touchOpen(String fileName) {
        File file = new File(fileName);
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("touch open gone wrong");
        }
        return file;
    }

    public void read(String input) throws IOException {
        Object[] data = new Object[11];
        //get stop words file and put into data[0]
        FileInputStream inputStream = new FileInputStream("stop_words.txt");
        data[0] = IOUtils.toString(inputStream).split(","); // data[0] holds the stop words

        data[1] = new String();                             // data[1] is line (max 80 characters)
        data[2] = new Integer(-1);                          // data[2] is index of the start_char of word
        data[3] = new Integer(0);                           // data[3] is index on characters, i = 0
        data[4] = new Boolean(false);                       // data[4] is flag indicating if word was found
        data[5] = new String();                             // data[5] is the word
        data[6] = new String();                             // data[6] is word,NNNN
        data[7] = new Integer(0);                           // data[7] is frequency
        data[8] = new File("word_freq");                    // data[8] is word frequencies
        data[9] = new File("temp_file");                    // data[9] is temporary file

        //use buffered reader to process input file
        BufferedReader reader = new BufferedReader(new FileReader(input));
        //Loop over input file's lines
        while ((data[1] = reader.readLine()) != null) {
            data[2] = -1;
            data[3] = 0;
            //Loop over characters in the line
            for (int i = 0; i < data[1].toString().toCharArray().length; ++i) {
                if ((Integer) data[2] == -1) {
                    if (Character.isLetterOrDigit(data[1].toString().charAt(i))) {
                        //We found the start of a word
                        data[2] = data[3];
                    }
                } else if (!Character.isLetterOrDigit(data[1].toString().charAt(i))) {
                    //We found the end of a word. Process it
                    data[4] = false;
                    data[5] = data[1].toString().substring((Integer) data[2], (Integer) data[3]).toLowerCase();
                    //Ignore words with len < 2, and stop words
                    if ((data[5].toString().length() >= 2)
                            && (Arrays.binarySearch((String[]) data[0], data[5].toString()) < 0)) { //here use binary search method

                        BufferedReader freqReader = new BufferedReader(new FileReader("word_freq"));
                        BufferedWriter freqWriter = new BufferedWriter(new FileWriter("temp_file", false));
                        //Let's see if it already exists
                        while ((data[6] = freqReader.readLine()) != null) {
                            data[7] = Integer.valueOf(data[6].toString().split(",")[1]);
                            //word, no white space
                            data[6] = data[6].toString().split(",")[0];
                            if (data[5].toString().equals(data[6].toString())) {
                                data[7] = (Integer) data[7] + 1;
                                data[4] = true;
                                freqWriter.write(data[5] + "," + data[7]);
                            } else {
                                freqWriter.write(data[6] + "," + data[7]);
                            }
                            freqWriter.newLine();
                        }
                        freqReader.close();
                        if (!(Boolean) data[4]) {
                            freqWriter.write(data[5].toString() + "," + "1");
                            freqWriter.newLine();
                        }
                        freqWriter.close();
                        //each time we use tmp file to create word_freq file
                        ((File) data[8]).delete();
                        ((File) data[9]).renameTo((File) data[8]);
                        ((File) data[9]).createNewFile();
                    }
                    data[2] = -1;
                }
                data[3] = (Integer) data[3] + 1;
            }
        }
        ((File) data[9]).delete();
        reader.close();
    }

    public void sort() throws IOException {
        PriorityQueue<Pair> data = new PriorityQueue<Pair>(25, new Comparator<Pair>() {
            public int compare(Pair o1, Pair o2) {
                return o2.num - o1.num;
            }
        });
        String newLine;
        BufferedReader freqReader = new BufferedReader(new FileReader("word_freq"));
        while ((newLine = freqReader.readLine()) != null) {
            data.offer(new Pair(newLine.split(",")[0], Integer.valueOf(newLine.split(",")[1])));

        }
        for (int i = 0; i < 25; ++i) {
            System.out.println(data.peek().s + " " + data.peek().num);
            data.poll();
        }
        freqReader.close();
    }

    public static void main(String[] args) throws IOException {
        goodOldTimes test = new goodOldTimes();
        test.touchOpen("word_freq");
        test.touchOpen("temp_file");
        test.read("pride-and-prejudice.txt");
//        test.read("test1.txt");
        test.sort();
    }

    public class Pair {
        public String s = "";
        public int num = -1;

        public Pair(String s, int num) {
            this.s = s;
            this.num = num;
        }
    }
}













































