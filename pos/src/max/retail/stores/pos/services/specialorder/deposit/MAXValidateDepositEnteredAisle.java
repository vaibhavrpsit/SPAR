/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.3	22/Aug/2013	  	Jyoti, Validation added for Expected time 
  Rev 1.2	13/Aug/2013		Prateek, Changes done for Special Order CR - Suggested Tender
  Rev 1.1	29/May/2013	  	Tanmaya, Bug 6049 - System Allow user to send home delivery in back date. 
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import java.util.Calendar;
import java.util.Date;

import max.retail.stores.domain.order.MAXOrderStatusIfc;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import max.retail.stores.pos.ui.beans.MAXSpecialOrderDepositBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Displays error message indicating deposit is invalid or sets the deposit.
    <P>
    @version $Revision: 5$
**/
//--------------------------------------------------------------------------
public class MAXValidateDepositEnteredAisle extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5708078401855997523L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 5$";
    /**
       Special Order Deposit Minimum string
    **/
    public static final String DEPOSIT_MINIMUM = "MinimumDepositRequired";
    /**
       Invalid Special Order Deposit string
    **/
    public static final String INVALID_DEPOSIT = "InvalidSpecialOrderDeposit";

    //----------------------------------------------------------------------
    /**
       Displays error message indicating deposit is invalid. If deposit is
       below minimum, displays the MinimumDepositRequired error screen. If deposit
       is greater than the balance due, then displays the InvalidSpecialOrderDeposit
       error screen.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo) bus.getCargo();
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        MAXSpecialOrderDepositBeanModel beanModel =
            (MAXSpecialOrderDepositBeanModel) ui.getModel(POSUIManagerIfc.SPECIAL_ORDER_DEPOSIT);

        // initializes values from model
        CurrencyIfc depositAmount       = beanModel.getDepositAmountValue();
        CurrencyIfc minimumDeposit      = orderTransaction.getOrderStatus().getMinimumDepositAmount();
        CurrencyIfc grandTotal          = orderTransaction.getOrderStatus().getTotal();
        EYSDate eysDate = new EYSDate();
        EYSDate beanModelDate = beanModel.getExpectedDeliveryDate();
		//Rev 1.3 changes start
		EYSTime eysTime = new EYSTime();
        EYSTime beanModelTime = beanModel.getExpectedDeliveryTime();
				//Rev 1.3 changes end
		/**MAX Rev 1.2 Change : Start**/
		String suggestedTender = beanModel.getSuggestedTender().trim();
		/**MAX Rev 1.3 Change : Start**/
        // If initial deposit is less than minimum deposit...
        if (depositAmount.compareTo(minimumDeposit) < 0 )
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(DEPOSIT_MINIMUM);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (depositAmount.compareTo(grandTotal) > 0) // deposit > total
        {
            String[] args = new String[1];
            args[0] = grandTotal.toFormattedString();
            DialogBeanModel model = new DialogBeanModel();
            model.setArgs(args);
            model.setResourceID(INVALID_DEPOSIT);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if(removeTime(beanModelDate).before(removeTime(eysDate)) )
        {
        	DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("BeforeDateForSpecialOrderOrSend");
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        //Rev 1.3 change start
        else if((removeTime(beanModelDate).equals(removeTime(eysDate)))&&(beanModelTime.before(eysTime))){
        		DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("BeforeTimeForSpecialOrderOrSend");
				model.setType(DialogScreensIfc.ERROR);
				model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
						CommonLetterIfc.RETRY);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			
		}//Rev 1.3 changes end
        else
        {
            // The deposit is valid and a deposit is created and set to the cargo
            ((MAXOrderStatusIfc) orderTransaction.getOrderStatus()).setDepositAmount(depositAmount);
            if(orderTransaction instanceof MAXOrderTransactionIfc)
            {
            	((MAXOrderTransactionIfc)orderTransaction).setExpectedDeliveryDate(beanModel.getExpectedDeliveryDate());
            	((MAXOrderTransactionIfc)orderTransaction).setExpectedDeliveryTime(beanModel.getExpectedDeliveryTime());
				/**MAX Rev 1.2 Change : Start**/
				((MAXOrderTransactionIfc)orderTransaction).setSuggestedTender(suggestedTender);
				/**MAX Rev 1.2 Change : End**/
            }
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
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
        return   new EYSDate(cal.getTime());
    }
}
