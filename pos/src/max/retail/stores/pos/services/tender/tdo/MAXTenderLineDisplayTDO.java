/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.tender.tdo;

import java.util.Locale;

import org.python.parser.ast.If;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.transaction.MAXSaleReturnTransactionADO;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderLineDisplayTDO;

public class MAXTenderLineDisplayTDO extends TenderLineDisplayTDO {
	 public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
	    {
		 String tenderTypeDesc="";
	        // convert txn to RDO
	        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

	        // our display line
	        String result = "";
	        
	        int tenderLISize = txnRDO.getTenderLineItemsSize();
	        if(tenderLISize > 0)
	        {
	            // process most current line item
	            TenderLineItemIfc tenderLI = txnRDO.getTenderLineItems()[tenderLISize - 1];
	           // tenderTypeDesc = MAXTenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenderLI.getTypeCode()];
	            if(tenderLI instanceof TenderCash) {
	            	TenderCash tenderCash = (TenderCash) tenderLI;
	            	if (tenderCash.isEWalletTenderType()) {
	            		//((TenderCash) tenderLI).setTypeCode(20);
	            		tenderTypeDesc = "EWallet";
					}
	            	else {
		            	tenderTypeDesc = MAXTenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenderLI.getTypeCode()];
		            }
	            }
	            else if(tenderLI instanceof TenderStoreCreditIfc ) {
	            	
	            	if(txnADO instanceof MAXSaleReturnTransactionADO) {
	            		MAXSaleReturnTransaction trans = ((MAXSaleReturnTransaction)(txnADO.toLegacy()));
		            	
		            	if((trans).isEWalletTenderFlag()) {
		            		tenderTypeDesc = "EWallet";
		            	}
	            	}
	            	else {
	            		tenderTypeDesc = MAXTenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenderLI.getTypeCode()];
	            	}
	            	
	            	
	            }
	            else {
	            	tenderTypeDesc = MAXTenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenderLI.getTypeCode()];
	            }
	            // get context
	            ADOContextIfc context = ContextFactory.getInstance().getContext();
	            // get utility manager
	            UtilityManagerIfc utility =  (UtilityManagerIfc) context.getManager(UtilityManagerIfc.TYPE);
	            // get localized text for tender type
	            tenderTypeDesc = utility.retrieveLineDisplayText(tenderTypeDesc,tenderTypeDesc);
	            // get locale for pole display
	            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.POLE_DISPLAY);
	            // get formatted tender amount
	            String displayTenderAmount = null;
	            if (tenderLI instanceof TenderAlternateCurrencyIfc &&
	                ((TenderAlternateCurrencyIfc)tenderLI).getAlternateCurrencyTendered() != null)
	            {
	                //When alternate currency is tendered, 
	                //the tender amount displayed is the alternate currency value
	                displayTenderAmount = 
	                  ((TenderAlternateCurrencyIfc)tenderLI).getAlternateCurrencyTendered().toFormattedString(locale);
	            }
	            else
	            {
	                displayTenderAmount = tenderLI.getAmountTender().toFormattedString(locale);
	            }
	                        
	            
	            // format string
	            StringBuffer sb = new StringBuffer();
	            int length = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - displayTenderAmount.length();
	            sb.append(Util.formatTextData(tenderTypeDesc, length, false))
	              .append(Util.formatTextData(displayTenderAmount, displayTenderAmount.length(), false));
	            
	            result = sb.toString();
	        }        
	        return result;
	    }
}
