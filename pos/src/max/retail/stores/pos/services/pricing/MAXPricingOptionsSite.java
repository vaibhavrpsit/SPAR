/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.3  6/Jan/2015  	Akanksha Chauhan\Bhanu Priya,  	Disabled GC discount button if discount has already applied.
  Rev 1.2   Shruti Singh    12/08/2014      			Centralized Employee Discount 
  Rev 1.1	Jyoti Rawal		26/04/2013					Changes for Gift Card FES
  Rev 1.0	Jyoti Rawal		09/04/2013					Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.pricing;

import max.retail.stores.pos.services.priceadjustment.PriceAdjustmentUtilities.MAXPriceAdjustmentUtilities;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays pricing options.
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXPricingOptionsSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8442514143434738763L;
	/** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: 3$";
    ParameterManagerIfc pm;
    //----------------------------------------------------------------------
    /**
       Displays the pricing options
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {  
    	pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        MAXPricingCargo pricingCargo = (MAXPricingCargo)bus.getCargo();		//Rev 1.0 changes 

        ListBeanModel model = getModifyItemBeanModel(pricingCargo.getItems());

        // Check the parameter to see if the Price Adjustment button is enabled.
        boolean isPriceAdjustmentButtonEnabled = false;
        try
        {            
            ParameterManagerIfc parameterManager = 
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            
            Boolean displayPriceAdjButtonObj 
                = parameterManager.getBooleanValue(MAXPriceAdjustmentUtilities.PARAMETER_PRICE_ADJUST_ENABLE);      
            isPriceAdjustmentButtonEnabled = displayPriceAdjButtonObj.booleanValue();
        }
        catch(ParameterException pe)
        {
            logger.error("Parameter error: ", pe);
        }      
        pricingCargo.setPriceAdjustmentButtonEnabled(isPriceAdjustmentButtonEnabled);

        configureLocalButtons(model, pricingCargo);

        ui.showScreen(POSUIManagerIfc.PRICING_OPTIONS, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Builds the ModifyItemBeanModel; this bean contains the the line item
     *   and the model that sets the local navigation buttons to their correct
     *   enabled states.
     *   @param  lineItemList     The itemlist to modify.
     *   @return ModifyItemBeanModel
     */
    //----------------------------------------------------------------------
    protected ListBeanModel getModifyItemBeanModel(SaleReturnLineItemIfc[] lineItemList)
    {
        ListBeanModel model = new ListBeanModel();

        if (lineItemList != null)
        {
            model.setListModel(lineItemList);
            model.setUpdateStatusBean(false);
        }
        return model;
    }

    //----------------------------------------------------------------------
    /**
     *   Configures the local buttons
     *   @param  model ListBeanModel
     *   @param  pricingCargo PricingCargo
     */
    //----------------------------------------------------------------------
    public void configureLocalButtons(ListBeanModel model, MAXPricingCargo pricingCargo)	//Rev 1.0 changes
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        SaleReturnLineItemIfc[] lineItemList = pricingCargo.getItems();
        
        // always on
        navModel.setButtonEnabled("Discount",true);
        
        //System.out.println("pricingCargo.isEmpID() 173=============="+pricingCargo.isEmpID());
        
        // Enable the price adjustment button only if the button has been manually configured
        // and transaction re-entry is off
        boolean transReentryModeOn =
            pricingCargo.getRegister().getWorkstation().isTransReentryMode();
        /**
         * Rev 1.0 changes start here
         */
        pricingCargo.setEmployeeDiscountID(null);
        String employeeDiscountAppliedID =  pricingCargo.getEmployeeDiscountID();
      // pricingCargo.setEmployeeDiscountID("null");
		boolean employeeDiscountAppliedFlag = false;
		if (employeeDiscountAppliedID != null && employeeDiscountAppliedID != "")// if(((null).equals(employeeDiscountAppliedID))
																					// ||
																					// (("").equals(employeeDiscountAppliedID)))
			employeeDiscountAppliedFlag = true;
        //navModel.setButtonEnabled("PriceAdjustment", pricingCargo.isPriceAdjustmentButtonEnabled() && !transReentryModeOn);
		/**
         * Rev 1.0 changes end here
         */
		
		/** Changes for Rev 1.2 : Starts **/
		String parmgiftcardvalue="";
		String giftcardid="";
		boolean gcdiscount = false;
		for(int i=0; i<lineItemList.length; i++){
			ItemDiscountStrategyIfc[] itemdiscounts = lineItemList[i].getItemPrice().getItemDiscounts();
			 
			 //Rev 1.3  changes starts
			  
			 giftcardid	=lineItemList[i].getItemID();
			if((giftcardid.equalsIgnoreCase("GiftCard")||giftcardid.equals("70071000"))&& itemdiscounts.length!=0){
				 if( (lineItemList != null) && (lineItemList.length > 0) &&
			                (lineItemList[0].getPLUItem() instanceof GiftCardPLUItemIfc ))
				gcdiscount=true;
			}
			 //Rev 1.3  changes End
			for(int j=0; j<itemdiscounts.length; j++){
				if(itemdiscounts[j].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE){
					if(itemdiscounts[j].getDiscountEmployeeID() != null &&
							itemdiscounts[j].getDiscountEmployeeID() != ""){
						employeeDiscountAppliedFlag = true;
						break;
					}
				}
			}
		}
		/** Changes for Rev 1.2 : Ends **/
        if ( lineItemList == null || lineItemList.length == 0 )
        // nothing selected
        {
            navModel.setButtonEnabled("PriceOverride", false);
            navModel.setButtonEnabled("Markdown", false);
            navModel.setButtonEnabled("Employee", false);
            navModel.setButtonEnabled("Damage", false);
            navModel.setButtonEnabled("RemoveEmployee", false);//Rev 1.0 changes
            if(gcdiscount){
            navModel.setButtonEnabled("GCDiscount", false);//Rev 1.1 changes
        }}
             
        else if(pricingCargo.isEmpID()==true)
        {
        	 navModel.setButtonEnabled("Employee",false);
        	 navModel.setButtonEnabled("RemoveEmployee", false);
        }
        
        else
        // single select
        if (lineItemList.length == 1)
        {
            if ( priceOverrideable(lineItemList[0]) )
            {         
                navModel.setButtonEnabled("PriceOverride", true);
            }
            else
            {
                navModel.setButtonEnabled("PriceOverride", false);
            }
			/**
         * Rev 1.2 changes Starts here
         */
//            navModel.setButtonEnabled("Markdown",true); //Rev 1.0 changes
            navModel.setButtonEnabled("Employee",true);
//            navModel.setButtonEnabled("Damage", true);//Rev 1.0 changes
            /**
             * Rev 1.0 changes start here
             */
        	if (employeeDiscountAppliedFlag) {
				navModel.setButtonEnabled("Employee", false);
				navModel.setButtonEnabled("RemoveEmployee", true);
			} else {
				navModel.setButtonEnabled("Employee", true);
				navModel.setButtonEnabled("RemoveEmployee", false); 
					/**
         * Rev 1.2 changes end here
         */
			}
        	if (("Manual").equals(pricingCargo.getEmployeeDiscountMethod())) {
				navModel.setButtonEnabled("RemoveEmployee", false); //Rev 1.0 changes
				navModel.setButtonEnabled("Employee", false); // This button will
																// be always
																// enable if
																// Manual
																// discount is
																// given
			}
        	  /**
             * Rev 1.0 changes end here
             */
        }
        else
        // multi select
        if(lineItemList.length > 1)
        {
            navModel.setButtonEnabled("PriceOverride", false);
//            navModel.setButtonEnabled("Markdown",true); //Rev 1.0 changes
            navModel.setButtonEnabled("Employee", false);
//            navModel.setButtonEnabled("Damage", true);//Rev 1.0 changes
            /**
             * Rev 1.0 changes start here
             */
        	if (employeeDiscountAppliedFlag) {
				navModel.setButtonEnabled("Employee", false);
				navModel.setButtonEnabled("RemoveEmployee", false);
			} else {
				navModel.setButtonEnabled("Employee", false);
				navModel.setButtonEnabled("RemoveEmployee", false);
			}
			if (("Manual").equals(pricingCargo.getEmployeeDiscountMethod())) {
				navModel.setButtonEnabled("RemoveEmployee", false);
			}
			 /**
             * Rev 1.0 changes end here
             */
        }
        
        //Tax cannot be modified for GiftCard.
        if( (lineItemList != null) && (lineItemList.length > 0) &&
            (lineItemList[0].getPLUItem() instanceof GiftCardPLUItemIfc || lineItemList[0].getPLUItem() instanceof GiftCertificateItemIfc || lineItemList[0].getPLUItem().isKitHeader()))
        {
            navModel.setButtonEnabled("PriceOverride", false);
        }
        /**
         * Rev 1.1 changes start here
         */
        if( (lineItemList != null) && (lineItemList.length > 0) &&
                (lineItemList[0].getPLUItem() instanceof GiftCardPLUItemIfc ))
            {
        	//Rev 1.3  changes starts
        	if(gcdiscount){
        		navModel.setButtonEnabled("GCDiscount", false); 
        	}
        	else{
        	navModel.setButtonEnabled("GCDiscount", true);//Rev 1.0 changes
        	}
    		navModel.setButtonEnabled("Employee", false);
			navModel.setButtonEnabled("RemoveEmployee", false);
			navModel.setButtonEnabled("Discount", false);
        	
        }else{
        	navModel.setButtonEnabled("GCDiscount", false);//Rev 1.0 changes
        }
      //Rev 1.3  changes End
        /**
         * Rev 1.1 changes end here
         */
        model.setLocalButtonBeanModel(navModel);
    }
    
    //----------------------------------------------------------------------
    /**
     *   Check the price is overrideable for giving lineItem
     *   @param  lineItem SaleReturnLineItemIfc
     *   @return overrideable boolean
     */
    //----------------------------------------------------------------------
    protected boolean priceOverrideable(SaleReturnLineItemIfc lineItem)
    {
        boolean overrideable = false;
        ItemIfc item = lineItem.getPLUItem().getItem();
        if ( item != null && 
             item.getItemClassification() != null &&
             item.getItemClassification().getPriceOverridable()== true )
        {
            overrideable = true;
        }
        return overrideable;
    }
}
