/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/ValidateOfflinePaymentAisle.java /main/13 2012/08/07 16:19:57 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/29/12 - wptg - merged keys
 *                         LayawayInvalidPayment.CannotExceed/LessThanEqual
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:24 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.5  2004/06/11 20:55:33  rsachdeva
 *   @scr 5421 Removed Unused Imports
 *
 *   Revision 1.4  2004/06/11 20:20:45  mng
 *   Layaway Offline Payment, expiration date that Grace period has passed is rejected.  SCR2466
 *
 *
 *    Rev 1.1   Feb 16 2004 16:05:06   mbowling
 * Merge
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Sep 16 2002 10:20:04   jriggins
 * Changed CurrencyIfc.toFormattedString() call to toFormattedString(locale).
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   03 Sep 2002 15:45:20   dfh
 * cleanup dialog msg, deprecation
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.1   03 Sep 2002 14:11:28   dfh
 * added error message text for offline payment error, allow total balance to be entered when offline
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.0   Apr 29 2002 15:20:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:40   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 10 2002 18:00:28   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   29 Nov 2001 16:45:42   jbp
 * modified layaway dialog screens.
 * Resolution for POS SCR-335: Layaway Updates
 *
 *    Rev 1.0   Sep 21 2001 11:21:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// java imports
import java.util.Calendar;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;

//--------------------------------------------------------------------------
/**
    Displays error message indicating payment is invalid.
    <P>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class ValidateOfflinePaymentAisle extends LaneActionAdapter
{

    /**
     *
     */
    private static final long serialVersionUID = 673710028917344293L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       Invalid Layaway Payment string
    **/
    public static final String INVALID_PAYMENT = "LayawayInvalidPayment";
    /**
    Invalid Layaway Payment string
    **/
    public static final String INVALID_EXPIRATION_DATE = "LayawayInvalidExpirationDate";
   
   
    //----------------------------------------------------------------------
    /**
       Displays error message indicating payment is invalid. If the payment
       is greater than the balance due, or equal to it, then the
       InvalidLayawayPayment error screen is displayed.  This is because a
       layaway cannot be completed offline.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        OfflinePaymentBeanModel beanModel =
            (OfflinePaymentBeanModel) ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);

        // set values from user entry
        CurrencyIfc paymentAmount = beanModel.getPaymentAmount();
        CurrencyIfc balanceDue = beanModel.getBalanceDue();
        CurrencyIfc zeroCurrencyValue = DomainGateway.getBaseCurrencyInstance();

        // initialize to zero
        zeroCurrencyValue.setZero();

        // Get the expiration date entered
        EYSDate expirationDate = beanModel.getExpirationDate();
        EYSDate todayEYSDate = DomainGateway.getFactory().getEYSDateInstance();
        todayEYSDate.initialize(EYSDate.TYPE_DATE_ONLY);
        boolean isExpirationDatePassed = false;
        if ( expirationDate.before(todayEYSDate) )
        {
            // Default grace period is 365 days
            Integer gracePeriodInt = new Integer(365);
            // add grace period date to expiration date
            expirationDate.add(Calendar.DAY_OF_MONTH, gracePeriodInt.intValue());
            if ( expirationDate.before(todayEYSDate) )
            {
                isExpirationDatePassed = true;
            }
        }

        if ( paymentAmount.compareTo(balanceDue) > 0 )
        {

            DialogBeanModel model = new DialogBeanModel();
            // set arg string to balance due
            String args[] = new String[1];

            args[0] = balanceDue.toFormattedString();
            
            model.setResourceID(INVALID_PAYMENT);
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (isExpirationDatePassed)
        {
            DialogBeanModel model = new DialogBeanModel();
            // set arg string to balance due
            String args[] = new String[1];
            args[0] = expirationDate.toFormattedString();
            model.setResourceID(INVALID_EXPIRATION_DATE);
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(new Letter("OfflinePayment"), BusIfc.CURRENT);
        }
    }



}
