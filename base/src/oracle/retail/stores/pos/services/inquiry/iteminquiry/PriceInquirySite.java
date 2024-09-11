/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/PriceInquirySite.java /main/16 2014/06/22 09:20:30 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/24/14 - Setting inquiry object to null for item search
 *                         to create a new one.
 *    asinton   09/24/14 - managing the cargo.getItemSearchResults list to
 *                         prevent application hang.
 *    asinton   09/03/14 - clearing the advance search flag in the cargo
 *    jswan     06/16/14 - Modified to support display of extended item
 *                         recommended items on the Sale Item Screen.
 *    cgreene   02/29/12 - skip showing screen if item number is already in
 *                         cargo
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Updated copy right header
 *
 *     $Log:
 *      4    360Commerce 1.3         2/26/2008 7:33:26 AM   Naveen Ganesh   Item
 *           getting added to the transaction, has been avoided to get added.
 *      3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:24:21 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:13:23 PM  Robert Pearse
 *     $
 *     Revision 1.3  2004/05/03 18:30:29  lzhao
 *     @scr 4544, 4556: keep user entered info when back to the page.
 *
 *     Revision 1.2  2004/04/30 18:43:01  lzhao
 *     @scr 4556: set the value user previously entered.
 *
 *     Revision 1.1  2004/02/16 22:42:17  lzhao
 *     @scr 3841:Inquiry Option Enhancement
 *     add price inquiry and advance search screens.
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site displays the PRICE_INQUIRY screen.
 * 
 * @version $Revision: /main/16 $
 */
public class PriceInquirySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2386650736050025220L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /** Advance Search Letter */
    public static final String ADVANCE_SEARCH_LETTER = "AdvanceSearch";

    /** Recommended Item Search Letter */
    public static final String RECOMMENDED_ITEM_SEARCH_LETTER = "RecommendedItemSearch";
    /**
     * Displays the ITEM_INFO_QUERY screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        SearchCriteriaIfc inquiry = cargo.getInquiry();

        if(cargo.isDisplayRecommendedItem())
        {
            bus.mail(RECOMMENDED_ITEM_SEARCH_LETTER);
        }
        else if (inquiry != null && inquiry.getItemNumber() != null)
        {
            bus.mail(CommonLetterIfc.NEXT);
        }
        else if (cargo.isSkipPriceInquiryFlag())
        {
            cargo.getItemSearchResults().clear();
            cargo.setInquiry(null);
            bus.mail(ADVANCE_SEARCH_LETTER);
        }
        else
        {
            POSBaseBeanModel beanModel = new POSBaseBeanModel();
            cargo.setSimpleSearchTypeFlow(true);
            cargo.setAdvanceSearch(false);
            cargo.getItemSearchResults().clear();
            if (inquiry != null)
            {
                String itemID = inquiry.getItemID();
                if (itemID != null)
                {
                    PromptAndResponseModel promptModel = new PromptAndResponseModel();
                    promptModel.setResponseText(itemID);
                    beanModel.setPromptAndResponseModel(promptModel);
                }
            }
            ui.showScreen(POSUIManagerIfc.PRICE_INQUIRY, beanModel);
        }
    }

    /**
     * Set item number in the price Inquiry .
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        SearchCriteriaIfc inquiry = cargo.getInquiry();
        if (inquiry != null)
        {
            String itemID = ui.getInput();
            if (!Util.isEmpty(itemID))
            {
                inquiry.setItemID(ui.getInput().trim());
            }
        }
    }
}