/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemvalidate/ValidateItemInfoSite.java /main/21 2013/11/07 17:41:21 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  11/07/13 - set the supported locales to retrieve the
 *                         descriptions for all the supported locales
 *    yiqzhao   06/28/13 - Change the dialog name to ItemNotFoundInStore.
 *    jswan     01/09/13 - Modified to support Item manager changes.
 *    yiqzhao   01/04/13 - Change after ADE merge.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    yiqzhao   01/02/13 - Change the flow to display ItemNotFoundInStore
 *                         after searching an item as a store coupon item.
 *    hyin      10/16/12 - offline work for PLU lookup and Advanced item
 *                         lookup.
 *    hyin      10/10/12 - enable POS item PluLookup WebService flow.
 *    cgreene   07/17/12 - formatting and cleanup
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         7/5/2007 4:55:21 PM    Ranjan X Ojha   Merge
 *          from .v7x CR 27496
 *    6    360Commerce 1.5         2/13/2006 11:11:39 AM  Brett J. Larsen Merge
 *          from ValidateItemInfoSite.java, Revision 1.4.1.0
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *:
 *    4    .v700     1.2.2.0     9/13/2005 15:37:32     Jason L. DeLeau Ifan
 *         id_itm_pos maps to multiple id_itms, let the user choose which one
 *         to use.
 *    3    360Commerce1.2         3/31/2005 15:30:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:41     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:29     Robert Pearse
 *
 *Log:
 *    7    360Commerce 1.6         7/5/2007 4:55:21 PM    Ranjan X Ojha   Merge
 *          from .v7x CR 27496
 *    6    360Commerce 1.5         2/13/2006 11:11:39 AM  Brett J. Larsen Merge
 *          from ValidateItemInfoSite.java, Revision 1.4.1.0
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *: ValidateItemInfoSite.java,v $
 *Log:
 *    7    360Commerce 1.6         7/5/2007 4:55:21 PM    Ranjan X Ojha   Merge
 *          from .v7x CR 27496
 *    6    360Commerce 1.5         2/13/2006 11:11:39 AM  Brett J. Larsen Merge
 *          from ValidateItemInfoSite.java, Revision 1.4.1.0
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *:
 *    4    .v710     1.2.3.0     9/21/2005 13:40:28     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    3    360Commerce1.2         3/31/2005 15:30:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:41     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:29     Robert Pearse
 *
 *   Revision 1.9.2.1  2004/12/02 17:22:32  lzhao
 *   @scr 7779: check kit components for kit authorizable.
 *
 *   Revision 1.9  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/05/27 19:31:33  jdeleau
 *   @scr 2775 Remove unused imports as a result of tax engine rework
 *
 *   Revision 1.7  2004/05/27 17:12:48  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.6  2004/04/17 17:59:28  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/03/03 23:57:05  mweis
 *   @scr 2467 Backoffice: Don't all items not authorized for sale to be added to the Sale Item panel.
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
 *
 *
 *    Rev 1.3   Jan 13 2004 18:56:02   sfl
 * Made the adjustment on the return letter value so that
 * if the regular item lookup didn't return any results, the process
 * will continue on store coupon lookup.
 * Resolution for 3691: Item ID 27600 shows as item not found
 *
 *    Rev 1.2   Nov 17 2003 08:45:46   jriggins
 * Removed unecessary logic that dealt with returning multiple items and setting the Retry letter for failed plu lookups
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   Nov 14 2003 14:11:48   sfl
 * Added code to read tax rules from the memory and assign them the items or kit component items.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 *    Rev 1.0   13 Nov 2003 10:39:00   jriggins
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * This site validates the item number stored in the cargo.
 * 
 * @version $Revision: /main/21 $
 */
@SuppressWarnings("serial")
public class ValidateItemInfoSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/21 $";
    /**
     * item number field
     */
    public static final int ITEM_NUMBER_FIELD = 1;
    /**
     * item description field
     */
    public static final int ITEM_DESC_FIELD = 2;
    /**
     * item manufacturer field
     */
    public static final int ITEM_MANUFAC_FIELD = 3;

    /**
     * Validate the item info stored in the cargo( number, desc and dept). If
     * the item is found, a Success letter is sent. Otherwise, a Failure letter
     * is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to be sent
        String letter = null;

        // get item inquiry from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        SearchCriteriaIfc inquiry = cargo.getInquiry();
        cargo.resetInvalidFieldCounter();

        // validate input fields prior to execute transaction
        if (inquiry.getDescription() == null &&
                inquiry.getItemID() == null &&
                inquiry.getPosItemID() == null &&
                inquiry.getItemNumber() == null &&
                inquiry.getManufacturer() == null)
        {
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            cargo.setInvalidField(ITEM_DESC_FIELD);
            cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else if ( !(inquiry.isSearchItemByItemID() && isValidItemNumber(inquiry.getItemID()) ||
                   (inquiry.isSearchItemByPosItemID() && isValidItemNumber(inquiry.getPosItemID())) ||
                   (inquiry.isSearchItemByItemNumber()&& isValidItemNumber(inquiry.getItemNumber()))) )
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_NUMBER_FIELD);
            if (!isValidField(inquiry.getDescription()))
            {
                // check if the search critera is valid
                cargo.setInvalidField(ITEM_DESC_FIELD);
            }
            if (!isValidField(inquiry.getManufacturer()))
            {
                // check if the search critera is valid
                cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            }
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getDescription()))
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_DESC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else if (!isValidField(inquiry.getManufacturer()))
        {
            // check if the search critera is valid
            cargo.setInvalidField(ITEM_MANUFAC_FIELD);
            letter = CommonLetterIfc.INVALID;
        }
        else
        {
            // retrieve item with USER_INTERFACE locale only
            inquiry.setLocaleRequestor(LocaleMap.getSupportedLocaleRequestor());
            inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
            inquiry.setPLURequestor(new PLURequestor());
            letter = CommonLetterIfc.SEARCH;
        }

        // Proceed to the next site
        if (letter !=null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
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

    /**
     * Returns a boolean, validates the item number field
     * 
     * @param the string data
     * @return boolean
     */
    public boolean isValidItemNumber(String data)
    {
        // isValid returns false if only a wild character is sent as data
        // or only spaces were given in the item number field, true other wise.
        boolean isValid = true;

        if (data == null || data.equals("%"))
        {
            isValid = false;
        }
        return (isValid);
    }

}
