/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionGiftRegistryReturnShuttle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
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
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:02:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.registry.ModifyTransactionGiftRegistryCargo;

//------------------------------------------------------------------------------
/**
    Return shuttle class for ModifyTransactionGiftRegistry service. <P>
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class ModifyTransactionGiftRegistryReturnShuttle extends FinancialCargoShuttle
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionGiftRegistryReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/13 $";
    /**
       dirty flag (indicates update needs to be performed)
    **/
    protected boolean dirtyFlag = false;
    /**
       new gift registry class
    **/
    protected RegistryIDIfc newRegistry;
    /**
       update-all-items flag
    **/
    protected boolean updateAllItemsFlag = true;

    /**
       retail transaction
    **/
    protected RetailTransactionIfc transaction;

    //---------------------------------------------------------------------
    /**
       Load from child (ModifyTransactionGiftRegistry) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ModifyTransactionGiftRegistryCargo class
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
        ModifyTransactionGiftRegistryCargo cargo =
            (ModifyTransactionGiftRegistryCargo) bus.getCargo();
        // check dirty flag
        dirtyFlag = cargo.getDirtyFlag();

        // if updates needed, retrieve new gift registry value and
        // update-all-items flag
        if (dirtyFlag)
        {
            newRegistry = cargo.getNewRegistry();
            updateAllItemsFlag = cargo.getUpdateAllItemsFlag();

            // see if the child service created a new transaction
            if (cargo.getTransactionCreated())
            {
                transaction = cargo.getTransaction();
            }
        }

    }

    //---------------------------------------------------------------------
    /**
       Unload to parent (ModifyTransaction) cargo class. <P>
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

            cargo.updateTransactionGiftRegistry(bus, newRegistry,
                                                updateAllItemsFlag);

            // set flag to update the parent cargo of Modify Transaction
            cargo.setUpdateParentCargoFlag(true);
        }

    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ModifyTransactionGiftRegistryReturnShuttle (Revision " +
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
}   // end class ModifyTransactionGiftRegistryReturnShuttle

