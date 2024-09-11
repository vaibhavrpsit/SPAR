/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      4    360Commerce 1.3         8/2/2006 4:59:54 PM    Brendan W. Farrell
           Create a change tender during a depletion of a gift card.
      3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:52 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:47 PM  Robert Pearse
     $
     Revision 1.5  2004/09/27 22:32:04  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.4  2004/04/28 15:46:37  blj
     @scr 4603 - Fix gift card change due defects.

     Revision 1.3  2004/02/12 16:48:26  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.3   Nov 17 2003 13:21:34   bwf
 * Updated error retry.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.2   Nov 14 2003 16:38:22   epd
 * added category attribute
 *
 *    Rev 1.1   Nov 14 2003 13:44:08   bwf
 * Added retry count.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 04 2003 11:19:44   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 30 2003 20:46:10   epd
 * Initial revision.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.tenderauth;

import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

/**
 *  Tender Authorization cargo
 */
public class MAXTenderAuthCargo extends AbstractFinancialCargo
{
    /* The tender that is currently being authorized */
    protected TenderADOIfc currentAuthTender;

    /* retry count */
    protected int retryCount = 0;

    /** The category of tenders being validated */
    protected TenderLineItemCategoryEnum tenderCategory;

    /** Amount of gift card depletion **/
    protected String giftCardDepletedAmount = null;

    /** flag to indicate whether gift card is approved or not */
    protected boolean isGiftCardApproved =  false;

    /**
     * @return
     */
    public TenderADOIfc getCurrentAuthTender()
    {
        return currentAuthTender;
    }

    /**
     * @param currentAuthTender
     */
    public void setCurrentAuthTender(TenderADOIfc currentAuthTender)
    {
        this.currentAuthTender = currentAuthTender;
    }

    //----------------------------------------------------------------------
    /**
        This method gets the retry count.
        @return
    **/
    //----------------------------------------------------------------------
    public int getRetryCount()
    {
        return retryCount;
    }

    //----------------------------------------------------------------------
    /**
        This method increments the retry count.
        @param retryCount
    **/
    //----------------------------------------------------------------------
    public void incrementRetryCount()
    {
        this.retryCount++;
    }

    //----------------------------------------------------------------------
    /**
        This method resets the retry count.
        @param retryCount
    **/
    //----------------------------------------------------------------------
    public void resetRetryCount()
    {
        this.retryCount = 0;
    }

    /**
     * @return
     */
    public TenderLineItemCategoryEnum getTenderCategory()
    {
        return tenderCategory;
    }

    /**
     * @param tenderCategory
     */
    public void setTenderCategory(TenderLineItemCategoryEnum tenderCategory)
    {
        this.tenderCategory = tenderCategory;
    }

    /**
     * This methods gets the gift card depleted amount.
     * @return
     */
    public String getGiftCardDepletedAmount()
    {
        return giftCardDepletedAmount;
    }

    /**
     * This method sets the gift card depleted amount.
     * @param giftCardDepletedAmount
     */
    public void setGiftCardDepletedAmount(String giftCardDepletedAmount)
    {
        this.giftCardDepletedAmount = giftCardDepletedAmount;
    }

	/**
	 * Return the gift card approved flag
	 * @return
	 */
	public boolean isGiftCardApproved() {
		return isGiftCardApproved;
	}

	/**
	 * Set the gift card approved flag
	 * @param isGiftCardApproved
	 */
	public void setGiftCardApproved(boolean isGiftCardApproved) {
		this.isGiftCardApproved = isGiftCardApproved;
	}

}
