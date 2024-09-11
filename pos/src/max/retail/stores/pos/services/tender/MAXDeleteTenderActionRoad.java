/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *
 *	Rev 1.0		May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender;

import max.retail.stores.domain.arts.MAXCertificateTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.utility.MAXStoreCredit;


public class MAXDeleteTenderActionRoad extends PosLaneActionAdapter {
	
	private static final long serialVersionUID = -2279275675396089690L;
	public static final String LANENAME = "MAXDeleteTenderActionRoad";

	public void traverse(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();

		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		TenderADOIfc tenderADO = cargo.getLineDisplayTender();
		//Change for Rev 1.1: Start	
		MAXTenderStoreCreditIfc tscRedeem = (MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem();
		if (tscRedeem !=null && tscRedeem instanceof MAXTenderStoreCredit)
		{
		MAXStoreCredit storeCredit = (MAXStoreCredit) tscRedeem.getStoreCredit();
		
		String SCId = storeCredit.getDocumentID();
		MAXCertificateTransaction dataTransaction = null;
		dataTransaction = (MAXCertificateTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);
		boolean result = dataTransaction.updateStoreCreditLockStatus(SCId,"N");
		}
		//Change for Rev 1.1: Ends
		txnADO.removeTender(tenderADO);

		// Journal the removal of the tender
		JournalFactoryIfc jrnlFact = null;
		try {
			jrnlFact = JournalFactory.getInstance();
		} catch (ADOException e) {
			logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
			throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
		}
		RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
		registerJournal.journal(tenderADO, JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);
	}
}
