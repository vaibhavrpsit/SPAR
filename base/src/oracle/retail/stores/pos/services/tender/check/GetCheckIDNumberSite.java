/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/GetCheckIDNumberSite.java /main/15 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    ohorne    08/18/11 - APF: check cleanup
 *    tksharma  07/29/11 - Moved EncryptionUtily from foundation project to
 *                         EncryptionClient
 *    rrkohli   07/19/11 - encryption cr
 *    rrkohli   06/22/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.2  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 15 2004 13:46:56   bwf
 * Set MSR Model to null so that Validate DL passes.
 * Resolution for 3622: After receiving error msg for bad Driver's License swipe, will not allow manual entry
 *
 *    Rev 1.1   Nov 13 2003 16:56:26   bwf
 * Create bad card swipe scree for check id entry.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 07 2003 16:11:46   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredData;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This class displays the get id number screen.
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class GetCheckIDNumberSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * This method displays the get id number screen.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the cargo, models, and tender attributes
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        CheckEntryBeanModel model = new CheckEntryBeanModel();
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();

        // set argument text with localized text
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        beanModel.setArguments(cargo.getLocalizedPersonalIDCode().getText(lcl));

        model.setPromptAndResponseModel(beanModel);

        // get already entered if there
        String idNum = (String)tenderAttributes.get(TenderConstants.ID_NUMBER);
        if (idNum != null)
        {
            model.setIDNumber(idNum);
        }

        // determine which screen to display
        UtilityIfc util;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        String idType = (String)tenderAttributes.get(TenderConstants.ID_TYPE);
        // if dl or state id and swipe drivers license parameter
        if ((idType.equals("DriversLicense") || idType.equals("StateID"))
                && util.getParameterValue("DriversLicenseSwipe", "Y").equals("Y"))
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
        EncipheredDataIfc personalID = null;

        if (letter.getName().equals("Next"))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            String idType = (String)cargo.getTenderAttributes().get(TenderConstants.ID_TYPE);

            UtilityIfc util;
            try
            {
                util = Utility.createInstance();
            }
            catch (ADOException e)
            {
                String message = "Configuration problem: could not instantiate UtilityIfc instance";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = null;

            // set to null in case this is the second try and manual
            // we need to have null to fail on validate dl
            cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, null);

            // get correct model
            if ((idType.equals("DriversLicense") || idType.equals("StateID"))
                    && util.getParameterValue("DriversLicenseSwipe", "Y").equals("Y"))
            {
                model = (CheckEntryBeanModel)ui.getModel(POSUIManagerIfc.ENTER_ID_NUMBER_SWIPE);

                if (model.isCardSwiped())
                {
                    MSRModel msrModel = model.getMSRModel();
                    cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, msrModel);
                    cargo.getTenderAttributes().put(TenderConstants.ID_ENTRY_METHOD, EntryMethod.Swipe);

                    // set Encrypted Personal from model and create
                    String encryptedPersonalId = msrModel.getEncipheredCardData().getEncryptedAcctNumber();
                    personalID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                            encryptedPersonalId);
                }
                else
                {
                    try
                    {
                        personalID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                                model.getIDNumber().getBytes());
                        cargo.getTenderAttributes().put(TenderConstants.ID_ENTRY_METHOD, EntryMethod.Manual);
                    }
                    catch (EncryptionServiceException e)
                    {
                        logger.error("Could not encrypt text" + e.getLocalizedMessage());
                    }
                }
            }
            else
            {
                model = (CheckEntryBeanModel)ui.getModel(POSUIManagerIfc.ENTER_ID_NUMBER);
                try
                {
                    personalID = new EncipheredData(model.getIDNumber().getBytes());
                }
                catch (EncryptionServiceException e)
                {
                    logger.error("Could not encrypt text" + e.getLocalizedMessage());
                }
                cargo.getTenderAttributes().put(TenderConstants.ID_ENTRY_METHOD, EntryMethod.Manual);
            }
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_DATA_ID_NUMBER, personalID);
        }

    }

}
