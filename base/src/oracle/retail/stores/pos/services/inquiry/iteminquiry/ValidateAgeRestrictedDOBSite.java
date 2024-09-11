/* ===========================================================================
* Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ValidateAgeRestrictedDOBSite.java /main/17 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  11/16/10 - AgeRestriction DOB not getting captured fix
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    npoola    10/14/10 - Logic to check if the PLUItem exists
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/13/09 - UI for Birthdate now accepts DOB in two separate
 *                         fields , the year accepts 4-digit format, removing
 *                         the logic of subtracting 100 years
 *    vchengeg  02/05/09 - Made changes to format EJournal for Discount and
 *                         Markdowns
 *
 * ===========================================================================
 * $Log:
 4    360Commerce 1.3         8/7/2007 4:01:29 PM    Anda D. Cadar   Fix for
 *    birthdate of PAT Customer
 3    360Commerce 1.2         6/19/2007 1:57:06 PM   Anda D. Cadar   the UI for
 *     birthdate now accepts a 2 digits year, meaning that by default years
 *    like 19 get translated by Java as 2019, and we actually want a value of
 *    1919
 2    360Commerce 1.1         5/18/2007 10:07:32 AM  Mathews Kochummen use
 *    locale date format
 1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This class validates the age entered and determines if the customer is old
 * enough or not. If not old enough, prompt error message and remove item from
 * transaction. $Revision: /main/17 $
 */
public class ValidateAgeRestrictedDOBSite extends PosSiteActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7013430414947152036L;

    /**
     * This method checks if the customer is old enough, if they aren;t display
     * error message.  if they are, mail continue.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        LetterIfc letter = null;
        EYSDate dob = cargo.getRestrictedDOB();
        if (pluItem == null && cargo.getItemList() != null && cargo.getItemList().length > 0)
        {
            pluItem = cargo.getItemList()[0];
        }
        if (pluItem != null)
        {
            int ageNeeded = pluItem.getRestrictiveAge();
            EYSDate today = new EYSDate();
            int yearsBetween = dob.yearsBetween(today);

            // if old enough
            if (yearsBetween >= ageNeeded)
            {
                letter = new Letter(CommonLetterIfc.CONTINUE);
            }
            else
            {
                // Display error message
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("AgeInvalidWarning");
                dialogModel.setType(DialogScreensIfc.ERROR);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            if (letter != null)
            {
                bus.mail(letter, BusIfc.CURRENT);
            }
        }
    }

    /**
     * This depart method checks the letter.  If invalid, remove item from
     * transaction.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();

        // create the transaction to store date in
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (letter.getName().equals("Invalid"))
        {
            journalDeletion(bus);
            //remove item from transaction
            cargo.setModifiedFlag(false);
            cargo.setPLUItem(null);
        }
        else
        {
            cargo.setModifiedFlag(true);
        }
    }

    /**
     * This method journals the item deleted and age violation information.
     * 
     * @param bus
     */
    public void journalDeletion(BusIfc bus)
    {
        JournalManagerIfc   journal =
            (JournalManagerIfc)
                bus.getManager(JournalManagerIfc.TYPE);
        if (journal != null)
        {
            StringBuffer strResult = new StringBuffer();
            ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
            Object[] dataArgs = new Object[2];
            if(cargo.getPLUItem() != null)
            {
                dataArgs[0] = cargo.getPLUItem().getItemID();
                strResult.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.ITEM_LABEL, dataArgs))
                        .append(Util.EOL);
                dataArgs[0] = cargo.getPLUItem().getRestrictiveAge();
                strResult.append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.MINIMUM_AGE_LABEL, dataArgs))
                        .append(Util.EOL);
            }
            else if (cargo.getItemList() != null)
            {
                PLUItemIfc itemList[] = cargo.getItemList();
                if(itemList[0] != null)
                {
                    dataArgs[0] = itemList[0].getPosItemID();
                    strResult.append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.ITEM_LABEL, dataArgs))
                            .append(Util.EOL);
                    dataArgs[0] = itemList[0].getRestrictiveAge();
                    strResult.append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.MINIMUM_AGE_LABEL, dataArgs))
                            .append(Util.EOL);
                }
            }
            Locale locale = LocaleMap
                    .getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            dataArgs[0] = cargo.getRestrictedDOB().toFormattedString(locale);
            strResult.append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DATE_OF_BIRTH_LABEL, dataArgs))
                    .append(Util.EOL);
            strResult.append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DELETED_AGE_VIOLATION_LABEL,
                            null)).append(Util.EOL);
            journal.journal(strResult.toString());
        }
    }
}
