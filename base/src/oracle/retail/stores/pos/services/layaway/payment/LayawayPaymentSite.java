/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/LayawayPaymentSite.java /main/19 2013/05/03 15:57:45 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     05/03/13 - Link business customer to layaway trans, 'null' is
 *                         added to customer name at Payment Detail screen
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    hyin      10/27/11 - fixed sending cancel letter twice problem when user
 *                         double click on cancel button
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    abondala  02/27/09 - updated
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *    cgreene   12/09/08 - removed no longer existing legal params
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         4/25/2007 8:52:24 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    6    360Commerce 1.5         5/4/2006 5:11:50 PM    Brendan W. Farrell
 *         Remove inventory.
 *    5    360Commerce 1.4         4/27/2006 7:07:07 PM   Brett J. Larsen CR
 *         17307 - inventory functionality removal - stage 2
 *    4    360Commerce 1.3         1/22/2006 11:45:11 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse
 *
 *   Revision 1.9.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.11  2004/10/12 16:38:52  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.10  2004/10/11 21:35:16  mweis
 *   @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.
 *
 *   Revision 1.9  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.8  2004/09/16 20:15:50  mweis
 *   @scr 7012 Correctly update the inventory counts when a layaway is picked up (completed).
 *
 *   Revision 1.7  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/06/11 20:19:47  mng
 *   Layaway Offline Payment, expiration date that Grace period has passed is rejected, SCR2466
 *
 *
 *    Rev 1.2   Mar 29 2004 01:11:32   cmsystem
 * Merge
 *   Revision 1.5  2004/03/26 22:05:48  jdeleau
 *   @scr 4200 Fix name of the INVALID_LAYOUT_ITEM screen (had some typos earlier)
 *
 *   Revision 1.4  2004/03/26 19:34:35  jdeleau
 *   @scr 4200 Change the screen name for layaway with a money order to INVALID_LAYAWAY_ITEM.
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
 *    Rev 1.0   Aug 29 2003 16:00:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Mar 19 2003 12:15:34   bwf
 * Code cleanup
 *
 *    Rev 1.2   Mar 19 2003 12:11:50   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.1   Sep 03 2002 10:51:24   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 *
 * Replaced hardcoded legal string with an attempt to pull the string from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:30   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 07 2002 17:39:12   dfh
 * updates to better print the layaway legal statement, 40 chars per line if needed, uses EOL from the system
 * Resolution for POS SCR-1414: Layaway does not support multi-line legal statement
 *
 *    Rev 1.3   Mar 01 2002 21:35:18   dfh
 * uses string array from parameter mgr to set the legal statement, add a newline char if line length is < 40
 * Resolution for POS SCR-1414: Layaway does not support multi-line legal statement
 *
 *    Rev 1.2   28 Feb 2002 11:10:58   jbp
 * Functionality to disallow giftcards on layaway transactions.
 * Resolution for POS SCR-1405: Unable to void a Layaway Pickup if transaction has a Gift Card item
 *
 *    Rev 1.1   Feb 05 2002 16:42:36   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:21:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import java.math.BigDecimal;
import java.util.Calendar;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.find.FindLayawayCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Displays layaway payment detail screen for making a payment on a layaway.
 *
 * @version $Revision: /main/19 $
 */
public class LayawayPaymentSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 709443204971518519L;

    /** class name constant */
    public static final String SITENAME = "LayawayPaymentSite";

    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** Customer name bundle tag */
    protected static final String CUSTOMER_NAME_TAG = "CustomerName";

    /** Customer name default text */
    protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";
    /**
     * ResourceID for invalid layaway item, used for a dialog and referred to in
     * dialogText_lang_locale.properties
     */
    protected static final String INVALID_LAYAWAY_ITEM = "INVALID_LAYAWAY_ITEM";

    /**
     * Displays the layaway payment detail screen. Sets model flags depending
     * upon the type of payment or pickup.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        String storeId = layawayCargo.getOperator().getStoreID();
        CodeListIfc layawayLocationsList =  utility.getReasonCodes(storeId, CodeConstantsIfc.CODE_LIST_LAYAWAY_LOCATION_REASON_CODES);
        String selectedLocationCode = null;

        // check to see if there is a gift card on the transaction
        boolean hasGiftCard = false;
        if(layawayCargo.getInitialLayawayTransaction() != null &&
                layawayCargo.getInitialLayawayTransaction().getTransactionType() ==
                TransactionIfc.TYPE_LAYAWAY_INITIATE)
        {
            AbstractTransactionLineItemIfc[] lineItems =
              layawayCargo.getInitialLayawayTransaction().getLineItems();
            for(int i=0; i<lineItems.length;i++)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];
                if(srli.getPLUItem() instanceof GiftCardPLUItemIfc)
                {
                    // There is a gift card
                    hasGiftCard = true;
                    // Set the transaction as a Tenderable Transaction for
                    // return shuttle to POS
                    layawayCargo.setTenderableTransaction(layawayCargo.getInitialLayawayTransaction());
                }
            }
        }

        if( layawayCargo.getDataExceptionErrorCode() == DataException.CONNECTION_ERROR )
        {
            OfflinePaymentBeanModel model;
            if (ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE) instanceof OfflinePaymentBeanModel)
            {
                model = (OfflinePaymentBeanModel)ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);
            }
            else
            {
                model = new OfflinePaymentBeanModel();
            }

            if(layawayCargo.getSeedLayawayTransaction() == null)
            {
                // Create transaction; the initializeTransaction() method is called
                // on UtilityManager
                TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
                transaction.setCashier(((AbstractFinancialCargo)layawayCargo).getOperator());
                TransactionUtilityManagerIfc util = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
                util.initializeTransaction(transaction);
                layawayCargo.setSeedLayawayTransaction(transaction);
            }
            ui.showScreen(POSUIManagerIfc.LAYAWAY_OFFLINE, model);
        }
        else if(hasGiftCard)
        {
            // Dialog screen mails undo letter to return to POS so
            // the gift card can be removed
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(LayawayPaymentSite.INVALID_LAYAWAY_ITEM);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            // Gets Layaway from cargo
            LayawayIfc layaway = layawayCargo.getLayaway();

            // Retrieve bean model to initialize its data and flags
            PaymentDetailBeanModel model = new PaymentDetailBeanModel();

            // Gets the Status of the layaway
            int status = layaway.getStatus();

            //  If layaway is undefined (initial layaway creation)...
            if (status == LayawayConstantsIfc.STATUS_UNDEFINED  && layawayCargo.isFirstRun())
            {
                LayawayTransactionIfc transaction = layawayCargo.getInitialLayawayTransaction();
                initializeNewLayaway(transaction, layawayCargo, pm);
                layawayCargo.setTenderableTransaction(transaction);
                layawayCargo.setFirstRun(false);
                model.setNewLayawayFlag(true);
            }
            else if (!(status == LayawayConstantsIfc.STATUS_UNDEFINED))  // Not a new layaway
            {
                model.setNewLayawayFlag(false);
                layawayCargo.setPreCreationFeeTotal(layaway.getBalanceDue());
                selectedLocationCode = layaway.getLocationCode().getCode();
            }
            else
            {
                model.setNewLayawayFlag(true);
            }

            // User intends to pick up layaway, pay in full
            if (layawayCargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_PICKUP)
            {
                model.setPickupLayawayFlag(true);
            }

            // Gets the customer
            CustomerIfc customer = layaway.getCustomer();

            // Create the customer name string from the bundle.          
            
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String pattern =
              utility.retrieveText("CustomerAddressSpec",
                                   BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                   CUSTOMER_NAME_TAG,
                                   CUSTOMER_NAME_TEXT);
            String customerName = null;
            if(customer.isBusinessCustomer())
            {
                customerName = customer.getCustomerName();
            }
            else
            {
                customerName =  LocaleUtilities.formatComplexMessage(pattern, parms);
            }
              

            // Set the model with layaway transaction details

            model.setLayawayNumber(layaway.getLayawayID());
            model.setCustomerName(customerName);
            model.setExpirationDate(layaway.getExpirationDate());
            model.setBalanceDue(layaway.getBalanceDue());
            model.setAmountPaid(layaway.getTotalAmountPaid());
            model.setLayawayFee(layaway.getCreationFee());
            model.setDeletionFee(layaway.getDeletionFee());
            model.setLayawayStatus(layaway.getStatus());
            model.setMinimumDownPayment(layaway.getMinimumDownPayment());
            model.setMinimumPayment(layaway.getMinimumDownPayment().add(layaway.getCreationFee()));
            model.inject(layawayLocationsList, selectedLocationCode, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

            // set the customer's name in the status area
            StatusBeanModel statusModel = new StatusBeanModel();

            statusModel.setCustomerName(customerName);
            model.setStatusBeanModel(statusModel);
            layawayCargo.setLayaway(layaway);

            ui.showScreen(POSUIManagerIfc.PAYMENT_DETAIL, model);
         }                               // Data Exception

    }

    /**
     * Initialize the attributes of a new layaway object.
     *
     * @param transaction layaway transaction object
     * @param ParameterManagerIfc parameter manager object
     * @return void
     * @exception none
     */
    public void initializeNewLayaway(LayawayTransactionIfc transaction,
                                     LayawayCargo layawayCargo,
                                     ParameterManagerIfc pm)
    { // begin initializeNewLayaway()
        // Default values for parameters
        String createCurrencyValue = "5.00";
        String deletionCurrencyValue = "5.00";
        Integer layawayDuration = new Integer(30);
        Integer gracePeriod = new Integer(30);
        Integer minimumDownPaymentPercent = new Integer(33);
        LayawayIfc layaway = transaction.getLayaway();
        TransactionTotalsIfc transactionTotals = transaction.getTransactionTotals();

        //Get layaway parameter values
        try
        {
            createCurrencyValue = pm.getStringValue(ParameterConstantsIfc.LAYAWAY_LayawayFee);
            deletionCurrencyValue = pm.getStringValue(ParameterConstantsIfc.LAYAWAY_DeletionFee);
            layawayDuration = pm.getIntegerValue(ParameterConstantsIfc.LAYAWAY_LayawayDuration);
            gracePeriod = pm.getIntegerValue(ParameterConstantsIfc.LAYAWAY_ExpirationGracePeriod);
            minimumDownPaymentPercent = pm.getIntegerValue(ParameterConstantsIfc.LAYAWAY_MinimumDownPaymentPercent);
        }
        catch (ParameterException e)
        {
            logger.error( Util.throwableToString(e));
        }

        // set the creation fee and deletion fee for the layaway
        CurrencyIfc layawayFee = DomainGateway.getBaseCurrencyInstance(createCurrencyValue);
        CurrencyIfc deletionFee = DomainGateway.getBaseCurrencyInstance(deletionCurrencyValue);
        layaway.setCreationFee(layawayFee);
        layaway.setDeletionFee(deletionFee);

        // Calculate Layaway Expiration Date
        EYSDate expirationDate = DomainGateway.getFactory().getEYSDateInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(expirationDate.dateValue());
        cal.add(Calendar.DAY_OF_MONTH, layawayDuration.intValue());
        expirationDate.initialize(cal.getTime());

        // calculate grace period date and set it
        EYSDate gracePeriodDate = DomainGateway.getFactory().getEYSDateInstance();
        cal.add(Calendar.DAY_OF_MONTH, gracePeriod.intValue());
        gracePeriodDate.initialize(cal.getTime());
        layaway.setGracePeriodDate(gracePeriodDate);

        // Calculate minimumDownPayment before layaway fee is added
        CurrencyIfc grandTotal = transactionTotals.getGrandTotal();
        CurrencyIfc preTaxTotal = grandTotal.subtract(transactionTotals.getTaxTotal());
        CurrencyIfc minimumDownPayment = DomainGateway.getBaseCurrencyInstance("0.00");

        if (minimumDownPaymentPercent.intValue() < 100)
        {
            minimumDownPayment = preTaxTotal.multiply(new BigDecimal
                (minimumDownPaymentPercent.floatValue()/100));
        }
        else  // parameter is 100 %
        {
            minimumDownPayment = preTaxTotal;
        }

        // Updates the grandTotal if Layaway is new - add creation fee
        layawayCargo.setPreCreationFeeTotal(grandTotal);
        CurrencyIfc total = grandTotal.add(layawayFee);

        // Total and Balance Due are the same for a new layaway
        layaway.setTotal(total);
        layaway.setBalanceDue(total);

        // Sets the minimum payment
        layaway.setMinimumDownPayment(minimumDownPayment);
    } // end initializeNewLayaway()

    /**
     * Clean the bean model for an offline payment when escaped
     *
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
        // check if this letter is a ButtonPressedLetter
        // from local navigation
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        LetterIfc letter = bus.getCurrentLetter();
        String letterName = letter.getName();

        // The bean model is recreated if escape or cancel is pressed
        if ((letter instanceof ButtonPressedLetter) && (letterName.equals("Undo")
                || letterName.equals("Cancel")))
        {
            OfflinePaymentBeanModel beanModel = new OfflinePaymentBeanModel();
            StatusBeanModel sbModel = new StatusBeanModel();
            sbModel.setCustomerName("");
            ui.customerNameChanged("",false);
            ui.setModel(POSUIManagerIfc.LAYAWAY_OFFLINE,beanModel);
        }
    }
}
