/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnOptionsCargo.java /main/17 2014/07/17 15:09:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/17/14 - Move same original transaction check to utility
 *                         class and make regular transaction and order
 *                         transaction call the same method.
 *    mchellap  05/23/14 - Changes for MPOS returns
 *    sgu       04/24/14 - update logic to get returnable quantity
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Pre code reveiw clean up.
 *    jswan     05/11/10 - Returns flow refactor: deprecated datamember
 *                         numberTypeText and access methods; add transactionID
 *                         and access methods.
 *    abondala  01/03/10 - update header date
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 * ===========================================================================
 *
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:52 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:54 PM  Robert Pearse
 * $
 * Revision 1.7  2004/07/27 19:45:40  jdeleau
 * @scr 6305 Flow corrections on return without a receipt for linked customer.
 *
 * Revision 1.6  2004/06/25 15:32:16  cdb
 * @scr 4286 Updated flow of Returns with customer required when offline.
 *
 * Revision 1.5  2004/02/16 13:36:40  baa
 * @scr  3561 returns enhancements
 *
 * Revision 1.4  2004/02/13 14:02:48  baa
 * @scr 3561 returns enhancements
 * Revision 1.3 2004/02/12 16:51:52 mcs
 * Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:52:25 rhafernik @scr 0 Log4J conversion and code
 * cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:20 cschellenger updating to pvcs
 * 360store-current
 *
 *
 *
 * Rev 1.3 Dec 29 2003 15:36:22 baa return enhancements
 *
 * Rev 1.2 Dec 17 2003 11:21:12 baa return enhancements Resolution for 3561:
 * Feature Enhacement: Return Search by Tender
 *
 * Rev 1.1 08 Nov 2003 01:43:02 baa cleanup -sale refactoring
 *
 * Rev 1.0 Aug 29 2003 16:06:22 CSchellenger Initial revision.
 *
 * Rev 1.5 Jul 03 2003 10:23:40 jgs Added code to support Driver's licence
 * validation. Resolution for 1874: Add Driver's License Validation to Return
 * Prompt for ID
 *
 * Rev 1.4 Apr 09 2003 14:44:12 HDyer Cleanup from code review. Resolution for
 * POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 * Rev 1.3 Jan 13 2003 14:33:34 RSachdeva Replaced
 * AbstractFinancialCargo.getCodeListMap() by
 * UtilityManagerIfc.getCodeListMap() Resolution for POS SCR-1907: Remove
 * deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 * Rev 1.2 Dec 13 2002 15:55:06 HDyer Added methods to facilitate populating
 * personal id entry screen during return. Resolution for POS-SCR 1854: Return
 * Prompt for ID feature for POS 6.0
 *
 * Rev 1.1 Aug 15 2002 17:11:46 jriggins Removed hardcoded string in favor of
 * extracting from the bundle. Resolution for POS SCR-1740: Code base
 * Conversions
 *
 * Rev 1.0 Apr 29 2002 15:05:16 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:46:32 msg Initial revision.
 *
 * Rev 1.3 Feb 05 2002 16:43:24 mpm Modified to use IBM BigDecimal. Resolution
 * for POS SCR-1121: Employ IBM BigDecimal
 *
 * Rev 1.2 Nov 28 2001 12:33:28 blj Removed unnecessary code. Resolution for
 * POS SCR-237: Gift Receipt Feature
 *
 * Rev 1.1 Nov 27 2001 18:06:28 blj Updated to allow return by gift receipt
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 * Rev 1.0 Sep 21 2001 11:25:30 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:12:46 msg header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.returns.returnoptions;

// java imports
import java.util.ArrayList;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnExternalOrderItemsCargoIfc;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.utility.TransactionUtility;

//--------------------------------------------------------------------------
/**
 * Cargo for the Return Options service.
 */
//--------------------------------------------------------------------------
public class ReturnOptionsCargo
extends ReturnItemCargo
implements ReturnExternalOrderItemsCargoIfc
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 1160495090778369665L;
    
    /**
     * Return Transaction
     */
    protected SaleReturnTransactionIfc transaction = null;
    /**
     * The text on the Receipt and Other Number buttons will be used later
     * @deprecated in 13.3 no longer used.
     */
    protected String numberTypeText = null;

    /**
     * CustomerIfc object
     */
    protected CustomerIfc customer = null;


    /**
     * Indicates the current override is for Drivers License validation
     */
    protected boolean drivesLicenceValidationOverride = false;
    /**
     * Indicates the current override is for Drivers License validation
     */
    protected boolean customerInfoCollected = false;
    /**
     * Number type receipt bundle tag.
     */
    public static final String NUMBER_TYPE_RECEIPT_TAG = "NumberTypeReceipt";
    /**
     * Number type receipt bundle tag.
     */
    public static final String NUMBER_TYPE_RECEIPT_TEXT = "receipt";
    /**
     * True if Must Link flow to be followed.
     */
    public boolean customerMustLink = false;

    /**
     * Track whether the the user attempted to link the customer.  If
     * they did and cancel, an error dialog appears, otherwise none appears.
     */
    public boolean triedLinkingCustomer = false;

    /** Holds the transaction ID entered in the returns options screen */
    protected String transactionIDString = null;
    
    /**
     * Code List for ID_TYPES
     */
    protected CodeListIfc idTypes = null;

    //----------------------------------------------------------------------
    /**
     * Class Constructor.
     * <p>
     * Initializes the reason code list for item returns.
     * <P>
     */
    //----------------------------------------------------------------------
    public ReturnOptionsCargo()
    {
    }

    //----------------------------------------------------------------------
    /**
     * Returns the numberTypeText.
     * <P>
     *
     * @return String
     * @deprecated in 13.3 no longer used
     */
    //----------------------------------------------------------------------
    public String getNumberTypeText()
    {
        // If numberTypeText is null load the default value.
        if (numberTypeText == null)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc) Gateway.getDispatcher().getManager(
                    UtilityManagerIfc.TYPE);
            numberTypeText =
                utility.retrieveText(
                    POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                    NUMBER_TYPE_RECEIPT_TAG,
                    NUMBER_TYPE_RECEIPT_TEXT);
        }

        return numberTypeText;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the numberTypeText.
     * @param value
     */
    //----------------------------------------------------------------------
    public void setNumberTypeText(String value)
    {
        numberTypeText = value;
    }

    //---------------------------------------------------------------------
    /**
     * Retrieves customer.
     * <P>
     *
     * @return CustomerIfc
     */
    //---------------------------------------------------------------------
    public CustomerIfc getCustomer()
    {
        return (customer);
    }

    //---------------------------------------------------------------------
    /**
     * Sets customer.
     * @param value the customer
     */
    //---------------------------------------------------------------------
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the drivesLicenceValidationOverride.
     *
     * @return boolean
     */
    //----------------------------------------------------------------------
    public boolean isDrivesLicenceValidationOverride()
    {
        return drivesLicenceValidationOverride;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the drivesLicenceValidationOverride.
     *
     * @param drivesLicenceValidationOverride
     *            The drivesLicenceValidationOverride to set
     */
    //----------------------------------------------------------------------
    public void setDrivesLicenceValidationOverride(boolean drivesLicenceValidationOverride)
    {
        this.drivesLicenceValidationOverride = drivesLicenceValidationOverride;
    }

    //----------------------------------------------------------------------
    /**
     * Returns True if Must Link flow to be followed.
     *
     * @return True if Must Link flow to be followed
     */
    //----------------------------------------------------------------------
    public boolean isCustomerMustLink()
    {
        return customerMustLink;
    }

    //----------------------------------------------------------------------
    /**
     * Sets if Must Link flow to be followed.
     *
     * @param customerMustLink True if Must Link flow to be followed
     */
    //----------------------------------------------------------------------
    public void setCustomerMustLink(boolean customerMustLink)
    {
        this.customerMustLink = customerMustLink;
    }


    //----------------------------------------------------------------------
    /**
     * Returns the customerInfoCollected.
     *
     * @return boolean
     */
    //----------------------------------------------------------------------
    public boolean isCustomerInfoCollected()
    {
        return customerInfoCollected;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the customerInfoCollected.
     *
     * @param customerInfoCollected
     *            The customerInfoCollected to set
     */
    //----------------------------------------------------------------------
    public void setCustomerInfoCollected(boolean customerInfoCollected)
    {
        this.customerInfoCollected = customerInfoCollected;
    }

    /**
     * Set whether or not the operator has attempted to link this return object
     * a customer
     *
     * @param value
     */
    public void setTriedLinkingCustomer(boolean value)
    {
        this.triedLinkingCustomer = value;
    }

    /**
     * Get whether or not the operator has attempted to link this return object
     * to a customer.
     *
     *  @return
     */
    public boolean getTriedLinkingCustomer()
    {
        return this.triedLinkingCustomer;
    }
    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String(
                    "Class:  ReturnOptionsCargo (Revision "
                    + getRevisionNumber()
                    + ")"
                    + hashCode());
        return (strResult);
    } // end toString()

    /**
     * @return the idTypes
     */
    public CodeListIfc getIdTypes()
    {
        return idTypes;
    }

    /**
     * @param idTypes the idTypes to set
     */
    public void setIdTypes(CodeListIfc idTypes)
    {
        this.idTypes = idTypes;
    }

    /**
     * @return Returns the transactionIDString.
     */
    public String getTransactionIDString()
    {
        return transactionIDString;
    }

    /**
     * @param transactionIDString The transactionIDString to set.
     */
    public void setTransactionIDString(String transactionIDString)
    {
        this.transactionIDString = transactionIDString;
    }
    
    /**
     * Add a transaction to the array of transactions on which items have been returned.
     * @param transaction SaleReturnTransactionIfc
     */
    public boolean isTransactionInOriginalTransactionList(SaleReturnTransactionIfc transaction)
    {
        boolean inArray = false;
        if (originalReturnTransactions != null)
        {
            for(SaleReturnTransactionIfc transFromList: originalReturnTransactions)
            {
                if (TransactionUtility.isOfSameOriginalReturnTransaction(transFromList, transaction))
                {
                    inArray = true;
                    break;
                }
            }
        }

        return inArray;
    }
    
    /**
     * This method determines if all return items are from a transaction.
     * @return true if all return items are from a transaction.
     */
    public boolean areAllItemsFromTransaction()
    {
        boolean fromTransaction = true;
        
        // Iterate through the list of return items to verify that
        // all items come from a transaction.
        if (getReturnData() != null)
        {
            ReturnItemIfc[] returnItems = getReturnData().getReturnItems();
            if (returnItems != null)
            {
                for(ReturnItemIfc returnItem: returnItems)
                {
                    if (!returnItem.isFromRetrievedTransaction())
                    {
                        fromTransaction = false;
                        break;
                    }
                }
            }
        }
        
        return fromTransaction;
    }

    /**
     * Get the tender line items from the original transaction(s)
     * @return an array of the TenderLineItemIfc objects.
     */
    public TenderLineItemIfc[] getOriginalTenderLineItemsArray()
    {
        TenderLineItemIfc[] tenderArray = new TenderLineItemIfc[0];
        
        if (isExternalOrder() && getOriginalExternalOrderReturnTransactions() != null)
        {
            ArrayList<TenderLineItemIfc> tenders = new ArrayList<TenderLineItemIfc>();
            for(SaleReturnTransactionIfc trans: getOriginalExternalOrderReturnTransactions())
            {
                for (TenderLineItemIfc tender: trans.getTenderLineItems())
                {
                    tenders.add(tender);
                }
                tenders.toArray(tenderArray);
            }
        }
        else
        if (getOriginalTransaction() != null && getOriginalTransaction().getReturnTenderElements() != null)
        {
            tenderArray = getOriginalTransaction().getTenderLineItems();
        }
        
        return tenderArray;
    }
}
