/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionSuspendLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
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
 *    Rev 1.0   Apr 29 2002 15:14:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:32   msg
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
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.suspend.ModifyTransactionSuspendCargo;

//--------------------------------------------------------------------------
/**
    Launch shuttle class for ModifyTransactionSuspend service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionSuspendLaunchShuttle extends FinancialCargoShuttle
{                                                                               // begin class ModifyTransactionSuspendLaunchShuttle
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionSuspendLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       the transaction
    **/
    protected RetailTransactionIfc transaction = null;

    //---------------------------------------------------------------------
    /**
       Loads from child (ModifyTransactionSuspend) cargo class. <P>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        ModifyTransactionCargo cargo =
            (ModifyTransactionCargo) bus.getCargo();

        transaction = cargo.getTransaction();

    }

    //---------------------------------------------------------------------
    /**
       Unloads to parent (ModifyTransaction) cargo class. <P>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        ModifyTransactionSuspendCargo cargo =
            (ModifyTransactionSuspendCargo) bus.getCargo();

        cargo.setTransaction(transaction);

    }

    //---------------------------------------------------------------------
    /**
       Launchs the string representation of the object. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  ModifyTransactionSuspendLaunchShuttle")
            .append(" (Revision ").append(getRevisionNumber())
            .append(")").append(hashCode());
        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
       Launchs the revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}                                                                       // end class ModifyTransactionSuspendLaunchShuttle

