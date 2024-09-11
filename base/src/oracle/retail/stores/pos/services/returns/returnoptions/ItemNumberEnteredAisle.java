/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ItemNumberEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Returns flow refactor: deprected obsolete class.
 *    mchellap  01/11/10 - Set prompt length to imei length
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         3/10/2008 3:51:48 PM   Sandy Gu        Specify
 *        store id for non receipted return item query.
 *  3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:29 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse
 * $
 * Revision 1.14  2004/06/07 19:59:00  mkp1
 * @scr 2775 Put correct header on files
 *
 * Revision 1.13  2004/03/23 18:49:12  aarvesen
 * @scr 3561 Use the NoColon version of the text string
 *
 * Revision 1.12  2004/03/11 21:14:52  aarvesen
 * @scr 3561 display an error when a null item number is entered
 *
 * Revision 1.11  2004/03/10 19:41:52  baa
 * @scr work for parsing size from scanned item
 *
 * Revision 1.10  2004/03/08 23:39:01  blj
 * @scr 0 - no changes
 *
 * Revision 1.9  2004/03/08 15:12:30  baa
 * @scr 0 remove unused imports
 *
 * Revision 1.8  2004/03/05 23:27:58  baa
 * @scr 3561 Retrieve size from scanned items
 *
 * Revision 1.7  2004/02/23 14:58:52  baa
 * @scr 0 cleanup javadocs
 *
 * Revision 1.6  2004/02/23 13:54:52  baa
 * @scr 3561 Return Enhancements to support item size
 *
 * Revision 1.5  2004/02/19 15:37:31  baa
 * @scr 3561 returns
 *
 * Revision 1.4  2004/02/18 20:36:20  baa
 * @scr 3561 Returns changes to support size
 * Revision 1.3 2004/02/12 16:51:52 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:52:25 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:20 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.1 Feb 09 2004 10:37:04 baa return - item not found
 *
 * Rev 1.0 05 Feb 2004 23:29:54 baa Initial revision.
 *
 * Rev 1.5 Jan 23 2004 16:10:24 baa continue returns developement
 *
 * Rev 1.4 Dec 30 2003 16:58:50 baa cleanup for return feature Resolution for 3561: Feature
 * Enhacement: Return Search by Tender
 *
 * Rev 1.3 29 Dec 2003 22:35:26 baa more return enhacements
 *
 * Rev 1.2 Dec 29 2003 15:36:28 baa return enhancements
 *
 * Rev 1.1 Dec 19 2003 13:23:04 baa more return enhancements Resolution for 3561: Feature
 * Enhacement: Return Search by Tender
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated in 13.3 no longer used.
 */
//------------------------------------------------------------------------------

public class ItemNumberEnteredAisle extends ValidateItemNumberAisle
{
    /**
     * This aisle retrieves and validates the item number from the response area
     * @param bus  the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel promptModel =((POSBaseBeanModel) ui.getModel(POSUIManagerIfc.RETURN_NO_RECEIPT))
                .getPromptAndResponseModel();

        String itemID = promptModel.getResponseText();
        // Store the item number in the cargo
        if (!Util.isEmpty(itemID))
        {
            ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
            boolean isScanned = promptModel.isScanned();
            if (isScanned)
            {
               itemID = processScannedItemNumber((ReturnItemCargoIfc)cargo,itemID);
            }
            else
            {
               cargo.setSearchCriteria(null);
            }
            cargo.setItemScanned(isScanned);
            cargo.setPLUItemID(itemID);

            //Have to set the GeoCode to get the tax rules back
            if(cargo.getStoreStatus() != null &&
                    cargo.getStoreStatus().getStore() != null )
            {
                cargo.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                cargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            }

            bus.mail(new Letter(CommonLetterIfc.SEARCH), BusIfc.CURRENT);
        } else
        {
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String[] args = { utility.retrieveCommonText("ItemNumberLabelNoColon")};
            UIUtilities.setDialogModel(ui,DialogScreensIfc.ACKNOWLEDGEMENT,
                    "InvalidNumberError",  args,  "Retry");
        }

    }


}
