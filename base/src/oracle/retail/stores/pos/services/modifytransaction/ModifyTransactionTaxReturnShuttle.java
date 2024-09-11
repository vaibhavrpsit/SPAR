/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionTaxReturnShuttle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
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
 *   Revision 1.4  2004/02/24 16:21:30  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.tax.ModifyTransactionTaxCargo;

//--------------------------------------------------------------------------
/**
    Return shuttle class for ModifyTransactionTax service. <P>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionTaxReturnShuttle extends FinancialCargoShuttle
{   // begin class ModifyTransactionTaxReturnShuttle
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionTaxReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/13 $";

    /**
       the transaction
    **/

    protected ModifyTransactionTaxCargo modifyTransactionTaxCargo =null;

    //---------------------------------------------------------------------
    /**
       Loads from child (ModifyTransactionTax) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransactionTaxCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo loaded
       </UL>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve cargo
        modifyTransactionTaxCargo = (ModifyTransactionTaxCargo) bus.getCargo();



    }
    //---------------------------------------------------------------------
    /**
       Unloads to parent (ModifyTransaction) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransaction class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo unloaded
       </UL>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        RetailTransactionIfc transaction = modifyTransactionTaxCargo.getTransaction();

        // retrieve parent cargo
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        // propagate a new transaction back to the parent service
        if (transaction != null)
        {
            cargo.setTransaction(transaction);
            transaction.setCustomer(modifyTransactionTaxCargo.getCustomer());
            if (transaction.getCustomer() != null)
            {
               if (logger.isInfoEnabled()) logger.info(
                           "Customer: " + modifyTransactionTaxCargo.getCustomer().toString() + "");
               cargo.setUpdateParentCargoFlag(true);
            }

            // if dirty flag set, perform updates
            if (modifyTransactionTaxCargo.getDirtyFlag())
            {
               cargo.updateTransactionTax(bus, modifyTransactionTaxCargo.getTransactionTax(),
                                          modifyTransactionTaxCargo.getUpdateAllItemsFlag());
               // set flag to update the parent cargo of Modify Transaction
               cargo.setUpdateParentCargoFlag(true);
               if (logger.isInfoEnabled()) logger.info(
                        "ModifyTransactionTaxReturnShuttle unload:  TransactionTaxIfc:  " + transaction.getTransactionTax().toString() + "\nTransaction tax total: " + transaction.getTransactionTotals().getTaxTotal().toString() + "\nUpdate parent flag:  " + new Boolean(cargo.getUpdateParentCargoFlag()).toString() + "");
            }

        }
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of the object. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ModifyTransactionTaxReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());

        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Returns the revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}   // end class ModifyTransactionTaxReturnShuttle

