/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXStartSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -9169557495928184017L;
	
	

	public void arrive(BusIfc bus) {

		// retrieve cargo
		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();

		SaleReturnLineItemIfc[] lineItems = null;
		 lineItems = (SaleReturnLineItemIfc[]) cargo.getTransaction().getLineItems();
		 ArrayList<String> itemList = null;
		 String itemId= null;
		 String letterName="NoInvoiceRuleApplied";
		 if ((cargo.getDiscountType()) == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE) {
		 letterName = "Percent";
		 }
		// check to see what type of discount this is
		if ((cargo.getDiscountType()) == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT) {
			// if this is an amount discount - mail a DiscountDollar letter
			letterName = "Dollar";
		}
//added By vaibhav
		if ((cargo.getDiscountType()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {
			letterName = "InvoicePercent";
		}
		if ((cargo.getDiscountType()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered ){
			letterName = "InvoiceDollar";
		}
		if ( (cargo.getDiscountType()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster){
			letterName = "BillBusterPercent";
		}
		if ( (cargo.getDiscountType()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster){
			letterName = "BillBusterAmount";
		}
		/*
		 * if(!cargo.isRuleApplied()) if ((cargo.getDiscountType()) == 99) { for(int
		 * i=0;i < cargo.getInvoiceDiscounts().size();i++) {
		 * 
		 * 
		 * if(Integer.parseInt(cargo.getInvoiceDiscounts().get(i).getReason().getCode())
		 * == MAXDiscountRuleConstantsIfc.
		 * DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster){
		 * 
		 * itemList=(ArrayList<String>)
		 * cargo.getInvoiceDiscounts().get(i).getItemList(); for (int j=0; j<
		 * cargo.getTransaction().getLineItemsVector().size(); j++) {
		 * itemId=cargo.getTransaction().getLineItemsVector().get(j).getItemID(); for
		 * (int k=0 ;k<itemList.size();k++) {
		 * if(itemId.equalsIgnoreCase(itemList.get(k).toString())){ letterName =
		 * "BillBusterPercent";
		 * 
		 * } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * else
		 * if(Integer.parseInt(cargo.getInvoiceDiscounts().get(i).getReason().getCode())
		 * == MAXDiscountRuleConstantsIfc.
		 * DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster){
		 * 
		 * itemList=(ArrayList<String>)
		 * cargo.getInvoiceDiscounts().get(i).getItemList(); for (int j=0; j<
		 * cargo.getTransaction().getLineItemsVector().size(); j++) {
		 * itemId=cargo.getTransaction().getLineItemsVector().get(j).getItemID(); for
		 * (int k=0 ;k<itemList.size();k++) { if
		 * (itemId.equalsIgnoreCase(itemList.get(k).toString())) { letterName =
		 * "BillBusterAmount";
		 * 
		 * } }
		 * 
		 * } //letterName = "BillBusterAmount"; } }
		 * 
		 * }
		 */
		if ((cargo.getDiscountType()) == 0) {
			letterName = "NoInvoiceRuleApplied";
		}
		

		bus.mail(new Letter(letterName), BusIfc.CURRENT);

	}
		}
