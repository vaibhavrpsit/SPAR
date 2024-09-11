/*===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/sellorderitem/CheckItemTypeLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         06/08/10 - rename mandatoryPrice to externalPrice to be
*                        consistent
* sgu         06/08/10 - add item # & desc to the screen prompt. fix unknow
*                        item screen to disable price and quantity for external
*                        item
* sgu         06/03/10 - refactor AddItemSite
* sgu         06/01/10 - check in after merge
* sgu         06/01/10 - skip UOM flow for external order item
* sgu         06/01/10 - check in order sell item flow
* sgu         05/26/10 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.externalorder.sellorderitem;

import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

import org.apache.log4j.Logger;

public class CheckItemTypeLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    /**
     * generated serial version UID
     */
	private static final long serialVersionUID = -363107166055925584L;

	/**
	 * The sell order item cargo
	 */
	protected SellOrderItemCargo sellOrderItemCargo = null;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(CheckItemTypeLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Loads cargo from sell order item service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>Cargo has set the plu item
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Shuttle has the plu item
     * </UL>
     *
     * @param bus Service Bus
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        sellOrderItemCargo = (SellOrderItemCargo) bus.getCargo();
    }

    /**
     * Loads data into itemvalidate service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * Calling service has set the search criteria for a particular item
     * <LI>
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Cargo will contain the search criteria for a particular item
     * </UL>
     *
     * @param bus Service Bus
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();

        cargo.setRegister(sellOrderItemCargo.getRegister());
        cargo.setPLUItem(sellOrderItemCargo.getPLUItem());
        cargo.setTransaction(sellOrderItemCargo.getTransaction());
        cargo.setStoreStatus(sellOrderItemCargo.getStoreStatus());
        cargo.setOperator(sellOrderItemCargo.getOperator());
        cargo.setCustomerInfo(sellOrderItemCargo.getCustomerInfo());
        cargo.setTenderLimits(sellOrderItemCargo.getTenderLimits());
        cargo.setItemList(sellOrderItemCargo.getItemList());
        cargo.setSalesAssociate(sellOrderItemCargo.getSalesAssociate());
        cargo.skipUOMCheck(true);	// no UOM check for an external order item
        cargo.setExternalPrice(sellOrderItemCargo.getExternalOrderItem().getPrice());

        if (sellOrderItemCargo.getPLUItem() != null && sellOrderItemCargo.getPLUItem().isKitHeader())
        {
            ((ItemKitIfc) cargo.getPLUItem()).setindex(-1);
        }

        cargo.setModifiedFlag(false);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  CheckItemTypeLaunchShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     **/
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

}
