package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderCash;
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
import oracle.retail.stores.pos.ado.tender.TenderCashADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.CashTenderActionSite;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;


public class MAXEWalletCashTenderActionSite extends CashTenderActionSite
{
    private static final long serialVersionUID = 7584938423225256838L;

    /**
     * Create a cash tender and attempt to add it to the transaction.
     * If validation fails, either punt, or attempt override, depending on the
     * problem.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // If we already have the cash tender in cargo, we have used it to
        // try and override the tender limits, attempt to add it to the txn
        // again.
        TenderCashADO cashTender = null;
        if (cargo.getTenderADO() == null)
        {
            // Get tender attributes
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            // add tender type
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CASH);
            // below code is added by aks for ewallet 1
           // tenderAttributes.put("EWALLET_TENDER", "true");
            try
            {
                // create a new cash tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                cashTender = (TenderCashADO)factory.createTender(tenderAttributes);
                //System.out.println(tenderAttributes.get("AMOUNT").toString());
                //Float amt = Float.parseFloat(tenderAttributes.get("AMOUNT").toString());
                Float amt = Float.parseFloat(tenderAttributes.get("AMOUNT").toString().replace(",", ""));
                if(amt.compareTo((float) 0.00) == -1)
                ((TenderCash)cashTender.toLegacy()).setCollected(false);
                ((MAXTenderCash)cashTender.toLegacy()).setEwalletTenderType(false);
                ((MAXTenderCash)cashTender.toLegacy()).setTypeCode(21);
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
                    // TODO: uncomment assert() when moved to JDK1.4
                    //assert(false) : "This should never happen, because UI enforces proper format";
                }
            }
        }
        else
        {
            cashTender =  (TenderCashADO)cargo.getTenderADO();
        }
        
        // attempt to add Cash tender to transaction
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.validateTenderLimits(cashTender.getTenderAttributes());
            txnADO.addTender(cashTender);
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
        catch (TenderException e)
        {
            // There was a problem parsing the tender attributes data.
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(cashTender);                
            
              //  displayErrorDialog(bus, "AmountExceedsMaximum", DialogScreensIfc.CONFIRMATION);
				 displayErrorDialog(bus, "CashAmountExceedsMaximum", DialogScreensIfc.ERROR);
            }
            else if (error == TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(cashTender);
                displayErrorDialog(bus, "CashBackExceedsLimit", DialogScreensIfc.CONFIRMATION);
            }
            else if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(bus, "OvertenderNotAllowed", DialogScreensIfc.ERROR);
            }
        }
    }
    
}
