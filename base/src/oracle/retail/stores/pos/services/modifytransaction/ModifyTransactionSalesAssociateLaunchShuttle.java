/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionSalesAssociateLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
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
 *   Revision 1.5  2004/07/28 16:15:40  rsachdeva
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
 *    Rev 1.0   Apr 29 2002 15:14:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:22   msg
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
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifytransaction.salesassociate.ModifyTransactionSalesAssociateCargo;

//------------------------------------------------------------------------------
/**
    Launch shuttle class for ModifyTransactionSalesAssociate service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ModifyTransactionSalesAssociateLaunchShuttle extends FinancialCargoShuttle
{   // begin class ModifyTransactionSalesAssociateLaunchShuttle
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionSalesAssociateLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       vector of items from sale return transaction
    **/
    protected Vector lineItems;
    /**
       default sales associate class
    **/
    protected EmployeeIfc salesAssociate;
     /**
       sales associate set using modify transaction sales associate
     **/ 
     protected boolean alreadySetSalesAssociate;

    /**
       Flag to determine whether a transaction can be created by the
       child service
    **/
    protected boolean createTransaction;

    //---------------------------------------------------------------------
    /**
       Load parent (ModifyTransaction) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransactionCargo class
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
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
        // pull out sales associate, line items
        RetailTransactionIfc transaction = cargo.getTransaction();

        if (transaction != null)
        {
            lineItems = transaction.getLineItemsVector();
            salesAssociate = transaction.getSalesAssociate();
            createTransaction = false;
        }
        else
        {
            lineItems = new Vector();
            salesAssociate = cargo.getSalesAssociate();
            createTransaction = true;
        }
        alreadySetSalesAssociate = cargo.isAlreadySetTransactionSalesAssociate();
    }

    //---------------------------------------------------------------------
    /**
       Unload to child (ModifyTransactionSalesAssociate) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransactionSalesAssociateCargo class
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

        // retrieve cargo
        ModifyTransactionSalesAssociateCargo cargo =
            (ModifyTransactionSalesAssociateCargo) bus.getCargo();
        // initialize cargo with line items, sales associate
        cargo.initialize(lineItems, salesAssociate);
        cargo.setCreateTransaction(createTransaction);
        if (alreadySetSalesAssociate)
        {
            cargo.setAlreadySetTransactionSalesAssociate(true);
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
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  ModifyTransactionSalesAssociateLaunchShuttle (Revision " +
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
}   // end class ModifyTransactionSalesAssociateLaunchShuttle

