/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/GetDocumentFaceValueAisle.java /main/2 2012/04/12 06:12:23 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.CertificateTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This class gets the face value amount from the UI, sets the base and 
    currency amounts and journals the tender.  This occurs only when the 
    validation is off line.
    $Revision: /main/2 $
 **/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class GetDocumentFaceValueAisle extends PosLaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method gets the face value amount from the UI, sets the base and 
        currency amounts and journals the tender.  This occurs only when the 
        validation is off line.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.LaneActionIfc#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        TenderCertificateIfc tenderLineItem = (TenderCertificateIfc)cargo.getTenderADO().toLegacy();
        AbstractTenderDocumentIfc document = null;
        
        if (tenderLineItem instanceof TenderStoreCreditIfc)
        {
            document = ((TenderStoreCreditIfc)tenderLineItem).getBaseDocument();
            // Since this document has not been retrieved for validation, these
            // values are not available.  Set them known, but useless values.
            ((StoreCreditIfc)document).setFirstName("offline");
            ((StoreCreditIfc)document).setLastName("offline");
        }
        else
        if (tenderLineItem instanceof TenderGiftCertificateIfc)
        {
            document = ((TenderGiftCertificateIfc)tenderLineItem).getBaseDocument();
            // Since this document has not been retrieved for validation, these
            // values are not available.  Set them known, but useless values.
            TransactionIDIfc transID = DomainGateway.getFactory().getTransactionIDInstance();
            transID.setTransactionID(cargo.getCurrentTransactionADO().getTransactionID());
            ((GiftCertificateDocumentIfc)document).setIssuingStoreID(transID.getStoreID());
            ((GiftCertificateDocumentIfc)document).setIssuingWorkstationID(transID.getWorkstationID());
            ((GiftCertificateDocumentIfc)document).setIssuingBusinessDate(transID.getBusinessDate());
            ((GiftCertificateDocumentIfc)document).setIssuingTransactionSeqNumber(transID.getSequenceNumber());
            ((GiftCertificateDocumentIfc)document).setIssuingLineItemNumber(0);
        }
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        // Get the amount entered for the document
        String amount = ui.getInput().trim();
        CertificateTypeEnum type = (CertificateTypeEnum)cargo.getTenderADO().getTenderAttributes().
            get(TenderConstants.CERTIFICATE_TYPE);

        if (type != null && type.equals(CertificateTypeEnum.FOREIGN))
        {
            CurrencyTypeIfc tenderType = (CurrencyTypeIfc)cargo.getTenderAttributes().get(TenderConstants.ALTERNATE_CURRENCY_TYPE);
            CurrencyIfc faceAmount = DomainGateway.getAlternateCurrencyInstance(tenderType.getCurrencyCode(), amount);
            ((TenderAlternateCurrencyIfc)tenderLineItem).setAlternateCurrencyTendered(faceAmount);
            tenderLineItem.setAmountTender(DomainGateway.convertToBase(faceAmount));
            document.setAmount(faceAmount);
        }
        else
        {
            CurrencyIfc faceAmount = DomainGateway.getBaseCurrencyInstance(amount);
            tenderLineItem.setAmountTender(faceAmount);
            document.setAmount(faceAmount);
        }

        // add tender with no validation
        cargo.getCurrentTransactionADO().addValidTender(cargo.getTenderADO());
        cargo.setLineDisplayTender(cargo.getTenderADO());

        // journal the added tender
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException adoExcep)
        {
            String message = "Configuration problem: could not instantiate JournalFactoryIfc instance";
            logger.error(message, adoExcep);
            throw new RuntimeException(message, adoExcep);
        }
        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
        registerJournal.journal(cargo.getTenderADO(), JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
