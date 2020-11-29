package com.denmats.java1;

public class Main {
    /*
        Basic Genetic Algorithm Implementation
     */
    final private int a;
    final private int b;
    final private int c;
    final private int d;
    final private double pCrossover;
    final private double pMutation;
    final private int range;
    final private int bits;
    private int[] parents;
    private int[] offsprings;
    private long maximumOfFunction;
    private int progressCounter;


    public Main(int a, int b, int c, int d, double pCrossover, double pMutation, int range, int bits) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.pCrossover = pCrossover;
        this.pMutation = pMutation;
        this.range = (range%2 == 0)? range:6; //the range is supposed being divided by 2 without remain
        this.bits = (bits < 4)? 5: bits; //number of bits can be applied from 5 and higher
        this.parents = new int[range];
        this.offsprings = new int[range];
        this.maximumOfFunction = 0;
        this.progressCounter = 0;
    }

    public static void main(String[] args) {
        /*Basic Genetic Algorithm Implementation*/
        Main m = new Main(2,3,4,-101, 0.8, 0.2, 6, 5);

        /* Generate parent's array of chromosomes range <1,31>*/
        System.out.println("Initialization");
        m.parents = getFirstGeneration(m.range, 31, 1);
        printOut2DArray(getGeneration(m.parents, m.bits), m.bits);
        printOutPhenotypeArray(m.parents);
        System.out.println("Calculation initial fitness");
        /*calculate parent's fitness*/
        Fitness pFitness = new Fitness();
        pFitness.calculateFitness(m.a, m.b, m.c, m.d, m.parents);
        pFitness.printOutFitness();
        m.maximumOfFunction = pFitness.getFunctionMax();

        /*Condition to stop algorithm*/
        while (m.progressCounter < 5) {
            /*Select new generation (phenotype)*/
            m.offsprings = getNewSelectionOfChromosomes(pFitness, m.parents);
            pFitness.resetFunctionMaxAndxMaxToZero();
            /*Make a crossing of gens on new generation*/
            int[] offspringsAfterCrossoverModification = crossover(m.offsprings, m.pCrossover, m.bits);
            /*Make a mutation of gens on new generation*/
            int[] offspringsAfterMutatingModification = mutation(offspringsAfterCrossoverModification, m.pMutation, m.bits);
            /*Calculate fitness for new generation*/
            pFitness.calculateFitness(m.a, m.b, m.c, m.d, offspringsAfterMutatingModification);

            /*Comparing prior function maximum with the current one*/
            /*if progress doesn't happen the certain times, the counter will reset*/
            if (m.maximumOfFunction < pFitness.getFunctionMax()) {
                m.progressCounter++;
                m.maximumOfFunction = pFitness.getFunctionMax();
            } else {
                m.progressCounter = (m.progressCounter > 0) ? m.progressCounter-- : 0;
            }
            /*Eventually, the new generation is becoming parents, and the cycle repeated until the condition will be met*/
            m.parents = offspringsAfterMutatingModification;
        }

        /*Printing out results*/
        printOutFinalResult(m.parents, pFitness, m.bits, m.progressCounter);
    }

    /*Printing out results*/
    public static void printOutFinalResult(int[] parents, Fitness fitness, int bits, int progress){
        System.out.println();
        System.out.println("==================Final result======================");
        printOut2DArray(getGeneration(parents, bits), bits); // The new generation of parents
        printOutPhenotypeArray(parents); //The new generation of parents
        System.out.println("Function (fitness) maximum = " + fitness.getFunctionMax());
        System.out.println("X maximum = " + fitness.getxMax());
        System.out.println("Iteration counter = " + fitness.getIterationCounter());
        System.out.println("Progress counter = " + progress);
    }

    /*Genetic operation. Mutation*/
    public static int[] mutation(int[] array, double pMutation, int bits) {
        int len = array.length;
        int[] mArray = new int[len];
        /*Generate a random number for every chromosome in order to define is it being mutated or not
        Generate a locus if a mutation has to occur
        Convert phenotype array to binary one and make mutation at certain locus (swap bit from 1 to 0 or otherwise)*/
        for (int i = 0; i < len; i++) {
            if (getRandomDouble(1, 0) < pMutation) {
                int locus = getRandomInteger(bits, 0);
                System.out.println("locus mutation = "+locus);
                int[] temp = Dec2BinConverter(array[i], bits);

                if (temp[locus] == 1) {
                    temp[locus] = 0;
                } else {
                    temp[locus] = 1;
                }
                mArray[i] = Bin2DecConverter(temp);
            } else {
                mArray[i] = array[i];
            }
        }
        return mArray;
    }

    /*Genetic operation. Crossover*/
    public static int[] crossover(int[] array, double pCrossover, int bits) {
        int len = array.length;
        int[] pArray = new int[len];
       /* Generate a random number for every pairs in order to define is it being cross or not
        Generate a locus for this crossover
        Convert phenotype array to binary one and make a crossover starting at certain locus*/
        for (int i = 0; i < len; i += 2) {
            double rand = getRandomDouble(1, 0);
            if (rand < pCrossover) {
                int locus = getRandomInteger(len - 1, 0);

                int[] arrA = Dec2BinConverter(array[i], bits);
                int[] arrB = Dec2BinConverter(array[i + 1], bits);

                int[] modifiedA = new int[bits];
                int[] modifiedB = new int[bits];
                for (int j = 0; j < bits; j++) {
                    if (j < locus) {
                        modifiedA[j] = arrA[j];
                        modifiedB[j] = arrB[j];
                    } else {
                        modifiedA[j] = arrB[j];
                        modifiedB[j] = arrA[j];
                    }
                }
                pArray[i] = Bin2DecConverter(modifiedA);
                pArray[i + 1] = Bin2DecConverter(modifiedB);
            }
        }
        return pArray;
    }

    /*Selection new generation of chromosomes*/
    public static int[] getNewSelectionOfChromosomes(Fitness fitness, int[] parents) {
        /*Accumulate total of fitness shares into array (direction from zero to 100)*/
        int len = fitness.getFitnessArray().length;
        double[] fitnessShares = new double[len];
        double shares = 0;
        for (int i = 0; i < len; i++) {
            shares += (double) fitness.getFitnessArray()[i] / fitness.getFunctionMax() * 100;
            fitnessShares[i] = shares;
        }
         /*Compute the indices of next generation of chromosomes
         Generate double number from 0 to 100 for each chromosome
         Compare generated value with fitness share.
         When the generated value match span, pick index then.*/
        int[] offspringIndices = new int[len];
        for (int i = 0; i < len; i++) {
            double shot = getRandomDouble(100, 0);
            for (int j = 1; j < len; j++) {
                if (shot >= fitnessShares[j - 1] && shot < fitnessShares[j]) {
                    offspringIndices[i] = j;
                    break;
                }
            }
        }
        /*Create a new generation of chromosomes based on previous via random selection*/
        int[] offsprings = new int[len];
        for (int i = 0; i < len; i++) {
            offsprings[i] = parents[offspringIndices[i]];
        }
        return offsprings;
    }

    /*Create first population generated randomly*/
    public static int[] getFirstGeneration(int quantityOfChromosomes, int maximumOfGeneratedValue, int minimumOfGeneratedValue) {
        int[] parents = new int[quantityOfChromosomes];
        for (int i = 0; i < quantityOfChromosomes; i++) {
            parents[i] = getRandomInteger(maximumOfGeneratedValue, minimumOfGeneratedValue);
        }
        return parents;
    }

    /*Generator random integer value*/
    public static int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random() * (maximum - minimum))) + minimum;
    }

    /*Generator random double value*/
    public static double getRandomDouble(int maximum, int minimum) {
        return Math.random() * (maximum - minimum) + minimum;
    }

    /*Printing out 1D array of integer numbers*/
    public static void printOutPhenotypeArray(int[] array) {
        System.out.println();
        System.out.println("Phenotype: ");
        for (int i = 0; i < array.length; i++) {
            System.out.println("ch" + i + " = " + array[i]);
        }
        System.out.println();
    }

    /*Printing out 2D array (binary representation of phenotype array)*/
    public static void printOut2DArray(int[][] array, int bits) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("ch" + i + " = ");
            for (int j = 0; j < bits; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }

    }

    /*Return 2D binary array from 1D array of decimal numbers*/
    public static int[][] getGeneration(int[] arr, int bits) {
        int[][] generation = new int[arr.length][bits];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < bits; j++) {
                generation[i][j] = Dec2BinConverter(arr[i], bits)[j];
            }
        }
        return generation;
    }

    /*convert decimal number to binary*/
    public static int[] Dec2BinConverter(int decimal, int bits) {
        //convert decimal to binary number. The binary number needs to be reversed after that.
        int[] binary = new int[bits];
        int index = 0;
        while (decimal > 0) {
            binary[index++] = decimal % 2;
            decimal = decimal / 2;
        }
        //reverse order to read right binary presentation of decimal number
        int[] bin = new int[bits];
        for (int i = 0; i < bits; i++) {
            bin[i] = binary[binary.length - 1 - i];
        }
        return bin;
    }

    /*Convert binary number to decimal*/
    public static int Bin2DecConverter(int[] binary) {
        int len = binary.length;
        int[] weights = new int[len];
        for (int i = 0; i < len; i++) {
            weights[i] = (int) Math.pow(2, len - 1 - i);
        }
        int decimal = 0;
        for (int i = 0; i < len; i++) {
            decimal += binary[i] * weights[i];
        }
        return decimal;
    }
}

