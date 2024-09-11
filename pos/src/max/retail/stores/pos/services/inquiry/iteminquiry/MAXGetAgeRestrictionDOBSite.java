/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

Copyright (c) 2005 360Commerce, Inc.    All Rights Reserved.

$Log:
 2    360Commerce 1.1         7/6/2007 8:36:29 AM    Christian Greene Remove
      reference to deleted ItemProduct table
 1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape   
$

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.inquiry.iteminquiry;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AgeRestrictionBeanModel;

//--------------------------------------------------------------------------
/**
     This site determines if the age is needed for this item and displays
     a dob prompt if needed.
     $Revision: 2$
 **/
//--------------------------------------------------------------------------
public class MAXGetAgeRestrictionDOBSite extends PosSiteActionAdapter
{

    //----------------------------------------------------------------------
    /**
        This method determines if the DOB prompts is needed and then
        displays it when needed.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
	
	public MAXGetAgeRestrictionDOBSite()
    {
    }
	
	
    public void arrive(BusIfc bus)
    {
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
       
        ///changes by akhilesh for the gift card issue:: start
        //MAXPLUItemIfc pluItem =null;
        PLUItemIfc pluItem = cargo.getPLUItem();
        ///changes by akhilesh for the gift card issue:: END
        
     // Added by Sakshi.....for Invalid Item screen on apply discount button
     		if (pluItem == null && ("ApplyDiscounts").equals(cargo.getInitialOriginLetter())) {
     			SaleReturnTransactionIfc slr = (SaleReturnTransactionIfc) cargo.getTransaction();
     			int size = slr.getItemContainerProxy().getLineItemsSize();
     			SaleReturnLineItem item = (SaleReturnLineItem) slr.getItemContainerProxy().getLineItemsVector().get(size - 1);
     			pluItem = (MAXPLUItemIfc) item.getPLUItem();
     		}
     		// end ...added by Sakshi.....for Invalid Item screen on apply discount button
        
        if (pluItem == null && cargo.getItemList() != null && cargo.getItemList().length > 0)
        {
            pluItem = (MAXPLUItemIfc) cargo.getItemList()[0];
        }
        if (pluItem != null)
        {
            int restrictiveAge = pluItem.getRestrictiveAge();
            LetterIfc letter = null;
            if(cargo.getTransaction() != null)
            {
                cargo.setRestrictedDOB(((SaleReturnTransactionIfc)cargo.getTransaction()).getAgeRestrictedDOB());
            }
            
            if (restrictiveAge == 0)          
            {
                letter = new Letter("Continue");
            }
            else if (cargo.getRestrictedDOB() != null)
            {
                int year = cargo.getRestrictedDOB().getYear();
                // if skip was entered at the enter dob screen before
                // the birth date will be 1/1/x  where x is age that was skipped for
                // eg 0018 or 0021
                if (year < 1000)
                {                
                    // if the restricted age is greater than the highest restricted
                    // age already prompted for, then ask again aka more restrictive
                    if (restrictiveAge > year)
                    {
                        displayDOBPrompt(bus);
                    }
                    else
                    {
                        letter = new Letter("Continue");
                    }
                }
                else
                {
                    letter = new Letter("Next");
                }
            }
            else
            {
                displayDOBPrompt(bus);
            }        
            
            if (letter != null)
            {
                bus.mail(letter, BusIfc.CURRENT);
            }
        }
    }
    
    //----------------------------------------------------------------------
    /**
        This depart method captures the data entered from the ui and
        sets the dob is Skip was entered.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();  
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        if (letter.getName().equals("Next"))
        {
            // if there isnt a dob yet or
            // skip was entered before, we know because year was
            // less that 1000
            if (cargo.getRestrictedDOB() == null ||
                cargo.getRestrictedDOB().getYear() < 1000)
            {         
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                
                if(ui.getModel() instanceof AgeRestrictionBeanModel)
                {
                	AgeRestrictionBeanModel model = (AgeRestrictionBeanModel) ui.getModel();
                	cargo.setRestrictedDOB(model.getDateOfBirth());
                }
            }
        }
        else if (letter.getName().equals("Skip"))
        {
            if (cargo.getTransaction() == null)
            {
                SaleReturnTransactionIfc transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
                transaction.setCashier(cargo.getOperator());
                transaction.setSalesAssociate(cargo.getOperator());
                boolean transReentry = cargo.getRegister().getWorkstation().isTransReentryMode();
                ((SaleReturnTransaction)transaction).setReentryMode(transReentry);
                
                MAXUtilityManagerIfc utility =
                (MAXUtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                utility.initializeTransaction(transaction, bus, -1);
                cargo.setTransaction(transaction);
            }
            // set the year to the restrictive age.  this way we can check later
            ((SaleReturnTransactionIfc)cargo.getTransaction()).setAgeRestrictedDOB(new EYSDate(cargo.getPLUItem().getRestrictiveAge(), 1, 1));
        }
        else if (letter.getName().equals("Cancel") ||
                 letter.getName().equals("Undo"))
        {
            //remove item from transaction            
            cargo.setPLUItem(null);            
        }        
    }
    
    //----------------------------------------------------------------------
    /**
        This method displays the DOB screen.
        @param bus
    **/
    //----------------------------------------------------------------------
    public void displayDOBPrompt(BusIfc bus)
    {
        //display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        AgeRestrictionBeanModel model = new AgeRestrictionBeanModel();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        Boolean skipAllowed = Boolean.FALSE;
        try
        {
            skipAllowed = pm.getBooleanValue("AllowDateOfBirthPromptSkip");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }
        if (skipAllowed == Boolean.TRUE)
        {
            ui.showScreen(POSUIManagerIfc.ENTER_DOB, model);
        }
        else
        {                
            ui.showScreen(POSUIManagerIfc.ENTER_DOB_NO_SKIP, model);
        }
    }
}
