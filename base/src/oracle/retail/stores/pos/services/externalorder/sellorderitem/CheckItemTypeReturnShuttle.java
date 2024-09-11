/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/CheckItemTypeReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/11 16:05:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.externalorder.sellorderitem;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

import org.apache.log4j.Logger;

public class CheckItemTypeReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -4946444948502840056L;

	/**
    The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger.getLogger(CheckItemTypeLaunchShuttle.class);

	/**
   revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

	protected ItemInquiryCargo itemInquiryCargo = null;

	//----------------------------------------------------------------------
	/**
   Loads cargo from itemcheck service. <P>
   <B>Pre-Condition(s)</B>
   <UL>
   <LI>ItemInquiryCargo in the itemcheck service's bus has been modified as
   appropriate for the type of item it maintains.
   </UL>
   <B>Post-Condition(s)</B>
   <UL>
   <LI>Shuttle has a reference to the ItemInquiryCargo instance in the itemcheck service
   </UL>
   @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public void load(BusIfc bus)
	{
		super.load(bus);

		itemInquiryCargo = (ItemInquiryCargo) bus.getCargo();
	}

    //----------------------------------------------------------------------
    /**
       Loads data into sell order item service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo in the itemcheck service's bus has been modified as
       appropriate for the type of item it maintains.
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo instance of the calling service will be modified to reflect the
       changes made by the itemcheck service
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SellOrderItemCargo cargo = (SellOrderItemCargo) bus.getCargo();

        cargo.setPLUItem(itemInquiryCargo.getPLUItem());
        cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
        cargo.setTransaction((SaleReturnTransactionIfc)itemInquiryCargo.getTransaction());
        cargo.setModifiedFlag(itemInquiryCargo.getModifiedFlag());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CheckItemTypeReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
