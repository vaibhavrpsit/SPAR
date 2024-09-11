/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 02/14/13 - moving DOB from relateditemstation to iteminquiry
 *                      station
 *    yiqzha 09/26/12 - refactor related item to add cross sell, upsell and
 *                      substitute, remove pick one and pick many
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    aariye 02/02/09 - Added RelatedItemLaunchShuttle for Item basket
 *    aariye 01/28/09 - Adding elements for Item Basket
 *    vikini 01/21/09 - Creating Shuttle for launching Related item Station
 *    vikini 01/21/09 - Creating Shuttle for launching Related item Station
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.pos.services.itembasket.BasketDTO;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**

 $Revision: /main/7 $
 **/
//--------------------------------------------------------------------------
public class RelatedItemLaunchShuttle extends FinancialCargoShuttle
{
    /**
     The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.SaleLaunchShuttle.class);

    /**
     revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/7 $";

    protected ModifyTransactionCargo modifyTransactionCargo;
//  ----------------------------------------------------------------------
    /**
     Loads cargo from modifyitem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the selected item
     </UL>
     <B>Post-Condition(s)</B>
     <UL>
     <LI>
     </UL>
     @param  bus     Service Bus
     **/
//  ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        modifyTransactionCargo = (ModifyTransactionCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Loads data into alterations service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve cargo from the child
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        BasketDTO basket = modifyTransactionCargo.getBasketDTO();
        cargo.setAccessFunctionID(modifyTransactionCargo.getAccessFunctionID());

        cargo.setCustomerInfo(modifyTransactionCargo.getCustomerInfo());
        cargo.setEmployee(modifyTransactionCargo.getSalesAssociate());
        cargo.setLineItem(basket.getsaleReturnSpecifiedItem());
        cargo.setOperator(modifyTransactionCargo.getOperator());
        cargo.setPLUItem(basket.getSpecifiedItem());
        cargo.setPrimaryItemSequenceNumber(basket.getSeqNumber());
        cargo.setTransaction(basket.getTransaction());

        SaleReturnTransactionIfc[] originalTxns = modifyTransactionCargo.getOriginalReturnTransactions();
        for (int i = 0; (originalTxns != null) && (i < originalTxns.length); i++)
        {
            cargo.addOriginalReturnTransaction(originalTxns[i]);
        }

        // Record the timeout status in the cargo, so called services will know
        if(cargo instanceof TimedCargoIfc && modifyTransactionCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)modifyTransactionCargo).isTimeout());
        }
        
        cargo.setAddAutoRelatedItem(true);
        
        if ( cargo.getTransaction()!=null && cargo.getTransaction().getAgeRestrictedDOB()!=null )
        {
            cargo.setRestrictedDOB(cargo.getTransaction().getAgeRestrictedDOB());
        }
    }
}
