/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ModifyItemSerialNumberLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       12/17/10 - check in all
 *    sgu       12/17/10 - XbranchMerge sgu_bug-10373675 from
 *                         rgbustores_13.3x_generic_branch
 *    sgu       12/16/10 - add comment
 *    sgu       12/16/10 - rework the logic to check serial number dueplicates
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/15/10 - Added for external order integration.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// Java imports

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

//--------------------------------------------------------------------------
/**
    This shuttle sets up the Return Transaction service to retrieve kit component
    items for manual return.  A temporary transaction is created and initialized
    with tax values using the utility manager.  The transaction is used to
    initialize the kit component line items by adding the kit header plu item.
    The kit component line items from the temporary transaction are
    returned to the POS service and added to the transaction in progress.
**/
//--------------------------------------------------------------------------
public class ModifyItemSerialNumberLaunchShuttle extends FinancialCargoShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = -241836975145308775L;

    /**
     * Item Return Cargo
     */
    protected ReturnTransactionCargo returnCargo = null;

    //----------------------------------------------------------------------
    /**
       Store data from parent service in the shuttle
       <P>
       @param  bus     Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        returnCargo = (ReturnTransactionCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Transfer parent data to child cargo.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();
        cargo.setTransaction(returnCargo.getTransaction());
        cargo.setReturnSaleLineItems(returnCargo.getReturnSaleLineItems());
        cargo.setCustomer(null);
        cargo.setInquiry(null);

        cargo.setRegister(returnCargo.getRegister());
        cargo.setForReturn(true);
        SaleReturnLineItemIfc srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
        srli.setLineNumber(-1);	// set the line number to -1 since it is not added to the transaction
        srli.setPLUItem(returnCargo.getItemFromTransaction(returnCargo.getPLUItemID()));
        cargo.setLineItems(new SaleReturnLineItemIfc[] {srli});
    }
}
