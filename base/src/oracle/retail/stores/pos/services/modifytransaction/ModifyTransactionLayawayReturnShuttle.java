/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionLayawayReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  02/01/10 - added cash under warning boolean in the cargo
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
 *    Rev 1.0   Aug 29 2003 16:02:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:14:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:30:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargoIfc;

//------------------------------------------------------------------------------
/**
    Shuttles the required data from the Layaway cargo to the Modify Transaction Cargo.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ModifyTransactionLayawayReturnShuttle extends FinancialCargoShuttle
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        layaway transaction
    **/
    protected LayawayCargoIfc layawayCargo;

    //---------------------------------------------------------------------
    /**
       Get a local copy of the Layaway cargo. Retrieve the layaway transaction.
       <P>
       @param bus the bus being loaded
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve layaway cargo
        layawayCargo = (LayawayCargoIfc) bus.getCargo();
    }

    //---------------------------------------------------------------------
    /**
       Copy required data from the Layaway cargo to the Modify Transaction Cargo.
       sets the retailtransaction to the newly created layaway transaction.

       @param bus the bus being unloaded
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        // retrieve Modify Transaction cargo
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        // pass along the layaway transaction
        TenderableTransactionIfc tenderableTransaction = layawayCargo.getTenderableTransaction();
        if (tenderableTransaction != null &&
            !(tenderableTransaction instanceof LayawayTransactionIfc) &&
            tenderableTransaction instanceof RetailTransactionIfc)
        {
            cargo.setTransaction((RetailTransactionIfc)tenderableTransaction);
        }
        else
        if (layawayCargo.getInitialLayawayTransaction() != null)
        {
            cargo.setTransaction(layawayCargo.getInitialLayawayTransaction());
        }
        cargo.setUpdateParentCargoFlag(true);

        //Check for cash drawer warning added
        cargo.setCashDrawerUnderWarning(layawayCargo.isCashDrawerUnderWarning());
    }

}   // end class ModifyTransactionLayawayReturnShuttle

