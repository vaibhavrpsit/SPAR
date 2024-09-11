/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionSendReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/08/24 14:58:51  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.2  2004/08/16 21:26:03  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.1  2004/08/09 19:17:52  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;



import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the transaction level send service cargo 
    to the  transaction service cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionSendReturnShuttle extends FinancialCargoShuttle
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       valid send line items
    **/
    protected SaleReturnLineItemIfc[] items = null;
    /**
       transaction
    **/
    protected RetailTransactionIfc transaction = null;

    //---------------------------------------------------------------------
    /**
       Retrieve from transaction level send service
       <P>
       @param bus the bus being loaded
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve item cargo
        ModifyTransactionCargo mCargo = (ModifyTransactionCargo) bus.getCargo();
        items = mCargo.getItems();
        transaction = mCargo.getTransaction();
    }

    //---------------------------------------------------------------------
    /**
       Copies information to  the ModifyTransaction service.
       @param bus the bus being unloaded
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
        cargo.setTransaction(transaction);
        if (items != null && 
              cargo.getTransaction() != null && (cargo.getTransaction() instanceof SaleReturnTransactionIfc))
        {
            for (int i = 0; i < items.length; i++)
            {
                ((SaleReturnTransactionIfc)cargo.getTransaction()).replaceLineItem(items[i], items[i].getLineNumber());
            }
        }
        cargo.setUpdateParentCargoFlag(true);
    }
    
    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ModifyTransactionSendReturnShuttle (Revision " +
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
