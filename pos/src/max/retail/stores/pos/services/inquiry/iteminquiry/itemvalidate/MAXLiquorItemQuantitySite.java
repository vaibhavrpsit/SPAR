package max.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXLiquorItemQuantitySite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	@Override
	public void arrive(BusIfc bus) {
		System.out.println("Inside arrive MAXLiquorItemQuantityAisle");

		// <!-- MAX Rev 1.1 Change : Start -->
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityIfc utility;

		String letter = null;

		// get item inquiry from cargo
		ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
		SearchCriteriaIfc inquiry = cargo.getInquiry();
		// MAXPLUItemIfc plu = (MAXPLUItemIfc)bus.getCargo();
		// System.out.println("129:"+cargo.getLiqcat());
		// System.out.println(((MAXPLUItemIfc) pluItem).getliqcat());
		// cargo.resetInvalidFieldCounter();

		
		  System.out.println("(MAXSaleReturnTransaction) cargo.getTransaction() 55::"
		  +cargo.getTransaction());
		  
		  //System.out.println(""+liquor.getBeertot());
		  
		  // <!-- MAX Rev 1.1 Change : Start -->
		  
		  MaxLiquorDetails liquorDetail = null; MAXHotKeysTransaction
		  hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
		  .create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION); try {
		  //System.out.println("inquiry.getItemID()"+inquiry.getItemNumber());
		  //COMMENTING AS IN BASE 14 ITEM IS COMING UNDER ITEM NUMBER liquorDetail =
		  ((MAXHotKeysTransaction) hotKeysTransaction)
		  .getLiquorUMAndCategory(inquiry.getItemNumber()); MAXSaleReturnTransaction
		  liquor = (MAXSaleReturnTransaction)cargo.getTransaction(); String indLiq =
		  null; String frnLiq = null; String beer = null; String liqureTotal = null;
		  double value = Double.parseDouble(liquorDetail.getLiqUMinLtr()); double total
		  = 0;
		  
		  try { ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
		  .getManager(ParameterManagerIfc.TYPE);
		  
		  indLiq = parameterManager.getStringValue("IndianLiqureTotal"); beer =
		  parameterManager.getStringValue("BeerLiqureTotal"); frnLiq =
		  parameterManager.getStringValue("ForeigenLiqureTotal"); liqureTotal =
		  parameterManager.getStringValue("OverallLiqureTotal"); double beertot = 0.0;
		 // liquor.setBeertot(beertot);
		  //System.out.println("liquor.setBeertot(beertot)"liquor.setBeertot(beertot));
		  if(liquorDetail.getLiquorCategory().equals("BEER")) {
		  System.out.println("MAXLiquorItemQuantityAisle"+beer);
		  if(value<=(Double.parseDouble(beer)) && total<=
		  (Double.parseDouble(liqureTotal)) ) { //double beertot = 0.0;
		  //liquor.setBeertot(beertot); beertot = liquor.getBeertot()+value;
		  System.out.println("beertot :"+beertot);
		  System.out.println("getBeertot 89:"+liquor.getBeertot());
		 // liquor.setBeertot(beertot); //liquor.setBeertot(beertot);
		  System.out.println("getBeertot 92:"+liquor.getBeertot()); //total = value +
		 // total; 
		  if(liquor.getBeertot()>(Double.parseDouble(beer))) {
		  System.out.println("Inside getBeertot"); DialogBeanModel beanModel = new
		  DialogBeanModel(); beanModel.setResourceID("CRMCustomersearchError");
		  beanModel.setType(DialogScreensIfc.ERROR);
		  beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Final");
		  uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel); }
		  
		  else { beertot = value + beertot; total = value + total; }
		  
		  }
		  
		  else { DialogBeanModel beanModel = new DialogBeanModel();
		  beanModel.setResourceID("CRMCustomersearchError");
		  beanModel.setType(DialogScreensIfc.ERROR);
		  beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Search");
		  uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel); }
		  
		  } } catch (ParameterException e) { // TODO Auto-generated catch block
		  e.printStackTrace(); } }
		  
		  catch (DataException e) {}
		 
		bus.mail("Search");
	}

}
