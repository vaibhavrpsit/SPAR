/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * 
 *   Revision 1.0  11 MAY, 2017 initial revision
 *   partial payment with GC, and over tendering with cash showing unexpected error.
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderGiftCardADO;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
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
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCashADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.ForcedCashChangeActionSite;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * A cash tender is created with the sum amount of all gift card tenders whose
 * remaining balance was depleted.
 */
public class MAXForcedCashChangeActionSite extends ForcedCashChangeActionSite
{
    private static final long serialVersionUID = 8343448437190841265L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        //commented for rev 1.0 : don't go for authorization.
       /* // First see if any tenders need authorization before continuing
        if (cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING).length > 0)
        {
        	boolean auth=true;
        	TenderADOIfc[] tenderADO=cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING);
        	for (int i = 0; i < tenderADO.length; i++) {
				if(tenderADO[i] instanceof MAXTenderGiftCardADO)
				{
						auth=false; break;
				}
        	}
        	if(auth)
        	{
        		bus.mail(new Letter("Authorize"), BusIfc.CURRENT);
        		return;
            }
        } 
*/
        if (((AbstractRetailTransactionADO)cargo.getCurrentTransactionADO()).getForcedCashChangeAmount().signum() ==
                        CurrencyIfc.POSITIVE)
        {
            AbstractRetailTransactionADO txn = (AbstractRetailTransactionADO)cargo.getCurrentTransactionADO();
            HashMap<Object,Object> tenderAttributes = new HashMap<Object,Object>(2);
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CASH);
            
            // If we've already added the forced change to the transcation, we don't want to do that
            // again.  This block of code ensures that
            CurrencyIfc forcedChange = txn.getForcedCashChangeAmount();
            CurrencyIfc negativeCash = getNegativeCashAmount(txn);
            if (forcedChange.equals(negativeCash))
            {
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                return;
            }
            
            String amountStr = txn.getForcedCashChangeAmount().getDecimalValue().toString(); 
            amountStr = "-" + amountStr; // negative because it's change
            tenderAttributes.put(TenderConstants.AMOUNT, amountStr); 
            
            TenderCashADO cashTender = null;
            try
            {
                // create a new cash change tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                cashTender = (TenderCashADO)factory.createTender(tenderAttributes);
                
                txn.addTender(cashTender);
                cargo.setLineDisplayTender(cashTender);
                
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
                registerJournal.journal(cashTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
            
                // mail a letter
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                
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
            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
    
}
