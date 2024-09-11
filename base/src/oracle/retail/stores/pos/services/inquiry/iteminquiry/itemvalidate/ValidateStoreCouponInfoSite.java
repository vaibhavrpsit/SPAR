/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemvalidate/ValidateStoreCouponInfoSite.java /main/18 2013/06/28 17:29:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/28/13 - Change the dialog name to ItemNotFoundInStore.
 *    jswan     01/09/13 - Deprecated due to Item Manager changes.
 *    yiqzhao   01/04/13 - Change after ADE merge.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    yiqzhao   01/02/13 - Change the flow to display ItemNotFoundInStore
 *                         after searching an item as a store coupon item.
 *    jswan     08/29/12 - Modified to support requesting Coupons using the
 *                         SearchCriteria class.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   01/22/09 - Use new getStoreCouponItem(inquiry) method to allow
 *                         use of store number
 * 
 * ===========================================================================
 *   $Log:
 *    4    360Commerce 1.3 *       11/15/2007 11:01:52 AM Christian Greene
 *    *    Belize merge
 *    3    360Commerce 1.2 *       3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1 *       3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0 *       2/11/2005 12:15:29 PM  Robert Pearse   
 *   $
 *   Revision 1.6  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/17 17:59:28  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:15  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 * 
 *    Rev 1.1   Jan 15 2004 08:23:18   sfl
 * Added the handling of store credit expiration checking.
 * Resolution for 3707: Store coupon pricing rule expiration message display not working
 *
 *    Rev 1.0   Jan 13 2004 18:58:14   sfl
 * Initial revision.
 * Resolution for 3691: Item ID 27600 shows as item not found
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates the store coupon number stored in the cargo.
 * 
 * @version $Revision: /main/18 $
 * @deprecated inversion 14.0; no longer used.
 */
public class ValidateStoreCouponInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6418460508614895472L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/18 $";
    /**
     * item number field
     */
    public static final int ITEM_NUMBER_FIELD = 1;

    /**
     * item description field
     */
    public static final int ITEM_DESC_FIELD = 2;

    /**
     * constant for parameter name
     */
    public static final String ITEM_MAXIMUM_MATCHES = "ItemMaximumMatches";

    /**
     * Letter for multiple items found
     */
    public static final String LETTER_MULTIPLE_ITEMS_FOUND = "MultipleItemsFound";

    /**
     * Letter for one item found
     */
    public static final String LETTER_ONE_ITEM_FOUND = "OneItemFound";
    
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";


    /**
     * Validate the store coupon info stored in the cargo( number, desc and
     * dept) . If the item is found, a Success letter is sent. Otherwise, a
     * Failure letter is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to be sent
        String letter = null;

        // get item inquiry from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        SearchCriteriaIfc inquiry = cargo.getInquiry();
        cargo.resetInvalidFieldCounter();

        // validate input fields prior to execute transaction
        if (inquiry.getDescription() == null && inquiry.getItemID() == null)
        {
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            cargo.setInvalidField(ITEM_DESC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getItemID()))
        {
            // check if the search criteria is valid
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            if (!isValidField(inquiry.getDescription()))
            {
                // check if the search criteria is valid
                cargo.setInvalidField(ITEM_DESC_FIELD);
            }
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getDescription()))
        {
            // check if the search criteria is valid
            cargo.setInvalidField(ITEM_DESC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else
        {
            // go ahead with the database search
            letter = getItems(bus, cargo, inquiry);
        }

        /*
         * Proceed to the next site
         */
        if (letter != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }

    }

    /**
     * Returns a list of items matching the search criteria
     * 
     * @param bus
     * @param cargo
     * @param inquiry the inquiry search criteria
     * @return String The letter to be sent
     */
    public String getItems(BusIfc bus, ItemInquiryCargo cargo, SearchCriteriaIfc inquiry)
    {
        // letter to be sent
        String letter = null;
        try
        {
            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
            inquiry.setLookupStoreCoupon(true);
            PLUItemIfc storeCoupon = mgr.getPluItem(inquiry);

            if (storeCoupon != null)
            {
                if (storeCoupon.hasAdvancedPricingRules())
                {
                    cargo.setPLUItem(storeCoupon);
                    letter = LETTER_ONE_ITEM_FOUND;
                }
                else
                {
                    // no rules found for a valid itemID
                    // assumption is that rules were found but expired
                    letter = CommonLetterIfc.DATERANGE;
                }
            }
            else
            {
                letter = CommonLetterIfc.RETRY;
            }
        }
        catch (DataException de)
        {
            int errorCode = de.getErrorCode();
            logger.warn("ItemNo: " + inquiry.getItemID() + " \nItem Desc: " + inquiry.getDescription()
                    + " \nItem Dept: " + inquiry.getDepartmentID() + "");

            logger.warn("Error: " + de.getMessage() + " \n " + de + "");

            cargo.setDataExceptionErrorCode(errorCode);
            logger.warn(
                    "ItemNo: " + inquiry.getItemNumber() + " \nItem Desc: " + inquiry.getDescription() + " \nItem Dept: " + inquiry.getDepartmentID() + "" );

                logger.warn(
                    "Error: " + de.getMessage() + " \n " + de + "");

            boolean skipWebService = false;
            if (DataException.ERROR_CODE_EXTENDED_OFFLINE == de.getErrorCodeExtended())
            {
                skipWebService = true;
            }
            boolean isXcEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "XChannelEnabled", false);
            if ((DataException.NO_DATA == errorCode) && isXcEnabled && (!skipWebService))
            {
                showWebServiceDialog(bus, inquiry);
            }
            else
            {
                letter = CommonLetterIfc.RETRY;
            }

        }

        return (letter);
    }

    /**
     * Returns a boolean, validates the field
     * 
     * @param the string data
     * @return boolean
     */
    public boolean isValidField(String data)
    {
        // isValid returns false if only a wild character is sent as data
        // true other wise.
        boolean isValid = true;

        // when using wild card search at least one character must be used
        // with the wildcard.This is to narrow the search
        if (data != null && data.equals("%"))
        {
            isValid = false;
        }
        return (isValid);
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays webService Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
    private void showWebServiceDialog(BusIfc bus, SearchCriteriaIfc inquiry)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the choice dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ItemNotFoundInStore");
        String msg[] = new String[1];
        msg[0] = inquiry.getItemNumber();
        model.setArgs(msg);
        
        model.setType(DialogScreensIfc.SEARCHWEBSTORE_CANCEL);
        model.setButtonLetter(DialogScreensIfc.BUTTON_SEARCH_WEBSTORE, CommonLetterIfc.SEARCH);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.RETRY);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
