/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AlterationsErrorSite.java /main/11 2012/08/27 11:22:56 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed placeholder from key NoLinkedCustomer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse   
 *
 *   Revision 1.3.4.1  2004/10/20 22:07:31  jdeleau
 *   @scr 7399 Fix hang when db is offline and user selects "No" on
 *   customer not found dialog
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 05 2003 18:18:12   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.1   Aug 21 2002 11:21:20   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This site handles all error dialogs for the alterations service.
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class AlterationsErrorSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
        no link customer screen name
    **/
    private static final String RESOURCE_ID = "NoLinkedCustomer";
  
    //--------------------------------------------------------------------------
    /**
       Show the 'no linked customer' error dialog. <P>
       @param the service bus
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the managers from the bus
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Using "generic dialog bean", display the error dialog.
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(RESOURCE_ID);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Cancel");

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
