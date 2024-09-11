/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/GetIDNumberSite.java /main/14 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *
 * ===========================================================================

     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape
     $

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.validateid;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This class displays the get id number screen.
 *
 * @version $Revision: /main/14 $
 */
@SuppressWarnings("serial")
public class GetIDNumberSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * This method displays the get id number screen.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the cargo, and models
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        CheckEntryBeanModel model = new CheckEntryBeanModel();

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        beanModel.setArguments(cargo.getLocalizedPersonalIDCode().getText(lcl));
        model.setPromptAndResponseModel(beanModel);

        // get already entered if there
        String idNum = cargo.getIDNumber();
        if(idNum != null)
        {
            model.setIDNumber(idNum);
        }

        // determine which screen to display
        String idType = cargo.getIdTypeName();

        // if dl or state id and swipe drivers license parameter
        if(cargo.isAllowSwipe() &&
            (idType.equals("DriversLicense") ||
            		idType.equals("StateID")))
        {
            ui.showScreen(POSUIManagerIfc.ENTER_ID_NUMBER_SWIPE, model);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.ENTER_ID_NUMBER, model);
        }
    }

    /**
     * This method captures the input from the ui.
     * 
     * @param bus BusIfc
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        EncipheredDataIfc idNumber = null;

        if (letter.getName().equals("Next"))
        {
            ValidateIDCargoIfc cargo = (ValidateIDCargoIfc) bus.getCargo();
            String idType = cargo.getIdTypeName();

            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = null;

            // set to null incase this is the second try and manual
            // we need to have null to fail on validate dl
            cargo.setMSRModel(null);

            // get correct model
            if(cargo.isAllowSwipe() && (idType.equals("DriversLicense") ||
                idType.equals("StateID")))
            {
                model = (CheckEntryBeanModel) ui.getModel(POSUIManagerIfc.ENTER_ID_NUMBER_SWIPE);

                if(model.isCardSwiped())
                {
                    MSRModel msrModel  = model.getMSRModel();
                    cargo.setMSRModel(msrModel);
                    cargo.setIDNumber(msrModel.getAccountNumber());
                }
                else
                {
                    try
                    {
                        idNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                                model.getIDNumber().getBytes());
                    }
                    catch (EncryptionServiceException e)
                    {
                        logger.error("Could not encrypt text" + e.getLocalizedMessage());
                    }
                    cargo.setIdNumberEncipheredData(idNumber);
                }
            }
            else
            {
                model = (CheckEntryBeanModel) ui.getModel(POSUIManagerIfc.ENTER_ID_NUMBER);
                try
                {
                    idNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                            model.getIDNumber().getBytes());
                }
                catch (EncryptionServiceException e)
                {
                    logger.error("Could not encrypt text" + e.getLocalizedMessage());
                }
                cargo.setIdNumberEncipheredData(idNumber);
            }
        }
    }
}
