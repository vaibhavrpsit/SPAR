/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
  	Rev 1.3	May 04, 2017	Kritica Agarwal 	GST Changes
  	Rev 1.2 25/aug/2015     Gaurav Bug : 16369
    Rev 1.1	22/Aug/2013	  	Jyoti, Validation added for Expected time 
	Rev 1.0	29/May/2013	  	Tanmaya, Bug 6049 - System Allow user to send home delivery in back date. 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.send.displaysendmethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import max.retail.stores.domain.MAXUtils.MAXIGSTTax;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXReadTaxOnPLUItem;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ui.beans.MAXShippingMethodBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.send.address.SendCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXSendValidateDeliveryDateAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = -6678518724541969338L;

	public void traverse(BusIfc bus) {
		SendCargo cargo = (SendCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		// Initialize bean model values
		MAXShippingMethodBeanModel beanModel = (MAXShippingMethodBeanModel) ui
				.getModel();

		EYSDate eysDate = new EYSDate();
		EYSTime eysTime = new EYSTime();
		EYSDate beanModelDate = beanModel.getExpectedDeliveryDate();
		EYSDate beanModelTime = beanModel.getExpectedDeliveryTime();
		if (removeTime(beanModelDate).before(removeTime(eysDate))) {
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("BeforeDateForSpecialOrderOrSend");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					CommonLetterIfc.RETRY);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		//Rev 1.1 changes start here
		 else if((removeTime(beanModelDate).equals(removeTime(eysDate)))&&(beanModelTime.before(eysTime))){
				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("BeforeTimeForSpecialOrderOrSend");
				model.setType(DialogScreensIfc.ERROR);
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						CommonLetterIfc.RETRY);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}//Rev 1.1 changes end here
		else {
			//Rev 1.2  Bug : 16369 : Start
			((MAXSaleReturnTransactionIfc)cargo.getTransaction()).setSendTransaction(true);
			//Rev 1.2  Bug : 16369 : End
			
			//Change for Rev 1.3 : Starts
			if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){
			MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc)cargo.getTransaction();
			if(transaction.isGstEnable() &&  transaction.getHomeStateCode()!= null){
				if( transaction.getHomeStateCode().equalsIgnoreCase(transaction.getCaptureCustomer().getState()))
					transaction.setIgstApplicable(false);
				else
					transaction.setIgstApplicable(true);
			}
			else
				 ((MAXSaleReturnTransactionIfc) transaction).setIgstApplicable(false);
			//set tostate in transaction object so that IGST can be calculated
			if(transaction!= null && transaction.isIgstApplicable() && transaction.getCaptureCustomer() != null &&  transaction.getCaptureCustomer().getState()!=null){
					((MAXSaleReturnTransactionIfc) transaction).setToState(((MAXSaleReturnTransactionIfc) transaction).getStates().get((((MAXSaleReturnTransactionIfc) transaction).getCaptureCustomer().getState())));
					logger.info("To State is " + ((MAXSaleReturnTransactionIfc) transaction).getToState());
			}	
			//Recalculate TAX
			if(transaction!= null  &&  transaction.isIgstApplicable()){
				recalculateTax(transaction);				
									
				transaction.setTransactionTotals(DomainGateway.getFactory().getTransactionTotalsInstance());
				transaction.getTransactionTotals().updateTransactionTotals(
						transaction.getItemContainerProxy().getLineItems(),
						transaction.getItemContainerProxy().getTransactionDiscounts(),
						transaction.getItemContainerProxy().getTransactionTax()
						);	
				
				}
			}
			//Change for Rev 1.3 : Ends
			bus.mail("Success");
		}
	}

	public EYSDate removeTime(EYSDate date) {
		Calendar cal = Calendar.getInstance();
		Date d = new Date(date.getYear(), date.getMonth(), date.getDay());
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new EYSDate(cal.getTime());
	}
	//Change for Rev 1.3 : Starts
		protected void recalculateTax(MAXSaleReturnTransactionIfc transaction){
			if(((MAXPLUItemIfc)((MAXSaleReturnLineItemIfc)transaction.getItemContainerProxy().getLineItems()[0]).getPLUItem()).getTaxAssignments()!= null){
			int taxCategory=((MAXPLUItemIfc)((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]).getPLUItem()).getTaxAssignments()[0].getTaxCategory();
			
			//check for -1
			MAXIGSTTax igstTax = new MAXIGSTTax();
			igstTax.setTaxCategory(String.valueOf(taxCategory));
			igstTax.setStoreId(((MAXSaleReturnTransactionIfc) transaction).getTransactionIdentifier().getStoreID());
			igstTax.setFromRegion(((MAXSaleReturnTransactionIfc) transaction).getHomeState());
			igstTax.setToRegion(((MAXSaleReturnTransactionIfc) transaction).getToState());
			MAXReadTaxOnPLUItem tax = new MAXReadTaxOnPLUItem();
			tax = (MAXReadTaxOnPLUItem) DataTransactionFactory
					.create(MAXDataTransactionKeys.ReadIGSTTaxTransactions);
			ArrayList<MAXTaxAssignment> taxAssignment = null;
				 try {
					  taxAssignment=tax.readTax(igstTax);
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			// Get the line items from the retrieve transaction
			// Process each line item
			MAXSaleReturnLineItemIfc srli = ((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]);
			
			CurrencyIfc taxinclusiveSellingPrice =srli.getItemPrice().getExtendedDiscountedSellingPrice();
			
			
			 MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUpDetail = new MAXLineItemTaxBreakUpDetail();
			  lineItemTaxBreakUpDetail.setTaxableAmount(taxinclusiveSellingPrice.multiply(((MAXTaxAssignmentIfc)taxAssignment.get(0)).getTaxableAmountFactor()));
			  lineItemTaxBreakUpDetail.setTaxAmount(taxinclusiveSellingPrice.multiply(((MAXTaxAssignmentIfc)taxAssignment.get(0)).getTaxAmountFactor()));
			  lineItemTaxBreakUpDetail.setTaxRate(((MAXTaxAssignmentIfc) taxAssignment.get(0)).getTaxRate());
			  lineItemTaxBreakUpDetail.setTaxAssignment(((MAXTaxAssignmentIfc)taxAssignment.get(0)));
			
				ArrayList lineItemTaxBreakUpList = new ArrayList();
				lineItemTaxBreakUpList.add(lineItemTaxBreakUpDetail);

				MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetailList = new MAXLineItemTaxBreakUpDetail[lineItemTaxBreakUpList.size()];
				for (int k = 0; k <= lineItemTaxBreakUpDetailList.length - 1; k++) {
					lineItemTaxBreakUpDetailList[k] = (MAXLineItemTaxBreakUpDetailIfc) lineItemTaxBreakUpList
							.get(k);
				}     
				((MAXItemTaxIfc) (srli.getItemPrice().getItemTax())).setLineItemTaxBreakUpDetail(lineItemTaxBreakUpDetailList);
				
				MAXTaxAssignmentIfc[] taxAssignmentIfc = new MAXTaxAssignmentIfc[taxAssignment.size()];
				 for (int k = 0; k <= taxAssignmentIfc.length - 1; k++) {
					taxAssignmentIfc[k] = (MAXTaxAssignmentIfc) taxAssignment.get(k);
				}
				((MAXPLUItemIfc)srli.getPLUItem()).setTaxAssignments(taxAssignmentIfc);	
				srli.setLineItemTaxBreakUpDetails(lineItemTaxBreakUpDetailList);
			}else{
				logger.info("Item is not associate with tax");
			}
			
		}
		
		public CurrencyIfc getTaxInclusiveSellingRetail(TaxLineItemInformationIfc item)
		{
			CurrencyIfc retValue = DomainGateway.getBaseCurrencyInstance();

			if (item.getExtendedDiscountedSellingPrice() != null) {
				retValue = item.getExtendedDiscountedSellingPrice();
			}
			return retValue;
		}
		//Change for Rev 1.3 : Ends

}
