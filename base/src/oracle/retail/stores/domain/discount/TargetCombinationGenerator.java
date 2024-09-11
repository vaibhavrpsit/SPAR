/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TargetCombinationGenerator.java /rgbustores_13.4x_generic_branch/1 2011/08/11 13:24:27 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  08/11/11 - BUG#12623177 Added support for Equal or Lesser Value
 *                         (EOLV)
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TargetCombinationGenerator is used to generate various combinations of target
 * items so that the targets can be exhaustively evaluated for the discount
 * rule. If there are n targets and any r of them need to be selected, then each
 * group would contain r target items and there would be n!/[r! *(n-r)!]
 * combinations.
 */
public class TargetCombinationGenerator
{

    private int[] indices;

    private int numOfElements;

    private int numOfElementsInEachCombination;

    private BigInteger numOfCombinationsLeft;

    private BigInteger totalNumOfCombinations;

    private Object objArr[];

    public TargetCombinationGenerator(int numOfElements, int numOfElementsInEachCombination)
    {
        if (numOfElementsInEachCombination > numOfElements)
        {
            throw new IllegalArgumentException();
        }
        if (numOfElements < 1)
        {
            throw new IllegalArgumentException();
        }
        this.numOfElements = numOfElements;
        this.numOfElementsInEachCombination = numOfElementsInEachCombination;
        indices = new int[numOfElementsInEachCombination];

        // Calculate the total number of possible combinations.
        BigInteger nFact = getFactorial(numOfElements);
        BigInteger rFact = getFactorial(numOfElementsInEachCombination);
        BigInteger nminusrFact = getFactorial(numOfElements - numOfElementsInEachCombination);
        totalNumOfCombinations = nFact.divide(rFact.multiply(nminusrFact));
        reset();
    }

    public TargetCombinationGenerator(ArrayList array, int numOfElementsInEachCombination)
    {
        int numOfElements = array.size();
        objArr = new Object[numOfElements];

        objArr = array.toArray();
        if (numOfElementsInEachCombination > numOfElements)
        {
            throw new IllegalArgumentException();
        }
        if (numOfElements < 1)
        {
            throw new IllegalArgumentException();
        }
        this.numOfElements = numOfElements;
        this.numOfElementsInEachCombination = numOfElementsInEachCombination;
        indices = new int[numOfElementsInEachCombination];

        // Calculate the total number of possible combinations.
        BigInteger nFact = getFactorial(numOfElements);
        BigInteger rFact = getFactorial(numOfElementsInEachCombination);
        BigInteger nminusrFact = getFactorial(numOfElements - numOfElementsInEachCombination);
        totalNumOfCombinations = nFact.divide(rFact.multiply(nminusrFact));
        reset();
    }

    /**
     * Resets the array indices[] and the number of combinations left.
     */
    public void reset()
    {
        for (int i = 0; i < indices.length; i++)
        {
            indices[i] = i;
        }
        numOfCombinationsLeft = new BigInteger(totalNumOfCombinations.toString());
    }

    /**
     * Returns the number of combinations that are yet to be generated.
     * 
     * @return
     */
    public BigInteger getNumOfCombinationsLeft()
    {
        return numOfCombinationsLeft;
    }

    /**
     * Returns a boolean value indicating if there is a next combination.
     * 
     * @return
     */
    public boolean hasNext()
    {
        return numOfCombinationsLeft.compareTo(BigInteger.ZERO) == 1;
    }

    /**
     * Returns the total number of combinations that can be generated for the
     * elements in the array objArr for the given
     * numOfElementsInEachCombination.
     * 
     * @return
     */
    public BigInteger getTotalNumOfCombinations()
    {
        return totalNumOfCombinations;
    }

    /**
     * Computes the factorial for the input parameter n
     * 
     * @param n
     * @return
     */
    private static BigInteger getFactorial(int n)
    {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--)
        {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    /**
     * Returns an int array containing the indices for the next combination of
     * elements.
     * 
     * @return indices for the next combination of elements
     */
    public int[] getNexSetOftIndices()
    {
        if (numOfCombinationsLeft.equals(totalNumOfCombinations))
        {
            numOfCombinationsLeft = numOfCombinationsLeft.subtract(BigInteger.ONE);
            return indices;
        }

        int i = numOfElementsInEachCombination - 1;
        while (indices[i] == numOfElements - numOfElementsInEachCombination + i)
        {
            i--;
        }
        indices[i] = indices[i] + 1;
        for (int j = i + 1; j < numOfElementsInEachCombination; j++)
        {
            indices[j] = indices[i] + j - i;
        }

        numOfCombinationsLeft = numOfCombinationsLeft.subtract(BigInteger.ONE);
        return indices;

    }

    /**
     * Returns the next target group combination.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList next()
    {
        ArrayList nextTargetCombination = new ArrayList();
        int indices[] = this.getNexSetOftIndices();
        for (int i = 0; i < indices.length; i++)
        {
            nextTargetCombination.add(objArr[indices[i]]);
        }
        return nextTargetCombination;
    }

    public static void main(String[] args)
    {
        String[] elements = { "a", "b", "c", "d", "e", "f", "g" };
        int[] indices;
        TargetCombinationGenerator combinations = new TargetCombinationGenerator(elements.length, 3);
        StringBuffer combination;
        while (combinations.hasNext())
        {
            combination = new StringBuffer();
            indices = combinations.getNexSetOftIndices();
            for (int i = 0; i < indices.length; i++)
            {
                combination.append(elements[indices[i]] + ", ");
            }
            System.out.println(combination.toString());
        }

        ArrayList temArray = new ArrayList();
        for (int i = 0; i < elements.length; i++)
        {
            temArray.add(elements[i]);
        }

        combinations = new TargetCombinationGenerator(temArray, 3);

        ArrayList combinationHolder = null;
        while (combinations.hasNext())
        {
            combination = new StringBuffer();
            combinationHolder = combinations.next();
            for (Iterator candidateIter = combinationHolder.iterator(); candidateIter.hasNext();)
            {
                combination.append(candidateIter.next() + ", ");
            }
            System.out.println(combination.toString());
        }

    }

}
