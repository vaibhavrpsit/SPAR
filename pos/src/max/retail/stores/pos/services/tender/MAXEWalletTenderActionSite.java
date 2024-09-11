package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
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

public class MAXEWalletTenderActionSite extends CashTenderActionSite
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
        MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
        //System.out.println("Arrive in maxEwallettenderactionsite");
        //System.out.println("Cargo Value :"+cargo.isEWalletTenderFlag());
        // If we already have the cash tender in cargo, we have used it to
        // try and override the tender limits, attempt to add it to the txn
        // again.
        //System.out.println("47 :"+cargo.eWalletMobileNumber);
        //System.out.println("48 :"+cargo.eWalletTraceId);
        //System.out.println("cargo.geteWalletTraceId() :"+cargo.eWalletReturnGetOtpResponse);
        //System.out.println("cargo.geteWalletTraceId() :"+cargo.isEWalletTenderFlag());
        TenderCashADO cashTender = null;
        if (cargo.getTenderADO() == null)
        {
        	//System.out.println("Arrive in if condition maxEwallettenderactionsite");
            // Get tender attributes
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            // add tender type
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CASH);
            
            try
            {
				
				  if(cargo.getOxigenAppliedAmt()!=null) { tenderAttributes.put("AMOUNT",
				  cargo.getOxigenAppliedAmt()); }
				 
                // create a new cash tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                cashTender = (TenderCashADO)factory.createTender(tenderAttributes);
                //System.out.println(tenderAttributes.get("AMOUNT").toString());
                //Float amt = Float.parseFloat(tenderAttributes.get("AMOUNT").toString());
                Float amt = Float.parseFloat(tenderAttributes.get("AMOUNT").toString().replace(",", ""));
               //System.out.println("Float amt "+amt);
                if(amt.compareTo((float) 0.00) == -1) {
                	//System.out.println("Inside if condition 72");
                	((TenderCash)cashTender.toLegacy()).setCollected(false);
                	//((TenderCash)cashTender.toLegacy()).setCollected(true);
                //	System.out.println("((TenderCash)cashTender.toLegacy()).setCollected(false) :"+cashTender);
                }
                
                //changes by kamlesh 
                //if(cargo.isEWalletTenderFlag()==true)
                MAXSaleReturnTransaction abc =(MAXSaleReturnTransaction)cargo.getTransaction();
               // System.out.println("ABC value :"+abc);
               // System.out.println("(abc.isEWalletTenderFlag()) :"+(abc.isEWalletTenderFlag()));
               if (abc.isEWalletTenderFlag()){
                //if(((TenderCash)cashTender.toLegacy()).isEWalletTenderType()==true) {
                ((TenderCash)cashTender.toLegacy()).setEWalletTenderType(true);
               // System.out.println(" ((TenderCash)cashTender.toLegacy()).setEWalletTenderType(true)");	
                ((TenderCash)cashTender.toLegacy()).setTypeCode(21);
                }
                
                //end Here
                
                //System.out.println("cashTender 70 :"+cashTender);
                //}
				/*
				 * else {
				 * System.out.println("Value in else condition is:"+((TenderCash)cashTender.
				 * toLegacy()).isEWalletTenderType());
				 * ((TenderCash)cashTender.toLegacy()).setTypeCode(0); }
				 */
               
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
        	//System.out.println("Arrive in else condition maxEwallettenderactionsite");
        	//System.out.println("cashTender"+ cashTender);
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
