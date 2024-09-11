/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FinancialTotalsSummaryBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *        issues with tills and CurrencyID.
 *   4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:41 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:05 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/07/30 21:20:49  dcobb
 *  @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *  Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports

import java.util.Vector;

import javax.swing.DefaultListModel;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 * This class handles the data transport for the FinancialTotalsSummaryBean.
 * <P>
 * It has two primary methods: one for converting FinancialTotals data to data
 * for the summary. The other method takes the data and creates a list model
 * for the data.
 * <P>
 * 
 * @see FinancialTotalsSummaryBean, FinancialTotals.
 * @version $KW=@(#); $Ver=pos_4.5.0:15; $EKW;
 * @deprecated as of release 5.0.0
 */
//------------------------------------------------------------------------------
public class FinancialTotalsSummaryBeanModel extends POSBaseBeanModel
{ // begin class FinancialTotalsSummaryBeanModel
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(FinancialTotalsSummaryBeanModel.class);

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:15; $EKW;";
    /**
     * Hold all the tender names associated with pickups; this is temporary
     * variable used to build the model.
     */
    protected transient Vector tenderDesc = new Vector();
    /**
     * Hold all the tender amounts associated with pickups; this is temporary
     * variable used to build the model.
     */
    protected transient Vector tenderAmount = new Vector();
    /**
     * Default List Model
     */
    protected DefaultListModel listModel = new DefaultListModel();

    //---------------------------------------------------------------------
    /**
     * Constructs FinancialTotalsSummaryBeanModel object.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     */
    //---------------------------------------------------------------------
    public FinancialTotalsSummaryBeanModel()
    { // begin FinancialTotalsSummaryBeanModel()
    } // end FinancialTotalsSummaryBeanModel()

    //---------------------------------------------------------------------
    /**
     * Creates list model object from financial totals.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * 
     * @param totals
     *            FinancialTotals object.
     */
    //---------------------------------------------------------------------
    public void createListModel(FinancialTotalsIfc totals)
    { // begin createListModel()
        FinancialCountTenderItemIfc[] enteredItems =
            totals.getCombinedCount().getEntered().getTenderItems();
        FinancialCountTenderItemIfc enteredItem = null;
        FinancialCountTenderItemIfc expectedItem = null;
        FinancialCountIfc fc = totals.getCombinedCount().getExpected();
        FinancialTotalsSummaryEntry ftse = null;
        CurrencyIfc expectedAmt = DomainGateway.getBaseCurrencyInstance();
        String cashText =
            DomainGateway
                .getFactory()
                .getTenderTypeMapInstance()
                .getDescriptor(
                TenderLineItemIfc.TENDER_TYPE_CASH);

        // Add Tenders to the the list model
        for (int i = 0; i < enteredItems.length; i++)
        {
            if (enteredItems[i].isSummary())
            {                
                expectedItem =
                    fc.getSummaryTenderItemByDescriptor(
                        enteredItems[i].getTenderDescriptor());
                expectedAmt.setZero();
                if (expectedItem == null)
                {
                    if (logger.isInfoEnabled())
                        logger.info(
                            "FinancialTotalsSummaryBeanModel.createListModel(); Matching expected tender for "
                                + enteredItems[i].getDescription()
                                + " not found.");
                }
                else
                {
                    expectedAmt = expectedItem.getAmountTotal();
                }

                ftse = new FinancialTotalsSummaryEntry();
                ftse.setType(enteredItems[i].getDescription());
                ftse.setExpected((CurrencyIfc) expectedAmt.clone());
                ftse.setEntered(enteredItems[i].getAmountTotal());
                listModel.addElement(ftse);
            }
        }

        // Add Starting Float to list model
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setCountryCode(DomainGateway.getBaseCurrencyInstance().getCountryCode());
        td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        
        enteredItem =
            totals
                .getStartingFloatCount()
                .getEntered()
                .getSummaryTenderItemByDescriptor(td);
        
        ftse = new FinancialTotalsSummaryEntry();
        ftse.setType("Starting Float");
        ftse.setDisplayExpected(false);
        if (enteredItem == null)
        {
            if (logger.isInfoEnabled())
                logger.info(
                    "FinancialTotalsSummaryBeanModel.createListModel(); Entered Starting Float not found.");
            ftse.getEntered().setZero();
        }
        else
        {
            ftse.setEntered(enteredItem.getAmountTotal());
        }
        listModel.addElement(ftse);

        // Add Ending Float to list model
        enteredItem =
            totals
                .getEndingFloatCount()
                .getEntered()
                .getSummaryTenderItemByDescriptor(td);
        ftse = new FinancialTotalsSummaryEntry();
        ftse.setDisplayExpected(false);
        ftse.setType("Ending Float");
        if (enteredItem == null)
        {
            if (logger.isInfoEnabled())
                logger.info(
                    "FinancialTotalsSummaryBeanModel.createListModel(); Entered Ending Float not found.");
            ftse.getEntered().setZero();
        }
        else
        {
            ftse.setEntered(enteredItem.getAmountTotal());
        }
        listModel.addElement(ftse);

        // Add Loans to list model
        ReconcilableCountIfc[] rc = totals.getTillLoans();
        CurrencyIfc enteredAmt = DomainGateway.getBaseCurrencyInstance();

        for (int y = 0; y < rc.length; y++)
        {
            enteredItem =
                rc[y].getEntered().getSummaryTenderItemByDescriptor(td);
            enteredAmt = enteredAmt.add(enteredItem.getAmountTotal());
        }
        ftse = new FinancialTotalsSummaryEntry();
        ftse.setType("Till Loans");
        ftse.setEntered((CurrencyIfc) enteredAmt.clone());
        ftse.setDisplayExpected(false);
        listModel.addElement(ftse);

        // Add pickups to list model; build the vectors of descriptions and
        // amounts.
        buildPickTenderVectors(totals.getTillPickups());

        if (tenderDesc.size() > 0)
        {
            for (int i = 0; i < tenderDesc.size(); i++)
            {
                ftse = new FinancialTotalsSummaryEntry();
                String desc = (String) tenderDesc.elementAt(i);
                ftse.setType(desc + " Pickups");
                enteredAmt = (CurrencyIfc) tenderAmount.elementAt(i);
                ftse.setEntered((CurrencyIfc) enteredAmt.clone());
                ftse.setDisplayExpected(false);
                listModel.addElement(ftse);
            }
        }
        else
        {
            enteredAmt.setZero();
            ftse = new FinancialTotalsSummaryEntry();
            ftse.setType("Till Pickups");
            ftse.setEntered((CurrencyIfc) enteredAmt.clone());
            ftse.setDisplayExpected(false);
            listModel.addElement(ftse);
        }

    } // end createListModel()

    //---------------------------------------------------------------------
    /**
     * Builds the PickupTender Vectors.
     * <P>
     * 
     * @param ReconcilableCountIfc[]
     *            contains pickup info.
     */
    //---------------------------------------------------------------------
    protected void buildPickTenderVectors(ReconcilableCountIfc[] rc)
    {
        // Interate trough the pickup Reconcilable Count array...
        for (int i = 0; i < rc.length; i++)
        {
            // Get the summary pickup tender items...
            FinancialCountTenderItemIfc[] fcti =
                rc[i].getEntered().getSummaryTenderItems();
            for (int y = 0; y < fcti.length; y++)
            {
                // Get the amount and desciption for each count tender item...
                String desc = fcti[y].getDescription();
                CurrencyIfc amount = fcti[y].getAmountTotal();

                // If the tender does not already appear in the vectors,
                // add new elements.
                int index = tenderDesc.indexOf(desc);
                if (index == -1)
                {
                    tenderDesc.addElement(desc);
                    tenderAmount.addElement(amount);
                }
                else
                    // Otherwise, add the current amount to the accumulator.
                    {
                    CurrencyIfc oAmount =
                        (CurrencyIfc) tenderAmount.elementAt(index);
                    amount = amount.add(oAmount);
                    tenderAmount.setElementAt(amount, index);
                }
            }
        }
    }

    public DefaultListModel getListModel()
    {
        return listModel;
    }

    //---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    } // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
     * FinancialTotalsSummaryBeanModel main method.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>toString() output
     * </UL>
     * 
     * @param String
     *            args[] command-line parameters
     */
    //---------------------------------------------------------------------
    public static void main(String args[])
    { // begin main()
        // instantiate class
        FinancialTotalsSummaryBeanModel c =
            new FinancialTotalsSummaryBeanModel();
        // output toString()
        System.out.println(c.toString());
    } // end main()

} // end class FinancialTotalsSummaryBeanModel
