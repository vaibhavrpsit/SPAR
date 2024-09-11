/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/itembasket/ItemBasketLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aariyer   03/18/09 - For the list of departments being diplayed on the
 *                         ItemNotFound screen
 *    aariyer   02/02/09 - Added files for Item Basket feature
 *    aariyer   01/28/09 - Adding elemts for Item Basket Feature
 *    vikini    01/21/09 - Setting the ItembasketCargo
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.itembasket;


import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

/**
 * This shuttle carries the required contents from
 * Modify Transaction site to Itembasket Site. <P>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */

public class ItemBasketLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3393854184142666999L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        class name constant
    **/
    public static final String SHUTTLENAME = "ItemBasketLaunchShuttle";

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.itembasket.ItemBasketLaunchShuttle.class);


    SaleReturnTransactionIfc saleReturnTrn = null;

    ModifyTransactionCargo cargo = null;


    public void load(BusIfc bus)
    {
    	cargo = (ModifyTransactionCargo)bus.getCargo();
    	saleReturnTrn = (SaleReturnTransactionIfc)cargo.getTransaction();
    }

    //--------------------------------------------------------------------------
    /**
        Copies information to the cargo used in the Item Basket service. <P>
        @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
    	ItemBasketCargo itembsktCargo = (ItemBasketCargo)bus.getCargo();
        itembsktCargo.setEmployee(cargo.getSalesAssociate());
        itembsktCargo.setOperator(cargo.getOperator());
        itembsktCargo.setRegister(cargo.getRegister());
        itembsktCargo.setStoreStatus(cargo.getStoreStatus());
        itembsktCargo.setTenderLimits(cargo.getTenderLimits());
        itembsktCargo.setStoreID(cargo.getOperator().getStoreID());
        saleReturnTrn = (SaleReturnTransactionIfc)cargo.getTransaction();
        itembsktCargo.setTransaction(saleReturnTrn);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return revisionNumber;
    }
}
