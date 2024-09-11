/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/ValidatePaymentEnteredAisle.java /main/19 2012/08/07 16:19:59 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/29/12 - wptg - merged keys
 *                         DialogSpec.LayawayInvalidPayment.CannotExceed/LessThanEqual.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jkoppolu  08/02/10 - Fix for Bug#9955719, incorrect balance due amount
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    jswan     04/02/09 - Fix an issue saving the layaway to the database.
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/25/2007 8:52:24 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         5/4/2006 5:11:50 PM    Brendan W. Farrell
 *         Remove inventory.
 *    4    360Commerce 1.3         4/27/2006 7:07:07 PM   Brett J. Larsen CR
 *         17307 - inventory functionality removal - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.6.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.7  2004/10/12 16:38:52  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.6  2004/09/21 20:29:40  mweis
 *   @scr 7012 Enable correct inventory accounting for kits w.r.t. Layaways
 *
 *   Revision 1.5  2004/09/14 23:07:03  mweis
 *   @scr 7012 Inventory parameters for Layaway.
 *
 *   Revision 1.4  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
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
 *    Rev 1.4   Mar 19 2003 12:16:26   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
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
 *    Rev 1.0   Apr 29 2002 15:20:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:42   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 10 2002 18:00:28   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   21 Dec 2001 14:14:58   jbp
 * use business date instead of system date when initializing a payment.
 * Resolution for POS SCR-437: Layaway payment cannot be added
 *
 *    Rev 1.3   29 Nov 2001 16:45:44   jbp
 * modified layaway dialog screens.
 * Resolution for POS SCR-335: Layaway Updates
 *
 *    Rev 1.2   27 Nov 2001 16:17:24   jbp
 * removed depricated methods
 * Resolution for POS SCR-327: Layaway pickup cannot be voided when initial and pickup are different business days
 *
 *    Rev 1.1   27 Nov 2001 16:07:04   jbp
 * set payment date to be new EYSDate.  Removed unused code.
 * Resolution for POS SCR-327: Layaway pickup cannot be voided when initial and pickup are different business days
 *
 *    Rev 1.0   Sep 21 2001 11:21:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;

/**
 * Displays error message indicating payment is invalid.
 *
 * @version $Revision: /main/19 $
 */
public class ValidatePaymentEnteredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 4810656266961423017L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * Layaway Payment Minimum string
     */
    public static final String PAYMENT_MINIMUM = "LayawayPaymentMinimum";

    /**
     * Invalid Layaway Payment string
     */
    public static final String INVALID_PAYMENT = "LayawayInvalidPayment";


    /**
     * Displays error message indicating payment is invalid. If payment is below
     * minimum, displays the LayawayPaymentMinimum error screen. If payment is
     * greater than the balance due, then displays the InvalidLayawayPayment
     * error screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();
        LayawayIfc layaway = layawayCargo.getLayaway();

        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        PaymentDetailBeanModel beanModel =
            (PaymentDetailBeanModel) ui.getModel(POSUIManagerIfc.PAYMENT_DETAIL);

        // initializes values
        CurrencyIfc minimumDownPayment  = layaway.getMinimumDownPayment();
        CurrencyIfc layawayFee          = beanModel.getLayawayFee();
        CurrencyIfc minimumPayment      = minimumDownPayment.add(layawayFee);
        CurrencyIfc paymentAmount       = beanModel.getPayment();
        CurrencyIfc balanceDue          = beanModel.getBalanceDue();
        CurrencyIfc zeroCurrencyValue   = DomainGateway.getBaseCurrencyInstance();

        // initialize to zero
        zeroCurrencyValue.setZero();

        // If the creation fee changed...
        if ( layaway.getCreationFee().compareTo(layawayFee) != 0 )
        {
            minimumPayment = minimumDownPayment.add(layawayFee);
        }

        layaway.setCreationFee(layawayFee);
    layaway.setBalanceDue(balanceDue);
        // Make sure that there is a location code object in the layaway.
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        String storeId = layawayCargo.getOperator().getStoreID();
        CodeListIfc rcl = utility.getReasonCodes(storeId, CodeConstantsIfc.CODE_LIST_LAYAWAY_LOCATION_REASON_CODES);

        String  reason      = beanModel.getSelectedReasonKey();

        if (rcl != null)
        {
            CodeEntryIfc entry = rcl.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());

        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }

        layaway.setLocationCode(localizedCode);

        // If initial payment is less than minimum payment...
        if ( beanModel.getLayawayStatus() == LayawayConstantsIfc.STATUS_UNDEFINED
            && paymentAmount.compareTo(minimumPayment) < 0 )
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(PAYMENT_MINIMUM);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (paymentAmount.compareTo(balanceDue) > 0) // payment > balance due
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
        else
        {
            // The payment is valid and a payment is created and set to the cargo
            PaymentIfc payment = DomainGateway.getFactory().getPaymentInstance();
            payment.setReferenceNumber(layaway.getLayawayID());
            payment.setPaymentAccountType(PaymentConstantsIfc.ACCOUNT_TYPE_LAYAWAY);
            payment.setTransactionID(layaway.getInitialTransactionID());
            payment.setPaymentAmount(paymentAmount);
            payment.setBusinessDate(layawayCargo.getStoreStatus().getBusinessDate());
            layawayCargo.setPayment(payment);
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
