/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:42 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:51 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
     $
     Revision 1.4.2.1  2004/11/12 14:28:53  kll
     @scr 7337: JournalFactory extensibility initiative

     Revision 1.4  2004/07/22 22:38:41  bwf
     @scr 3676 Add tender display to ingenico.

     Revision 1.3  2004/07/16 19:35:04  bwf
     @scr 5997 Fixed debit bad mag swipe screen.

     Revision 1.2  2004/07/12 21:42:19  bwf
     @scr 6125 Made available expiration validation of debit before pin.

     Revision 1.1  2004/04/08 19:30:59  bwf
     @scr 4263 Decomposition of Debit and Credit.

     Revision 1.2  2004/02/12 16:48:22  mcs
     Forcing head revision

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.2   Jan 07 2004 15:51:44   epd
 * supports new dialog for invalid debit number
 * 
 *    Rev 1.1   Dec 02 2003 16:21:22   epd
 * Updates for debit
 * 
 *    Rev 1.0   Dec 01 2003 19:04:40   epd
 * Initial revision.
     
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *  Attempts to create and add the debit to the transaction
 */
public class MAXPineLabDebitTenderActionSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        TenderADOIfc debitTender = cargo.getTenderADO();
        try
        {
            // attempt to add the tender to the transaction
            cargo.getCurrentTransactionADO().addTender(debitTender);
            cargo.setLineDisplayTender(debitTender);
            
            // journal the added tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(debitTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
        
            // mail a letter
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            
            if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE)
            {
                displayBadMagStripeDialog(ui);
                return;
            }
            else if (error == TenderErrorCodeEnum.DEBIT_NOT_SWIPED)
            {
                displayNotSwipedDialog(utility, ui);
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER)
            {
                showInvalidNumberDialog(utility, ui);
                return;
            }
            else if (error == TenderErrorCodeEnum.EXPIRED)
            {
                showExpiredCardDialog(utility, ui);
            }
        }
        
    }
    
    /**
     * Display dialog indicating bad mag swipe
     * @param utility
     * @param ui
     */    
    protected void displayBadMagStripeDialog(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();

        // set model properties
        dialogModel.setResourceID("DebitBadMSRReadError");
        dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    /**
     * Display dialog indicating bad mag swipe
     * @param utility
     * @param ui
     */    
    protected void displayNotSwipedDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidDebitNoSwipe");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

        String cardString = utility.retrieveDialogText("InvalidDebitNoSwipe.Debit", "debit");
        String args[] = new String[2];
        args[0] = cardString;
        args[1] = cardString;

        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }    
    
    //--------------------------------------------------------------------------
    /**
     Shows the Invalid Number Error dialog screen.
     **/
    //--------------------------------------------------------------------------    
    protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidNumberError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

        String[] args = new String[2];
        args[1] = utility.retrieveDialogText("Debit", "Debit");
        args[1] += " " + utility.retrieveDialogText("Number", "number");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        args[0] = args[1].toLowerCase(locale);
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }  
    
    //--------------------------------------------------------------------------
    /**
     * Shows the expired card dialog screen.
     */
    //--------------------------------------------------------------------------
    protected void showExpiredCardDialog(
        UtilityManagerIfc utility,
        POSUIManagerIfc ui)
    {
        // set screen args
        String titleTag = "ExpiredDebitCardTitle";
        String cardString =
            utility.retrieveDialogText("ExpiredCardError.Debit", "debit");

        // Display error message
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ExpiredCardError");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(
            DialogScreensIfc.BUTTON_OK,
            CommonLetterIfc.INVALID);
        dialogModel.setTitleTag(titleTag);
        String args[] = new String[2];
        args[0] = cardString;
        args[1] = cardString;
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
