/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/StoreCreditActionSite.java /main/17 2013/03/04 14:33:28 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  03/04/13 - Handling postvoided store credit certificate error
 *                         condition
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    rabhawsa  08/20/12 - removed placeholder from key TenderRedeemed
 *    jswan     04/11/12 - Modified to support centralized validation of gift
 *                         certificates and store credits.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    jkoppolu  04/01/11 - Added check to see if same storeCredit is being used
 *                         in a single transaction.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

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
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * @author blj
 */
@SuppressWarnings("serial")
public class StoreCreditActionSite extends PosSiteActionAdapter
{
    /**
     * Add store credit tender to the transaction.
     * 
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        // add tender type to attributes
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        
        tenderAttributes.put(TenderConstants.STORE_NUMBER, storeID);
        tenderAttributes.put(TenderConstants.STATE, TenderCertificateIfc.REDEEMED);
        String storeCreditNumber = (String)tenderAttributes.get(TenderConstants.NUMBER);
        String storeCreditAmount = (String)tenderAttributes.get(TenderConstants.AMOUNT);

        // check if same storeCredit is being used in a single transaction
        if(cargo.isStoreCreditUsed(storeCreditNumber,storeCreditAmount))
        {
            displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, "Invalid");
            return;
        }

        TenderStoreCreditADO storeCreditTender = null;
        if (cargo.getTenderADO() == null)
        {
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
                storeCreditTender = (TenderStoreCreditADO) factory.createTender(tenderAttributes);
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                TenderErrorCodeEnum error = e.getErrorCode();
                if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
                {
                    assert(false) : "This should never happen, because UI enforces proper format";
                }
            }
        }
        else
        {
            storeCreditTender = (TenderStoreCreditADO) cargo.getTenderADO();
        }

        // add the tender to the transaction
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        try
        {
            // calculate base tender amount for foreign certificates
            // TODO: move after verification that this is correct
            // storeCreditTender.calculateBaseTenderAmount();
            storeCreditTender.setTenderAttributes(tenderAttributes);
            storeCreditTender.getTenderAttributes();
            txnADO.addTender(storeCreditTender);
            cargo.setLineDisplayTender(storeCreditTender);
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
            registerJournal.journal(storeCreditTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();

            // save tender in cargo
            cargo.setTenderADO(storeCreditTender);
            
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            if (error == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                String args[] = new String[2];

                args[0] = (String)storeCreditTender.getTenderAttributes().get(TenderConstants.NUMBER);
                args[1] = (String)storeCreditTender.getTenderAttributes().get(TenderConstants.REDEEM_DATE);

                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderRedeemed", args, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_NUMBER )
            {
                String[] args = {(String) tenderAttributes.get(TenderConstants.NUMBER)};
                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CERTIFICATE || error == TenderErrorCodeEnum.CERTIFICATE_VOIDED)
            {
                displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CURRENCY)
            {
                displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditCurrency", null, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                // display dialog screen
                cargo.setTenderADO(storeCreditTender);
                String type = utility.retrieveDialogText("StoreCredit", "StoreCredit");
                String args[] = {type, type};
                displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Offline");
                return;
            }
        }
    }

    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        if (letter != null)
        {
            UIUtilities.setDialogModel(ui, screenType, message, args, letter);
        }
        else
        {
            UIUtilities.setDialogModel(ui, screenType, message, args);
        }
    }
}
