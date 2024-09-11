/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftCardBeanModel.java /rgbustores_13.4x_generic_branch/2 2011/07/26 16:57:39 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:24 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Jan 30 2004 14:14:10   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.3   Dec 23 2003 09:17:22   lzhao
 * deprecate getExpirationDate() and setExpirationDate()
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.2   Dec 08 2003 09:26:34   lzhao
 * remove expiration date
 * 
 *    Rev 1.1   Oct 30 2003 09:49:02   lzhao
 * add reload amount attribute into the model.
 * 
 *    Rev 1.0   Aug 29 2003 16:10:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:48:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:16   msg
 * Initial revision.
 * 
 *    Rev 1.4   Feb 05 2002 16:43:46   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.3   Jan 19 2002 12:15:08   mpm
 * Fixed merge problems.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   Oct 23 2001 11:19:42   cir
 * Added openAmount
 * Resolution for POS SCR-224: Open Amount Gift Card
 * 
 *    Rev 1.0   Sep 21 2001 11:36:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;

/**
 * This class is the data model for GiftCardBean.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class GiftCardBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -2439968130638739243L;

    // File revision number
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Swiping indicator
    protected boolean swiped = false;

    // Scanning indicator
    protected boolean scanned = false;

    // Gift Card Number
    protected String giftCardNumber = null;

    // Gift Card initial balance
    protected BigDecimal giftCardInitialBalance = null;

    // Gift Card amount label
    protected String giftCardAmountLabel;

    // Gift Card amount
    protected BigDecimal giftCardAmount = null;

    // Gift Card status
    protected StatusCode giftCardStatus = null;

    // Gift Card status
    protected boolean openAmount = false;

    // Found the card in gift card inquiry
    protected boolean validInquriy = true;

    /**
     * Constructor
     */
    public GiftCardBeanModel()
    {
    }

    /**
     * Get swiping flag
     * 
     * @return boolean flag
     */
    public boolean isSwiped()
    {
        return swiped;
    }

    /**
     * Sets the swiping flag
     * 
     * @param value boolean
     */
    public void setSwiped(boolean value)
    {
        swiped = value;
    }

    /**
     * Get scanning flag
     * 
     * @return boolean flag
     */
    public boolean isScanned()
    {
        return scanned;
    }

    /**
     * Sets the scanning flag
     * 
     * @param value boolean
     */
    public void setScanned(boolean value)
    {
        scanned = value;
    }

    /**
     * Gets the gift card number.
     * 
     * @return String gift card number
     */
    public String getGiftCardNumber()
    {
        return giftCardNumber;
    }

    /**
     * Sets the gift card number.
     * 
     * @param number the gift card number
     */
    public void setGiftCardNumber(String number)
    {
        giftCardNumber = number;
    }

    /**
     * Gets the gift card amount label.
     * 
     * @return String gift card amount label
     */
    public String getGiftCardAmountLabel()
    {
        return giftCardAmountLabel;
    }

    /**
     * Sets the gift card amount label.
     * 
     * @param label the gift card amount label.
     */
    public void setGiftCardAmountLabel(String label)
    {
        giftCardAmountLabel = label;
    }

    /**
     * Gets the gift card initial balance.
     * 
     * @return BigDecimal gift card initial balance
     */
    public BigDecimal getGiftCardInitialBalance()
    {
        return giftCardInitialBalance;
    }

    /**
     * Sets the gift card initial balance.
     * 
     * @param amount the gift card initial balance
     */
    public void setGiftCardInitialBalance(BigDecimal amount)
    {
        giftCardInitialBalance = amount;
    }

    /**
     * Gets the gift card amount.
     * 
     * @return BigDecimal gift card amount
     */
    public BigDecimal getGiftCardAmount()
    {
        return giftCardAmount;
    }

    /**
     * Sets the gift card amount.
     * 
     * @param amount the gift card amount
     */
    public void setGiftCardAmount(BigDecimal amount)
    {
        giftCardAmount = amount;
    }

    /**
     * Gets the gift card status.
     * 
     * @return String gift card status
     */
    public StatusCode getGiftCardStatus()
    {
        return giftCardStatus;
    }

    /**
     * Sets the gift card status.
     * 
     * @param status the gift card status
     */
    public void setGiftCardStatus(StatusCode status)
    {
        giftCardStatus = status;
    }

    /**
     * Gets the open amount flag.
     * 
     * @return openAmount as boolean
     */
    public boolean getOpenAmount()
    {
        return (openAmount);
    }

    /**
     * Sets the open amount flag.
     * 
     * @param value as boolean
     */
    public void setOpenAmount(boolean value)
    {
        openAmount = value;
    }

    /**
     * Gets the card is valid or not.
     * 
     * @return isCardValid as boolean
     */
    public boolean isValidInquriy()
    {
        return (validInquriy);
    }

    /**
     * Sets the card is found in inquiry or not.
     * 
     * @param value as boolean
     */
    public void setValidInquiry(boolean value)
    {
        validInquriy = value;
    }

    /**
     * Converts to a string representing the data in this Object
     * 
     * @returns string representing the data in this Object
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append("Class: GiftCardBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Swiped         [" + swiped + "]\n");
        buff.append("Scanned        [" + scanned + "]\n");
        buff.append("CardNumber     [" + giftCardNumber + "]\n");
        buff.append("CardAmount     [" + giftCardAmount + "]\n");
        buff.append("CardStatus     [" + giftCardStatus + "]\n");

        return buff.toString();
    }
}
