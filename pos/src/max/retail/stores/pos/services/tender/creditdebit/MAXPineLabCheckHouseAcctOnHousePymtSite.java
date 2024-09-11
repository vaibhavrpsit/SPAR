/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2003 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
     $
     Revision 1.1  2004/04/08 19:30:59  bwf
     @scr 4263 Decomposition of Debit and Credit.

     Revision 1.2  2004/03/17 22:32:54  rzurga
     @scr 3965 Swiping a credit card causes app to crash
     Fixed wrong card number lookup that caused null string 
     to be passed as a parameter

     Revision 1.1  2004/02/25 18:32:52  bwf
     @scr 3883 Credit Rework.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This method checks to see if this is a house account payment using
    a house account.  That is not acceptable.
    $Revision: 1.1 $
**/
//--------------------------------------------------------------------------
public class MAXPineLabCheckHouseAcctOnHousePymtSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** revision number **/
    public static final String revisionNumber = "$Revision: 1.1 $";

    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        String cardNumber = null;
        MSRModelIfc msrModel = (MSRModelIfc) cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);

        if(msrModel != null)
        {
            cardNumber = msrModel.getAccountNumber(); 
        }
        else
        {
            cardNumber = (String)cargo.getTenderAttributes().get(TenderConstants.NUMBER);
        }
        try
        {
            TenderCreditADO.checkHouseAcctOnHousePayment(cardData,
                                                         cargo.getCurrentTransactionADO());
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        catch(TenderException te)
        {
            TenderErrorCodeEnum error = te.getErrorCode();
            if (error == TenderErrorCodeEnum.INVALID_TENDER_TYPE)
            {
                POSUIManagerIfc ui =
                    (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                showInvalidTenderTypeDialog(ui);
            }                
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Shows the card type not accepted dialog screen.
     */
    //--------------------------------------------------------------------------
    protected void showInvalidTenderTypeDialog(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("HouseCardOnHousePayment");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
