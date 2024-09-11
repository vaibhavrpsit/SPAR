/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/UndoCargoRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    Undo the customer cargo due to Esc by the user.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UndoCargoRoad extends LaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "UndoCargoRoad";

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Resets the customer cargo due to cancel by the user. <p>
       @param bus the bus traversing this lane
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // resets the customer cargo
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo) bus.getCargo();

        // clear previously linked customer name if there is one
        if (cargo.getCustomerLinked())
        {
           // set the customer's name in the status area
           POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
           StatusBeanModel statusModel = new StatusBeanModel();

           statusModel.setCustomerName("");
           POSBaseBeanModel baseModel = new POSBaseBeanModel();
           baseModel.setStatusBeanModel(statusModel);
           ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
           cargo.setCustomer(null);
           if (cargo.getTransactionCreated())
           {
              cargo.setTransaction(null);
           }
        }
        cargo.resetCargo();
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
