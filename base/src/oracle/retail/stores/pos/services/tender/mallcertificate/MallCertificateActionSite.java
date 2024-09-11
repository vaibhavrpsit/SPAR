/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mallcertificate/MallCertificateActionSite.java /main/14 2014/07/22 16:26:20 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/22/14 - Fortify null deference fix for POS v14.1 phase II
 *    abhinavs  03/19/14 - Fix to validate Mall certificate if it has already
 *                         been tendered out
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:32 PM  Robert Pearse   
 *
 *   Revision 1.2.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.2  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.1  2004/04/02 22:34:35  epd
 *   @scr 4263 Updates to move Mall Cert. tender into sub tour
 *
 *   Revision 1.4  2004/02/17 19:26:17  epd
 *   @scr 0
 *   Code cleanup. Returned unused local variables.
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
 *    Rev 1.0   Dec 11 2003 13:17:08   bwf
 * Initial revision.
 * Resolution for 3538: Mall Certificate Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mallcertificate;

import java.util.HashMap;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderMallCertificateADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This class creates and stores a mall gift certificate tender in the
 * transaction.
 * 
 * @version $Revision: /main/14 $
 */
public class MallCertificateActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1384238595523462973L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * This method tries to create and add tender check.
     * 
     * @param bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        // add tender type to attributes
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.MALL_CERT);
        // adding training mode to attributes
        boolean isTrainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
        tenderAttributes.put(TenderConstants.TRAINING_MODE, new Boolean(isTrainingMode));
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // create the mall certificate tender
        TenderMallCertificateADO mallCertTender = null;
        if (cargo.getTenderADO() == null)
        {
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                mallCertTender = (TenderMallCertificateADO)factory.createTender(tenderAttributes);
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                assert (false) : "This should never happen, because UI enforces proper format";
            }
        }
        else
        {
            mallCertTender = (TenderMallCertificateADO)cargo.getTenderADO();
        }

        // add the tender to the transaction
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.addTender(mallCertTender);

            // set for display
            cargo.setLineDisplayTender(mallCertTender);

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
            registerJournal.journal(mallCertTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            // save tender in cargo
            cargo.setTenderADO(mallCertTender);
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.MALL_CERTIFICATE_NUMBER_ALREADY_TENDERED)
            {
                if(mallCertTender != null)
                {
                    showAlreadyTenderedDialog(utility, ui, mallCertTender.getTenderAttributes());
                }
                return;
            }
            else
            {
                String type = utility.retrieveDialogText("MallCertificate", "Mall Certificate");
                String args[] = { type, type };
                displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Offline");
                return;
            }

        }
    }

    /**
    *
    * @param bus
    * @param screenType
    * @param message
    * @param args
    * @param letter
    */
    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter)
    {
       POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
       UIUtilities.setDialogModel(ui, screenType, message, args, letter);
   }

    private void showAlreadyTenderedDialog(UtilityManagerIfc utility, POSUIManagerIfc ui, HashMap tenderAttributes)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ALREADY_TENDERED");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

        String[] args = new String[1];
        args[0] = utility.retrieveDialogText("MallCertificate", "Mall Certificate");
        args[0] += " number " +(String) tenderAttributes.get(TenderConstants.NUMBER);
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        
    }
        
}
