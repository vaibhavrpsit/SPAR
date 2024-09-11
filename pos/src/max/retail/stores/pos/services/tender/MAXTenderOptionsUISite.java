
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	Rev 1.2     Jan 22, 2017		Ashish Yadav			Food Totals FES
*   Rev 1.1     Nov 8, 2016         Nadia             		MAX-StoreCredi_Return requirement.
*	Rev 1.0     Oct 19, 2016		Ashish Yadav			Initial Draft Food Totals requirement.
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */ 
package max.retail.stores.pos.services.tender;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.stock.MerchandiseClassification;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ShowOnScreenKeyboardAisle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.TenderOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * @author Veeresh Singh
 * 
 */
public class MAXTenderOptionsUISite extends PosSiteActionAdapter {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1359946699548818136L;
    
	protected BigDecimal foodTotals = null;
	protected BigDecimal nonFoodTotals = null;
	protected BigDecimal easyBuyTotals = null;
	private String FOOD_CATEFORY = "Yes";
    private String EASYBUY_CATEFORY = "EASY BUY";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
        boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.TenderOptions");
        }
        catch (TDOException tdoe)
        {
            logger.error("Problem creating Tender Options screen: " + tdoe.getMessage());
        }


		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof SaleReturnTransactionIfc){
		calcFoodAndNonFoodTotals((SaleReturnTransactionIfc) cargo.getTransaction());
		}
		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof MAXOrderTransactionIfc){
			BigDecimal total = cargo.getTransaction().getTransactionTotals().getGrandTotal().getDecimalValue();
			
			if(total.compareTo(cargo.getTransaction().getTenderTransactionTotals().getGrandTotal().getDecimalValue()) < 0)
			{
				BigDecimal otherCharges = cargo.getTransaction().getTenderTransactionTotals().getGrandTotal().getDecimalValue().subtract(total);
				nonFoodTotals = nonFoodTotals.add(otherCharges);
			}
		}

        
        // Create map for TDO
        HashMap<String,Object> attributeMap = new HashMap<String,Object>(4);
        attributeMap.put(TenderOptionsTDO.BUS, bus);
        attributeMap.put(TenderOptionsTDO.TRANSACTION, ((AbstractFinancialCargo) bus.getCargo()).getCurrentTransactionADO());
        attributeMap.put(TenderOptionsTDO.TRANSACTION_REENTRY_MODE, new Boolean(transReentryMode));
        attributeMap.put(TenderOptionsTDO.SWIPE_ANYTIME, new Boolean(cargo.getPreTenderMSRModel() != null));
        
		attributeMap.put("fooTotals", foodTotals);
		attributeMap.put("nonFoodTotals", nonFoodTotals);
		attributeMap.put("easyBuyTotals", easyBuyTotals);
		
		  if(isSbiPointConversion(bus) && !cargo.isSbiFlag())
		  {
			  cargo.setSbiFlag(true);
			  bus.mail("sbiPoints");
		  }
		 
		
		HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
		if (tenderAttributes.get("foodTotals") == null) {
			tenderAttributes.put("foodTotals", foodTotals);
		}
		if (tenderAttributes.get("nonFoodTotals") == null) {
			tenderAttributes.put("nonFoodTotals", nonFoodTotals);
		}
		if (tenderAttributes.get("easyBuyTotals") == null) {
			tenderAttributes.put("easyBuyTotals", easyBuyTotals);
		}
		cargo.setTenderAttributes(tenderAttributes);

		/** MAX Rev 1.2 Change : Start **/
		if (cargo.getTransaction() instanceof MAXOrderTransactionIfc) {
			MAXOrderTransactionIfc trxn = (MAXOrderTransactionIfc) cargo
					.getTransaction();
			trxn.setFoodTotals((BigDecimal) tenderAttributes.get("foodTotals"));

		}
		/** MAX Rev 1.2 Change : End **/
		/** MAX Rev 1.5 Change : Start **/
		else if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
			MAXSaleReturnTransaction trxn = (MAXSaleReturnTransaction) cargo
					.getTransaction();
			trxn.setFoodTotals((BigDecimal) tenderAttributes.get("foodTotals"));
			trxn.setNonFoodTotals((BigDecimal) tenderAttributes
					.get("nonFoodTotals"));
			trxn.setEasyBuyTotals((BigDecimal) tenderAttributes
					.get("easyBuyTotals"));
		}
		else if (cargo.getTransaction() instanceof MAXSaleReturnTransaction
				&& ((MAXSaleReturnTransaction) cargo.getTransaction())
						.isSendTransaction()
				&& ((MAXSaleReturnTransaction) cargo.getTransaction())
						.iseComSendTransaction()) {
			MAXSaleReturnTransaction trxn = (MAXSaleReturnTransaction) cargo
					.getTransaction();
			trxn.setFoodTotals((BigDecimal) tenderAttributes.get("foodTotals"));
			trxn.setNonFoodTotals((BigDecimal) tenderAttributes
					.get("nonFoodTotals"));
			trxn.setEasyBuyTotals((BigDecimal) tenderAttributes
					.get("easyBuyTotals"));
		}
		/**MAX Rev 1.5 Change : End**/

		/**MAX Rev 1.5 Change : Start**/
		if (cargo.getTransaction() instanceof MAXSaleReturnTransaction
				&& ((MAXSaleReturnTransaction) cargo.getTransaction())
						.iseComSendTransaction()) {

			displayCorrectScreen(bus, MAXPOSUIManagerIfc.TENDER_OPTIONS3,
					MAXPOSUIManagerIfc.TENDER_OPTIONS3_CPOI, attributeMap, tdo);
		} else {
			
			//added by Kumar Vaibhav for reentry mode tender options
              if ( ((MAXSaleReturnTransaction) cargo.getTransaction())
						.isReentryMode()) {
            	  /** MAX Rev 1.5 Change : End **/
              
			displayCorrectScreen(bus, MAXPOSUIManagerIfc.TENDER_OPTIONS4,
					MAXPOSUIManagerIfc.TENDER_OPTIONS4_CPOI, attributeMap, tdo);
              }//end
			/** MAX Rev 1.5 Change : Start **/ else {	
            	  
            	  displayCorrectScreen(bus, MAXPOSUIManagerIfc.TENDER_OPTIONS,
      					MAXPOSUIManagerIfc.TENDER_OPTIONS_CPOI, attributeMap, tdo);
              }
		}

        // display the on screen keyboard
        showOnScreenKeyboard(bus);

    }

    /**
     * Call the ShowOnScreenKeyboard Aisle to display the On Screen Keyboard
     * after displaying the Tender Options and POI screen 
     * @param bus
     */
    protected void showOnScreenKeyboard(BusIfc bus)
    {
        new ShowOnScreenKeyboardAisle().traverse(bus);        
    }

    /**
     * At this point, we know the amount entered. Save it in the tender attributes.
     *
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        // reset the tender attributes Map. At this point
        // it is either no longer needed, or we have a new tender.
     // Chnages starts for Rev 1.2 (Ashish : Food totals)
        HashMap tenderAttributes = cargo.getTenderAttributes();
        cargo.resetTenderAttributes();
        
        
        if (tenderAttributes.get("foodTotals") != null)
			cargo.getTenderAttributes().put("foodTotals", tenderAttributes.get("foodTotals"));
		if (tenderAttributes.get("nonFoodTotals") != null)
			cargo.getTenderAttributes().put("nonFoodTotals", tenderAttributes.get("nonFoodTotals"));
		// Chnages starts for Rev 1.2 (Ashish : Food totals)
        if (tenderAttributes.get("easyBuyTotals") != null)
			cargo.getTenderAttributes().put("easyBuyTotals", tenderAttributes.get("easyBuyTotals"));
			
		cargo.setTenderADO(null);
        cargo.setOverrideOperator(null);

        // save the entered amount in the tender attributes
        String letterName = bus.getCurrentLetter().getName();
        if (!CommonLetterIfc.UNDO.equals(letterName)
            && !CommonLetterIfc.CLEAR.equals(letterName)
            && !CommonLetterIfc.CANCEL.equals(letterName))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            String input = ui.getInput();
            Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            if (input == null ||
                input.equals(""))
            {
                input = cargo.getCurrentTransactionADO().getBalanceDue().toFormattedString();
            }
            String amount = LocaleUtilities.parseCurrency(input, defaultLocale).toString();
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);
        }
        else if (bus.getCurrentLetter().getName().equals("Cancel"))
        {
            // nullify msr model.  user canceled.
            cargo.setPreTenderMSRModel(null);
		} else if (bus.getCurrentLetter().getName().equals("Undo")
				&& cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc)
		// changes end for rev 1.4
		{
			((MAXSaleReturnTransactionIfc) cargo.getTransaction())
					.setSendTransaction(false);
		}

    }

    /**
     * This method displays the 3 button screen on the CPOI.
     *
     * @param bus
     * @deprecated as of 13.4. No replacement
     */
    public void showCPOIThreeButton(BusIfc bus)
    {
    }

    /**
     * This method displays the 4 button screen on the CPOI.
     *
     * @param bus
     * @deprecated as of 13.4. No replacement
     */
    public void showCPOIFourButton(BusIfc bus)
    {
    }

    /**
     * Displays screen or swipeScreen depending on the result of
     * POSDeviceActions.isFormOnline().equals(Boolean.TRUE).
     *
     * @param bus
     * @param screen
     * @param swipeScreen
     * @param attributeMap
     * @param tdo
     */
    protected void displayCorrectScreen(BusIfc bus, String screen, String swipeScreen,
            HashMap<String,Object> attributeMap, TDOUIIfc tdo)
    {

        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = tdo.buildBeanModel(attributeMap);
        if((cargo.getTransType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE) || (cargo.getTransType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
                || (cargo.getTransType() == TransactionIfc.TYPE_ORDER_COMPLETE) ||  (cargo.getTransType() == TransactionIfc.TYPE_ORDER_PARTIAL))
        {
            NavigationButtonBeanModel nModel = model.getLocalButtonBeanModel();
            nModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
            nModel.setButtonEnabled("Wallet", true);
            model.setLocalButtonBeanModel(nModel);
        }
		// Gift cert disabled from tender - Karni
		model.getLocalButtonBeanModel().setButtonEnabled("GiftCert", false);	
        /*
         * setting the model after showing the screen causes the screen to be updated
         * which is necessary for the enabled/disabled buttons to display correctly
         */
        ui.showScreen(screen, model);
    }
    
	/**
	 * Added By Veeresh Singh to calculate food and non food items total
	 * */
	private void calcFoodAndNonFoodTotals(SaleReturnTransactionIfc txnRDO) {
		foodTotals = new BigDecimal("0.00");
		nonFoodTotals = new BigDecimal("0.00");
		easyBuyTotals = new BigDecimal("0.00"); 
		
		for (int i = 0; i < ((SaleReturnTransaction) txnRDO)
				.getItemContainerProxy().getLineItemsVector().size(); i++) {
			SaleReturnLineItem lineItem = (SaleReturnLineItem) ((SaleReturnTransaction) txnRDO)
					.getItemContainerProxy().getLineItemsVector().get(i);
			Iterator itr = (Iterator) lineItem.getPLUItem()
					.getItemClassification()
					.getMerchandiseClassificationIterator();
			int j = 0;
			while (itr.hasNext()) {
				
				MerchandiseClassificationIfc mrc = (MerchandiseClassification) itr
						.next();
				// Ashish : Changes start fro Rev 1.0 (Food Totals)
				if (j == 2) {
				if (mrc != null) {
					if (mrc.getIdentifier().equalsIgnoreCase(FOOD_CATEFORY))
						foodTotals = foodTotals.add(lineItem.getItemPrice()
								.getExtendedDiscountedSellingPrice()
								.getDecimalValue());
				}
				}
				j++;
				// Ashish : Changes ends for Rev 1.0 (Food Totals)
			}
			j = 0;
			itr = (Iterator) lineItem.getPLUItem()
					.getItemClassification()
					.getMerchandiseClassificationIterator();
			while (itr.hasNext()) {
				
				MerchandiseClassificationIfc mrc = (MerchandiseClassification) itr
						.next();
				// Ashish : Changes start fro Rev 1.0 (Food Totals)
				if (j == 4) {
				if (mrc != null) {
					if (mrc.getIdentifier().equalsIgnoreCase(EASYBUY_CATEFORY))
						easyBuyTotals = easyBuyTotals.add(lineItem.getItemPrice()
								.getExtendedDiscountedSellingPrice()
								.getDecimalValue());
				}
				}
				j++;
				// Ashish : Changes ends for Rev 1.0 (Food Totals)
			}
		}
		int flag = foodTotals.compareTo(txnRDO.getTransactionTotals().getGrandTotal().getDecimalValue());
		if(flag <= 0)
			nonFoodTotals = txnRDO.getTransactionTotals().getGrandTotal().getDecimalValue().subtract(foodTotals);
	}

	
	/*private boolean isSbiPointConversion(MAXTenderCargo cargo) {
		boolean flag = false;
		String pointConFlag;
		try {
			
			ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			pointConFlag = "isSbiPointConversion";
			pointConFlag = parameterManager.getStringValue(pointConFlag);
			}
			catch(Exception e)
			{
				
			}
		MAXConfigParametersIfc configParam = getAllConfigparameter();
		boolean pointConFlag = configParam.isSbiPointConversion();
		boolean sbiFlag = false;
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction && cargo.getTransaction().getTransactionType() ==1 ) {
			sbiFlag =((MAXSaleReturnTransaction) cargo.getTransaction()).isSbiRewardredeemFlag();
		}else if(cargo.getTransaction() instanceof MAXLayawayTransaction  && cargo.getTransaction().getTransactionType() ==19 ) {
			sbiFlag =((MAXLayawayTransaction) cargo.getTransaction()).isSbiRewardredeemFlag();
		}else {
			sbiFlag = true;
		}
		
		if(pointConFlag && !sbiFlag) {
			flag = true;
		}
		return flag;
	}*/
	
	public MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (oracle.retail.stores.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return configParameters;
	}
	
	private boolean isSbiPointConversion(BusIfc bus) {
		boolean flag = false;
		boolean isSbiPointConversionflag = false;
		MAXTenderCargo cargo=(MAXTenderCargo)bus.getCargo();
		String pointConFlag=null;
		try {
			
			ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			
			pointConFlag = parameterManager.getStringValue("isSbiPointConversion");
			}
			catch(Exception e)
			{
				
			}
		//MAXConfigParametersIfc configParam = getAllConfigparameter();
		if(pointConFlag.equals("Y")) {
			isSbiPointConversionflag=true;
		}else {
			isSbiPointConversionflag=false;
		}
		
		boolean sbiFlag = false;
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction && cargo.getTransaction().getTransactionType() ==1 ) {
			sbiFlag =((MAXSaleReturnTransaction) cargo.getTransaction()).isSbiRewardredeemFlag();
		}else if(cargo.getTransaction() instanceof MAXLayawayTransaction  && cargo.getTransaction().getTransactionType() ==19 ) {
			sbiFlag =((MAXLayawayTransaction) cargo.getTransaction()).isSbiRewardredeemFlag();
		}else {
			sbiFlag = true;
		}
		
		if(isSbiPointConversionflag && !sbiFlag) {
			flag = true;
		}
		return flag;
	}
}
