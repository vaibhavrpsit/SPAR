/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.1  23/May/2013				 Prateek	       Changes done for single bar code requirement.
 *  Rev 1.0  12/April/2013               Himanshu              MAX-POS-PLU-ITEM-FES_v1.0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

// foundation imports
import java.math.BigDecimal;

import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


//--------------------------------------------------------------------------
/**
    This site checks to see if additional Unit of Measure information
    is needed.
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCheckUnitOfMeasureSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 3$";
    /**
        Constant for unit of measure UNITS.
    **/
    public static final String UNITS = "UN";
    
    /**
     * Prompt respose spec
     */
    protected static final String PROMPT_SPEC = "PromptAndResponsePanelSpec";

    /**
     * Prompt message tag
     */
    protected static final String PROMPT_MESSAGE_TAG = "UnitOfMeasureKitPrompt";

    /**
     *  Prompt message default text
     */
    protected static final String PROMPT_MESSAGE = "Enter total {0} for item number {1}.";
    

    //----------------------------------------------------------------------
    /**
        Checks the Unit of Measure information to see if additional
        information is needed.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letter = null;
        // retrieve item object
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        String originLetter = cargo.getInitialOriginLetter();
        //MAXSaleReturnTransaction liq = (MAXSaleReturnTransaction) cargo.getTransaction();
        //System.out.println("MAXCheckUnitOfMeasureSite 81"+liq);
        if(cargo.isApplyBestDeal() || (originLetter != null && originLetter.equals("Tender")))
        {
        	bus.mail("Continue");
        }
        else{
        PLUItemIfc pluItem = cargo.getPLUItem();
		/**MAX Rev 1.1 Change : Start**/
        if(cargo.getSingleBarCodeData() == null)
        {
		/**MAX Rev 1.1 Change : End**/
        //if it is a kit we must go through each item and check for UOM items.
        if (pluItem.isKitHeader()){
            KitComponentIfc kc[] = ((ItemKitIfc)cargo.getPLUItem()).getComponentItems();
            int index = ((ItemKitIfc)cargo.getPLUItem()).getindex();
            index++;
            while (index < kc.length)
            {    
                if (kc[index].getUnitOfMeasure() == null ||
                        kc[index].getUnitOfMeasure().getUnitID().equals(UNITS))
                {   
                    index = index+1;
                    continue;
                }
                else
                {
                    // save the index of the kit component unit of measure item
                    ((ItemKitIfc)cargo.getPLUItem()).setindex(index);
                    // initialize the bean model
                    POSBaseBeanModel baseModel = new POSBaseBeanModel();
                    PromptAndResponseModel beanModel = new PromptAndResponseModel();
                    UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                    String pattern =
                        utility.retrieveText(
                                PROMPT_SPEC,
                                BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                                PROMPT_MESSAGE_TAG,
                                PROMPT_MESSAGE);
                    
                    String argSt[] = new String[] {kc[index].getUnitOfMeasure().getUnitName(), kc[index].getItemID()};
                    String message = LocaleUtilities.formatComplexMessage(pattern, argSt);
                    
                    beanModel.setPromptText(message); 
                    baseModel.setPromptAndResponseModel(beanModel);

                    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                    ui.showScreen(POSUIManagerIfc.UNIT_OF_MEASURE, baseModel);
                    break;
                }
            }

            if (index >= kc.length)
            {
                letter = CommonLetterIfc.CONTINUE;
                bus.mail(new Letter(letter), BusIfc.CURRENT);
            }   
        }
        else
        {
        	// <!-- MAX Rev 1.0 Change : Start -->
        	
            if (!(pluItem.getItemWeight().toString().equals("0.00"))|| pluItem.getUnitOfMeasure() == null ||
                    pluItem.getUnitOfMeasure().getUnitID().equals(UNITS))
            {   // Default UOM; if it is a gift card the next site will get
                // the gift card information
                if (pluItem instanceof GiftCardPLUItemIfc)
                {
                    letter = CommonLetterIfc.GIFTCARD;
                }
                else
                {
                    letter = CommonLetterIfc.CONTINUE;
                }
                
                if(!(pluItem.getItemWeight().toString().equals("0.00")) && !(pluItem.getItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE )){
					cargo.setItemQuantity(new BigDecimal(pluItem.getItemWeight().toString()));
				}
                bus.mail(new Letter(letter), BusIfc.CURRENT);
			}
            
         // <!-- MAX Rev 1.0 Change : end -->
                
            
            else
            {
                // initialize the bean model
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                PromptAndResponseModel beanModel = new PromptAndResponseModel();

                //beanModel.setResponseText(cargo.getItemQuantity().toString());
                beanModel.setArguments(pluItem.getUnitOfMeasure().getUnitName());
                baseModel.setPromptAndResponseModel(beanModel);

                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.UNIT_OF_MEASURE, baseModel);
            }
        }
    }
    /**MAX Rev 1.1 Change : Start**/
        
    else
    {
    	((MAXPLUItem)pluItem).setWeightedBarCode(true);
    	cargo.setPLUItem(pluItem);
    	bus.mail("Next");
    }
    /**MAX Rev 1.1 Change : End**/
    }
}
}
