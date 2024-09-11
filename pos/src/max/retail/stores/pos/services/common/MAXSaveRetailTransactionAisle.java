/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Nov 22, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *	 Changes to capture ManagerOverride for Reporting purpose
 ********************************************************************************/
package max.retail.stores.pos.services.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.arts.MAXCertificateTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXLayawayReadRoundedAmountDataTransaction;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.manager.utility.MAXTransactionUtilityManager;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.UpdateReturnedItemsCommandDataTransaction;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.common.SaveRetailTransactionAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle does all of the necessary processing needed when saving a
 * transaction.
 *
 * @version $Revision: /main/17 $
 */
public class MAXSaveRetailTransactionAisle extends SaveRetailTransactionAisle
{
    private static final long serialVersionUID = -8679816756068789293L;

    /**
     * lane name constants
     */
    public static final String LANENAME = "SaveRetailTransactionAisle";

    /**
     * logger
     */
	 // changes start for rev 1.0
    //public static final Logger logger = Logger.getLogger(SaveRetailTransactionAisle.class);
	public static final Logger logger = Logger.getLogger(MAXSaveRetailTransactionAisle.class);
	//Changes ends for Rev 1.0
    /**
     * thank you tag
     */
    public static final String CPOI_THANK_YOU_TAG = "ThankYou";

    /**
     * thank you default
     */
    public static final String CPOI_THANK_YOU_DEFAULT = "Thank You";

    /**
     * Saves the transaction to database and updates Financial Totals. A letter
     * is sent if it succeeds.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RetailTransactionCargoIfc rtCargo = (RetailTransactionCargoIfc) bus.getCargo();
        RetailTransactionIfc trans = rtCargo.getRetailTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        boolean saveTransSuccess   = true;
        boolean saveHardTotSuccess = true;
		// Changes starts for Rev 1.0
		 MAXLayawayReadRoundedAmountDataTransaction radt = null;
        radt = (MAXLayawayReadRoundedAmountDataTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.TRANSACTION_LAYAWAY_READ_ROUNDED_AMOUNT);
		// Changes ends for Rev 1.0
        // Save the transaction to persistent storage
        // and update financial totals
        try
        {
		//Changes start for Rev 1.0
			if (trans instanceof MAXLayawayTransaction) {
				LayawayIfc l = ((MAXLayawayTransaction) trans).getLayaway();
				String roundedAmt = radt
						.maxLayawayReadRoundedAmountDataTransaction(l);
				if (roundedAmt != null) {
					CurrencyIfc c = DomainGateway
							.getBaseCurrencyInstance(roundedAmt);
					((MAXTransactionTotalsIfc) trans.getTransactionTotals()).setOffTotal(c);
				}
			}
        	//Changes ends for Rev 1.0
            AbstractFinancialCargoIfc afCargo = (AbstractFinancialCargoIfc) bus.getCargo();
            TillIfc                   till = afCargo.getRegister().getCurrentTill();
            RegisterIfc           register = afCargo.getRegister();
            FinancialTotalsIfc totals = null;

            // Don't do this if we are in training mode
            if (!trans.isTrainingMode())
            {
                // This updates the financial totals for the register
                totals = afCargo.getRegister().addTransaction(trans);
            }
         // Changes for Manager Override Report Requirement - Start
    		//if(null != ((MAXSaleReturnTransactionIfc) utility).getManagerOverrideMap())
    		if(null!=utility.getManagerOverrideMap())
            {
    	       	if(null != trans && trans instanceof MAXSaleReturnTransactionIfc)
    	    	{
    			((MAXSaleReturnTransactionIfc)trans).setManagerOverrideMap(utility.getManagerOverrideMap());
    	    	}
    			utility.setManagerOverrideMap(new HashMap());
    			/*HashMap  map=(HashMap) utility.getManagerOverrideMap().get("managerId");
    			System.out.println(map);
    			HashMap  map1=(HashMap) utility.getManagerOverrideMap().get("featureId ");
    			System.out.println(map1);
    			HashMap  map2=(HashMap) utility.getManagerOverrideMap().get("storeCreditId");
    			System.out.println(map2);
    			HashMap  map3=(HashMap) utility.getManagerOverrideMap().get("itemId");
    			System.out.println(map3);
    			HashMap  map4=(HashMap) utility.getManagerOverrideMap().get("transactionID");
    			System.out.println(map4);
    			HashMap  map5=(HashMap) utility.getManagerOverrideMap().get("storeID");
    			System.out.println(map5);
    			HashMap  map6=(HashMap) utility.getManagerOverrideMap().get("wsID");
    			System.out.println(map6);
    			HashMap  map7=(HashMap) utility.getManagerOverrideMap().get("businessDay");
    			System.out.println(map7);
    			HashMap  map8=(HashMap) utility.getManagerOverrideMap().get("sequenceNumber");
    			System.out.println(map8);
    			MAXSaveManagerOverrideTransaction dbTrans = null;
    			dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
    			try {
					//dbTrans.saveManagerOverride((MAXManagerOverride) utility.getManagerOverrideMap().values());
    				System.out.println(((MAXSaleReturnTransactionIfc)trans).getManagerOverrideMap().entrySet());
    				
    				dbTrans.saveManagerOverride((MAXManagerOverride) ((MAXSaleReturnTransactionIfc)trans).getManagerOverrideMap().entrySet());
				} catch (com.extendyourstore.foundation.manager.data.DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();}*/
				
    			
    		
    		// MAX Changes for Manager Override Report Requirement - End

            if (logger.isDebugEnabled())
            {
                String wsID = register.getWorkstation().getWorkstationID();
                long sequenceNumber = trans.getTransactionSequenceNumber();
                logger.debug("(save trans): workstation ID: " + wsID + ",  trans seq num: " + sequenceNumber);
            }

            utility.saveTransaction(trans, totals, till, register);

            afCargo.setLastReprintableTransactionID(trans.getTransactionID());
            utility.writeHardTotals(bus);
        }}
        catch (DataException ex)
        {
        	saveTransSuccess = false;
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(ex);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            logger.error("DataException occurred saving transaction.", ex);
        }
        // catch exception in writing hard totals
        catch (DeviceException dex)
        {
            saveHardTotSuccess = false;
            // set bean model
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("WriteHardTotalsError");
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
            model.setType(DialogScreensIfc.ERROR);

            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            logger.error("DeviceException writing hardTotals.", dex);
        }

        // These additional updates should be in a single transaction,
        // but can't be because they might go to different locations.
        // They CANNOT be in the same try block because if they are, and
        // the second one fails, the transaction sequence numbers get out
        // of wack.
        if (saveTransSuccess && trans instanceof VoidTransactionIfc)
        {
            try
            {
                // If this is a void transaciton, call this object to update
                // the original Transaction.
                AbstractTransactionLineItemIfc[] itemArray =
                    ((VoidTransactionIfc) trans).getLineItems();

                UpdateReturnedItemsCommandDataTransaction dbTrans = null;

                dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);
                dbTrans.updateVoidedReturnedItems(itemArray);
            }
            catch (DataException e)
            {
                logger.error( "Could not update Voided Return Line Items.");
                logger.error( "" + e + "");
            }
        }

      //Change start for Rev 1.0 (Ashish : Employee Discount)
      		//If transaction has employee discount and has been saved in POS DB then update the employee discount limit in Central DB.
      		if (saveTransSuccess){
      			if(trans instanceof MAXSaleReturnTransaction){
      				MAXSaleReturnTransaction transaction=(MAXSaleReturnTransaction)trans;
      				
      				updateEmployeeDiscountLimit(transaction);
      				updateStoreCreditInOldCO(transaction);
      			}
      			if(trans instanceof MAXLayawayTransaction){
      				MAXLayawayTransaction transaction=(MAXLayawayTransaction)trans;
      				updateEmployeeDiscountLimit(transaction);
      				updateStoreCreditInOldCO(transaction);
      			}
      			/*if(trans instanceof MAXLayawayPaymentTransaction){
      				MAXLayawayPaymentTransaction transaction=(MAXLayawayPaymentTransaction)trans;
      				updateStoreCreditInOldCO(transaction);
      			}*/

      		}
      	//Change ends for Rev 1.0 (Ashish : Employee Discount)
        // If the db write is ok, update the origninal transaction.
        if (saveTransSuccess)
        {
            // SCR: 2746
            // the following call needs to take place in both live & training modes
            // if it doesn't, values in DB get messed up and post void doesn't
            // work for Layway Pickup.
            processOriginalTransactions(rtCargo.getOriginalReturnTransactions(),
                                        bus.getServiceName());

            if (saveHardTotSuccess)
            {
                // if all updates succeeded, mail the continue letter.
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }

    }

    /**
     * Saves the original transactions to database if there are any. This is not
     * required to complete the transaction, so if there is an error log it but
     * continue on.
     *
     * @param transactions array of transactions
     * @param serviceName service name (used for log)
     */
    public void processOriginalTransactions(SaleReturnTransactionIfc[] transactions, String serviceName)
    {
        if (transactions != null)
        {
            SaleReturnTransactionIfc[] validTransactions = getValidTransactions(transactions);
            if (validTransactions.length > 0)
            {
                try
                {
                    // Return Items
                    UpdateReturnedItemsCommandDataTransaction dbTrans = null;
    
                    dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(
                            DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);
    
                    dbTrans.updateReturnedItems(validTransactions);
    
                }
                catch (DataException de)
                {
                    logger.error(de.toString());
                }
            }
        }
    }

    /*
     * Cross channel orders do not have complete transaction ID information.  This method filters
     * out the transactions that cannot update the transaction in the store DB.  The return of
     * a completed order line item does not depend on quantity returned in the Transaction Line
     * Item table.  Orders maintain their own count of quantity returned. 
     */
    protected SaleReturnTransactionIfc[] getValidTransactions(SaleReturnTransactionIfc[] transactions)
    {
        ArrayList<SaleReturnTransactionIfc> validTransactions = new ArrayList<SaleReturnTransactionIfc>();
        for(SaleReturnTransactionIfc transaction: transactions)
        {
            if (transaction instanceof OrderTransactionIfc)
            {
                if (transaction.getTransactionIdentifier() != null &&
                    !StringUtils.isEmpty(transaction.getTransactionIdentifier().getStoreID()) &&
                    !StringUtils.isEmpty(transaction.getTransactionIdentifier().getWorkstationID()) &&
                    transaction.getTransactionIdentifier().getSequenceNumber() != -1)
                {
                    validTransactions.add(transaction);
                }
            }
            else
            {
                validTransactions.add(transaction);
            }
        }
        
        
        SaleReturnTransactionIfc[] validTransactionArray = new SaleReturnTransactionIfc[validTransactions.size()];
        if (validTransactions.size() > 0)
        {
            validTransactions.toArray(validTransactionArray);
        }
        return validTransactionArray;
    }
    
    // Changes start for Rev 1.0 (Ashish : Employee Discount)
    public void updateEmployeeDiscountLimit(SaleReturnTransactionIfc transaction){
		if(transaction.getEmployeeDiscountID()!=null && transaction.getEmployeeDiscountID().length()>0){
			boolean updateResult=false;
			MAXCentralEmployeeTransaction centralEmployeeTransaction = (MAXCentralEmployeeTransaction) DataTransactionFactory.
					create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_TRANSACTION);
			//Change for Rev 1.2:Starts
			double employeeDiscountAmount =getTotalAmountForEmployeeDiscount(transaction);
			if(transaction instanceof LayawayTransaction)
			{
			if(((LayawayTransaction) transaction).getLayaway().getStatus() == 5)
				{
				employeeDiscountAmount = employeeDiscountAmount * -1;
				}
			if(((LayawayTransaction) transaction).getLayaway().getStatus() == 4)
				{
				employeeDiscountAmount = employeeDiscountAmount * 0;
				}
			}
			//Change for Rev 1.2:Ends
			if(employeeDiscountAmount!=0.0){
				
				// below code added by atul shukla
				String companyName=null;
				MAXSaleReturnTransaction maxLs;
				if(transaction instanceof MAXSaleReturnTransaction)
				{
					maxLs=(MAXSaleReturnTransaction)transaction;
					if(maxLs.getEmployeeCompanyName() !=null)
					{
					companyName=maxLs.getEmployeeCompanyName().trim().toString();
					}
				}
				updateResult=centralEmployeeTransaction.updateEmployeeDetails(transaction.getEmployeeDiscountID(), employeeDiscountAmount, companyName);
				//updateResult=centralEmployeeTransaction.updateEmployeeDetails(transaction.getEmployeeDiscountID(), employeeDiscountAmount);
				if(updateResult)
					logger.info("Employee Discount Limit with employee id "+transaction.getEmployeeDiscountID()+" has been successfully updated in central database.");
				else
					logger.error("ERROR!!! persisiting Employee Discount Limit with employee id "+transaction.getEmployeeDiscountID()+"in central database.");
			}
		}
	}
    public Double getTotalAmountForEmployeeDiscount(SaleReturnTransactionIfc transaction){
		Double price=0.0;
		Vector lineItemVector=transaction.getItemContainerProxy().getLineItemsVector();
		for(Object lineItemObject:lineItemVector){
			MAXSaleReturnLineItem lineItem=(MAXSaleReturnLineItem)lineItemObject;
			AdvancedPricingRuleIfc[] advancedPricingRuleArray=(AdvancedPricingRuleIfc[])lineItem.getPLUItem().getAdvancedPricingRules();
			/*if(advancedPricingRuleArray.length>0){
				for(AdvancedPricingRuleIfc advancedPricingRule:advancedPricingRuleArray){
					if(((MAXAdvancedPricingRuleIfc) advancedPricingRule).getCustomerType().equalsIgnoreCase("E")){
						//price=price+lineItem.getItemPrice().getExtendedDiscountedSellingPrice().getDoubleValue();
					}
				}
			}else{
				price=price+lineItem.getItemPrice().getExtendedDiscountedSellingPrice().getDoubleValue();
			}*/
			if(advancedPricingRuleArray.length==0 && (lineItem.getBdwList()==null ||lineItem.getBdwList()!=null && lineItem.getBdwList().size()==0)){
				
				price = price + lineItem.getItemPrice().getExtendedDiscountedSellingPrice().getDoubleValue();
			}
		}
		return price;
	}
    public void updateStoreCreditInOldCO(SaleReturnTransactionIfc transaction){
		TenderLineItemIfc[] tenderLineItems=transaction.getTenderLineItems();
		for(Object tender:tenderLineItems){
			if(tender instanceof MAXTenderStoreCreditIfc){
				MAXTenderStoreCreditIfc storeCredit=(MAXTenderStoreCreditIfc)tender;
				MAXCertificateTransaction dataTransaction = null;
				if(logger.isInfoEnabled())
				{
					logger.info("Updating Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
				}
				dataTransaction = (MAXCertificateTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);
				int result=dataTransaction.triggerStoreCreditInOldCO(storeCredit);
				if(result==0)
					logger.error("Error!!! updating Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
				else if(result==1)
					logger.info("Successfuly updated Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
			}
		}
	}
	//Change for Rev 1.1:Ends

	public void updateStoreCreditInOldCO(LayawayPaymentTransaction transaction){
		TenderLineItemIfc[] tenderLineItems=transaction.getTenderLineItems();
		for(Object tender:tenderLineItems){
			if(tender instanceof MAXTenderStoreCreditIfc){
				MAXTenderStoreCreditIfc storeCredit=(MAXTenderStoreCreditIfc)tender;
				MAXCertificateTransaction dataTransaction = null;
				if(logger.isInfoEnabled())
				{
					logger.info("Updating Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
				}
				dataTransaction = (MAXCertificateTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);
				int result=dataTransaction.triggerStoreCreditInOldCO(storeCredit);
				if(result==0)
					logger.error("Error!!! updating Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
				else if(result==1)
					logger.info("Successfuly updated Store Credit: "+storeCredit.getStoreCreditID()+" in ORCO 12.");
			}
		}
	}
 // Changes ends for Rev 1.0 (Ashish : Employee Discount) 
}