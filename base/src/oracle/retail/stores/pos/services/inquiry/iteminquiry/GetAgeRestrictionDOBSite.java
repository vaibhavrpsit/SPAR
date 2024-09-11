/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/GetAgeRestrictionDOBSite.java /rgbustores_13.4x_generic_branch/3 2011/08/22 16:55:00 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/22/11 - prompt for dob if it is null
 *    tksharma  08/09/11 - Introducing dialog box for age restriction site
 *    mchellap  07/14/11 - BUG#12396459 Setting modified flag in case DOB check
 *                         is skipped
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  11/16/10 - setting cargo modified flag to true to initialize
 *                         transaction
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    dwfung    09/23/10 - fixed logic if DOB should be captured
 *    sgu       06/08/10 - fix tab
 *    sgu       06/03/10 - add item # & description to date of birth
 *                         verification screen.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Serialisation Code changes
 *    nkgautam  03/13/09 - Chnages to take the input DOB as two separate fields
 *
 * ===========================================================================
 * $Log:
 2    360Commerce 1.1         7/6/2007 8:36:29 AM    Christian Greene Remove
 *    reference to deleted ItemProduct table
 1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AgeRestrictionBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site determines if the age is needed for this item and displays a dob
 * prompt if needed. $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class GetAgeRestrictionDOBSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -6463053510157441659L;

    /**
     * This method determines if the DOB prompts is needed and then displays it
     * when needed.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = null;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        if (pluItem == null && cargo.getItemList() != null && cargo.getItemList().length > 0)
        {
            pluItem = cargo.getItemList()[0];
            cargo.setPLUItem(pluItem);
        }
        if (pluItem != null)
        {
            int restrictiveAge = pluItem.getRestrictiveAge();
            if (restrictiveAge == 0)
            {
                letter = new Letter(CommonLetterIfc.CONTINUE);
                bus.mail(letter, BusIfc.CURRENT);
            }
            else
            if (cargo.getTransaction() != null)
            {
                cargo.setRestrictedDOB(((SaleReturnTransactionIfc)cargo.getTransaction()).getAgeRestrictedDOB());
                if (cargo.getRestrictedDOB() != null)
                {
                    if (isOldEnough(cargo.getRestrictedDOB(), restrictiveAge))
                    {
                        letter = new Letter(CommonLetterIfc.CONTINUE);
                        bus.mail(letter, BusIfc.CURRENT);
                    }
                    else
                    {
                        dialogForAgeRestriction(bus);
                    }
                }
                else
                {
                    displayDOBPrompt(bus);
                }
            }
            else
            {
                displayDOBPrompt(bus);
            }
        }
        else
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * This depart method captures the data entered from the ui and sets the
     * dob if Skip was entered.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            AgeRestrictionBeanModel model = (AgeRestrictionBeanModel)ui.getModel();
            cargo.setRestrictedDOB(model.getDateOfBirth());
        }
        else if (letter.getName().equals("Skip"))
        {
            cargo.setRestrictedDOB(new EYSDate(cargo.getPLUItem().getRestrictiveAge(), 1, 1));
            cargo.setModifiedFlag(true);
        }
        else if(letter.getName().equals("Continue"))
        {
            cargo.setModifiedFlag(true);
        }
        else if (letter.getName().equals("Cancel") ||
                 letter.getName().equals("Undo"))
        {
            //remove item from transaction
            cargo.setModifiedFlag(false);
        }
        else if (letter.getName().equals("Invalid"))
        {
            cargo.setModifiedFlag(false);
            cargo.setPLUItem(null);
        }
    }

    /**
     * This method displays the DOB screen.
     *
     * @param bus
     */
    public void displayDOBPrompt(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        //display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        AgeRestrictionBeanModel model = new AgeRestrictionBeanModel();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        Boolean skipAllowed = Boolean.FALSE;
        model.setBirthdateValid(true);
        model.setBirthYearValid(true);

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        String[] args = new String[2];
        args[0] = cargo.getPLUItem().getPosItemID();
        args[1] = cargo.getPLUItem().getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        parModel.setArguments(args);
        model.setPromptAndResponseModel(parModel);

        try
        {
            skipAllowed = pm.getBooleanValue("AllowDateOfBirthPromptSkip");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }
        if (skipAllowed == Boolean.TRUE)
        {
            ui.showScreen(POSUIManagerIfc.ENTER_DOB, model);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.ENTER_DOB_NO_SKIP, model);
        }
    }

    /**
     * There are two possible value of the captured Date of Birth. It could be
     * the actual birthday of the customer. When skip was entered at the enter
     * dob screen before the birth date will be 1/1/x where x is age that was
     * skipped for example 0018 or 0021
     *
     * @param restrictedDOB
     * @param itemRestrictiveAge
     * @return
     */
    protected boolean isOldEnough(EYSDate restrictedDOB, int itemRestrictiveAge)
    {
        int yearOfRestrictedDOB = restrictedDOB.getYear();
        if (yearOfRestrictedDOB < 1000)
        {
            // old enough if the skipped age is not less
            return yearOfRestrictedDOB >= itemRestrictiveAge;
        }

        // old enough if customer's age is larger than item's restricted age
        int customerAge = restrictedDOB.yearsBetween(new EYSDate());
        return customerAge >= itemRestrictiveAge;
    }

    public void dialogForAgeRestriction(BusIfc bus)
    {

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("AgeInvalidWarning");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
