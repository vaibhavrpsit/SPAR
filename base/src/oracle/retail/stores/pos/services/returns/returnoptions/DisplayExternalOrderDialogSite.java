/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/DisplayExternalOrderDialogSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     05/19/10 - Add transaction reentry classes
 *    jswan     05/19/10 - Add
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// foundation imports
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;

import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * Determines if External Order Return dialog should be displayed before
 * proceeding with return process.
**/
//--------------------------------------------------------------------------
public class DisplayExternalOrderDialogSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1700748982065009436L;

    /** Dialog name */
    protected static final String RETRIEVE_TRANS_FOR_EXTERNAL_ORDER = "RetrieveTransForExternalOrder";
    /** Tag for more text */
    protected static final String MORE_TAG = "MoreText"; 
    /** Default more text */
    protected static final String MORE_DEFAULT_TEXT = "more...";
    /** More text tag prefix */
    protected static final String COMMON = "Common";

    //----------------------------------------------------------------------
    /**
        Determines if External Order Return dialog should be displayed before
        proceeding with return process.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        if (cargo.isExternalOrder())
        {
            if (areThereExternalOrderItemsToReturn(cargo))
            {
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(RETRIEVE_TRANS_FOR_EXTERNAL_ORDER);
                model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                model.setArgs(getExternalOrderArgs(cargo));
                model.setButtonLetter(0, CommonLetterIfc.OK);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            {
                bus.mail(new Letter(CommonLetterIfc.CUSTOMER), BusIfc.CURRENT);
            }
        }
        else 
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /*
     * Determines if all the order items have been returned yet.
     */
    private boolean areThereExternalOrderItemsToReturn(ReturnOptionsCargo cargo)
    {
        boolean eoiToReturn = false;
        // Get all external order items that have not been returned.
        for(int i = 0; i < cargo.getExternalOrderItemReturnStatusElements().size(); i++)
        {
            if (!cargo.getExternalOrderItemReturnStatusElements().get(i).isReturned())
            {
                eoiToReturn = true;
                i = cargo.getExternalOrderItemReturnStatusElements().size();
            }
        }
        
        return eoiToReturn;
    }


    /*
     * Gets the list of items to display on the external order dialog panel
     */
    private String[] getExternalOrderArgs(ReturnOptionsCargo cargo)
    {
        ArrayList<ExternalOrderItemIfc> returnList = new ArrayList<ExternalOrderItemIfc>();
        String[] args   = new String[4];
        int argsIndex   = 0;
        int startRow    = 0;
        int returnIndex = 0;
        
        // Get all external order items that have not been returned.
        for(int i = 0; i < cargo.getExternalOrderItemReturnStatusElements().size(); i++)
        {
            if (!cargo.getExternalOrderItemReturnStatusElements().get(i).isReturned())
            {
                returnList.add(cargo.getExternalOrderItemReturnStatusElements().get(i).getExternalOrderItem());
            }
        }
        
        if (returnList.size() < 3)
        {
            startRow = 1;
        }
        
        for(argsIndex = 0; argsIndex < 4; argsIndex++)
        {
            // If the row is before first display row, or 
            // after the last display row...
            if (argsIndex < startRow || argsIndex >=  returnList.size() + startRow)
            {
                args[argsIndex] = "";
            }
            else
            // if there are more than 4 return items
            // and this is the 4th line...
            if (returnList.size() > 4 && argsIndex == 3)
            {
                UtilityManagerIfc utility =
                    (UtilityManagerIfc) Gateway.getDispatcher().getManager(
                        UtilityManagerIfc.TYPE);
                String moreText = utility.retrieveText(COMMON,
                        BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                        MORE_TAG, MORE_DEFAULT_TEXT);
                args[argsIndex] = moreText;
            }
            else
            {
                args[argsIndex] = returnList.get(returnIndex).getDescription() + 
                    ", " + 
                    returnList.get(returnIndex).getPOSItemId();
                returnIndex++;
            }
        }
        
        return args;
    }
}
