/* ===========================================================================
* Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/RelatedItemLaunchShuttle.java /main/13 2013/02/15 10:23:12 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   02/14/13 - moving DOB from relateditemstation to iteminquiry
 *                         station
 *    yiqzhao   09/26/12 - refactor related item to add cross sell, upsell and
 *                         substitute, remove pick one and pick many
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 1    360Commerce 1.0         12/13/2005 4:47:03 PM  Barry A. Pape   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**

 $Revision: /main/13 $
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
    public static final String revisionNumber = "$Revision: /main/13 $";
    
    protected SaleCargoIfc saleCargo;
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
        saleCargo = (SaleCargoIfc)bus.getCargo();
        
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
        cargo.setAccessFunctionID(saleCargo.getAccessFunctionID());
        cargo.setPasswordRequired(saleCargo.isPasswordRequired());
        cargo.setCustomerInfo(saleCargo.getCustomerInfo());
        cargo.setEmployee(saleCargo.getEmployee());
        cargo.setLineItem(saleCargo.getLineItem());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setPLUItem(saleCargo.getPLUItem());
        cargo.setPrimaryItemSequenceNumber(saleCargo.getLineItem().getLineNumber());
        cargo.setTransaction(saleCargo.getTransaction());

        SaleReturnTransactionIfc[] originalTxns = saleCargo.getOriginalReturnTransactions();
        for (int i = 0; (originalTxns != null) && (i < originalTxns.length); i++)
        {
            cargo.addOriginalReturnTransaction(originalTxns[i]);
        }

        // Record the timeout status in the cargo, so called services will know
        if(cargo instanceof TimedCargoIfc && saleCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)saleCargo).isTimeout());  
        }
        
        cargo.setAddAutoRelatedItem(true);
        
        if ( cargo.getTransaction()!=null && cargo.getTransaction().getAgeRestrictedDOB()!=null )
        {
            cargo.setRestrictedDOB(cargo.getTransaction().getAgeRestrictedDOB());
        }
    }
}
