/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/DisplayOrderErrorSite.java /main/9 2014/05/14 11:44:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/16/11 - repackage commext
 *    vtemker   07/18/11 - Added the case when orders belonging to the given
 *                         customer is not found
 *    abondala  06/24/10 - new error code for siebel multiple shippings
 *    abondala  06/16/10 - fix error handling
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/20/10 - updated search flow
 *    abondala  05/19/10 - search flow update
 *    abondala  05/12/10 - updated
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This Aisle is responsible for displaying the errors
 * occurred during retrieving external orders.
 *
 * @author abondala
 */
public class DisplayOrderErrorSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 6083371752002692757L;
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/9 $";
    
    
    public void arrive(BusIfc bus)
    {
        
        SearchOrderCargo cargo = (SearchOrderCargo)bus.getCargo();
        
        switch (cargo.getExceptionErrorCode())
        {
            case ExternalOrderException.CONNECTION_ERROR:
                displayOfflineError(bus);
                break;

            case ExternalOrderException.RESPONSE_MIN_ORDERS_EXCEEDED:
                displayExternalOrderError(bus,"UnknownExternalOrderError", null,CommonLetterIfc.FAILURE);
                break;
                
            case ExternalOrderException.RESPONSE_MAX_ORDERS_EXCEEDED:
                displayExternalOrderError(bus,"TooManyMatches", null,CommonLetterIfc.RETRY);
                break;
                
            case ExternalOrderException.RESPONSE_MAX_LINE_ITEMS_EXCEEDED:
                displayExternalOrderError(bus,"TooManyExtOrderLineItems", null,CommonLetterIfc.FAILURE);
                break;
                
            case ExternalOrderException.RESPONSE_UNEXPECTED_ORDER_IDS:
                displayExternalOrderError(bus,"DataErrorWithExternalOrder", null,CommonLetterIfc.FAILURE);
                break;
                
            case ExternalOrderException.RESPONSE_TOO_MANY_NON_PRICED_SHIPPING:
                displayExternalOrderError(bus,"DataErrorWithExternalOrder", null,CommonLetterIfc.FAILURE);
                break;  
                
            case ExternalOrderException.ORDER_NOT_FOUND_ERROR:
                displayExternalOrderError(bus, "INFO_NOT_FOUND_ERROR", null,CommonLetterIfc.RETRY);
                break;
                
            default:
                displayExternalOrderError(bus,"UnknownExternalOrderError", null,CommonLetterIfc.FAILURE);
        }

    }

    /**
     * Display maximum matches error
     * @param bus
     * @param resourceID
     * @param argText
     * @param type
     * @param letter
     */
    public void displayExternalOrderError(BusIfc bus,String resourceID, String argText, String letter)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Set the correct argument
        String args[] = null;
        if (argText != null)
        {
           args = new String[1];
           args[0] = argText;
         }
        UIUtilities.setDialogModel(ui,DialogScreensIfc.ERROR,resourceID, args,letter);
    }

    /**
     * Display Offline error
     * @param bus
     */
    public void displayOfflineError(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ExternalOrderOffline");
        dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Offline");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "Failure");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }  
    

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
         // return string
         return(revisionNumber);
    }      

}
