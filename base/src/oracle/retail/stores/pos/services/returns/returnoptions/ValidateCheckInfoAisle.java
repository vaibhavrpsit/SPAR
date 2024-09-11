/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ValidateCheckInfoAisle.java /main/13 2012/01/26 14:15:51 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    01/26/12 - Fix for Check tender search by masked MICR
 *    ohorne    01/26/12 - XbranchMerge ohorne_bug-13619784 from
 *                         rgbustores_13.4x_generic_branch
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    ohorne    08/10/11 - masked aba and account number for check
 *    nkgautam  09/23/10 - added check for invalid MICR Number
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:27 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.8  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.7  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.6  2004/03/09 17:23:47  baa
 *   @scr 3561 Add bin range, check digit and bad swipe dialogs
 *
 *   Revision 1.5  2004/03/04 20:50:28  baa
 *   @scr 3561 returns add support for units sold
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   05 Feb 2004 23:27:26   baa
 * return multiple items
 * 
 *    Rev 1.2   29 Dec 2003 22:35:26   baa
 * more return enhacements
 * 
 *    Rev 1.1   Dec 29 2003 15:36:24   baa
 * return enhancements
 * 
 *    Rev 1.0   Dec 17 2003 11:37:08   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:06:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 15 2002 13:30:50   jriggins
 * Replaced deprecated EYSDate.toFormattedString() calls in favor of calls which use the locale.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:04:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.Locale;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//------------------------------------------------------------------------------
/**
    Validates user input for return by Check Info and, if valid, assembles SearchCriteria
    for locating Transaction.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------

public class ValidateCheckInfoAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3753837150432829016L;

    /**
     * bank acct field
     */
    public static final String BANK_ACCOUNT_FIELD   = "checkAccountNumberField";
    
    /**
     * MICR Number tag
     */
    public static final String MICR_NUMBER_TAG = "MICR";
    
    //--------------------------------------------------------------------------
    /**     Gets the store number from UI
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
       
        Letter letter = new Letter(CommonLetterIfc.VALIDATE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel)ui.getModel(POSUIManagerIfc.RETURN_BY_CHECK);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo(); 
        byte[] ABANumber = null;
        byte[] accountNumber = null;
        byte[] micrNumber = null;

        // Setup date range
        int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);

        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        if (searchCriteria == null)
        {
           searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();   
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));
       
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // Retrieve check info
        try
        {
            if (model.isCheckMICRed())
            {
                MICRModel micr = model.getChkModel();

                // check MICR data
                if (Util.isEmpty(micr.getTransitNumber()) ||  Util.isEmpty(micr.getAccountNumber()))
                {
                    logger.info("Bad MICR Data received. Prompting user to enter the information manually...");

                    // do not mail a letter here
                    letter = null;
                }
                // if data is Ok, put it in criteria
                else
                {
                    ABANumber = micr.getTransitNumber().getBytes();
                    accountNumber = micr.getAccountNumber().getBytes();
                    micrNumber = new byte[ABANumber.length + accountNumber.length];

                    //concatenate ABA and account numbers to form MICR search string
                    System.arraycopy(ABANumber, 0, micrNumber, 0, ABANumber.length);
                    System.arraycopy(accountNumber, 0, micrNumber, ABANumber.length, accountNumber.length);
                    searchCriteria.setMaskedMICRNumber(getMaskedMICR(micrNumber));

                }
            }
            // manual input data into criteria object
            else
            {
                try
                {
                    micrNumber = model.getValueAsString(BANK_ACCOUNT_FIELD).getBytes();
                }
                catch(Exception ignore){}

                if (micrNumber == null || micrNumber.length == 0)
                {
                    // missing MICR search string 
                    letter = new Letter(CommonLetterIfc.INVALID) ;
                }
                else
                {
                    //mask the search string 
                    searchCriteria.setMaskedMICRNumber(getMaskedMICR(micrNumber));
                }
            }
        }
        finally 
        {
            Util.flushByteArray(ABANumber);
            Util.flushByteArray(accountNumber);
            Util.flushByteArray(micrNumber);
        }

        cargo.setSearchCriteria(searchCriteria);
        cargo.setSearchByTender(true);
        cargo.setHaveReceipt(false);

        if(letter != null && 
                letter.getName().equals(CommonLetterIfc.INVALID))
        {
            String[] args = new String[] {utility.retrieveDialogText(MICR_NUMBER_TAG,"MICR").toLowerCase(locale)};
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args);
        }
        else if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        else
        {
            // Display error message
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("BadMICRReadError");
            dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, CommonLetterIfc.CONTINUE);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Applies mask to MICR Number
     * @param micrNumber ummasked MICR number
     * @return the masked MICR
     */
    protected String getMaskedMICR(byte[] micrNumber)
    {
        //get the number of unmasked leading/trailing digits for a MICR Number (configured in domain.properties)
        int unMaskedfirstDigits = DomainUtil.getNumberOfMicrFirstDigits();
        int unMaskedLastDigits = DomainUtil.getNumberOfMicrLastDigits();
        byte maskChar = (byte)DomainUtil.getMaskChar();

        //apply mask
        String maskedMICR = null;
        try
        {
            for (int i = unMaskedfirstDigits; i < micrNumber.length; i++)
            {
                if (i < (micrNumber.length - unMaskedLastDigits))
                {
                    micrNumber[i]= maskChar;
                }
            }
            maskedMICR = new String(micrNumber);
        }
        finally
        {
            Util.flushByteArray(micrNumber);
        }

        return maskedMICR;
    }
}
