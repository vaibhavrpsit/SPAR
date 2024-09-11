/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/LinkCustomerReturnShuttle.java /main/17 2012/09/12 11:57:21 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    blarsen   02/16/12 - XbranchMerge
 *                         blarsen_bug13689528-ej-issue-customer-link-uses-prev-trans-id
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *    deghosh   12/23/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/11/2008 3:55:53 PM   Gloria Wang     Code
 *         reviewed by Dan.
 *    4    360Commerce 1.3         6/5/2008 11:07:20 AM   Gloria Wang     CR
 *         31733.
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:03  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:47  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Dec 26 2002 15:00:52   crain
 * Removed check for database offline
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.2   Aug 29 2002 13:40:56   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   24 Jun 2002 11:45:20   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.0   Apr 29 2002 15:21:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:44   msg
 * Initial revision.
 *
 *    Rev 1.2   04 Feb 2002 17:36:04   jbp
 * journal layaway info after Layaway Customer screen.
 * Resolution for POS SCR-996: Adding new Customer thru Layaway causes system to hang
 *
 *    Rev 1.1   29 Jan 2002 09:30:46   baa
 * fix null pointer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 * Resolution for POS SCR-706: Scanning/swiping customer card on Customer Options hangs application
 *
 *    Rev 1.0   Sep 21 2001 11:20:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create;
// java imports
import java.util.Calendar;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle updates the parent cargo with information from the child cargo.
**/
//--------------------------------------------------------------------------
public class LinkCustomerReturnShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2416878862229027488L;

    /**
        customer main cargo
    **/
    protected CustomerMainCargo customerMainCargo = null;
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.layaway.create.LinkCustomerReturnShuttle.class);
    /**
        blanks for layaway header journal string
    **/
    public static final String headerBlanks = "                 ";
    /**
        transaction discount journal string
    **/
    public static final String transactionDiscount = "TRANS: Discount";
    /**
        discount percent journal string
    **/
    public static final String discountPercent = "Discount % ";
    /**
        discount reason journal string
    **/
    public static final String discountReason = "Disc. Rsn. ";
    /**
        layawy id modifier string
    **/
    public static final String idModifier = "yyMMdd";
    /**
        journal exit customer string
    **/
    public static final String exitCustomer = "Exiting Customer";
    /**
        journal link customer string
    **/
    public static final String linkCustomer = "  Link Customer: ";

     /**
       Customer name bundle tag
     **/
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
       Customer name default text
     **/
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    //----------------------------------------------------------------------
    /**
        Copies information needed from child service to the calling service.
        <P>
        @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the customer service
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by parent service. Creates a layaway
        transaction if one not already created and links this customer.
        <P>
        @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();

        // retrieve cargo from the parent
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();
        SaleReturnTransactionIfc saleTransaction = layawayCargo.getSaleTransaction();
        LayawayTransactionIfc layawayTransaction = null;
        JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RegisterIfc register = (RegisterIfc) layawayCargo.getRegister();
        boolean journalLink = true; // whether to journal the linked customer
        layawayCargo.setDataExceptionErrorCode(customerMainCargo.getDataExceptionErrorCode());

        TransactionUtilityManagerIfc utility =
                (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        // determines if customer service linked the customer
        if (customerMainCargo.isLink() && customer != null)
        {
            layawayCargo.setCustomer(customer);

            if (saleTransaction == null)
            {
                // Create sale transaction; the initializeTransaction() method is called
                // in UtilityManagerIfc  Set cashier and sales associate.
                saleTransaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
                saleTransaction.setCashier(layawayCargo.getOperator());
                saleTransaction.setSalesAssociate(customerMainCargo.getSalesAssociate());

                utility.initializeTransaction(saleTransaction,
                                              UtilityManagerIfc.GENERATE_SEQUENCE_NUMBER,
                                              customer.getCustomerID());
                journalLink = false;
            }
            // build the layaway based upon the sale transaction
            layawayTransaction = DomainGateway.getFactory().getLayawayTransactionInstance();
            layawayTransaction.initialize((SaleReturnTransaction)saleTransaction);
            initializeNewLayaway(bus,
                                 layawayTransaction,
                                 jmgr,
                                 pm,
                                 customer,
                                 register,
                                 ui,
                                 journalLink);
            layawayCargo.setInitialLayawayTransaction(layawayTransaction);
            // reset non-critical database exception
            layawayCargo.setDataExceptionErrorCode(DataException.UNKNOWN);
        }

        if (layawayTransaction != null) {
            CustomerUtilities.journalCustomerExit(bus, layawayTransaction.getCashier().getEmployeeID(),
                layawayTransaction.getTransactionID());
        }

    }

   //--------------------------------------------------------------------------
    /**
        Calculates and sets the layaway expiration date, links the customer to the
        layaway, journals the layaway creation, and sets
        the customer's name in the ui status area.
        <P>
        @param  bus reference
        @param  layawayTransaction  reference
        @param  JournalManagerIfc reference
        @param  ParameterManagerIfc reference
        @param  CustomerIfc reference
        @param  RegisterIfc reference
        @param  POSUIManagerIfc reference
        @param journalLinke link to journal indicator
    **/
    //--------------------------------------------------------------------------
    protected void initializeNewLayaway(BusIfc bus,
                                        LayawayTransactionIfc layawayTransaction,
                                        JournalManagerIfc jmgr,
                                        ParameterManagerIfc pm,
                                        CustomerIfc customer,
                                        RegisterIfc register,
                                        POSUIManagerIfc ui,
                                        boolean journalLink)
    {                                   // begin initializeNewLayaway()
        Integer layawayDuration = new Integer(30);

        // get layaway duration parameter value
        try
        {
            layawayDuration = pm.getIntegerValue(ParameterConstantsIfc.LAYAWAY_LayawayDuration);
        }
        catch (ParameterException e)
        {
            logger.warn( Util.throwableToString(e));
        }

        // Calculate and set Layaway Expiration Date
        EYSDate expirationDate = DomainGateway.getFactory().getEYSDateInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(expirationDate.dateValue());
        cal.add(Calendar.DAY_OF_MONTH, layawayDuration.intValue());
        expirationDate.initialize(cal.getTime());
        layawayTransaction.setLayaway(DomainGateway.getFactory().getLayawayInstance());
        layawayTransaction.getLayaway().setExpirationDate(expirationDate);

        // link the customer, set type, id, and update cargo
        layawayTransaction.linkCustomer(customer);
        layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_INITIATE);
        layawayTransaction.getLayaway().setLayawayID(register.getNextUniqueID());
        layawayTransaction.setUniqueID(register.getCurrentUniqueID());
        layawayTransaction.getLayaway().setInitialTransactionBusinessDate(register.getBusinessDate());

        // journal link customer
        if (journalLink)
        {
            CustomerUtilities.journalCustomerLink(bus, layawayTransaction.getCashier().getEmployeeID(),
                        customer.getCustomerID(),
                        layawayTransaction.getTransactionID());
            // journal the customer pricing after customer has been linked
            // to the transaction in order to pickup the correct pricing.
            CustomerUtilities.journalCustomerPricing(bus, layawayTransaction, customer.getPricingGroupID());
        }

        // journal discounts as needed
        CustomerGroupIfc[] customerGroups = customer.getCustomerGroups();
        DiscountRuleIfc[] discounts = null;
        if (customerGroups != null && customerGroups.length > 0)
        {
            customer.getCustomerGroups()[0].getDiscountRules();
            discounts = customerGroups[0].getDiscountRules();
        }
        if (discounts != null && discounts.length > 0)
        {
            DiscountRuleIfc rule = discounts[0];
            Object[] dataArgs = new Object[2];
            dataArgs[0] = 100.0 * rule.getDiscountRate().doubleValue();
            StringBuffer sb = new StringBuffer();
            sb.append(Util.EOL
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL, null)
                    + Util.EOL
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DISCOUNT_PERCENT_LABEL,
                            dataArgs)
                    + Util.EOL);
            dataArgs[0] = rule.getName(LocaleMap
                    .getLocale(LocaleConstantsIfc.JOURNAL));
            sb.append( I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs));

           /* String message = "\n" + transactionDiscount
                + "\n  " + discountPercent
                + 100.0 * rule.getDiscountRate().doubleValue()
                + "\n  " + discountReason + rule.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));*/
            jmgr.journal(layawayTransaction.getCashier().getEmployeeID(),
                        layawayTransaction.getTransactionID(),
                        sb.toString());
        }
        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();

        // Create the string from the bundle.
        CustomerIfc layawayCustomer = layawayTransaction.getCustomer();
        UtilityManagerIfc utility =
          (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Object parms[] = { layawayCustomer.getFirstName(),
                           layawayCustomer.getLastName() };
        String pattern =
          utility.retrieveText("CustomerAddressSpec",
                               BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                               CUSTOMER_NAME_TAG,
                               CUSTOMER_NAME_TEXT);
        String customerName =
          LocaleUtilities.formatComplexMessage(pattern, parms);

        ui.customerNameChanged(customerName);

        POSBaseBeanModel baseModel = null;
        baseModel = (POSBaseBeanModel)ui.getModel();

        if (baseModel == null)
        {
            baseModel = new POSBaseBeanModel();
        }

        baseModel.setStatusBeanModel(statusModel);
    }                                   // end initializeNewLayaway()
}
