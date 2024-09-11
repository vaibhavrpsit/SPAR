/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/DisplayErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:07  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 03 2003 09:51:42   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 02 2002 10:09:24   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:40:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the invalid search criteria dialog error screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayErrorAisle extends PosLaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "DisplayErrorAisle";
    /**
      default invalid  text
    **/
    public static final String INVALID_SEARCH_CRITERIA = "Invalid search criteria";
   /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays the invalid search criteria dialog error screen with a specific
       error message from cargo.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        EJournalCargo cargo = (EJournalCargo) bus.getCargo();

        String[] args = new String[1];
        args[0] = utility.retrieveDialogText(cargo.getErrorMsg(),
                                             INVALID_SEARCH_CRITERIA);
        

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidSearchCriteria");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

    }
}
