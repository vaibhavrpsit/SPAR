/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/tenderauth/MAXItemActivationRequest.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.manager.tenderauth;

import java.io.Serializable;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public class MAXItemActivationRequest implements MAXTenderAuthConstantsIfc, Serializable {
	
	 static final long serialVersionUID = 4283153221233572173L;

	    protected int       actionCode  = NO_ACTION;
	    protected int       itemType    = GIFT_CARD;      // use types defined in TenderAuthConstantsIfc
	    protected EYSDate   timeStamp   = DomainGateway.getFactory().getEYSDateInstance();
	    protected GiftCardIfc  giftCard    = null;
	    protected String  transactionID    = null;
	    protected int requestType          = 0;


	    //---------------------------------------------------------------------
	    /**
	        Returns the requestType.  <P>
	        @return String requestType
	    **/
	    //---------------------------------------------------------------------
	/*    public int getRequestType()
	    {
	        return ITEM_ACTIVATION;
	    }
	*/


	    //---------------------------------------------------------------------
	    /**
	        Returns the itemType.  <P>
	        @return String itemType
	    **/
	    //---------------------------------------------------------------------
	    public int getItemType()
	    {
	        return itemType;
	    }

	    //---------------------------------------------------------------------
	    /**
	        Sets the itemType value.
	        @param type String
	    **/
	    //---------------------------------------------------------------------
	    public void setItemType(int type)
	    {
	        itemType = type;
	    }

	    //---------------------------------------------------------------------
	    /**
	        Returns the timeStamp.  <P>
	        @return EYSDate timeStamp
	    **/
	    //---------------------------------------------------------------------
	    public EYSDate getTimeStamp()
	    {
	        return timeStamp;
	    }

	    //-------------------------------------------------------------------------
	    /**
	        Sets the activation date.
	        @param date time stamp
	    **/
	    //-------------------------------------------------------------------------
	    public void setTimeStamp(EYSDate date)
	    {
	        timeStamp = date;
	    }

	    //-------------------------------------------------------------------------
	    /**
	        Sets the gift card.
	        @param card GiftCard
	    **/
	    //-------------------------------------------------------------------------
	    public void setGiftCard(GiftCardIfc card)
	    {
	        giftCard = card;
	    }

	    //---------------------------------------------------------------------
	    /**
	        Returns the gift card.
	        @return GiftCard
	    **/
	    //---------------------------------------------------------------------
	    public GiftCardIfc getGiftCard()
	    {
	        return giftCard;
	    }

	    // KLM: Adding this method in support of pulling in the Bose
	    // ISD work from services
	    //
	    /**
	     * @return
	     */
	    public String getTransactionID()
	    {
	        return transactionID;
	    }

	    // KLM: Adding this method in support of pulling in the Bose
	    // ISD work from services
	    //
	    /**
	     * @param string
	     */
	    public void setTransactionID(String string)
	    {
	        transactionID = string;
	    }

	    //---------------------------------------------------------------------
	    /**
	        Returns attribute descriptor String  <P>
	        @return String formatted attribute descriptor
	    **/
	    //---------------------------------------------------------------------
	    public String toString()
	    {
	        StringBuffer values = new StringBuffer("\n");

	        values.append("Request Type:            [" + requestType + "]\n");
	        values.append("Item Type:               [" + itemType + "]\n");
	        values.append("Action Code:             [" + actionCode + "]\n");

	        if (timeStamp != null)
	        {
	            values.append("Time Stamp:              [" + timeStamp + "]\n");
	        }
	        else
	        {
	            values.append("Time Stamp:              [NOT SET]\n");
	        }

	        values.append("Gift Card:");

	        if (giftCard != null)
	        {
	            values.append(giftCard);
	        }
	        else
	        {
	            values.append("               [null]\n");
	        }

	        return super.toString() + values.toString();
	    }
	    /**
	     * Returns the actionCode.
	     * @return int
	     */
	    public int getActionCode()
	    {
	        return actionCode;
	    }

	    /**
	     * Sets the actionCode.
	     * @param actionCode The actionCode to set
	     */
	    public void setActionCode(int actionCode)
	    {
	        this.actionCode = actionCode;
	    }

}
