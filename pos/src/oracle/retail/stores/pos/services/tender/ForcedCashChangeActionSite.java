/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ForcedCashChangeActionSite.java /main/13 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/30/2007 9:01:57 AM   Anda D. Cadar   code
 *         cleanup
 *    5    360Commerce 1.4         5/18/2007 9:19:17 AM   Anda D. Cadar
 *         always use decimalValue toString
 *    4    360Commerce 1.3         4/25/2007 8:52:46 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse   
 *
 *   Revision 1.6.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.6  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.5  2004/04/28 19:45:10  epd
 *   @scr 4513 Fixes site so that passing through site multiple times is protected.
 *
 *   Revision 1.4  2004/04/16 15:19:24  epd
 *   @scr 4322 Tender invariant work.
 *
 *   Revision 1.3  2004/04/15 22:03:37  epd
 *   @scr 4322 Updates for Tender Invariant work: handling Change invariant
 *
 *   Revision 1.2  2004/04/15 14:40:01  epd
 *   @scr 4322 Tender Invariant work
 *
 *   Revision 1.1  2004/04/14 22:37:53  epd
 *   @scr 4322 Tender Invariant work.  Specifically for change invariant
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
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
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * A cash tender is created with the sum amount of all gift card tenders whose
 * remaining balance was depleted.
 */
public class ForcedCashChangeActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8343448437190841265L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // First see if any tenders need authorization before continuing
        if (cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.AUTH_PENDING).length > 0)
        {
            bus.mail(new Letter("Authorize"), BusIfc.CURRENT);
            return;
        } 

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
    
    /**
     * Gets the amount of negative cash change (as a positive value)
     * @param txn
     * @return
     */
    protected CurrencyIfc getNegativeCashAmount(AbstractRetailTransactionADO txn)
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();
        
        TenderADOIfc[] tenders = txn.getTenderLineItems(TenderLineItemCategoryEnum.ALL);
        for (int i = 0; i < tenders.length; i++)
        {
            TenderADOIfc tender = tenders[i];
            if (tender.getTenderType() == TenderTypeEnum.CASH &&
                tender.getAmount().signum() == CurrencyIfc.NEGATIVE)
            {
                result = result.add(tender.getAmount().abs());
            }
        }
        return result;
    }
}
