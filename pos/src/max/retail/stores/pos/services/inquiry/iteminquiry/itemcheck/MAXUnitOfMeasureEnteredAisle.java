/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  23/May/2013				 Prateek	       Changes done for single bar code requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import java.math.BigDecimal;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck.UnitOfMeasureEnteredAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXUnitOfMeasureEnteredAisle extends UnitOfMeasureEnteredAisle {
	 public void traverse(BusIfc bus)
	    {
	        boolean mail = true;
	        String letter = CommonLetterIfc.CONTINUE;
	        //  retrieve cargo
	        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();

	        //  get a hold of the ui manager
	        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

	        // get units from ui input field
	        SingleBarCodeData data = cargo.getSingleBarCodeData();
	        String amountStr = null;
	        if(data == null)
	        	amountStr = LocaleUtilities.parseNumber(ui.getInput(),
	                                                      LocaleConstantsIfc.USER_INTERFACE).toString();
	        else
	        	amountStr = LocaleUtilities.parseNumber(data.getQuantity()+"",
                        LocaleConstantsIfc.USER_INTERFACE).toString();
	        
	        if (  amountStr!=null &&
	           new BigDecimal(amountStr).compareTo(BigDecimalConstants.ZERO_AMOUNT) != 0)
	        {
	            BigDecimal units = new BigDecimal(amountStr);
	            if (cargo.getPLUItem().isKitHeader())
	            {
	                letter = "NextItem";
	                KitComponentIfc kc[] = ((ItemKitIfc)cargo.getPLUItem()).getComponentItems();
	                int index = ((ItemKitIfc)cargo.getPLUItem()).getindex();
	                kc[index].setQuantity(units);
	                
	            }
	            else
	                cargo.setItemQuantity(units);
	                
	            cargo.setModifiedFlag(true);
	        }
	        else
	        if ( amountStr != null &&  new BigDecimal(amountStr).compareTo(BigDecimalConstants.ZERO_AMOUNT) == 0)
	        {
	            mail = false;
	            DialogBeanModel dModel = new DialogBeanModel();
	            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	            dModel.setResourceID("QuantityCannotBeZero");
	            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
	            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
	        }
	        else
	        {
	            cargo.setModifiedFlag(false);
	        }
	        if (mail)
	        {
	            bus.mail(new Letter(letter), BusIfc.CURRENT);
	        }
	    }
}
