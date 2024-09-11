/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/CustomerSurveyReward.java /main/12 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/10/09 - add debugging and allow for completed status since
 *                         transaction may have been saved already by the time
 *                         printing occurs.
 *    cgreene   11/13/08 - deprecate getSurveyText in favor of isSurveyExpected
 *                         and editing Survey.bpt
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:16 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/14 17:44:56  mweis
 *   @scr 5578 Customer Survey / Reward needs to have an interface
 *
 *   Revision 1.2  2004/03/19 21:04:12  mweis
 *   @scr 4025 Use the ISO_DATE datatype with Customer Survey/Reward
 *
 *   Revision 1.1  2004/03/17 20:28:07  mweis
 *   @scr 4025 Automagically determines when to have the customer survey/reward
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;

import org.apache.log4j.Logger;

/**
 * Determines if it is time to enable the printing of the customer survey/reward
 * text on the (customer's) receipt.
 * <p>
 * If the {@link #isSurveyExpected(SessionBusIfc, TenderableTransactionIfc)}
 * method returns <code>true</code>, then it is time to print the
 * survey as part of the receipt.
 * <p>
 * The actual printing of the survey/reward is left to the receipt blueprint
 * printing framework. See {@link BlueprintedReceipt} and Survey_en.bpt.
 * 
 * @version $Revision: /main/12 $
 */
public class CustomerSurveyReward implements CustomerSurveyRewardIfc
{   
    /** The logger to which log messages will be sent */
    private static final Logger logger = Logger.getLogger(CustomerSurveyReward.class);

    /**
     * The list of transaction types that are even capable of printing a customer survey/reward.
     * Typically, these "pre-approved" types are sale related.
     */
    protected static int[] CAPABLE_TRANS_TYPES = {TransactionIfc.TYPE_SALE,
                                                  TransactionIfc.TYPE_ORDER_COMPLETE};
    
    /**
     * The list of transaction status codes that are valid for printing a customer survey/reward.
     * Typically, these are "in-progress" or "completed" status codes.
     */
    protected static int[] CAPABLE_TRANS_STATUS = {TransactionIfc.STATUS_IN_PROGRESS,
                                                   TransactionIfc.STATUS_COMPLETED};
    
    /**
     * Constructor.
     */
    public CustomerSurveyReward()
    {      
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.printing.CustomerSurveyRewardIfc#isSurveyExpected(oracle.retail.stores.foundation.tour.service.SessionBusIfc, oracle.retail.stores.domain.transaction.TenderableTransactionIfc)
     */
    public boolean isSurveyExpected(SessionBusIfc bus, TenderableTransactionIfc trans)
    {
        boolean shouldSurvey = false;
        // Any errors thrown are simply logged, since a survey shouldn't blow us up.
        try
        {
            // 1) Get the parameter manager.
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            
            // 2) See if we are even doing surveys.
            boolean surveying = pm.getBooleanValue(CUSTOMER_SURVEY_REWARD_AVAILABLE).booleanValue();
            if (logger.isDebugEnabled())
                logger.debug("Customer survey turned on: " + surveying);
            if (!surveying)
            {                
                return shouldSurvey; // false
            }

            // 3a) Ensure this type of transaction is capable of displaying a survey.
            if (!verifyCapableTransType(trans.getTransactionType()))
            {    
                return shouldSurvey; // false
            }
            
            // 3b) Ensure the status of the transaction is in our list.
            if (!verifyCapableTransStatus(trans.getTransactionStatus()))
            {
                return shouldSurvey; // false
            }
            
            // 4) See if we are still within the time period of surveying.
            //    Note: If today == start || today == end, we *will* offer the survey/reward.
            EYSDate today = DomainGateway.getFactory().getEYSDateInstance();
            today.initialize(EYSDate.TYPE_DATE_ONLY);
            EYSDate start = EYSDate.getEYSDateFromISO(pm.getStringValue(CUSTOMER_SURVEY_REWARD_START));
            EYSDate end   = EYSDate.getEYSDateFromISO(pm.getStringValue(CUSTOMER_SURVEY_REWARD_END));
            if (logger.isDebugEnabled())
                logger.debug("Customer survey starts "+start+" and ends " + end);
            if (today.before(start) || today.after(end))
            {
                return shouldSurvey; // false
            }
            
            // 5) Get method of surveying: "transaction amount" -or- "every 'n' (sale) transactions".
            String method = pm.getStringValue(CUSTOMER_SURVEY_REWARD_METHOD);
            
            // 6) If "transaction amount":
            //      a) check transaction grand total to see if we met/crossed the threshold
            //      b) if appliable, get the survey text
            if (method.equalsIgnoreCase(METHOD_TRANS_AMOUNT))
            {
                String min = pm.getStringValue(CUSTOMER_SURVEY_REWARD_TRANS_AMOUNT);
                CurrencyIfc minTransAmount = DomainGateway.getBaseCurrencyInstance(min);
                
                CurrencyIfc transAmount = trans.getTransactionTotals().getGrandTotal();
                if (logger.isDebugEnabled())
                    logger.debug("Customer survey minimumAmount: "+minTransAmount + " and this amount is " + transAmount);
                
                int comparison = transAmount.compareTo(minTransAmount);
                if (comparison == CurrencyIfc.GREATER_THAN || comparison == CurrencyIfc.EQUALS)
                {
                    shouldSurvey = true;
                }
            }
            
            // 7) If "every 'n' transactions":
            //      a) increment the receipt counter
            //      b) check to see if we have met/crossed the threshold
            //      c) if applicable: 
            //          -- get the survey text
            //          -- reset the counter
            if (method.equalsIgnoreCase(METHOD_N_NUMBER_TRANS))
            {
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc) bus.getManager(PrintableDocumentManagerIfc.TYPE);
                int count = pdm.getPrintReceiptCount();
                pdm.setPrintReceiptCount(++count);      // increment since we are now contributing
                
                int minNumberTrans = pm.getIntegerValue(CUSTOMER_SURVEY_REWARD_N_TRANS).intValue();
                if (logger.isDebugEnabled())
                    logger.debug("Customer survey num trans: "+ minNumberTrans + " and this is trans " + count);
                
                if (count >= minNumberTrans)
                {
                    shouldSurvey = true;
                    pdm.setPrintReceiptCount(0);  // reset
                }
            }

        }
        catch (Exception e)
        {
            logger.warn("Unable to properly return customer survey/reward text: " + e.getMessage());
        }
        return shouldSurvey;
    }
    
    /**
     * Returns whether the given transaction type is in the list of pre-approved ones.
     * @param transType The given transaction type.
     * @return Whether the given transaction type is in the list of pre-approved ones.
     * @see #CAPABLE_TRANS_TYPES
     */
    protected static boolean verifyCapableTransType (int transType)
    {
        // Unless we find our guy, we assume that we cannot print surveys.
        boolean capable = false;
        
        for (int i = 0; i < CAPABLE_TRANS_TYPES.length; ++i)
        {
            if (transType == CAPABLE_TRANS_TYPES[i])
            {
                capable = true;
                break;           // no need to keep on looking
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Customer survey transaction type "+transType+" allowed: " + capable);
        return capable;
    }
    
    /**
     * Returns whether the given transaction status is in the list of pre-approved ones.
     * @param transStatus The given transaction status.
     * @return Whether the given transaction status is in the list of pre-approved ones.
     * @see #CAPABLE_TRANS_STATUS
     */
    protected static boolean verifyCapableTransStatus (int transStatus)
    {
        // Unless we find our guy, we assume that we cannot print surveys.
        boolean capable = false;

        for (int i = 0; i < CAPABLE_TRANS_STATUS.length; ++i)
        {
            if (transStatus == CAPABLE_TRANS_STATUS[i])
            {
                capable = true;
                break; // no need to keep on looking
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Customer survey transaction status "+transStatus+" allowed: " + capable);
        return capable;
    }
}
