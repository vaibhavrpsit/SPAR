/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 01/03/10 - update header date
 *    aariye 02/19/09 - Added capapbility for Size check and items not
 *                      authorized for sale in basket
 *    aariye 02/02/09 - Added BasketLineItemDTO for ItemBasket feature
 *    aariye 01/28/09 - Adding element for ItemBasket feature
 *    vikini 01/23/09 - OverRide toString method
 *    vikini 01/21/09 - BasketLineItemDTO Checkin for the firt time
 *    vikini 01/21/09 - BasketLineItemDTO creation
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.itembasket;

import java.io.Serializable;
import java.math.BigDecimal;

public class BasketLineItemDTO implements Serializable
{
	private String itemID = null;
	private BigDecimal itemQuantity = null;
	private String sizeCode = null;

	public String getItemID()
	{
		return itemID;
	}

	public void setItemID(String itemID)
	{
		this.itemID = itemID;
	}

	public BigDecimal getItemQuantity()
	{
		return itemQuantity;
	}

	public String getItemSizeCode()
	{
		return sizeCode;
	}

	public void setItemQuantity(BigDecimal itemQuantity)
	{
		this.itemQuantity = itemQuantity;
	}
	public void setItemSize(String sizeCode)
	{
		this.sizeCode = sizeCode;
	}


	public String toString()
	{
		return ("ITem ID: " + itemID + " Quantity : " + itemQuantity);
	}
}
