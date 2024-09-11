/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/InquiryOptionsCargo.java /main/12 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:07 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jan 02 2003 15:04:06   crain
 * Deprecated code list map methods
 * Resolution for 1875: Adding a business customer offline crashes the system
 * 
 *    Rev 1.1   Oct 14 2002 16:09:44   DCobb
 * Added alterations service to item inquiry service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * 
 *    Rev 1.0   Apr 29 2002 15:21:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 17:21:38   baa
 * get department list from reason codes
 * Resolution for POS SCR-1562: Get Department list from Reason Codes, not separate Dept. list.
 *
 *    Rev 1.0   Mar 18 2002 11:33:12   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 05 2002 16:42:26   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   Dec 11 2001 20:47:54   dfh
 * added transaction type
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.2   28 Nov 2001 16:54:18   baa
 *
 * fix cancel behavior
 *
 * Resolution for POS SCR-244: Code Review  changes
 *
 * Resolution for POS SCR-306: 'Cancel' on 'Item Inventory' does not return to calling service
 *
 *
 *
 *    Rev 1.1   25 Oct 2001 17:41:16   baa
 *
 * cross store inventory feature
 *
 * Resolution for POS SCR-230: Cross Store Inventory
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
// Java imports
import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import java.math.BigDecimal;


import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;


public class InquiryOptionsCargo extends AbstractFinancialCargo implements CargoIfc
{
    private static final long serialVersionUID = 3641712801884035460L;

    // The current register
    protected RegisterIfc register = null;
    // The giftcard to add or search for
    protected GiftCardIfc giftcard = null;
    // PLU item to add or search for
    protected PLUItemIfc pluItem = null;
    /**
     * The serial number of item to be added to current transaction.
     */
    protected String itemSerial = null;

    /**
     * This flag indicates whether the item should be added to the transaction.
     * True, if the item should be added to the transaction. False otherwise.
     * Default is true.
     */
    protected boolean modifiedFlag = false;
    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = BigDecimalConstants.ONE_AMOUNT;

    // transaction type of current transaction, if one in progress
    protected int transType = TransactionIfc.TYPE_UNKNOWN;

    /**
     * Transaction
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * Constructs InquiryOptionsCargo object.
     */
    public InquiryOptionsCargo()
    {
    }

    /**
     * Returns the register object.
     * 
     * @return The register object.
     */
    public RegisterIfc getRegister()
    {
        return register;
    }

    /**
     * Sets the register object.
     * 
     * @param value The register object.
     */
    public void setRegister(RegisterIfc value)
    {
        register = value;
    }

    /**
     * Sets the item selected.
     * 
     * @param itemID The selected item.
     */
    public void setPLUItem(PLUItemIfc item)
    {
        pluItem = item;

    }

    /**
     * Returns the item.
     * 
     * @return PLUItemIfc The item.
     */
    public PLUItemIfc getPLUItem()
    {
        return pluItem;
    }

    /**
     * Returns the giftcard.
     * 
     * @return GiftCardIfc The item.
     */
    public GiftCardIfc getGiftCard()
    {
        return giftcard;
    }

    /**
     * Sets the giftcard selected.
     * 
     * @param giftcard The selected giftcard
     */
    public void setGiftCard(GiftCardIfc value)
    {
        giftcard = value;
    }

    /**
     * Returns the item serial number.
     * 
     * @return The item serial number.
     */
    public String getItemSerial()
    {
        return itemSerial;
    }

    /**
     * Sets the item serial number.
     * 
     * @param addedItemSerial The new item serial number.
     */
    public void setItemSerial(String addedItemSerial)
    {
        itemSerial = addedItemSerial;
    }

    /**
     * Returns true if the item should be added to the current transaction.
     * False otherwise.
     * 
     * @return boolean True if the item should be added to the current
     *         transaction. False otherwise.
     */
    public boolean getModifiedFlag()
    {
        return modifiedFlag;
    }

    /**
     * Sets the flag that determines whether the item should be added to the
     * current transaction.
     * 
     * @param value True if the item should be added to the current transaction.
     *            False otherwise.
     */
    public void setModifiedFlag(boolean value)
    {
        modifiedFlag = value;
    }

    /**
     * Gets the item quantity value.
     * 
     * @return BigDecimal value
     */
    public BigDecimal getItemQuantity()
    {
        return itemQuantity;
    }

    /**
     * Sets the Item quantity value.
     * 
     * @param value the item quantity
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    /**
     * Returns the transaction type or TYPE_UNKNOWN, if no transaction in
     * progress.
     * 
     * @return The transaction type or TYPE_UNKNOWN.
     */
    public int getTransactionType()
    {
        return transType;
    }

    /**
     * Sets the transaction type for the current transaction.
     * 
     * @param value The transaction type.
     */
    public void setTransactionType(int value)
    {
        transType = value;
    }

    /**
     * Returns the transaction.
     * 
     * @return The transaction.
     */
    public RetailTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Sets the transaction.
     * 
     * @param value The transaction.
     */
    public void setTransaction(RetailTransactionIfc value)
    {
        transaction = value;
    }
}