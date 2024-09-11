/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/ValidateMICRSite.java /main/13 2012/03/29 15:26:16 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/25/11 - encryption cr issues fix
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    rrkohli   07/19/11 - encryption CR
 *    rrkohli   06/22/11 - Encryption CR
 *    ohorne    05/09/11 - clean-up
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
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
 *    Rev 1.0   Nov 07 2003 16:11:54   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This method determines if the info enter into get check site was valid.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class ValidateMICRSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/13 $";
    
    /**
     * This method determines if the info entered was correct.
     * 
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        byte[] account = null;
        
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();

        String ABANumber = (String) tenderAttributes.get(TenderConstants.ABA_NUMBER);
        
        EncipheredDataIfc accountNumber = (EncipheredDataIfc) tenderAttributes.get(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER);
        account = accountNumber.getDecryptedNumber();

        if(tenderAttributes.get(TenderConstants.ENTRY_METHOD).equals(EntryMethod.Manual))
        { // was it entered when it should have been swiped
            try
            {
                if (pm.getBooleanValue(ParameterConstantsIfc.TENDER_SwipeMICRFirst) && cargo.getMicrCounter() < 1)
                { // display error
                    displayDialog(ui, "MustScanMicr", 
                                  DialogScreensIfc.ACKNOWLEDGEMENT, "Failure");  
                }
                else if(StringUtils.isEmpty(ABANumber) || StringUtils.isEmpty(new String(account)))
                {  // display error
                   Util.flushByteArray(account);
                   displayDialog(ui, "BadMICRReadError", DialogScreensIfc.ERROR, "Failure");
                }   
                else
                {
                    Util.flushByteArray(account);
                    bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
                }     
            }
            catch (ParameterException e)
            {
                Util.flushByteArray(account);
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
                logger.error("Unable to determine SwipeMICRFirst parameter.", e);
            }                        
        }
        else
        {
            String checkNum = (String) tenderAttributes.get(TenderConstants.CHECK_NUMBER);
            // check if everything got read
            if (StringUtils.isEmpty(ABANumber) || StringUtils.isEmpty(new String(account))
                    || checkNum.equals("") || (ABANumber.indexOf('?') != -1)
                    || (new String(account).indexOf('?') != -1) || (checkNum.indexOf('?') != -1))
            { // display error
                Util.flushByteArray(account);
                displayDialog(ui, "BadMICRReadError", DialogScreensIfc.ERROR, "Failure");
            }
            else
            {
                Util.flushByteArray(account);
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }            
        }
    }

    /**
     * Display the specified Dialog screen
     * 
     * @param POSUIManagerIfc UI Manager to handle the IO
     * @param String name of the Dialog to display
     * @param int type dialog type -- types are in DialogScreensIfc
     * @param String letter name
     */
    private void displayDialog(POSUIManagerIfc ui, String name, int type, String letter)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(type);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
