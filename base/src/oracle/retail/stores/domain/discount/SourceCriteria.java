/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/SourceCriteria.java /main/31 2013/10/11 14:25:36 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  10/11/13 - Quantity based AnyCombo fix done in
 *                         evaluateQuantities method by incrementing quantites
 *                         for anyCombo rules
 *    tksharma  10/04/13 - removed uneccesary removal of candidates for
 *                         anyCombo qualifier
 *    rabhawsa  09/10/13 - multi-threshold item entries should be sorted.
 *    rabhawsa  08/27/13 - multi-threshold quantitySatisfied should not be
 *                         dependent on criteria.
 *    rabhawsa  08/27/13 - multi-threshold amountSatisfied should not be
 *                         dependent on criteria.
 *    tksharma  08/07/13 - fixed getDiscountSources(boolean) and corresponding
 *                         Targets method to return either Sale item or
 *                         non-receipt return item based on argument value
 *    tksharma  07/30/13 - Modified evaluateAmounts method to correct the
 *                         implementation for anyCombo. Refactored to bring it
 *                         in lines with evaluateQuantities method
 *    tksharma  07/23/13 - fixed multithreshold for nbr times per transaction
 *                         more than one
 *    tksharma  06/25/13 - modified evaluateAmounts() to support multithreshold
 *                         rules with item and threshold qualifier
 *    tksharma  05/21/13 - modified evaluate method to incoporate for
 *                         'AnyCombo' Qualifier for multithreshold rule
 *    tksharma  08/02/12 - multithreshold-merge with sthallam code
 *    tksharma  08/02/12 - multithreshold- discount rule
 *    mkutiana  03/29/12 - XbranchMerge stallama_bug-13101164 from
 *                         rgbustores_13.1.5_generic_branch
 *    mchellap  08/11/11 - BUG#12623177 Added support for Equal or Lesser Value
 *                         (EOLV)
 *    abhayg    09/15/10 - Discount should be removed from Target item after
 *                         amount falls below the threshold.
 *    cgreene   06/22/10 - Do not clone rule criteria when cloning rule to
 *                         avoid heap space wastage
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/19/10 - Added code to handle discount rules when the source
 *                         and target is a class.
 *    abondala  01/03/10 - update header date
 *    cgreene   10/29/09 - XbranchMerge cgreene_nonreceiptreturns from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/27/09 - use new method isAllRequired
 *    lslepeti  11/05/08 - add rules of type BuyNorMoreOfXforZ%off and
 *                         BuyNorMoreOfXforZ$each
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         11/15/2007 10:48:46 AM Christian Greene
 *         Belize merge - add support for Any/All sources/targets
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:19 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jun 11 2003 14:46:54   cdb
 * According to SCR-2005, Allow Repeating Sources should be evaluated regardless if the threshold type is by quantity or amount.
 * 
 *    Rev 1.2   Mar 20 2003 09:26:32   jgs
 * Changes due to code reveiw.
 * Resolution for 103: New Advanced Pricing Features
 * 
 *    Rev 1.1   Jan 20 2003 11:50:16   jgs
 * Added allow repeating sources, deal distribution, and percent off lowest priced Item to Advanced Pricing Rule processing.
 * Resolution for 103: New Advanced Pricing Features
 * 
 *    Rev 1.0   Jun 03 2002 16:50:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:58:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:18:36   msg
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

/**
 * SourceCriteria represents the behavior for testing the source criteria for an
 * advanced pricing rule. The criteria's state is maintained in the DiscountList
 * superclass.
 * 
 * @version $Revision: /main/31 $
 */
public class SourceCriteria extends DiscountList
{
    private static final long serialVersionUID = 713877386575652129L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/31 $";

    /**
     * If false, each source must have a different Item ID.
     */
    protected boolean allowRepeatingSources = true;

    /**
     * Flag as to whether sources are discounted (targets).
     */
    protected boolean sourcesAreTargets;

    /**
     * Tests to see if the source criteria for this rule have been met. Adds any
     * items which satisfy a source criterion to the collection of selected
     * sources for a rule. The contents of the collection are reset each time
     * this method is called.
     * 
     * @param ArrayList containing potential sources for a rule
     * @param ArrayList to hold selected sources for the rule
     * @return boolean indicating whether the source criteria maintained by this
     *         list have been met.
     */
    @Override
    public boolean evaluate(ArrayList candidates, ArrayList selected)
    {
    	return evaluate(candidates, selected, false);
    }
    
    /**
     * Tests to see if the source criteria for this rule have been met. Adds any
     * items which satisfy a source criterion to the collection of selected
     * sources for a rule. The contents of the collection are reset each time
     * this method is called.
     * 
     * @param ArrayList containing potential sources for a rule
     * @param ArrayList to hold selected sources for the rule
     * @param sourcesAreTargets indicates if sources are also considered targets
     * @return boolean indicating whether the source criteria maintained by this
     *         list have been met.
     */
    @Override
    public boolean evaluate(ArrayList candidates, ArrayList selected, boolean sourcesAreTargets)
    {
        boolean value = false;

        switch (thresholdType) {
        case THRESHOLD_QUANTITY: {
            // see if all quantity thresholds have been achieved
            value = evaluateQuantities(candidates, selected);
            break;
        }
        case THRESHOLD_AMOUNT: {
            // see if all amount thresholds have been achieved
            value = evaluateAmounts(candidates, selected);
            break;
        }
        default: {
            throw new IllegalStateException("Invalid thresholdTypeCode loaded for pricing rule ");
        }
        }
        return value;
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

        if (obj instanceof SourceCriteria && super.equals(obj))
        {
            SourceCriteria s = (SourceCriteria) obj;
            if (allowRepeatingSources == s.allowRepeatingSources &&
                sourcesAreTargets == s.sourcesAreTargets)
            {
                return true;
            }
        }

        return false;
    }

    
    /**
     * Clones the criteria list.
     * 
     * @return new SourceCriteria Object
     */
    @Override
    public Object clone()
    {
        SourceCriteria newList = new SourceCriteria();
        setCloneAttributes(newList);
        return newList;
    }

    /**
     * Gets the Allow Repeating Sources Flag.
     * 
     * @return true if sources may have the same Item ID.
     */
    public boolean getAllowRepeatingSources()
    {
        return allowRepeatingSources;
    }

    /**
     * Sets the Allow Repeating Sources Flag.
     * 
     * @param value - the Allow Repeating Sources flag.
     */
    public void setAllowRepeatingSources(boolean value)
    {
        allowRepeatingSources = value;
    }

    /**
     * Gets the sourcesAreTargets flag.
     * 
     * @return true if sources are discounted
     */
    public boolean getSourcesAreTargets()
    {
        return sourcesAreTargets;
    }

    /**
     * Sets the sourcesAreTargets flag.
     * 
     * @param value - the sourcesAreTargets flag.
     */
    public void setSourcesAreTargets(boolean value)
    {
        sourcesAreTargets = value;
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
        strResult.append("SourceCriteria (Revision ");
        strResult.append(super.toString());
        return (strResult.toString());
    } 

    /**
     * Sets the attributes for a clone of this list.
     * 
     * @param uninitialized DiscountList object
     */
    protected void setCloneAttributes(SourceCriteria newList)
    {
        super.setCloneAttributes(newList);
        newList.allowRepeatingSources = allowRepeatingSources;
        newList.sourcesAreTargets = sourcesAreTargets;
    }
    
    /**
     * Evaluates whether the quantity requirements have been met. For ANY Sources a second iteration is
     * performed to check if the items belonging to a single ANY criterion has the required ANY quantity
     *
     * @param candidates containing potential sources for this rule.
     * @param selected sources that have been selected for this rule.
     * @param allEligibleSources this value is true only when both the source and target
     *        are set to Class.
     * @return true if all the source criteria for this rule have been met.
     */
    protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected, boolean allEligibleSources)
    {
        boolean returnVal = false;
        // Evaluate Any Qty first in order to give best deal to customer. 
        if (!isAllRequired())
        {
            returnVal = evaluateQuantities(candidates, selected, allEligibleSources, true);
        }
        else
        {
            returnVal = evaluateQuantities(candidates, selected, allEligibleSources, false);
        }
        return returnVal;
    }

    
    /**
     * Evaluates whether the quantity requirements have been met.
     * 
     * @param candidates containing potential sources for this rule.
     * @param selected sources that have been selected for this rule.
     * @param allEligibleSources this value is true only when both the source
     *            and target are set to Class.
     * @param reEvaluateSources flag to re-evaluate the sources belonging to a
     *            single criterion for ANY quantity
     * @return true if all the source criteria for this rule have been met.
     */
    protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected,boolean allEligibleSources, boolean reEvaluateSources)
    {
        // reset previously counted quantities
        resetQuantities();
        selected.clear();

        String criterion = null;
        DiscountSourceIfc candidate = null;
        boolean criteriaSatisfied = false;

        // check to see if a source matches the criterion
        for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext();)
        {
            candidate = (DiscountSourceIfc)candidateIter.next();
            // for each String in the criteria list
            for (Iterator<String> p = criteria(); p.hasNext();)

            {
                criterion = p.next();

                if (attributesEqual(criterion, candidate) && // comparison basis
                                                             // equals
                        itemIdIsAcceptable(criterion, selected, candidate)) // id
                                                                            // allowed
                                                                            // to
                                                                            // repeat
                {

                    // if match is found, increment the quantity counted,
                    // add item to the selected bucket and remove from
                    // candidates
                    if (quantitySatisfied(criterion))
                    {
                        if (getDescription().equals(
                                DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZPctoff)
                                || getDescription().equals(
                                        DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each))
                        {
                            // if all is required, add now. We determine "any"
                            // selections later
                            if (isAllRequired())
                            {
                                selected.add(candidate);
                                // selected candidates are no longer candidates
                                candidateIter.remove();
                            }
                            incrementQuantity(criterion, candidate);
                            List entries = new ArrayList();
                            // add qualified dles to the list
                            for (Iterator i = map.values().iterator(); i.hasNext();)
                            {
                                DiscountListEntry dle = (DiscountListEntry)i.next();
                                if (dle.quantitySatisfied())
                                {
                                    entries.add(dle);
                                }
                            }
                            sortAnyEntries(entries);
                        }
                        else if (allEligibleSources)
                        {
                            if (isAllRequired())
                            {
                                selected.add(candidate);
                                candidateIter.remove(); // selected candidates
                                                        // are no longer
                                                        // candidates
                            }
                            incrementQuantity(criterion, candidate);
                        }
                        // If evaluating for the Any Qty against ANY source
                        // criteria.
                        else if (reEvaluateSources && quantitySatisfied(criterion) && !altQuantitySatisfied(criterion))
                        {
                            incrementAltQuantity(criterion, candidate);
                        }
                        else
                        {
                            break;
                        }
                    }
                    else if(!quantitySatisfied(criterion) || allEligibleSources)                       
                    {                        
                        // if all is required, add now. We determine "any" selections later
                        if (isAllRequired())
                        {
                            selected.add(candidate);
                            candidateIter.remove(); // selected candidates are
                                                    // no longer candidates

                            incrementQuantity(criterion, candidate);
                        }

                        // increment quantity only if ANY criteria is not
                        // satisfied
                        else if (!anySatisfied() || isAnyComboAcceptable())
                        {
                            incrementQuantity(criterion, candidate);
                        }
                    }
                }// end if
            }// end for
        }// end for

        if (isAnyComboAcceptable() && isMultiThreshold())
        {
            criteriaSatisfied = evaluateAnyComboQuantitySatisfied(candidates, selected);
        }
        else
        {
            criteriaSatisfied = (!isAllRequired()) ? evaluateAnySatisfied(candidates, selected) : allSatisfied();
        }
        if (criteriaSatisfied)
        {
            checkSourcesForManualDiscounts(selected, candidates);
        }

        return criteriaSatisfied;
    }

    
    /**
     * This method evaluates the selected sources to check if there are any manually discounted items with the same item id
     * in the candidates list, and when it finds one of such items, those two items are swapped so that the discounted
     * item doesn't end up in the target list.
     * @param selected
     * @param candidates
     */
    protected void checkSourcesForManualDiscounts(ArrayList selected, ArrayList candidates)
    {
    	//This is not required for the rules of type Buy X for.
        if(ruleReasonCode != DISCOUNT_REASON_BuyNofXforZPctoff &&
                ruleReasonCode != DISCOUNT_REASON_BuyNofXforZ$off &&
                ruleReasonCode != DISCOUNT_REASON_BuyNofXforZ$)
        {
	        ArrayList swappedSelected = new ArrayList(2);
	        ArrayList swappedCandidates = new ArrayList(2);
	        for (Iterator iter = selected.iterator(); iter.hasNext(); )
	        {
	            SaleReturnLineItemIfc selectedSource = (SaleReturnLineItemIfc)iter.next();
	            for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext(); )
	            {
	                SaleReturnLineItemIfc candidate = (SaleReturnLineItemIfc)candidateIter.next();
	                if(selectedSource.getItemID().equals(candidate.getItemID())
	                        && selectedSource.getExtendedDiscountedSellingPrice().compareTo(
	                                candidate.getExtendedDiscountedSellingPrice()) == 1)
	                {
	                    swappedSelected.add(candidate);
	                    swappedCandidates.add(selectedSource);
	                    iter.remove();
	                    candidateIter.remove();
	                    break;
	                }
	            }
	        }
	        if(swappedSelected.size() > 0 && swappedCandidates.size() > 0 )
	        {
	            selected.addAll(swappedSelected);
	            candidates.addAll(swappedCandidates);
	        }
        }
    }
    
    /**
     * Tests whether the target ANY quantity has been achieved for this entry.
     * This method is used in the second iteration of sources to determine if there
     * are any eligible sources belonging to a ANY criterion matching the ANY quantity.
     *
     * @param criterion - the source criterion
     * @return boolean true if the String is associated with an entry and
     * has been incremented the required number of times, false otherwise
     */

    public boolean altQuantitySatisfied(String criterion)
    {
        boolean satisfied = false;
        int qtySatisfied = 0;

        // check if the minimum quantity has been met for each target criterion
        for (Iterator iterator = map.values().iterator(); iterator.hasNext();)
        {
            DiscountListEntry dle = (DiscountListEntry)iterator.next();
            if (dle.quantitySatisfied())
            {
                qtySatisfied += dle.getQuantity() / dle.getQuantityRequired();
            }
        }
        satisfied = qtySatisfied >= anyQuantity;
        return satisfied;
    }
    
    /**
     * Overridden from superclass to provide ability to count Any sources more
     * than once if {@link #getAllowRepeatingSources()} is true.
     *
     * @return boolean true if any has been satisfied.
     */
    @Override
    public boolean anySatisfied()
    {
       DiscountListEntry dle = null;
       int qtySatisfied = 0;
       boolean anyQtySatisfied = false;
       // check if the minimum quantity has been met for each target criterion
       for (Iterator iterator = map.values().iterator(); iterator.hasNext(); )
       {
           dle = (DiscountListEntry) iterator.next();
           if (dle.quantitySatisfied())
           {
               qtySatisfied += 1;
           }
       }

       anyQtySatisfied = qtySatisfied >= anyQuantity;
       //Re-check if sources have satisfied the required ANY quantity
       qtySatisfied = 0;
       
       if(!anyQtySatisfied)
       {
           for (Iterator iterator = map.values().iterator(); iterator.hasNext(); )
           {
               dle = (DiscountListEntry) iterator.next();
               if (dle.quantitySatisfied())
               {
                   qtySatisfied += dle.getQuantity()/dle.getQuantityRequired();
               }               
           }
           anyQtySatisfied = qtySatisfied >= anyQuantity;
       }
       return anyQtySatisfied;
    }
    
    /**
     * Tests
     * 
     * @param ArrayList containing potential sources for this rule
     * @param
     * @return boolean indicating whether all the source criteria for this rule
     *         have been met.
     */
    protected boolean evaluateAmounts(ArrayList candidates, ArrayList selected)
    {
        // reset previously counted amounts
        resetAmounts();
        resetQuantities();
        selected.clear();

        String criterion = null;
        DiscountSourceIfc candidate = null;
        boolean criteriaSatisfied = false;

        // for each String in the criteria list
        for (Iterator<String> p = criteria(); p.hasNext();)
        {
            criterion = p.next();

            // check to see if an item has a matching attribute
            for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext();)
            {
                candidate = (DiscountSourceIfc)candidateIter.next();

                if (attributesEqual(criterion, candidate) && itemIdIsAcceptable(criterion, selected, candidate))
                {
                    // if match is found, increment the amount counted,
                    // add item to the selected bucket and remove from
                    // candidates
                    if (amountSatisfied(criterion))
                    {
                        break;
                    }
                    else
                    {
                        if (isAllRequired())
                        {
                            selected.add(candidate);
                            candidateIter.remove();
                        }
                        addToAmount(criterion, candidate.getExtendedDiscountedSellingPrice());
                        addDiscountItem(criterion, candidate);
                    }
                }// end if
            }// end for
        }// end for

        if (isAnyComboAcceptable() && isMultiThreshold())
        {
            criteriaSatisfied = evaluateAnyComboAmountSatisfied(candidates, selected);
        }
        else
        {
            criteriaSatisfied = (!isAllRequired()) ? evaluateAnyAmountSatisfied(candidates, selected) : allAmountsSatisfied();
        }

        return criteriaSatisfied;
    }

    /**
     * Calls {@link #itemIdIsAcceptable(String, ArrayList, DiscountSourceIfc)}
     * with null criterion.
     */
    protected boolean itemIdIsAcceptable(ArrayList selected, DiscountSourceIfc item)
    {
        return itemIdIsAcceptable(null, selected, item);
    }
    
    /**
     * Tests to make sure that the Item ID is not in the selected list if the
     * <code>allowRepeatingSources</code> flag is set to false.
     * 
     * @param criterion the criterion used to match the possible item
     * @param selected the list of selected sources may be empty if anyQty == 0
     * @param the potential source item
     * @return false if the specified item already is in selected list
     */
    protected boolean itemIdIsAcceptable(String criterion, ArrayList selected, DiscountSourceIfc item)
    {
        if (!allowRepeatingSources)
        {
            String itemID = item.getItemID();
            List otherSelections = null;
            if (criterion == null || isAllRequired())
            {
                otherSelections = selected;
            }
            else
            {
                // check against items in DLE for this item.
                DiscountListEntry dle = (DiscountListEntry)map.get(criterion);
                otherSelections = dle.discountItems;
            }
            if (isItemAlreadySelected(itemID, otherSelections))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Sorts sources based on the reason code of the rule.
     * <p>
     * BuyNofXgetHighestPricedXatZ%off, Buy$NorMoreofXGetYatZ$off,
     * Buy$NorMoreofXGetYatZ%off, Buy$NorMoreofXGetYatZ$,
     * BuyNofXforZ$ and BuyNofXforZ%off should have the sources sorted by price
     * descending.
     * <p>
     * BuyNofXgetLowestPricedXatZ%off should be sort price ascending.
     * 
     * @param entries the {@link DiscountListEntry}s to sort.
     */
    @Override
    protected void sortAnyEntries(List entries)
    {
        String description = getDescription();
        
        if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXforZ$off))
        {
            // Can't predict best grouping for something of this nature
            // Selecting lowest priced items might cause some prices to go negative.
            // Selecting highest priced items might prevent an item from being used
            //     for another better rule.
        }
        else if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXforZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNofXforZ$)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetHighestPricedXatZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NofXforZ$off)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NofXforZPctoff))
        {
            // Percent Off of a source that's a target - we definitely want the highest priced
            // items included.
            Collections.sort(entries, Comparators.discountListEntryDescending);            
        }
        else if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetLowestPricedXatZPctoff))
        {
            // We have to have the lowest priced item in the group to satisfy this rule
            Collections.sort(entries, Comparators.discountListEntryAscending);
        }
        else if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZPctoff)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$))
        {
            // Other scenarios depend on whether sources are targets
            if (sourcesAreTargets)
            {
                Collections.sort(entries, Comparators.discountListEntryDescending);
            }
            else
            {
                Collections.sort(entries, Comparators.discountListEntryAscending);
            }
        }
        // Do no harm when sources are targets for these selections
        // DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$off
        // DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$off

        else if (!sourcesAreTargets)
        {
            Collections.sort(entries, Comparators.discountListEntryAscending);
        }
    }

    /**
     * Return true if the itemID passed already exists in the otherSelections.
     * 
     * @param itemID
     * @param otherSelections
     * @return
     */
    private boolean isItemAlreadySelected(String itemID, List otherSelections)
    {
        // check against rest in selected list
        for (Iterator j = otherSelections.iterator(); j.hasNext();)
        {
            DiscountSourceIfc temp = (DiscountSourceIfc) j.next();

            if (temp instanceof AbstractTransactionLineItemIfc)
            {
                String tempID = ((AbstractTransactionLineItemIfc) temp).getItemID();
                if (tempID.equals(itemID))
                {
                    return true;
                }
            }
        }// end for
        return false;
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
    	boolean value = false;

        switch (thresholdType) {
        case THRESHOLD_QUANTITY: {
            // see if all quantity thresholds have been achieved
            value = evaluateQuantities(candidates, selected,true);
            break;
        }
        case THRESHOLD_AMOUNT: {
            // see if all amount thresholds have been achieved
            value = evaluateAmounts(candidates, selected);
            break;
        }
        default: {
            throw new IllegalStateException("Invalid thresholdTypeCode loaded for pricing rule ");
        }
        }
        return value;
    }

    /**
     * Tests whether the target quantity has been achieved for a given
     * ArrayList.
     * 
     * @param ArrayList eligibleSources - the ArrayList containing the sources
     * @return boolean value indicating whether the source quantity is satisfied
     *         or not.
     */
    @Override
    public boolean quantitySatisfied(ArrayList eligibleSources)
    {
        boolean satisfied = false;
        
        if(isAllRequired())
        {
            satisfied = allSatisfied(eligibleSources);
        }
        else
        {
            satisfied = anySatisfied(eligibleSources);
        }
        return satisfied;
    }
    
    /**
     * Called from the method quantitySatisfied(ArrayList).
     * Checks if the sources list satisfies ANY criterion.
     * @param sources list of eligible source items
     * @return
     */
    public boolean anySatisfied(ArrayList sources)
    {
        boolean satisfied = false;

        //holds the quantity achieved for each criterion.
        int qtySatisfied = 0;

        //holds the quantity Any criteria.
        int anyQtySatisfied = 0;
        Object o = null;
        String criterion = null;
        DiscountListEntryIfc sle = null;
        // check if the minimum quantity has been met for each target criterion

        for (Iterator sourceCriteria = criteria(); sourceCriteria.hasNext(); )
        {
            criterion = (String)sourceCriteria.next();
            sle = map.get(criterion);
            //reset qtySatisfied for each iteration of criteria.
            qtySatisfied = 0;

            for (Iterator sourceItems = sources.iterator();sourceItems.hasNext();)
            {
                o = sourceItems.next();
                if(attributesEqual(criterion, (DiscountItemIfc)o))
                {
                    qtySatisfied += 1;
                }
            }
            if (qtySatisfied >= sle.getQuantityRequired())
            {
                //if qtySatisfied satisfies the qty required, increment ANY qty.
                anyQtySatisfied += 1;
            }
        }

        satisfied = anyQtySatisfied >= anyQuantity;
        //Re-check if all the sources belonging to a single ANY criterion have the required ANY quantity
        if(!satisfied)
        {
            // check if the minimum quantity has been met for each target criterion
            for (Iterator sourceCriteria = criteria(); sourceCriteria.hasNext(); )
            {
                criterion = (String)sourceCriteria.next();
                sle = map.get(criterion);
                qtySatisfied = 0;
                for (Iterator sourceItems = sources.iterator();sourceItems.hasNext();)
                {
                    o = sourceItems.next();
                    if(attributesEqual(criterion, (DiscountItemIfc)o))
                    {
                        qtySatisfied += 1;
                    }
                }
                if (qtySatisfied >= (sle.getQuantityRequired() * anyQuantity))
                {
                    satisfied = true;
                    break;
                }
            }
        }

        return satisfied;
    }

    /**
     * Called from the method quantitySatisfied(ArrayList).
     * Checks if the sources list satisfies ALL criterion.
     * @param sources
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean allSatisfied(ArrayList sources)
    {
        boolean satisfied = true;

        //holds the quantity achieved for each criterion.
        int qtySatisfied = 0;
        String criterion = null;
        DiscountListEntryIfc sle = null;
        DiscountItemIfc item = null;

        ArrayList selectedSources = new ArrayList();

        //holds the total selling price of the selected items for each criterion.
        CurrencyIfc thresholdAmount = DomainGateway.getBaseCurrencyInstance();

        //check to see if the minimum quantity has been met for each target criterion
        for (Iterator sourceCriteria = criteria(); sourceCriteria.hasNext(); )
        {
            criterion = (String)sourceCriteria.next();
            sle = map.get(criterion);

            //reset the values of qtySatisfied and thresholdAmount for each iteration.
            qtySatisfied = 0;
            thresholdAmount.setZero();

            for (Iterator sourcesItems = sources.iterator();sourcesItems.hasNext();)
            {
                item = (DiscountItemIfc)sourcesItems.next();
                if(attributesEqual(criterion, item))
                {
                    selectedSources.add(item);
                    if(thresholdType == THRESHOLD_QUANTITY)
                    {
                        qtySatisfied += 1;
                        if(qtySatisfied >= sle.getQuantityRequired())
                            break;
                    }
                    else
                    {
                        thresholdAmount = thresholdAmount.add(item.getSellingPrice());
                        if(thresholdAmount.compareTo(sle.getAmountRequired()) >= 0)
                            break;
                    }
                }
            }

            //even if one criterion fails the requirements, return false.
            if (thresholdType == THRESHOLD_QUANTITY)
            {
                if (qtySatisfied < sle.getQuantityRequired())
                {
                    satisfied = false;
                    break;
                }
            }
            else
            {
                if (thresholdAmount.compareTo(sle.getAmountRequired()) == -1)
                {
                    satisfied = false;
                    break;
                }
            }
        }
        if(satisfied)
        {
            sources.clear();
            sources.addAll(selectedSources);
        }
        return satisfied;
    }

    /**
     * When both the source and target of the discount rule are set to "Class",
     * or when EOLV is enabled, evaluates the eligible sources and targets
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
        boolean satisfied = false;
        String criterion = null;
        Object o = null;
        ArrayList temp = new ArrayList();
        if(!quantitySatisfied(eligibleSources))
        {
            //if the source quantity is not satisfied,  then check if any of the targets meet the
            //source criteria and add the eligible targets to the source bucket until the source 
            //quantity is satisfied.
            for (Iterator i = eligibleTargets.iterator(); i.hasNext();)
            {
                o = i.next();
                if(!satisfied)
                {
                    // for each String in the criteria list
                    for (Iterator p = criteria(); p.hasNext();)
                    {
                        criterion = (String) p.next();
                        if(attributesEqual(criterion, (DiscountItemIfc)o))
                        {
                            eligibleSources.add(o);
                            temp.add(o);
                            if(quantitySatisfied(eligibleSources))
                            {
                                satisfied = true;
                                 break;
                            }
                        }
                    }
                }
            }
            for (Iterator j = temp.iterator(); j.hasNext();)
            {
                o = j.next();
                eligibleTargets.remove(o);
            }
        }
        else
        {
            //See to it that eligibleSources contains only the minimum number of eligible sources 
            //which meet the source criteria.
            for (Iterator i = eligibleSources.iterator(); i.hasNext();)
            {
                o = i.next();
                if(!satisfied)
                {
                    temp.add(o);
                    if(quantitySatisfied(temp))
                    {
                        satisfied = true;
                        break;
                    }

                }
            }
            eligibleSources.clear();
            eligibleSources.addAll(temp);
        }
        return satisfied;
    }
    
    /**
     * Evaluates whether the quantity requirements have been met.
     *
     * @param candidates containing potential sources for this rule.
     * @param selected sources that have been selected for this rule.
     * @return true if all the source criteria for this rule have been met.
     */
    protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected)
    {
    	return evaluateQuantities(candidates, selected, false);
    }

}
