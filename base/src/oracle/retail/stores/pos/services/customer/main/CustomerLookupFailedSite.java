/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/CustomerLookupFailedSite.java /main/14 2013/10/17 14:59:38 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   10/17/13 - Differentiate customer offline messages in XChannel
 *                         and non-XChannel environment
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   06/01/09 - Removed class deprecation since class is still in
 *                         use.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/16 16:22:55  aachinfiev
 *   @scr 1752 - Wrong error was showing up during Layaway Offline find by Customer
 *
 *   Revision 1.4  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   May 27 2003 12:28:50   baa
 * rework offline flow for customer
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 * 
 *    Rev 1.3   May 27 2003 08:48:06   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.2   Mar 03 2003 16:48:00   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 16:01:32   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:32:02   msg
 * Initial revision.
 * 
 *    Rev 1.2   26 Mar 2002 10:45:32   baa
 * fix flow on customer offline
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.1   Mar 18 2002 23:13:04   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:16:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Determines how to handle a database error when looking up a customer.
    <p>
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class CustomerLookupFailedSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 414160667445486478L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";
    
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    //----------------------------------------------------------------------
    /**
        Determines how to handle a database error when looking up a customer.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        int errorCode = cargo.getDataExceptionErrorCode();
        DialogBeanModel dialogModel = new DialogBeanModel();
        boolean isXcEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
        int offlineCode = cargo.getOfflineIndicator();
        
        
        boolean showScreen = true;
        
        // Remember if we are a "Returns" transaction, as those get kinder dialogs.
        boolean isReturn = false;

        if (errorCode == DataException.NO_DATA)
        {
            // build the dialog screen
            dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Retry");
        }
        else if (errorCode == DataException.CONNECTION_ERROR)
        {
            if (isXcEnabled)
            {
                switch (offlineCode)
                {
                    case CustomerCargo.OFFLINE_ADD:
                    case CustomerCargo.OFFLINE_LINK:
                        dialogModel.setResourceID("XChannelLinkCustOfflineError");
                        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
                        break;
                        
                    case CustomerCargo.OFFLINE_EXIT:
                    case CustomerCargo.OFFLINE_DELETE:
                    case CustomerCargo.OFFLINE_UNKNOWN:
                        isReturn = isReturn(cargo);
                        if (isReturn)
                        {
                            dialogModel.setResourceID("DatabaseErrorForReturns");
                        }
                        else
                        {
                            dialogModel.setResourceID("DatabaseError");
                        }

                        dialogModel.setType(DialogScreensIfc.ERROR);
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Exit");
                        break;
                    case CustomerCargo.OFFLINE_LAYAWAY:
                        // Skip error dialog if came from layaway
                        // Layaway will show appropriate dialog
                        showScreen = false;
                        break;
                }
            }
            else
            {

                // The database is offline select the configured action
                switch (offlineCode)
                {
                    case CustomerCargo.OFFLINE_ADD:
                        dialogModel.setResourceID("AddCustOffline");
                        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Add");
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Exit");
                        break;

                    case CustomerCargo.OFFLINE_LINK:
                        dialogModel.setResourceID("LinkCustOffline");
                        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Link");
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Exit");
                        break;

                    case CustomerCargo.OFFLINE_EXIT:
                    case CustomerCargo.OFFLINE_DELETE:
                    case CustomerCargo.OFFLINE_UNKNOWN:
                        isReturn = isReturn(cargo);
                        if (isReturn)
                        {
                            dialogModel.setResourceID("DatabaseErrorForReturns");
                        }
                        else
                        {
                            dialogModel.setResourceID("DatabaseError");
                        }

                        dialogModel.setType(DialogScreensIfc.ERROR);
                        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Exit");
                        break;

                    case CustomerCargo.OFFLINE_LAYAWAY:
                        // Skip error dialog if came from layaway
                        // Layaway will show appropriate dialog
                        showScreen = false;
                        break;
                }
            }

            // If not using "Returns" kinder dialog, set the correct argument
            if (!isReturn)
            {
                String args[] = new String[1];
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                args[0] = utility.getErrorCodeString(errorCode);
                dialogModel.setArgs(args);
            }
        }
       else    // generic database error
        {
            // Set the correct argument
            String args[] = new String[1];
            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            args[0] = utility.getErrorCodeString(errorCode);

            // build the dialog screen
            dialogModel.setResourceID("DatabaseError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(args);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Exit");
        }

        
        if (showScreen)
        {
            // show the screen
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            cargo.setCustomer(null);
            cargo.setLink(false);
            bus.mail(new Letter(CommonLetterIfc.OFFLINE), BusIfc.CURRENT);
        }
    }

    /**
     * Returns whether this cargo is a "Returns" type one.
     * @param cargo the cargo
     * @return Whether this cargo is a "Returns" type one.
     */
    protected boolean isReturn(CustomerMainCargo cargo)
    {
        return (cargo.isReturn());
    }
}
