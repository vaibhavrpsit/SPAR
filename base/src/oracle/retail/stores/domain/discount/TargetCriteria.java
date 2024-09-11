/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TargetCriteria.java /main/15 2013/07/30 11:49:07 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  07/30/13 - changed sortAnyEntries() method to use description
 *                         instad of reasonCode to check if the rule needs to
 *                         be sorted
 *    mchellap  08/11/11 - BUG#12623177 Added support for Equal or Lesser Value
 *                         (EOLV)
 *    cgreene   06/22/10 - Do not clone rule criteria when cloning rule to
 *                         avoid heap space wastage
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/25/10 - changes to handle discount rules with source and
 *                         target as class.
 *    vapartha  02/19/10 - Added code to handle discount rules when the source
 *                         and target is a class.
 *    abondala  01/03/10 - update header date
 *    cgreene   10/29/09 - XbranchMerge cgreene_nonreceiptreturns from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/27/09 - use new method isAllRequired
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/15/2007 10:48:46 AM Christian Greene
 *         Belize merge - add support for Any/All sources/targets
 *    3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:37 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:50:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:58:24   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:18:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Dec 2001 08:36:54   pjf
 * Added clone method to criteria classes, changed cast in AdvancedPricingRule.clone();
 * Resolution for POS SCR-245: Domain Refactoring
 *
 *    Rev 1.0   21 Dec 2001 14:17:12   pjf
 * Initial revision.
 * Resolution for POS SCR-245: Domain Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;

/**
 * TargetCriteria represents the behavior for testing the target criteria for an
 * advanced pricing rule. The criteria's state is maintained in the DiscountList
 * superclass.
 * 
 * @version $Revision: /main/15 $
 */
public class TargetCriteria extends DiscountList
{
    private static final long serialVersionUID = -8505810069644524570L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
    
    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(TargetCriteria.class);

    /**
     * Tests to see if the source criteria for this rule have been met. Adds any
     * objects which satisfy a source criterion to the collection of sources for
     * this rule. The contents of this collection are reset each time this
     * method is called.
     * 
     * @param ArrayList containing potential sources for this rule
     * @return boolean indicating whether all the source criteria for this rule
     *         have been met.
     */
    @Override
    public boolean evaluate(ArrayList candidates, ArrayList selected)
    {
        return evaluateQuantities(candidates, selected,false);
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        return (obj instanceof TargetCriteria && super.equals(obj));
    }

    /**
     * Clones the criteria list.
     * 
     * @return new TargetCriteria Object
     */
    @Override
    public Object clone()
    {
        TargetCriteria newList = new TargetCriteria();
        setCloneAttributes(newList);
        return newList;
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append("TargetCriteria (Revision ");
        strResult.append(super.toString());
        return (strResult.toString());
    }

    /**
     * Tests to see if the target criteria for this rule have been met. Adds any
     * objects which satisfy a target criterion to the selected list.
     * 
     * @param ArrayList containing potential targets for this rule
     * @return boolean indicating whether all the target criteria for this rule
     *         have been met.
     */
    protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected, boolean allEligibleTargets)
    {
        // reset previously counted quantities
        resetQuantities();
        selected.clear();

        String criterion = null;
        DiscountTargetIfc candidate = null;

        // for each String in the criteria list
        for (Iterator<String> p = criteria(); p.hasNext();)
        {
            criterion = p.next();

            // check to see if a source matches the criterion
            for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext();)
            {
                candidate = (DiscountTargetIfc) candidateIter.next();

                if (attributesEqual(criterion, candidate))
                {
                    // if match is found, increment the quantity counted,
                    // add item to the selected bucket and remove from candidates
                   
                    if(allEligibleTargets || !quantitySatisfied(criterion))
                    {
                        if (anyQuantity == 0) // wait to determine "any" selections
                        {
                            selected.add(candidate);
                            candidateIter.remove(); // selected candidates are no longer 
                                                    //candidates
                        }
                        incrementQuantity(criterion, candidate);
                    }

                    else
                    {
                        break;
                    }
                }// end if
            }// end for
        }// end for

        return (!isAllRequired()) ? evaluateAnySatisfied(candidates, selected, allEligibleTargets) : allSatisfied();
    }

    /**
     * Sorts targets based on the reason code of the rule.
     * <p>
     * BuyNofXgetYatZ%off, BuyNofXgetYatZ$, Buy$NorMoreofXGetYatZ%off and
     * Buy$NorMoreofXGetYatZ$ should have the targets sorted by price
     * descending.
     * 
     * @param entries the {@link DiscountListEntry}s to sort.
     */
    @Override
    protected void sortAnyEntries(List entries)
    {
        String description = getDescription();

        if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$))
        {
            Collections.sort(entries, Comparators.discountListEntryDescending);
        }
    }

    /**
     * Tests to see if the criteria for this list have been met. Adds items used
     * to satisfy a criterion to the selected ArrayList.
     * 
     * @param ArrayList containing potential sources
     * @param ArrayList empty ArrayList used to store selected sources
     * @return boolean indicating whether all the source criteria for this list
     *         have been met.
     */
    @Override
    public boolean evaluateAllEligibleSourcesAndTargets(ArrayList candidates, ArrayList selected)
    {
        return evaluateQuantities(candidates, selected, true);
    }

    /**
     * Tests whether the target quantity has been achieved for a given
     * ArrayList.
     * 
     * @param ArrayList eligibleTargets - the ArrayList containing the targets
     * @return boolean value indicating whether the target quantity is satisfied
     *         or not.
     */
    @Override
    public boolean quantitySatisfied(ArrayList eligibleTargets)
    {
        boolean satisfied = false;
        String criterion = null;
        DiscountSourceIfc candidate = null;
        // for each String in the criteria list
        for (Iterator p = criteria(); p.hasNext();)
        {
            criterion = (String) p.next();
            if (containsEntry(criterion))
            {
                DiscountListEntry dle = (DiscountListEntry) map.get(criterion);
                satisfied = eligibleTargets.size() >= dle.getQuantityRequired();
                break;
            }
        }
        return satisfied;
    }
    
    /**
     * When both the source and target of the discount rule are set to "Class"
     * or when EOLV is enabled, evaluates the eligible sources and targets
     * dynamically.
     * 
     * @param eligibleSources all the available sources
     * @param eligibleTargets all the available targets
     * @return returns a boolean value indicating the status of the evaluation.
     */
    @SuppressWarnings("unchecked")
    public boolean reevaluateQuantities(ArrayList eligibleSources, ArrayList eligibleTargets,
            boolean isEqualOrLesserValue)
    {
        boolean satisfied = false;
        ArrayList selectedTargets = new ArrayList();

        satisfied = evaluateQuantities(eligibleTargets, selectedTargets, false);

        if (isEqualOrLesserValue && satisfied)
        {
            if (getTotalPrice(selectedTargets).compareTo(getTotalPrice(eligibleSources)) == CurrencyIfc.GREATER_THAN)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Target chosen is not equal or lesser value. Re-evaluating.");
                }
                satisfied = false;
            }
        }
        // If target quantities and threshold have been met
        if (satisfied)
        {
            eligibleTargets.clear();
            eligibleTargets.addAll(selectedTargets);
        }
        return satisfied;
    }

    /**
     * When both the source and target of the discount rule are set to "Class",
     * or when EOLV is enabled evaluates the eligible sources and targets
     * dynamically.
     * 
     * @param eligibleSources all the available sources
     * @param eligibleTargets all the available targets
     * @param isEqualOrLesserValue denotes the EOLV flag
     * @return returns a boolean value indicating the status of the evaluation.
     */
    @Override
    public boolean reevaluate(ArrayList eligibleSources, ArrayList eligibleTargets, boolean isEqualOrLesserValue)
    {
        // If isEqualOrLesserValue is false then reevaluate the target
        // quantities and return the selected candidates.
        if (!isEqualOrLesserValue)
        {
            return reevaluateQuantities(eligibleSources, eligibleTargets, isEqualOrLesserValue);
        }

        // If isEqualOrLesserValue is true, reevaluate the targets exhaustively
        // to find the best eligible target combination.
        boolean satisfied = false;
        ArrayList<ArrayList> allTargetCombinations = new ArrayList<ArrayList>();
        String criterion = null;

        TargetCombinationGenerator targetCombinations = null;
        int[] indices;
        int[] qtyForEachCriterion;
        int[] qtyForAllOrAnyCombinations = new int[0];
        DiscountListEntry dle = null;

        // calculate the qty required for forming combinations
        if (isAllRequired())
        {
            qtyForAllOrAnyCombinations = new int[1];
            for (Iterator p = criteria(); p.hasNext();)
            {
                criterion = (String) p.next();
                dle = (DiscountListEntry) map.get(criterion);
                qtyForAllOrAnyCombinations[0] += dle.getQuantityRequired();
            }
        }
        else
        {
            // For Any criteria, the total number of target items required for
            // satisfying the rule might vary depending on the individual
            // quantity required for each criterion and the ANY quantity of the
            // criteria required. So the total number of target items required
            // for one iteration are computed dynamically.
            int index = 0;
            qtyForEachCriterion = new int[criteriaArray().length];
            for (Iterator p = criteria(); p.hasNext();)
            {
                criterion = (String) p.next();
                dle = (DiscountListEntry) map.get(criterion);
                qtyForEachCriterion[index] = dle.getQuantityRequired();
                index++;
            }

            targetCombinations = new TargetCombinationGenerator(index, anyQuantity);
            int combinationQtyTotal;

            index = 0;
            qtyForAllOrAnyCombinations = new int[targetCombinations.getTotalNumOfCombinations().intValue()];
            while (targetCombinations.hasNext())
            {
                combinationQtyTotal = 0;
                indices = targetCombinations.getNexSetOftIndices();
                for (int i = 0; i < indices.length; i++)
                {
                    combinationQtyTotal += qtyForEachCriterion[indices[i]];
                }
                qtyForAllOrAnyCombinations[index] = combinationQtyTotal;
                index++;
            }
        }

        ArrayList tempHolder = new ArrayList();
        ArrayList nextCombination = null;

        // Generate all possible target combinations
        int eliminateDupCombTotals[] = new int[qtyForAllOrAnyCombinations.length];

        for (int i = 0; i < qtyForAllOrAnyCombinations.length; i++)
        {
            satisfied = true;
            try
            {
                for (int j = 0; j < eliminateDupCombTotals.length; j++)
                {
                    if (eliminateDupCombTotals[j] == qtyForAllOrAnyCombinations[i])
                    {
                        satisfied = false;
                        break;
                    }
                }
                if (satisfied)
                {
                    eliminateDupCombTotals[i] = qtyForAllOrAnyCombinations[i];
                    targetCombinations = new TargetCombinationGenerator(eligibleTargets, qtyForAllOrAnyCombinations[i]);
                    while (targetCombinations.hasNext())
                    {
                        nextCombination = targetCombinations.next();
                        allTargetCombinations.add(nextCombination);
                    }
                }
            }
            catch (IllegalArgumentException iae)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Target chosen is not equal or lesser value. Re-evaluating.");
                }
            }
        }

        // Evaluate the target combinations to find the best combinations that
        // satisfies the rule.
        Collections.sort(allTargetCombinations, Comparators.targetCombinationTotalDescending);
        for (Iterator iter = allTargetCombinations.iterator(); iter.hasNext();)
        {
            tempHolder = (ArrayList) iter.next();
            satisfied = reevaluateQuantities(eligibleSources, tempHolder, isEqualOrLesserValue);
            if (satisfied)
            {
                eligibleTargets.clear();
                eligibleTargets.addAll(tempHolder);
                break;
            }
        }
        return satisfied;
    }
    
    /**
     * Returns the total price for all the items that have been selected to
     * participate in this rule.
     * 
     * @param selectedItems a List of {@link DiscountItemIfc} objects
     * @return CurrencyIfc the total selling price of the selected items.
     */
    @SuppressWarnings("unchecked")
    protected static CurrencyIfc getTotalPrice(List selectedItems)
    {
        CurrencyIfc totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
        // iterate over the entries and sum the amount
        for (Iterator iter = selectedItems.iterator(); iter.hasNext();)
        {
            DiscountItemIfc selectedItem = (DiscountItemIfc) iter.next();
            totalSellingPrice = totalSellingPrice.add(selectedItem.getSellingPrice());
        }
        return totalSellingPrice;
    }


}
