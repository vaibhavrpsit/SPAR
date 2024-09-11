package max.retail.stores.pos.services.postvoid;

import java.util.Vector;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXSaveTransactionSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 5271527977390207562L;
	public static final String revisionNumber = "$Revision: /main/12 $";

	public void arrive(BusIfc bus) {
		AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		VoidTransaction voidTrans =(VoidTransaction) txnADO.toLegacy();
	MAXSaleReturnTransaction trans =	(MAXSaleReturnTransaction) voidTrans.getOriginalTransaction();
		try {
			ContextFactory.getInstance().getContext().getRegisterADO().addTransaction(txnADO);

			RegisterADO register = ContextFactory.getInstance().getContext().getRegisterADO();
			txnADO.save(register);
		} catch (DataException dataException) {
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");
			DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(dataException);

			ui.showScreen("DIALOG_TEMPLATE", dialogModel);
			return;
		}

		cargo.setLastReprintableTransactionID(txnADO.getTransactionID());
		try {
			ContextFactory.getInstance().getContext().getRegisterADO().writeHardTotals();
		} catch (DeviceException de) {
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("WriteHardTotalsError");
			model.setType(1);

			ui.showScreen("DIALOG_TEMPLATE", model);
			return;
		}
		
		if(txnADO instanceof VoidTransactionADO)
		{
			updateEmployeeDiscountLimit(trans);
		}
		bus.mail(new Letter("Continue"), BusIfc.CURRENT);
	}
	
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
				// below code is added bye atul shukla employee discount FES
				MAXSaleReturnTransaction maxLs=null;
				String companyName=null;
				String employeeId=null;
				if(transaction instanceof MAXSaleReturnTransaction)
				{
					try
					{
						
					 maxLs=(MAXSaleReturnTransaction)transaction;
					 if(maxLs.getEmployeeCompanyName() != null)
					 {
					 companyName=maxLs.getEmployeeCompanyName().trim().toString();
					 }else
					 {
						 String empIdInPostVoid=maxLs.getEmployeeDiscountID();
						 String[] empData=empIdInPostVoid.split("-");
 						 employeeId=empData[0];					 
				 companyName=empData[1];
					 }
					}catch(NullPointerException ne)
					{
						ne.printStackTrace();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				// atul's changes end here
				//updateResult=centralEmployeeTransaction.updateEmployeeDetails(transaction.getEmployeeDiscountID(), employeeDiscountAmount,);
				updateResult=centralEmployeeTransaction.updateEmployeeDetails(employeeId, employeeDiscountAmount,companyName);
				if(updateResult)
					logger.info("Employee Discount Limit with employee id "+transaction.getEmployeeDiscountID()+" has been successfully updated in central database.");
				else
					logger.error("ERROR!!! persisiting Employee Discount Limit with employee id "+transaction.getEmployeeDiscountID()+"in central database.");
			}
		}
	}
	public Double getTotalAmountForEmployeeDiscount(SaleReturnTransactionIfc transaction){
		Double price=0.0;
		boolean empDisc = false;
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
			for(int i=0; lineItem.getItemPrice().getItemDiscounts().length > i; i++){
				if(lineItem.getItemPrice().getItemDiscounts()[0].getDiscountEmployee() != null){
					empDisc=true;
				}
			}
			if(empDisc){
			if(advancedPricingRuleArray.length==0 && (lineItem.getBdwList()==null ||lineItem.getBdwList()!=null && lineItem.getBdwList().size()==0)){
				double price1 = lineItem.getItemPrice().getExtendedDiscountedSellingPrice().getDoubleValue();
				price = price + (price1 * -1);
				empDisc = false;
			}
			}
		}
		return price;
	}

	public String getRevisionNumber() {
		return "$Revision: /main/12 $";
	}
}
