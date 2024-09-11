/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/ValidateIDInfoSite.java /main/14 2012/03/29 15:26:17 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    rrkohli   07/19/11 - encryption CR
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
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

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates the drivers license if needed or makes sure card swipe is
 * valid.
 * 
 * @version $Revision: /main/14 $
 */
public class ValidateIDInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -8487758230514359859L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * This method validates the drivers license number or card swipe if needed
     * and displays errors if either is invalid.
     * 
     * @param bus BusIfc
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc)bus.getCargo();
        boolean mailLetter = false;

        // if not swiped and DL
        if(cargo.getMSRModel() == null &&
            cargo.getIdTypeName().equals("DriversLicense"))
        {
            // if need to validate license
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
            if(util.getParameterValue("ValidateDriverLicenseFormat", "Y").equals("Y"))
            {
                byte[] IdNumber = null;
                try
                {
                    IdNumber = cargo.getIdNumberEncipheredData().getDecryptedNumber();
                  util.validateDriversLicense(IdNumber,
                      cargo.getIDState(),
                      cargo.getIDCountry());
                  
                    mailLetter = true;
                }
                catch(TenderException e)
                {
                    displayBadDriversLicense(bus, e);
                }
                finally
                {
                    Util.flushByteArray(IdNumber);
                }
            }
            else
            {
                mailLetter = true; // dont validate mail letter
            }
        }
        else if(cargo.getMSRModel() != null)
        {
            // make sure swiped id was ok
            MSRModel msrModel = cargo.getMSRModel();

            byte[] track2 = msrModel.getTrack2Data();
            // if the track 2 data is null or too little data, it is bad
            if (!(track2 != null &&
                track2.length > 1))
            {
               displayBadSwipe(bus);
            }
            else
            {
                mailLetter = true;
            }
        }
        else
        {
            mailLetter = true;
        }

        // mail letter if needed
        if(mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * This method displays the bad id swipe error.
     * 
     * @param bus
     */
    protected void displayBadSwipe(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc)bus.getCargo();

        // Display error message
        DialogBeanModel dialogModel = new DialogBeanModel();
        String args[] = {cargo.getIdTypeName()};

        dialogModel.setArgs(args);
        dialogModel.setResourceID("BadMSRReadError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * This method displays invalid drivers license error.
     * 
     * @param bus
     * @param e The exception to use in determining the text of the error dialog
     */
    protected void displayBadDriversLicense(BusIfc bus, TenderException e)
    {
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc)bus.getCargo();

        // display error message
        TenderErrorCodeEnum errorCode = e.getErrorCode();
        cargo.setAccessFunctionID(RoleFunctionIfc.ACCEPT_INVALID_DL_FORMAT);
        if(errorCode == TenderErrorCodeEnum.INVALID_LICENSE)
        {
            POSUIManagerIfc ui =
                            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UIUtilities.setDialogModel(ui,
                                       DialogScreensIfc.CONFIRMATION,
                                       "InvalidLicense");
        }
    }
}
