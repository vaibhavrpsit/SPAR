/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/GetCheckMICRSite.java /main/13 2012/08/07 16:20:01 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/29/12 - wptg - removed EnterBankInfo and merged string with
 *                         CheckEntryScreenPrompt
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    ohorne    09/08/11 - fix for MICR ClassCastException
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    ohorne    08/18/11 - EntryMode is now MICR for if Check MICR'ed
 *    rrkohli   07/25/11 - encryption cr issues fix
 *    masahu    07/20/11 - Encryption CR: Fix POS and CO build issues
 *    rrkohli   07/19/11 - encryption CR
 *    rrkohli   06/27/11 - Encryption CR
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/26 21:14:55  bwf
 *   @scr 6203 Fixed text on micr screen.
 *
 *   Revision 1.3  2004/07/26 20:16:06  bwf
 *   @scr 6203 Fixed text on micr screen.
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
 *    Rev 1.2   05 Dec 2003 14:24:26   gclancy
 * Added check number to the tender attributes
 *
 *    Rev 1.1   Nov 13 2003 12:49:08   bwf
 * Use integer instead of string for country code.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 07 2003 16:11:50   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import jpos.MICRConst;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.utility.ParseMICRNumber;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
    This class gets the mICR.
    $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class GetCheckMICRSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1835562624007059844L;

    /**
        revision number supplied by source-code control system
    */
    public static final String revisionNumber = "$Revision: /main/13 $";

    //micr country code statics
    public static final int MICR_USA              = 1;
    public static final int MICR_CANADA           = 2;
    public static final int MICR_UNKNOWN          = 3;

    /**
     * This arrive method display the screen.
     *
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel globalButton = new NavigationButtonBeanModel();

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
        CheckEntryBeanModel model = new CheckEntryBeanModel();
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        if (util.getParameterValue("SwipeMICRFirst", "Y").equals("Y") &&
            cargo.getMicrCounter() < 1)
        {
            parModel.setArguments("");
            model.setDisplayMicrLineFlag(false);
            globalButton.setButtonEnabled(CommonActionsIfc.NEXT, false);
            model.setGlobalButtonBeanModel(globalButton);
        }
        else
        {

            parModel.setArguments("");
            model.setDisplayMicrLineFlag(true);
            globalButton.setButtonEnabled(CommonActionsIfc.NEXT, true);
            model.setGlobalButtonBeanModel(globalButton);
        }
        model.setPromptAndResponseModel(parModel);
        ui.showScreen(POSUIManagerIfc.CHECK_ENTRY, model);
    }

    /**
     * This depart method captures the user input.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        EncipheredDataIfc accountNumber = null;
        EncipheredDataIfc micrNumber = null;
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        CheckEntryBeanModel model = (CheckEntryBeanModel) ui.getModel(POSUIManagerIfc.CHECK_ENTRY);
        byte[] accountNumberBytes = null;
        if (model.isCheckMICRed())
        {
            //MICR input by device
            cargo.incrementMicrCounter();
            MICRModel micr = model.getMICRData();
            accountNumberBytes = micr.getAccountNumber().getBytes();
            try
            {
                micrNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(micr.getRawData().getBytes());
                accountNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(accountNumberBytes);
            }
            catch (EncryptionServiceException e)
            {
                logger.error("Could not encrypt text" + e.getLocalizedMessage());
            }
            finally
            {
                Util.flushByteArray(accountNumberBytes);
            }
            cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Micr);
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER, micrNumber);
            cargo.getTenderAttributes().put(TenderConstants.ABA_NUMBER, micr.getTransitNumber());
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER, accountNumber);
            cargo.getTenderAttributes().put(TenderConstants.CHECK_NUMBER, micr.getSerialNumber());

            int countryCode;
            if (micr.getCountryCode() == MICRConst.MICR_CC_USA)
            {
                countryCode = MICR_USA;
            }
            else if (micr.getCountryCode() == MICRConst.MICR_CC_CANADA)
            {
                countryCode = MICR_CANADA;
            }
            else
            {
                countryCode = MICR_UNKNOWN;
            }
            cargo.getTenderAttributes().put(TenderConstants.COUNT, new Integer(countryCode));
        }
        else
        {
            //MICR input by manual keyboard entry
            byte[] micrNumberBytes = model.getMICRNumber().getBytes();
            ParseMICRNumber parser = new ParseMICRNumber( model.getMICRNumber().getBytes());
            byte[] abaNumberBytes = parser.getTransitNumberByte();
            accountNumberBytes = parser.getAccountNumberByte();
            try
            {
                micrNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(micrNumberBytes);
                accountNumber = FoundationObjectFactory.getFactory().createEncipheredDataInstance(accountNumberBytes);
            }
            catch (EncryptionServiceException e)
            {
                logger.error("Could not encrypt text" + e.getLocalizedMessage());
            }
            finally
            {
                Util.flushByteArray(accountNumberBytes);
                Util.flushByteArray(micrNumberBytes);
            }

            cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
            cargo.getTenderAttributes().put(TenderConstants.ABA_NUMBER, new String(abaNumberBytes));
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER, accountNumber);
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_DATA_MICR_NUMBER, micrNumber);
        }
    }
}
