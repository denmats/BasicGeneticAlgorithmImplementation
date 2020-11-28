package com.denmats.java1;

public class Fitness {

    private long xMax;
    private long functionMax;
    private long iterationCounter;
    private int[] fitnessArray;

    public Fitness() {
        xMax = 0;
        functionMax = 0;
        iterationCounter = 0;
        fitnessArray = null;
    }

    public void resetFunctionMaxAndxMaxToZero() {
        this.functionMax = 0;
        this.xMax = 0;
    }


    public long getxMax() {
        return xMax;
    }

    public long getFunctionMax() {
        return functionMax;
    }

    public long getIterationCounter() {
        return iterationCounter;
    }

    public int[] getFitnessArray() {
        return fitnessArray;
    }

    public void printOutFitness() {
        for (int arr : fitnessArray) System.out.println("Fitness = " + arr);
        System.out.println("Function maximum (fitness) is " + functionMax);
        System.out.println("X max =  " + xMax);
        System.out.println("Iteration: " + iterationCounter);
    }

    //f(x)=a*x^3 + b*x^2 + c*x + d
    public void calculateFitness(int a, int b, int c, int d, int[] phenotypeArray) {
        fitnessArray = new int[phenotypeArray.length];
        for (int i = 0; i < phenotypeArray.length; i++) {
            fitnessArray[i] = (int) (a * Math.pow(phenotypeArray[i], 3)) + b * phenotypeArray[i] * phenotypeArray[i] * c * phenotypeArray[i] + d;
            functionMax += fitnessArray[i];
            xMax += phenotypeArray[i];
        }
        iterationCounter++;

    }
}

