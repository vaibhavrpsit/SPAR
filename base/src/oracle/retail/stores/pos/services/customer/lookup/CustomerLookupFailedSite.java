/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/CustomerLookupFailedSite.java /main/12 2013/10/17 14:59:38 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   10/17/13 - Differentiate customer offline messages in XChannel
 *                         and non-XChannel environment
 *    acadar    07/27/12 - changes for XC
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
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
 *    Rev 1.0   Aug 29 2003 15:55:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 03 2003 16:43:46   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 15:55:42   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:32:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:44   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.6   20 Dec 2001 13:15:32   baa
 * updates for offline add
 * Resolution for POS SCR-466: Application hung on dB Error when offline trying to find Customer by Employee ID
 *
 *    Rev 1.5   20 Dec 2001 10:48:14   baa
 * updates  for offline add
 * Resolution for POS SCR-466: Application hung on dB Error when offline trying to find Customer by Employee ID
 * Resolution for POS SCR-467: Offline Cust Fnd screen missing Text 5,7, & 8
 *
 *    Rev 1.4   28 Nov 2001 17:46:32   baa
 * fix cancel for offline
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.3   19 Nov 2001 16:16:42   baa
 * customer & inquiry options cleanup
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.2   16 Nov 2001 10:33:22   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   05 Nov 2001 17:37:04   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 21 2001 11:15:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
    Determines how to handle a database error when looking up a customer.
    $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CustomerLookupFailedSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
        Determines how to handle a database error when looking up a customer.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";
    
    
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        int errorCode = cargo.getDataExceptionErrorCode();
        int offlineCode = cargo.getOfflineIndicator();
        boolean isXcEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);

        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String args[] = new String[1];
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.getErrorCodeString(errorCode);

        if (errorCode == DataException.NO_DATA)
        {
           UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "INFO_NOT_FOUND_ERROR",null,
                                      CommonLetterIfc.RETRY);
        }
        else if (errorCode == DataException.CONNECTION_ERROR || offlineCode == CustomerCargo.OFFLINE_ADD )
        {
            if (isXcEnabled)
            {
                int buttons[] = new int[1];
                String letters[] = new String[1];
                buttons[0] = DialogScreensIfc.BUTTON_OK;
                letters[0] = CommonLetterIfc.RETRY;
                switch (offlineCode)
                {
                    case CustomerCargo.OFFLINE_EXIT:

                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                CommonLetterIfc.OFFLINE);
                        break;
                    case CustomerCargo.OFFLINE_LINK:

                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT,
                                "XChannelLinkCustOfflineError", args, buttons, letters);
                        break;
                    case CustomerCargo.OFFLINE_ADD:

                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "XChannelDatabaseErrorFind",
                                args, buttons, letters);

                        break;
                    default:
                    {
                        // build the dialog screen
                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                CommonLetterIfc.RETRY);
                    }
                }
            }
            else
            {
                int buttons[] = new int[2];
                String letters[] = new String[2];
                buttons[0] = DialogScreensIfc.BUTTON_YES;
                buttons[1] = DialogScreensIfc.BUTTON_NO;
                switch (offlineCode)
                {
                    case CustomerCargo.OFFLINE_EXIT:
                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                CommonLetterIfc.OFFLINE);
                        break;
                    case CustomerCargo.OFFLINE_LINK:
                        letters[0] = CommonLetterIfc.LINK;
                        letters[1] = CommonLetterIfc.OFFLINE;

                        UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "LinkCustOffline", null, buttons,
                                letters);
                        break;
                    case CustomerCargo.OFFLINE_ADD:
                        // build the dialog screen
                        letters[0] = CommonLetterIfc.ADD;
                        letters[1] = CommonLetterIfc.CANCEL;
                        UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "DatabaseErrorFind", args,
                                buttons, letters);

                        // or the message could be "AddCustoffline" no args
                        break;
                    default:
                    {
                        // build the dialog screen
                        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                CommonLetterIfc.RETRY);
                    }
                }
            }
        }
        else    // generic database error
        {
                 UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                            CommonLetterIfc.CANCEL);
        }
    }


}
