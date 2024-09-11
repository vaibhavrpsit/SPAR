/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0       Prateek			16/08/2013		Initial Draft: Changes for Bug 6807
  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
     This class puts up the change due options screen.
     $Revision: 4$
 **/
//--------------------------------------------------------------------------
public class MAXChangeDueOptionsUISite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This is the arrive method that puts up the screen.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // only display this screen if balance due is greater than zero.  
        // This might happen if we have forced cash change due to depletion of gift card
        // remaining balance.  In lieu of showing this screen, move on directly to handle cash.
        if (cargo.getCurrentTransactionADO().getBalanceDue().signum() == CurrencyIfc.ZERO)
        {
            bus.mail(new Letter("ForcedCash"), BusIfc.CURRENT);
            return;
        }
        
        // Create map for TDO
        HashMap attributeMap = new HashMap();
        attributeMap.put(TenderTDOConstants.BUS, bus);
        attributeMap.put(TenderTDOConstants.TRANSACTION, cargo.getCurrentTransactionADO());
        
        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.ChangeDueOptions");
        }
        catch (TDOException tdoe)
        {
            // TODO Auto-generated catch block
            tdoe.printStackTrace();
        } 
        
        displayChangeDueScreen(bus);

        // display the configured change due options screen
        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = tdo.buildBeanModel(attributeMap);
        
        // if cash is the only option and this is the first time.  force cash
        if (cargo.getCurrentTransactionADO().isCashOnlyOptionForChangeDue() &&
            cargo.isFirstTimeChangeDue())
        {
            cargo.setCashOnlyOption(true);
            cargo.setFirstTimeChangeDue(false);
            bus.mail(new Letter("Cash"), BusIfc.CURRENT);
        }
        else
        {        
            ui.showScreen(POSUIManagerIfc.CHANGE_DUE_OPTIONS, model);
        }
    }    

    //----------------------------------------------------------------------
    /**
        This is the depart method that captures the amount.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();
        // reset the tender attributes Map.  At this point
        // it is either no longer needed, or we have a new tender.
        cargo.resetTenderAttributes();
        cargo.setTenderADO(null);

        String letterName = bus.getCurrentLetter().getName();
        
        // save the entered amount in the tender attributes
        if (!letterName.equals("Undo") &&
            !letterName.equals("Clear") &&
            !letterName.equals("Cancel") &&
            !letterName.equals("ForcedCash"))
        {
            HashMap attributes = cargo.getTenderAttributes();
            attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(bus.getCurrentLetter().getName()));
        
            // save the entered amount in the tender attributes
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            String inputStr = ui.getInput();
            String amountStr = null;
                                               
            // if cash was only option there wont be any input
            // or if the input was not a valid number.
            // travelers check keeps the amount of travelers check as input here.  
            // that is why we are checking cashOnlyOption
            if (inputStr.equals("") || !Util.isStringANumber(inputStr) || cargo.isCashOnlyOption())
            {
                amountStr = cargo.getCurrentTransactionADO().getBalanceDue().toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
                cargo.setCashOnlyOption(false);
            }
            else
            {                                        
                amountStr = LocaleUtilities.parseNumber(inputStr, LocaleConstantsIfc.USER_INTERFACE).toString();
                // reverse the sign of the amount because this is a refund (or change) tender
                amountStr = "-" + amountStr;
            }            
            if(tenderAttributes.get(TenderConstants.FIRST_NAME) != null)
            {
	            cargo.getTenderAttributes().put(TenderConstants.FIRST_NAME, tenderAttributes.get(TenderConstants.FIRST_NAME));
				cargo.getTenderAttributes().put(TenderConstants.LAST_NAME, tenderAttributes.get(TenderConstants.LAST_NAME));
				cargo.getTenderAttributes().put(TenderConstants.ID_TYPE, tenderAttributes.get(TenderConstants.ID_TYPE));
            }
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amountStr);
        }
    }
    
    //----------------------------------------------------------------------
    /**
        This method displays the change due screen.
        @param bus
    **/
    //----------------------------------------------------------------------
    public void displayChangeDueScreen(BusIfc bus)
    {
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String changeDueString = utility.retrieveCommonText("CIDChangeDue",
                                                                "CHANGE DUE");
            CIDAction clearAction = new CIDAction(CIDAction.CPOI_TENDER_SCREEN_NAME, CIDAction.CLEAR);
            pda.cidScreenPerformAction(clearAction);
            CIDAction balDueAction = new CIDAction(CIDAction.CPOI_TENDER_SCREEN_NAME, CIDAction.SET_BALANCEDUE);
            balDueAction.setStringValue(changeDueString);
            pda.cidScreenPerformAction(balDueAction);
            CIDAction balanceAction = new CIDAction(CIDAction.CPOI_TENDER_SCREEN_NAME, CIDAction.SET_TOTAL);
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            balanceAction.setCurrency(cargo.getCurrentTransactionADO().getBalanceDue());
            pda.cidScreenPerformAction(balanceAction);
            CIDAction showAction = new CIDAction(CIDAction.CPOI_TENDER_SCREEN_NAME, CIDAction.SHOW);
            pda.cidScreenPerformAction(showAction);
        }
        catch (DeviceException e)
        {
            logger.warn("Error while using tender line display for customer inteface: " + e.getMessage() + "");
        }
        
    }
}
