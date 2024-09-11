/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/RelatedItemLaunchShuttle.java /main/2 2013/02/15 10:23:11 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/14/13 - moving DOB from relateditemstation to iteminquiry
*                        station
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/21/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**

 $Revision: /main/2 $
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
    public static final String revisionNumber = "$Revision: /main/2 $";
    
    protected ItemCargo itemCargo;
    

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
        itemCargo = (ItemCargo)bus.getCargo();
        
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
    
       	cargo.setAccessFunctionID(itemCargo.getAccessFunctionID());
        cargo.setCustomerInfo(itemCargo.getCustomerInfo());

        cargo.setLineItem(itemCargo.getItem());
        cargo.setOperator(itemCargo.getOperator());
        cargo.setPLUItem(itemCargo.getPLUItem());
        cargo.setPrimaryItemSequenceNumber(itemCargo.getItem().getLineNumber());
        cargo.setTransaction((SaleReturnTransactionIfc)itemCargo.getTransaction());
        if(cargo instanceof TimedCargoIfc && itemCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)itemCargo).isTimeout());  
        }
        cargo.setAddAutoRelatedItem(false);
        
        if ( cargo.getTransaction()!=null && cargo.getTransaction().getAgeRestrictedDOB()!=null )
        {
            cargo.setRestrictedDOB(cargo.getTransaction().getAgeRestrictedDOB());
        }
    }
}
