/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcard/GiftCardCargo.java /main/12 2013/05/22 10:41:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/21/13 - added GIFT_CARD_OPTIONS_SCREEN to be used when
 *                         appropriate instead of the POPUPMENU for same
 *    asinton   03/30/12 - implemented giftcard issue
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         4/25/2007 8:52:27 AM   Anda D. Cadar   I18N
 *       merge
 *       
 *  3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:53 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse   
 *
 * Revision 1.10  2004/06/24 15:31:38  blj
 * @scr 5185 - Had to update gift card credit to get Amount from the tenderAttributes
 *
 * Revision 1.9  2004/04/22 20:52:17  epd
 * @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 * Revision 1.8  2004/04/14 20:10:26  lzhao
 * @scr  3872 Redeem, change gift card request type from String to in.
 *
 * Revision 1.7  2004/03/16 18:30:48  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.6  2004/02/19 04:07:05  blj
 * @scr 3284 - per code review findings.
 *
 * Revision 1.5  2004/02/12 16:50:20  mcs
 * Forcing head revision
 *
 * Revision 1.4  2004/02/12 16:44:49  blj
 * @scr 3824
 *
 * Revision 1.3  2004/02/12 16:42:03  blj
 * @scr 3824 - modified cargo and added a requestType attribute
 * and get/set methods.
 *
 * Revision 1.2  2004/02/11 21:49:49  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Jan 29 2004 12:00:40   blj
 * added gift card refund issue.
 * 
 *    Rev 1.2   Dec 16 2003 10:25:02   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Nov 26 2003 09:19:16   lzhao
 * use methods in utility, cleanup.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Oct 30 2003 11:02:44   lzhao
 * Initial revision.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcard;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;

/**
 * This class provides the cargo for the gift card service. <P>
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class GiftCardCargo extends SaleCargo
{
    /**
     * gift card object reference
     */
    protected GiftCardIfc giftCard = null;

    /**
     * The index of the line item which is activating
     */
    protected int lineItemCount    = -1;
    
    /**
     * The action code for gift card
     */
    protected int actionCode =  -1;
      
    /** 
     * The current gift card amount
     */
    protected CurrencyIfc amount = null;
    
    /**
     * This boolean is used for navigational purposes
     */
    protected boolean displayedGetAmountScreen = true;

    /**
     * Funding selection only flag.  This flag is used when using the gift card
     * service only for getting the Issue or Reload selection.  Default value
     * is false.
     */
    protected boolean fundingSelectionOnly = false;

    /**
     * Handle to the EncipheredCardData instance.
     */
    protected EncipheredCardDataIfc cardData = null;

    /**
     * flag to indicate if giftCard options screen should be shown
     * instead of the gift card options popup menu.  Default is false.
     * @since 14.0
     */
    protected boolean giftCardOptionScreen = false;

    /**
     * Gets the gift card reference
     * @return GiftCardIfc
     */
    public GiftCardIfc getGiftCard()
    {
        return giftCard;
    }
          
    /**
     Gets the Gift Card amount
     @return CurrencyIfc
     **/
    public CurrencyIfc getGiftCardAmount()
    {
        return amount;
    }

    /**
     Sets the Gift Card amount
     @param value CurrencyIfc
     **/
    public void setGiftCardAmount(CurrencyIfc value)
    {
        amount = value;
    }

    /**
        Sets the gift card reference
        @param value GiftCardIfc
    **/
    public void setGiftCard(GiftCardIfc value)
    {
        giftCard = value;
    }

    /**
        Gets the line item counter. 
        @return index of in the tender line which is doing activation  
    **/
    public int getLineItemCounter()
    {
        return lineItemCount;
    }

    /**
        Sets the line item counter
        @param inde index of in the tender line which is doing activation 
    **/
    public void setLineItemCounter(int index)
    {
        lineItemCount = index;
    }

    /**
     Gets the action code. 
     @return the actionCode  
     **/
    public int getActionCode()
    {
        return this.actionCode;
    }
    
    /**
     Sets the actionCode.
     @param actionCode
     **/
    public void setRequestType(int actionCode)
    {
        this.actionCode = actionCode;
    }
    
    /**
     * @return Returns the displayedGetAmountScreen.
     */
    public boolean isDisplayedGetAmountScreen()
    {
        return displayedGetAmountScreen;
    }

    /**
     * @param displayedGetAmountScreen The displayedGetAmountScreen to set.
     */
    public void setDisplayedGetAmountScreen(boolean displayedGetAmountScreen)
    {
        this.displayedGetAmountScreen = displayedGetAmountScreen;
    }

    /**
     * Returns the fundingSelectionOnly value.
     * @return the fundingSelectionOnly
     */
    public boolean isFundingSelectionOnly()
    {
        return fundingSelectionOnly;
    }

    /**
     * Sets the fundingSelectionOnly value.
     * @param fundingSelectionOnly the fundingSelectionOnly to set
     */
    public void setFundingSelectionOnly(boolean fundingSelectionOnly)
    {
        this.fundingSelectionOnly = fundingSelectionOnly;
    }

    /**
     * @return the cardData
     */
    public EncipheredCardDataIfc getCardData()
    {
        return cardData;
    }

    /**
     * @param cardData the cardData to set
     */
    public void setCardData(EncipheredCardDataIfc cardData)
    {
        this.cardData = cardData;
    }

    /**
     * Returns the <code>giftCardOptionScreen</code> value.
     * @return the giftCardOptionScreen
     * @since 14.0
     */
    public boolean isGiftCardOptionScreen()
    {
        return giftCardOptionScreen;
    }

    /**
     * Sets the <code>giftCardOptionScreen</code> value.
     * @param giftCardOptionScreen the giftCardOptionScreen to set
     * @since 14.0
     */
    public void setGiftCardOptionScreen(boolean giftCardOptionScreen)
    {
        this.giftCardOptionScreen = giftCardOptionScreen;
    }

}

