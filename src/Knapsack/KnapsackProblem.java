/*
    @author Nihar Parikh
    Objective: Implement an algorithm that uses the two design techniques dynamic programming
    and backtracking to solve the Knapsack Problem. Analyze and compare running times.
 */

package Knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Solves the Knapsack problem using Dynamic Programming and Backtracking
 */
public class KnapsackProblem {
    private static ItemList backtrackItems = new ItemList();

    public static void main(String[] args) throws FileNotFoundException {
        //Getting user input for n and k
        Scanner input = new Scanner(System.in);
        System.out.print("Number of Items(N): ");
        int n = input.nextInt();
        System.out.print("Capacity of Knapsack(K): ");
        int k = input.nextInt();
        input.close();

        //Getting all items from file
        Item[] items = getAllItems("knapsack.txt", n);

        //Solving dynamically
        long dynamicStart = System.nanoTime();
        ItemList dynamicItems = dynamicKnapsack(items, k);
        long dynamicStop = System.nanoTime();
        //Printing dynamic stuff out
        System.out.println(dynamicItems.toString("Dynamic Programming"));
        System.out.println("Time = " + (dynamicStop-dynamicStart));

        System.out.println("---------------------------------"); //Separator

        //Solving Backtrackingly
        long backtrackingStart = System.nanoTime();
        backtrackingKnapsack(items, k);
        long backtrackingStop = System.nanoTime();
        //Printing backtracking stuff out
        System.out.println(backtrackItems.toString("Backtracking"));
        System.out.println("Time = " + (backtrackingStop-backtrackingStart));
    }

    /**
     * Gets all the items from the file
     * @param filename name of the data file
     * @param n how many items
     * @return Array of all the items
     * @throws FileNotFoundException if does not find the specific file
     */
    public static Item[] getAllItems(String filename, int n) throws FileNotFoundException {
        Item[] items = new Item[n];
        Scanner file = new Scanner(new File(filename));
        for (int i=0; i<n; i++)
            items[i] = new Item(i+1, Integer.parseInt(file.nextLine()));
        file.close();
        return items;
    }

    /**
     * Solves the knapsack problem dynamically
     * @param s Array of items
     * @param k Capacity of the knapsack
     * @return Array containing the knapsack items
     */
    public static ItemList dynamicKnapsack(Item[] s, int k) {
        boolean[][] exist = new boolean[s.length+1][k+1];
        boolean[][] belong = new boolean[s.length + 1][k + 1];

        //everything is False except 0,0
        exist[0][0] = true;

        for(int row=1; row<=s.length; row++) {
            for (int col=0; col<=k; col++){
                exist[row][col]=false;
                if(exist[row-1][col]) {
                    exist[row][col]=true;
                    belong[row][col]=false;
                }else if(col-(s[row-1]).size >= 0) {
                    if (exist[row-1][col-(s[row-1]).size]){
                        exist[row][col]=true;
                        belong[row][col]=true;
                    }
                }
            }
        }
        //Getting the solution from the tables generated
        return solutionDynamicKnapsack(exist, belong, s, k);
    }

    /**
     * Generates the answer from the table belong
     * @param belong table generated while solving dynamically
     * @param s Array containing items
     * @param k Capacity of the Knapsack
     * @return Array with the solution to knapsack
     */
    public static ItemList solutionDynamicKnapsack(boolean[][] exist, boolean[][] belong, Item[] s, int k) {
        ItemList itemsToTake = new ItemList();
        int row = s.length;
        int col = k;

        //Getting which column to start from
        while (!exist[row][col]) {
            col--;
        }
        //Run until you each end of the table on top
        while (row!=0) {
            if (!belong[row][col]) {
                row--;
            } else if (belong[row][col]) {
                itemsToTake.add(s[row-1]);
                col -= s[row-1].size;
                row--;
            }
        }
        return itemsToTake;
    }

    /**
     * Solves the problem using backtracking
     * @param items Array of all the items
     * @param k Capacity of the knapsack
     */
    public static void backtrackingKnapsack(Item[] items, int k) {
        char[] s = new char[items.length];
        rec_backtrackKnapsack(s, 0, items, k);
    }

    /**
     * Recursive method for backtracking
     * @param s Array to store temp binary values values
     * @param k Variable used as indexing
     * @param items Array containing all the items
     * @param knapSize Capacity of the knapsack
     */
    public static void rec_backtrackKnapsack(char[] s, int k, Item[] items, int knapSize) {
        //Generates binaries
        for (int i = 0; i <= 1; i++) {
            s[k] = (char) ('0' + i);
            if (k == items.length - 1) {
                ItemList tempList = new ItemList();
                for (int j = 0; j < items.length; j++) {
                    //Replace the binary with items only where 1 ignore if 0
                    if (s[j] == '1') {
                        tempList.add(items[j]);
                    }
                }
                //Check javadoc for checkUpdateBacktrack()
                checkUpdateBacktrack(tempList, knapSize);
            } else
                rec_backtrackKnapsack(s, k + 1, items, knapSize);
        }
    }

    /**
     * Check if the new one is better than the previous one
     * @param items Array Containing possible solution with size
     * @param k Capacity of the knapsack
     */
    public static void checkUpdateBacktrack(ItemList items, int k) {
        //Checks if new size is less than knapsack capacity & is better than previous stored
        if (items.size <= k && items.size > backtrackItems.size) {
            backtrackItems.size = items.size;
            backtrackItems.itemsToTake = (ArrayList<Item>) items.itemsToTake.clone();
        }
    }

    /**
     * Contains item size and id (position in array)
     */
    private static class Item{
        private int id; //position in the array
        private int size;

        /**
         * Constructor for Item
         * @param id Position in array
         * @param size Size of the item
         */
        public Item(int id, int size) {
            this.id = id;
            this.size = size;
        }

        public String toString() {
            return "Item = " + this.id + ", Size = " + this.size;
        }
    }

    /**
     * Contains ArrayList for keeping track of solution plus size
     * Saves time to calculate size
     */
    private static class ItemList {
        private ArrayList<Item> itemsToTake;
        private int size;

        /**
         * Constructor for ItemList
         */
        public ItemList() {
            this.itemsToTake = new ArrayList<>();
            this.size = 0;
        }

        /**
         * Adding item to Array and updating the size
         * @param e Item to add
         */
        public void add(Item e) {
            this.itemsToTake.add(e);
            this.size += e.size;
        }

        /**
         * Returns the String representation of the solution
         * @param type What algorithm was used for this solution
         * @return String representation of data
         */
        public String toString(String type) {
            String output = type + ":\n";
            for (Item i: this.itemsToTake)
                output += i.toString() + "\n";
            output += "Total Size = " + size;
            return output;
        }
    }
}