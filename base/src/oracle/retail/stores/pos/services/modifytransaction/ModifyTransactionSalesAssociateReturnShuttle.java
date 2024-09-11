/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionSalesAssociateReturnShuttle.java /main/14 2012/09/12 11:57:10 blarsen Exp $
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
 *   Revision 1.5  2004/07/28 16:15:39  rsachdeva
 *   @scr 4865 Transaction Sales Associate
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
 *    Rev 1.0   Aug 29 2003 16:02:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 11 2003 13:17:08   baa
 * remove usage of  deprecated EployeeIfc methods get/setName
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 *
 *    Rev 1.0   Apr 29 2002 15:14:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:30:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.lang.reflect.Field;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifytransaction.salesassociate.ModifyTransactionSalesAssociateCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    Return shuttle class for ModifyTransactionSalesAssociate service.
    <P>
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionSalesAssociateReturnShuttle extends FinancialCargoShuttle
{   // begin class ModifyTransactionSalesAssociateReturnShuttle
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionSalesAssociateReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/14 $";

    /**
       vector of items from sale return transaction
    **/
    protected Vector lineItems;

    /**
       dirty flag (indicates update needs to be performed)
    **/
    protected boolean dirtyFlag = false;

    /**
       new sales associate class
    **/
    protected EmployeeIfc newSalesAssociate;

    /**
       retail transaction
    **/
    protected RetailTransactionIfc transaction;

    /**
       update-all-items flag
    **/
    protected boolean updateAllItemsFlag = true;
    /**
       sales associate set using modify transaction sales associate
    **/
    protected boolean salesAssociateAlreadySet = false;

    //---------------------------------------------------------------------
    /**
       Loads from child (ModifyTransactionSalesAssociate) cargo class.
       <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransactionSalesAssociateCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo loaded
       </UL>
       @param bus  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve cargo
        ModifyTransactionSalesAssociateCargo cargo;
        cargo = (ModifyTransactionSalesAssociateCargo)bus.getCargo();
        // check dirty flag
        dirtyFlag = cargo.getDirtyFlag();
        // if updates needed, retrieve new sales associate value and
        // update-all-items flag
        if (dirtyFlag)
        {
            newSalesAssociate = cargo.getEmployee();
            updateAllItemsFlag = cargo.getUpdateAllItemsFlag();
            cargo.setAlreadySetTransactionSalesAssociate(true);
            // see if the child service created a new transaction
            if (cargo.getTransactionCreated())
            {
                transaction = cargo.getTransaction();
            }
        }
        salesAssociateAlreadySet = cargo.isAlreadySetTransactionSalesAssociate();
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

        // load financial cargo
        super.unload(bus);

        // retrieve cargo
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        // if dirty flag set, perform updates
        if (dirtyFlag)
        {
            // propagate new transaction back to parent service
            if (transaction != null)
            {
                cargo.setTransaction(transaction);
            }
             cargo.updateTransactionSalesAssociate(bus, newSalesAssociate, updateAllItemsFlag);

             //Bug#7354705
             //Update the cargo with the new Sales Associate
             cargo.setSalesAssociate(newSalesAssociate);

            // set flag to update the parent cargo of Modify Transaction
            cargo.setUpdateParentCargoFlag(true);

            // get ui handle and indicate sales associate has changed
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.salesAssociateNameChanged(newSalesAssociate.getPersonName().getFirstLastName());
        }
        if (salesAssociateAlreadySet)
        {
            cargo.setAlreadySetTransactionSalesAssociate(true);
        }

    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       <p>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  ModifyTransactionSalesAssociateReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // if verbose mode, do inspection gig
        if (bVerbose)
        {                               // begin verbose mode

            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // fieldType contains the type of the field currently being examined
            Class fieldType = null;
            // fieldName contains the name of the field currently being examined
            String fieldName = "";
            // fieldValue contains the value of the field currently being examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            {                           // begin loop through fields
                // fields contains all noninherited field information
                Field[] fields = theClass.getDeclaredFields();

                // Go through each field, capturing information
                for (int i = 0; i < fields.length; i++)
                {
                    fieldType = fields[i].getType();
                    fieldName = fields[i].getName();

                    // get the field's value, if possible
                    try
                    {
                        fieldValue = fields[i].get(this);
                    }
                    // if the value can't be gotten, say so
                    catch (IllegalAccessException ex)
                    {
                        fieldValue = "*no access*";
                    }
                    // If it is a "simple" field, use the value
                    if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" +
                            fieldName +
                            ":\t" +
                            fieldValue;
                    }       // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" +
                            fieldName +
                            ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" +
                            fieldName +
                            ":\t" +
                            fieldType.getName() +
                            "@" +
                            fieldValue.hashCode();
                    }
                }   // for each field
                theClass = theClass.getSuperclass();
            }                           // end loop through fields
        }                               // end verbose mode

        return(strResult);
    }

}   // end class ModifyTransactionSalesAssociateReturnShuttle
