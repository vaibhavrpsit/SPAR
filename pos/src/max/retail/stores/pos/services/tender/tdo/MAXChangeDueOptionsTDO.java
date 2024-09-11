/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
    Rev 1.1       Jyoti			31/05/2013		Jyoti Changes For Bug 7447
  Rev 1.0       Tanmaya			24/05/2013		Initial Draft: Changes for Store Credit
  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.tdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.tdo.ChangeDueOptionsTDO;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

public class MAXChangeDueOptionsTDO extends ChangeDueOptionsTDO {

	 public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
	    {
	        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TenderTDOConstants.TRANSACTION);
	        // Get RDO version of transaction for use in some processing
	        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
	        
	        // get new tender bean model
	        TenderBeanModel model = new TenderBeanModel();
	        // populate tender bean model w/ tender and totals info
	        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());
	        
	        // We only want the forced cash change item here.
	        TenderADOIfc[] cashChange = txnADO.getTenderLineItems(TenderLineItemCategoryEnum.FORCED_CASH_CHANGE);
	        if (cashChange.length > 0)
	        {
	            Vector lineItemVector = new Vector();
	            lineItemVector.add(((ADOIfc)cashChange[0]).toLegacy());
	            model.setTenderLineItems(lineItemVector);
	        }
	        model.setLocalButtonBeanModel(getNavigationBeanModel(txnADO.getEnabledChangeOptions()));
	        if(txnRDO.getTenderLineItemsVector()!=null&&txnRDO.getTenderLineItemsVector().size()==1 && txnRDO.getTenderLineItemsVector().get(0) instanceof TenderStoreCreditIfc)
	        	model.setLocalButtonBeanModel(getNavigationBeanModel(new TenderTypeEnum[]{TenderTypeEnum.STORE_CREDIT}));	
	        
	        PromptAndResponseModel parModel = new PromptAndResponseModel();
	        String change = txnADO.getBalanceDue().abs()
	               .toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
	                        

	        parModel.setResponseText(change);
	        parModel.setResponseEditable(false);
	        //parModel.setGrabFocus(false);
	        model.setPromptAndResponseModel(parModel);
	        
	        return model;
	    }

 protected NavigationButtonBeanModel getNavigationBeanModel(TenderTypeEnum[] enabledTypes)
	    {
	        // convert to list
	        ArrayList typeList = new ArrayList(enabledTypes.length);
	        for (int i = 0; i < enabledTypes.length; i++)
	        {
	            typeList.add(enabledTypes[i]);
	        }
	        
	        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
	        navModel.setButtonEnabled(CASH_TYPE, typeList.contains(TenderTypeEnum.CASH));
	        //navModel.setButtonEnabled(MAIL_CHECK_TYPE, typeList.contains(TenderTypeEnum.MAIL_CHECK));  //For Bug 7447
	        navModel.setButtonEnabled(GIFT_CARD_TYPE, typeList.contains(TenderTypeEnum.GIFT_CARD));
	        navModel.setButtonEnabled(GIFT_CERTIFICATE_TYPE, typeList.contains(TenderTypeEnum.GIFT_CERT));
	        navModel.setButtonEnabled(STORE_CREDIT_TYPE, typeList.contains(TenderTypeEnum.STORE_CREDIT));
	        
	        return navModel;
	    }  
}
