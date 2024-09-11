/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/PrintableDocumentManager.java /main/62 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    subrdey   07/31/13 - eReceipts will print VAT code when
 *                         'VATCodeReceiptPrinting' is set to Yes
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    icole     07/03/12 - FORWARD PORT: PAT customer information not being
 *                         printed on the receipt.
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    hyin      10/25/11 - add checking before printing customer send info
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    vtemker   03/08/11 - Print Preview Quickwin for Reports
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     11/05/10 - Modified to prevent returns with employee discounts
 *                         from printing the Employee Discount Store Receipt.
 *    asinton   10/21/10 - Add credit disclosure to layaway, orders, and bill
 *                         pay transactions.
 *    mchellap  10/19/10 - BUG#10186755 Fixed void receipt printing for tax
 *                         exempted transactions.
 *    asinton   09/28/10 - More updates for credit card promotion disclosure.
 *    asinton   09/24/10 - Adding Credit Card Accountability Responsibility and
 *                         Disclosure Act of 2009 changes.
 *    npoola    09/02/10 - Print layway complete receipt instead of TaxExempt
 *    nkgautam  06/24/10 - bill pay changes
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    jswan     04/27/10 - Merges from refreshing.
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    jswan     04/23/10 - Refactored CTR to include more data in the
 *                         SaleReturnLineItem class and table to reduce the
 *                         data required in and retvieved from the CO database.
 *                         Modified this class to retain the final receipt
 *                         description in the SaleReturnLineItem.
 *    cgreene   04/21/10 - updating deprecated names
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    npoola    01/22/10 - Gift Card details in line items and gift receipt is
 *                         printed for PDO
 *    npoola    01/22/10 - fixed to print Layway receipt instead of Tax Exempt
 *                         for Layaway transactions
 *    abondala  01/03/10 - update header date
 *    cgreene   04/05/09 - changed method name to setGroupLikeItems
 *    npoola    03/19/09 - Fix to print the SpecialOrderReceipt when the order
 *                         is partial
 *    cgreene   03/09/09 - set receipt locale explicity since locale in map
 *                         changes depending on the customer loaded
 *    cgreene   02/24/09 - add tillloan as type to print its own canceled
 *                         receipt
 *    cgreene   02/24/09 - ensure that canceled transaction are using the
 *                         CanceledReceipt blueprint
 *    cgreene   02/21/09 - if canceled and type is tillpickup, ust print
 *                         tillpickup blueprint
 *    cgreene   02/20/09 - set default lcoale where appropriate and let Spring
 *                         set locale onto receiptparameterbean
 *    mahising  02/20/09 - Print PDO receipt when transaction is done by
 *                         purchase order
 *    sgu       02/18/09 - fix all callers of getGroupText and getText to use
 *                         best match locale
 *    acadar    02/12/09 - use default locale for date/time printing in the
 *                         receipts
 *    atirkey   01/15/09 - modified to remove multiple tender type displays
 *
 *    cgreene   01/13/09 - multiple send and gift receipt changes. deleted
 *                         SendGiftReceipt
 *    cgreene   01/08/09 - removed reference to getSendPackagesVector()
 *    atirkey   12/02/08 - Layaway delete receipt
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    cgreene   11/25/08 - added additional receipt types for flexibility in
 *                         receipt copy counts
 *    sgu       11/19/08 - add VAT enabled flag to receipt parameter bean
 *    cgreene   11/17/08 - deprecated isEchangeTransactionType method
 *    atirkey   11/17/08 - post void
 *    atirkey   11/17/08 - Removed commented code
 *    atirkey   11/14/08 - post void
 *    atirkey   11/14/08 - post void
 *    cgreene   11/14/08 - fix cast of PrintableDocumentParameterBeanIfc in
 *                         getParameterBean
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   11/13/08 - deprecate getSurveyText in favor of isSurveyExpected
 *                         and editing Survey.bpt
 *    cgreene   11/12/08 - removed reference to PATFotter parameter
 *
 * ===========================================================================
 * $Log:
 *  12   360Commerce 1.11        5/15/2008 6:30:49 AM   Sishant Kumar   Forward
 *        ported from V12x
 *  11   360Commerce 1.10        3/5/2008 10:33:39 PM   Sujay Beesnalli For CR#
 *        30382, enabling auto print customer copy flag for void transactions
 *       based on parameter settings.
 *  10   360Commerce 1.9         3/5/2008 3:34:40 PM    Siva Papenini   CR
 *       28,821 : Updated the code to print "Status:" instead of "Status"
 *  9    360Commerce 1.8         3/4/2008 4:35:02 AM    Manas Sahu      For CR
 *       # 29872
 *  8    360Commerce 1.7         11/15/2007 11:01:13 AM Christian Greene Belize
 *        merge - gift receipt printing fixes
 *  7    360Commerce 1.6         6/19/2007 5:14:31 PM   Maisa De Camargo Added
 *       logic to handle the vatCodeReceiptPrinting Parameter. This parameter
 *       indicates if the vatCode should be printed in the receipt for the
 *       item level or not.
 *  6    360Commerce 1.5         6/14/2007 6:37:09 PM   Ranjan X Ojha   Fix for
 *        Layaway Delete printing incorrect receipt format
 *  5    360Commerce 1.4         6/13/2007 12:12:08 PM  Alan N. Sinton  CR
 *       26485 - Changes per code review.
 *  4    360Commerce 1.3         6/4/2007 3:55:37 PM    Alan N. Sinton  CR
 *       26481 - Changes per review comments.
 *  3    360Commerce 1.2         5/15/2007 4:03:09 PM   Alan N. Sinton  CR
 *       26481 - Phase one for VAT modifications to ORPOS <ARG> Summary
 *       Reports.
 *  2    360Commerce 1.1         5/7/2007 4:07:14 PM    Alan N. Sinton  CR
 *       26485 - Modified to use the Gateway.getBooleanProperty() method.
 *  1    360Commerce 1.0         4/30/2007 7:00:39 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.manager.ConfigurableManager;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedDocumentManager;
import oracle.retail.stores.pos.reports.ReportTypeConstantsIfc;
import oracle.retail.stores.pos.reports.SummaryReport;
import oracle.retail.stores.pos.services.printing.CustomerSurveyReward;
import oracle.retail.stores.pos.services.printing.CustomerSurveyRewardIfc;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;



/**
 * Manager for printing Receipts, reports, etc.
 */
public class PrintableDocumentManager extends ConfigurableManager
    implements PrintableDocumentManagerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(PrintableDocumentManager.class);

    /**
     * The number of (sale) receipts printed since the last customer survey printed.
     */
    protected static int printReceiptCount = 0;

    /**
     * PrintableDocumentFactory.
     * @deprecated as of 13.1 since blueprints are used instead of printable documents.
     */
    protected PrintableDocumentFactoryIfc printableDocumentFactory;

    /**
     * Printable Document factory name.
     * @deprecated as of 13.1 since blueprints are used instead of printable documents.
     */
    protected String printableDocumentFactoryName = "oracle.retail.stores.pos.receipt.PrintableDocumentFactory";

   /**
     * Constructor for PrintableDocumentManager.
     *
     */
    public PrintableDocumentManager()
    {
    }

    /**
     * Prints the gift receipt for the given line items.
     *
     * @param bus
     * @param giftReceiptParameterBean
     * @throws PrintableDocumentException
     */
    public void printGiftReceipt(SessionBusIfc bus, GiftReceiptParameterBeanIfc giftReceiptParameterBean) throws PrintableDocumentException
    {
        logger.debug("Entered PrintableDocumentManager.printGiftReceipt()");
        SaleReturnTransactionIfc trans = null;
        if(giftReceiptParameterBean.getTransaction() instanceof SaleReturnTransactionIfc)
        {
            trans = (SaleReturnTransactionIfc)giftReceiptParameterBean.getTransaction();
        }
        SaleReturnLineItemIfc[] lineItems = null;
        lineItems = giftReceiptParameterBean.getSaleReturnLineItems();
        if (trans != null && lineItems != null)
        {
            lineItems = removeDamageDiscounts(lineItems);
            if(lineItems.length > 0)
            {
                POSDeviceActions pda = new POSDeviceActions(bus);
                EYSPrintableDocumentIfc receipt = null;
                SaleReturnLineItemIfc srli = lineItems[0];
                try
                {
                    PrintableDocumentFactoryIfc factory = this.getPrintableDocumentFactory();
                    if(lineItems.length > 1 ||  srli.isUnitOfMeasureItem() || srli.isKitHeader() || srli.isKitComponent())
                    {
                        receipt = factory.createPrintableDocument(giftReceiptParameterBean);
                        if(receipt == null)
                        {
                            throw new PrintableDocumentException("Could not find receipt for document type: " +
                                    giftReceiptParameterBean.getDocumentType());
                        }
                        pda.printDocument(receipt);
                    }
                    else
                    {
                        // Check the quantity of the item, print multiple gift receipts
                        // for multiple items if quantity is greater than one.
                        // Print the discounted selling price (not extended) on each gift receipt.

                        // Only print gift receipts for the items left to return.
                        BigDecimal returnQuantity = srli.getQuantityReturnable();
                        int quantity = returnQuantity.intValue();

                        for (int j = 0; j < quantity; j++)
                        {
                            giftReceiptParameterBean.setSaleReturnLineItems(new SaleReturnLineItemIfc[] { srli });
                            receipt = factory.createPrintableDocument(giftReceiptParameterBean);
                            if(receipt == null)
                            {
                                throw new PrintableDocumentException("Could not find receipt for document type: " +
                                        giftReceiptParameterBean.getDocumentType());
                            }
                            pda.printDocument(receipt);
                        }
                    }
                }
               catch (DeviceException e)
               {
                    logger.warn("Unable to print gift receipt. " + e.getMessage());

                    if (e.getCause() != null)
                    {
                        logger.warn(
                            "DeviceException.NestedException:  "
                            + " "
                            + Util.throwableToString(e.getCause()));
                    }

                    throw new PrintableDocumentException(
                            "DeviceException caugth while attempting to print document type: " +
                            giftReceiptParameterBean.getDocumentType(), e);
                }
            }
        }
        logger.debug("Exiting PrintableDocumentManager.printGiftReceipt()");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc#printSendGiftReceipt(oracle.retail.stores.foundation.tour.service.SessionBusIfc, oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc[])
     */
    public void printSendGiftReceipt(SessionBusIfc bus, GiftReceiptParameterBeanIfc[] beans) throws PrintableDocumentException
    {
        // POSDeviceActions for printing the document(s).
        POSDeviceActions pda = new POSDeviceActions(bus);

        // get document factory
        PrintableDocumentFactoryIfc factory = getPrintableDocumentFactory();

        for (int i = 0; i < beans.length; i++)
        {
            printSendGiftReceipt(bus, pda, factory, beans[i]);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc#printShippingSlip(oracle.retail.stores.foundation.tour.service.SessionBusIfc, oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc)
     */
    public void printShippingSlip(SessionBusIfc bus, PrintableDocumentParameterBeanIfc parameterBean)
        throws PrintableDocumentException
    {
        throw new IllegalAccessError("Not supported. Use BlueprintedDocumentManager instead.");
    }

    /**
     * Prints the order receipt for the given line item.
     *
     * @param bus               The session bus
     * @param receiptParameterBean The Instance of the OrderReceiptParameterBeanIfc.
     * @throws PrintableDocumentException
     */
    public void printOrderReceipt(SessionBusIfc bus,
            OrderReceiptParameterBeanIfc receiptParameterBean)
        throws PrintableDocumentException
    {
        logger.debug("Entered PrintableDocumentManager.printOrderReceipt()");
        if(bus ==  null)
        {
            throw new PrintableDocumentException("Argument 'bus' cannot be null.");
        }
        if(receiptParameterBean == null)
        {
            throw new PrintableDocumentException("Argument 'parameters' cannot be null.");
        }

        try
        {
            // create the receipt class
            EYSPrintableDocumentIfc orderReceipt =
                getPrintableDocumentFactory().createPrintableDocument(receiptParameterBean);

            if(orderReceipt == null)
            {
                throw new PrintableDocumentException("Could not find receipt for document type: " +
                                                     receiptParameterBean.getDocumentType());
            }
            // send to the printer
            POSDeviceActions pda = new POSDeviceActions(bus);
            pda.printDocument(orderReceipt);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to print receipt. " + e.getMessage() + "");

            if (e.getCause() != null)
            {
                logger.warn("DeviceException.NestedException: " +
                            Util.throwableToString(e.getCause()));
            }
            throw new PrintableDocumentException(
                    "DeviceException caught while printing for Document Type: " + receiptParameterBean.getDocumentType(),
                    e);
        }
        logger.debug("Exiting PrintableDocumentManager.printOrderReceipt()");
    }

    /**
     * Prints the receipt for the transaction.
     *
     * @param bus
     * @param parameters
     * @throws PrintableDocumentException
     */
    public void printReceipt(SessionBusIfc bus, PrintableDocumentParameterBeanIfc parameters)
        throws PrintableDocumentException
    {
        logger.debug("Entered PrintableDocumentManager.printReceipt()");
        if(bus ==  null)
        {
            throw new PrintableDocumentException("Argument 'bus' cannot be null.");
        }
        if(parameters == null)
        {
            throw new PrintableDocumentException("Argument 'parameters' cannot be null.");
        }

        try
        {
            // POSDeviceActions for printing the document(s).
            POSDeviceActions pda = new POSDeviceActions(bus);

            // get document factory
            PrintableDocumentFactoryIfc factory = getPrintableDocumentFactory();
            // get pritable document
            EYSPrintableDocumentIfc document = factory.createPrintableDocument(parameters);
            if(document == null)
            {
                throw new PrintableDocumentException("Could not find receipt for document type: " +
                                                     parameters.getDocumentType());
            }
            // print document
            pda.printDocument(document);
            // print gift receipts
            if((parameters instanceof ReceiptParameterBeanIfc) &&
                    (((ReceiptParameterBeanIfc)parameters).isPrintGiftReceipt()))
            {
                ReceiptParameterBeanIfc receiptParameters = (ReceiptParameterBeanIfc)parameters;
                printGiftReceipts(bus, pda, factory, receiptParameters);
            }
            // print alteration receipts
            if((parameters instanceof ReceiptParameterBeanIfc) &&
                    (((ReceiptParameterBeanIfc)parameters).isPrintAlterationReceipt()))
            {
                ReceiptParameterBeanIfc receiptParameters = (ReceiptParameterBeanIfc)parameters;
                printAlterationReceipt(bus, pda, factory, receiptParameters);
            }
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to print receipt. " + e.getMessage() + "");

            if (e.getCause() != null)
            {
                logger.warn("DeviceException.NestedException: " +
                            Util.throwableToString(e.getCause()));
            }
            throw new PrintableDocumentException(
                    "DeviceException caught while printing for Document Type: " + parameters.getDocumentType(),
                    e);
        }
        catch (ParameterException pe)
        {
            logger.error("A receipt parameter could not be retrieved from the ParameterManager.  " +
                         "The following exception occurred: " +
                         pe.getMessage());
            throw new PrintableDocumentException(
                    "ParameterException caught while printing for Document Type: " + parameters.getDocumentType(),
                    pe);
        }
        logger.debug("Exiting PrintableDocumentManager.printReceipt()");
    }

    /**
     * Prints the alteration receipt for the given arguments.
     * @param bus
     * @param pda
     * @param factory
     * @param receiptParameters
     * @throws ParameterException
     * @throws DeviceException
     * @deprecated as of 13.1 use {@link #printReceipt(SessionBusIfc, PrintableDocumentParameterBeanIfc)} instead
     */
    protected void printAlterationReceipt(SessionBusIfc bus,
                                          POSDeviceActions pda,
                                          PrintableDocumentFactoryIfc factory,
                                          ReceiptParameterBeanIfc receiptParameters)
        throws ParameterException, DeviceException
    {
        logger.debug("Entered PrintableDocumentManager.printAlterationReceipt()");
        AlterationReceiptParameterBeanIfc alterationParameters =
                getAlterationParameterBeanInstance(bus, receiptParameters);

        EYSPrintableDocumentIfc alterationReceipt =
            factory.createPrintableDocument(alterationParameters);
        pda.printDocument(alterationReceipt);
        logger.debug("Exiting PrintableDocumentManager.printAlterationReceipt()");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc#getReceiptParameterBeanInstance(java.lang.String)
     */
    public PrintableDocumentParameterBeanIfc getParameterBeanInstance(String documentType)
    {
        return (PrintableDocumentParameterBeanIfc)BeanLocator.getApplicationBean("application_" + documentType);
    }

    /**
     * Returns an instance of the ReceiptParameterBean initialized for the given transaciton.
     *
     * @param bus
     * @param transaction
     * @return An instance of the ReceiptParameterBean initialized for the given transaciton.
     * @throws ParameterException
     */
    public ReceiptParameterBeanIfc getReceiptParameterBeanInstance(SessionBusIfc bus,
                                                                   TenderableTransactionIfc transaction)
        throws ParameterException
    {
        // locale should be set by Spring
        ReceiptParameterBeanIfc receiptParameter = (ReceiptParameterBeanIfc)BeanLocator.getApplicationBean(ReceiptParameterBeanIfc.BEAN_KEY);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        String discountEmployeeNumber = null;
        if(transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc)transaction;
            discountEmployeeNumber = utility.getEmployeeIDForEmployeeDiscountReceipt(saleReturnTransaction);
            SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])saleReturnTransaction.getLineItems();
            for(SaleReturnLineItemIfc lineItem: lineItems)
            {
                lineItem.setReceiptDescriptionFromPLUItem(LocaleMap.getBestMatch(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT)));
            }
        }
        boolean autoPrintGiftReceiptGiftRegistry =
            pm.getStringValue(ParameterConstantsIfc.PRINTING_AutoPrintGiftReceiptForGiftRegistry).equalsIgnoreCase("Y");
        boolean autoPrintGiftReceiptItemSend =
            pm.getStringValue(ParameterConstantsIfc.PRINTING_AutoPrintGiftReceiptForSend).equalsIgnoreCase("Y");
        receiptParameter.setTransaction(transaction);
        receiptParameter.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
        boolean printItemTax = pm.getBooleanValue("PrintItemTax").booleanValue();
        boolean isVATEnabled = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
        receiptParameter.setPrintItemTax(printItemTax && !isVATEnabled);
        receiptParameter.setAutoPrintGiftReceiptGiftRegistry(autoPrintGiftReceiptGiftRegistry);
        receiptParameter.setAutoPrintGiftReceiptItemSend(autoPrintGiftReceiptItemSend);
        String vatNumber = pm.getStringValue("StoresVATNumber");
        boolean isVATCodeReceiptPrinting = pm.getStringValue(ParameterConstantsIfc.PRINTING_VATCodeReceiptPrinting).equalsIgnoreCase("Y");       
        if(isVATEnabled && isVATCodeReceiptPrinting)
        {
            receiptParameter.setVATNumber(vatNumber);
        }
        else
        {
            receiptParameter.setVATNumber(null);
        }
        receiptParameter.setReceiptStyle(pm.getStringValue(ParameterConstantsIfc.PRINTING_VATReceiptType));
        receiptParameter.setVATCodeReceiptPrinting(pm.getStringValue(ParameterConstantsIfc.PRINTING_VATCodeReceiptPrinting).equalsIgnoreCase("Y"));
        receiptParameter.setVATEnabled(isVATEnabled);

        int transactionType = transaction.getTransactionType();
        switch(transactionType)
        {
            case TransactionIfc.TYPE_SALE :
            {
                receiptParameter.setSurveyShouldPrint(isSurveyExpected(bus, transaction));
                if(Util.isEmpty(discountEmployeeNumber) == false)
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.EMPLOYEE_DISCOUNT);
                    receiptParameter.setDiscountEmployeeNumber(discountEmployeeNumber);
                }
                else
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.SALE);
                }
                SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc)transaction;
                receiptParameter.setPrintGiftReceipt(
                        calculatePrintGiftReceiptFlag(transaction,
                                                      autoPrintGiftReceiptGiftRegistry,
                                                      autoPrintGiftReceiptItemSend));
                receiptParameter.setPrintAlterationReceipt(saleReturnTransaction.hasAlterationItems());
                formatCreditCardPromotionalInformation(receiptParameter, saleReturnTransaction);
                receiptParameter.setTransactionHasSendItem(saleReturnTransaction.hasSendItems() ||
                                                           saleReturnTransaction.getIRSCustomer() != null); 
                break;
            }
            case TransactionIfc.TYPE_RETURN :
            {
                if(Util.isEmpty(discountEmployeeNumber) == false)
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.EMPLOYEE_DISCOUNT);
                    receiptParameter.setDiscountEmployeeNumber(discountEmployeeNumber);
                }
                else
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.RETURN);
                }
                receiptParameter.setPrintGiftReceipt(
                        calculatePrintGiftReceiptFlag(transaction,
                                                      autoPrintGiftReceiptGiftRegistry,
                                                      autoPrintGiftReceiptItemSend));
                receiptParameter.setPrintAlterationReceipt(((SaleReturnTransactionIfc) transaction).hasAlterationItems());
                break;
            }
            case TransactionIfc.TYPE_REDEEM :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.REDEEM);
                break;
            }
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.LAYAWAY_PICKUP);
                SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc)transaction;
                receiptParameter.setPrintGiftReceipt(
                        saleReturnTransaction.hasGiftReceiptItems() ||
                        (saleReturnTransaction.hasSendItems() && autoPrintGiftReceiptItemSend));
                formatCreditCardPromotionalInformation(receiptParameter, saleReturnTransaction);
                break;
            }
            case TransactionIfc.TYPE_LAYAWAY_INITIATE :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.LAYAWAY);
                formatCreditCardPromotionalInformation(receiptParameter, transaction);
                break;
            }
            case TransactionIfc.TYPE_LAYAWAY_PAYMENT :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.LAYAWAY_PAYMENT);
                formatCreditCardPromotionalInformation(receiptParameter, transaction);
                break;
            }
            case TransactionIfc.TYPE_LAYAWAY_DELETE :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.LAYAWAY_DELETE);
                break;
            }
            case TransactionIfc.TYPE_HOUSE_PAYMENT :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.HOUSE_PAYMENT);
                break;
            }
            case TransactionIfc.TYPE_ORDER_COMPLETE :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.SPECIAL_ORDER_COMPLETE);
                formatCreditCardPromotionalInformation(receiptParameter, transaction);
                break;
            }
            case TransactionIfc.TYPE_ORDER_CANCEL :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.SPECIAL_ORDER_CANCEL);
                break;
            }
            case TransactionIfc.TYPE_ORDER_INITIATE :
            {
                OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
                if(orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.PICKUP_DELIVERY_ORDER);
                    receiptParameter.setPrintGiftReceipt(
                            calculatePrintGiftReceiptFlag(transaction,
                                                          autoPrintGiftReceiptGiftRegistry,
                                                          autoPrintGiftReceiptItemSend));
                }
                else
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.SPECIAL_ORDER);
                }
                formatCreditCardPromotionalInformation(receiptParameter, orderTransaction);
                break;
            }
            case TransactionIfc.TYPE_ORDER_PARTIAL :
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.SPECIAL_ORDER);
                formatCreditCardPromotionalInformation(receiptParameter, transaction);
                break;
            }
            case TransactionIfc.TYPE_BILL_PAY :
            {
            	receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.BILL_PAY);
                formatCreditCardPromotionalInformation(receiptParameter, transaction);
            	break;
            }
            case TransactionIfc.TYPE_VOID :
            {
                VoidTransactionIfc voidTrans = (VoidTransactionIfc) transaction;
                TenderableTransactionIfc origTrans = voidTrans.getOriginalTransaction();
                int origTransType = origTrans.getTransactionType();

                switch (origTransType)
                {
                    case TransactionIfc.TYPE_LAYAWAY_PAYMENT :
                    case TransactionIfc.TYPE_LAYAWAY_INITIATE :
                    case TransactionIfc.TYPE_LAYAWAY_COMPLETE :
                    case TransactionIfc.TYPE_LAYAWAY_DELETE :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_LAYAWAY);
                        break;
                    }
                    case TransactionIfc.TYPE_SALE :
                    case TransactionIfc.TYPE_RETURN :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_SALE);
                        break;
                    }
                    case TransactionIfc.TYPE_REDEEM :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_REDEEM);
                        break;
                    }
                    case TransactionIfc.TYPE_PAYIN_TILL :
                    case TransactionIfc.TYPE_PAYOUT_TILL :
                    case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL :
                    case TransactionIfc.TYPE_PICKUP_TILL :
                    case TransactionIfc.TYPE_LOAN_TILL :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_TILL_ADJUSTMENTS);
                        break;
                    }
                    case TransactionIfc.TYPE_HOUSE_PAYMENT :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_HOUSE_PAYMENT);
                        break;
                    }
                    case TransactionIfc.TYPE_ORDER_INITIATE :
                    case TransactionIfc.TYPE_ORDER_CANCEL :
                    case TransactionIfc.TYPE_ORDER_PARTIAL :
                    case TransactionIfc.TYPE_ORDER_COMPLETE :
                    {
                        receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_SPECIAL_ORDER);
                        break;
                    }
                    case TransactionIfc.TYPE_BILL_PAY :
                    {
                    	receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.VOID_BILL_PAY);
                    }
                    default :
                    {
                        logger.warn("No Void Receipt Found");
                        break;
                    }
                }
                break;
            }
            default :
            {
                logger.warn("No Receipt Found");
                break;
            }
        }
        // account for transactions of status STATUS_CANCELED
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            if (transaction.getTransactionType() == TransactionIfc.TYPE_PICKUP_TILL)
            {
                // till pickup and loan knows how to prints its own cancel
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.TILLPICKUP);
            }
            else if (transaction.getTransactionType() == TransactionIfc.TYPE_LOAN_TILL)
            {
                // till pickup and loan knows how to prints its own cancel
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.TILLLOAN);
            }
            else
            {
                // canceled transactions should all use this receipt
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.CANCELED);
            }
            // no gift receipt for canceled transactions
            receiptParameter.setAutoPrintGiftReceiptGiftRegistry(false);
            receiptParameter.setAutoPrintGiftReceiptItemSend(false);
            receiptParameter.setPrintGiftReceipt(false);
        }
        else if (transaction instanceof RetailTransactionIfc)
        {
            if (((RetailTransactionIfc) transaction).getTransactionTax() != null
                && ((RetailTransactionIfc) transaction).getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                if (!(transactionType == TransactionIfc.TYPE_ORDER_INITIATE
                        || transactionType == TransactionIfc.TYPE_ORDER_PARTIAL
                        || transactionType == TransactionIfc.TYPE_LAYAWAY_INITIATE
                        || transactionType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
                        // BUG#10186755 TaxExemption receipt printed for voided transactions
                        && !(transaction instanceof VoidTransactionIfc))
                {
                    receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.TAX_EXEMPT);
                }
            }
        }
        // account for transactions of TYPE_EXCHANGE
        if (receiptParameter.getTransactionType() == TransactionConstantsIfc.TYPE_EXCHANGE)
        {
            if(Util.isEmpty(discountEmployeeNumber) == false)
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.EMPLOYEE_DISCOUNT);
                receiptParameter.setDiscountEmployeeNumber(discountEmployeeNumber);
            }
            else
            {
                receiptParameter.setDocumentType(ReceiptTypeConstantsIfc.EXCHANGE);
            }
        }
        //Read Group Like Items on Receipt parameter value.
        receiptParameter.setGroupLikeItems(pm.getBooleanValue(ParameterConstantsIfc.PRINTING_GroupLikeItemsOnReceipt));
        return receiptParameter;
    }

    /**
     * Formats and sets the credit card promotional information on the
     * ReceiptParameterBeanIfc instance.
     * @param receiptParameter
     * @param tenderableTransaction
     */
    protected void formatCreditCardPromotionalInformation(
            ReceiptParameterBeanIfc receiptParameter,
            TenderableTransactionIfc tenderableTransaction)
    {
        TenderLineItemIfc[] tenders = tenderableTransaction.getTenderLineItems();
        for(int i = 0; i < tenders.length; i++)
        {
            if(tenders[i] instanceof TenderChargeIfc)
            {
                TenderChargeIfc charge = (TenderChargeIfc)tenders[i];
                // if there's a promotion then copy in the promotional information into the
                // ReceiptParameterBeanIfc
                if(Util.isEmpty(charge.getPromotionDescription()) == false)
                {
                    receiptParameter.setCreditCardPromotionDescriptionPart1(formatCreditCardPromotionDescriptionPart1(charge));
                    receiptParameter.setCreditCardPromotionDescriptionPart2(formatCreditCardPromotionDescriptionPart2(charge));
                    receiptParameter.setCreditCardPromotionDuration(formatCreditCardPromotionDuration(charge));
                    receiptParameter.setFormattedCreditCardAccountRate(formatCreditCardAccountRate(charge));
                    receiptParameter.setFormattedCreditCardPromotionRate(formatCreditCardPromotionRate(charge));
                    // and break
                    i = tenders.length;
                }
            }
        }
    }

    /**
     * Returns the formatted rate and rate type for credit card promotion.
     * @param charge
     * @return The formatted rate and rate type for credit card promotion.
     */
    protected String formatCreditCardPromotionRate(TenderChargeIfc charge)
    {
        StringBuilder promotionRate = new StringBuilder();
        promotionRate.append(charge.getPromotionAPR());
        promotionRate.append("% (");
        promotionRate.append(charge.getPromotionAPRType());
        promotionRate.append(")");
        return promotionRate.toString();
    }

    /**
     * Returns the formatted rate and rate type for credit card account.
     * @param charge
     * @return The formatted rate and rate type for credit card account.
     */
    protected String formatCreditCardAccountRate(TenderChargeIfc charge)
    {
        StringBuilder accountRate = new StringBuilder();
        accountRate.append(charge.getAccountAPR());
        accountRate.append("% (");
        accountRate.append(charge.getAccountAPRType());
        accountRate.append(")");
        return accountRate.toString();
    }

    /**
     * Returns the credit card promotion duration.
     * @param charge
     * @return The credit card promotion duration.
     */
    protected String formatCreditCardPromotionDuration(TenderChargeIfc charge)
    {
        return charge.getPromotionDuration();
    }

    /**
     * Returns the 2nd part of the credit card promotion description.
     * @param charge
     * @return The 2nd part of the credit card promotion description.
     */
    protected String formatCreditCardPromotionDescriptionPart2(TenderChargeIfc charge)
    {
        String description = charge.getPromotionDescription();
        String formatted = description;
        // find the index of the first space after mid point
        int midpoint = description.length()/2;
        int splitIndex = description.substring(midpoint).indexOf(" ") + midpoint;
        // return the 2nd part if there's a space after the mid point, else return empty string
        if(splitIndex >= description.length()/2)
        {
            formatted = description.substring(splitIndex);
        }
        else
        {
            formatted = "";
        }
        return formatted;
    }

    /**
     * Returns the 1st part of the credit card promotion description.
     * @param charge
     * @return The 1st part of the credit card promotion description.
     */
    protected String formatCreditCardPromotionDescriptionPart1(TenderChargeIfc charge)
    {
        String description = charge.getPromotionDescription();
        String formatted = description;
        // find the index of the last space before mid point
        int midpoint = description.length()/2;
        int splitIndex = description.substring(midpoint).indexOf(" ") + midpoint;
        // return the 1st part if there's a space after the mid point, else return original string
        if(splitIndex >= description.length()/2)
        {
            formatted = description.substring(0, splitIndex);
        }
        return formatted;
    }

    /**
     * Returns an instance of the OrderReceiptParameterBeanIfc.
     *
     * @param bus
     * @param order
     * @return An instance of the OrderReceiptParameterBeanIfc.
     * @throws ParameterException
     */
    public OrderReceiptParameterBeanIfc getOrderReceiptParameterBeanInstance(
            SessionBusIfc bus,
            OrderIfc order)
    throws ParameterException
    {
        OrderReceiptParameterBeanIfc orderReceiptParameter = new OrderReceiptParameterBean();
        orderReceiptParameter.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
        orderReceiptParameter.setDefaultLocale(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        orderReceiptParameter.setOrder(order);
        boolean vatEnabled = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
        orderReceiptParameter.setVATEnabled(vatEnabled);
        orderReceiptParameter.setDocumentType(ReceiptTypeConstantsIfc.ORDER);
        formatCreditCardPromotionalInformation(orderReceiptParameter, (TenderableTransactionIfc)order.getOriginalTransaction());
        return orderReceiptParameter;
    }

    /**
     * Sets the document type on the given ReceiptParameterBeanIfc based on the vatEnabled
     * flag.
     *
     * @param vatEnabled
     * @param receiptParameter
     * @param vatType
     * @param notVATType
     * @deprecated as of 13.1 no need to use VAT specific document types.
     */
    protected void setDocumentType(boolean vatEnabled, ReceiptParameterBeanIfc receiptParameter, String vatType, String notVATType)
    {
        if(vatEnabled)
        {
            receiptParameter.setDocumentType(vatType);
        }
        else
        {
            receiptParameter.setDocumentType(notVATType);
        }
    }

    /**
     * Determines if list of string includes "none".
     *
     * @param stringValues array of strings
     * @return true if list includes "none"
     */
    protected boolean containsNone(String[] stringValues)
    {
        boolean retValue = false;

        for(int i=0; i<stringValues.length; i++)
        {
            if(stringValues[i].equalsIgnoreCase("none"))
            {
                retValue = true;
                break;
            }
        }

        return retValue;
    }

    /**
     * Returns whether the transaction type is an Exchange.
     *
     * @param transaction The transaction
     * @return Whether the transaction type is an Exchange.
     * @deprecated as of 13.1 see ReceiptParameterBeanIfc#getTransactionType()
     */
    public boolean isExchangeTransactionType(TenderableTransactionIfc transaction)
    {
        int tt = transaction.getTransactionType();

        // determine if this transaction is an exchange
        AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc) transaction).getLineItems();

        boolean returnItems = false;
        boolean saleItems = false;

        // determine if transaction is sale, return, or exchange
        for (int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];
            if (srli.isReturnLineItem())
            {
                // transaction has return items
                returnItems = true;
            }
            else
            {
                // transaction has sale items
                saleItems = true;
            }

            if (returnItems && saleItems)
            {
                tt = TransactionIfc.TYPE_EXCHANGE;
                // force exit
                i = lineItems.length;
            }
            else
                if (returnItems)
                {
                    tt = TransactionIfc.TYPE_RETURN;
                }
                else
                    if (saleItems)
                    {
                        tt = TransactionIfc.TYPE_SALE;
                    }
        }

        if (tt == TransactionIfc.TYPE_EXCHANGE)
            return true;

        return false;
    }

    /**
     * Calculates whether to print the gift receipt.
     * @param transaction
     * @param autoPrintGiftReceiptGiftRegistry
     * @param autoPrintGiftReceiptItemSend
     * @return
     */
    protected boolean calculatePrintGiftReceiptFlag(
            TenderableTransactionIfc transaction,
            boolean autoPrintGiftReceiptGiftRegistry,
            boolean autoPrintGiftReceiptItemSend)
    {
        return ((SaleReturnTransactionIfc) transaction).hasGiftReceiptItems()
            || (((SaleReturnTransactionIfc) transaction).hasGiftRegistryItems() && autoPrintGiftReceiptGiftRegistry)
            || (((SaleReturnTransactionIfc) transaction).hasSendItems() && autoPrintGiftReceiptItemSend);
    }

    /**
     * Returns true if a survey should be printed on the receipt. Delegates to
     * the CustomerSurveyRewardIfc manager and eats any exceptions.
     *
     * @param bus   The bus holding the parameter and utility managers.
     * @param trans The transaction.
     * @return true if a survey should be printed
     */
    protected boolean isSurveyExpected (SessionBusIfc bus, TenderableTransactionIfc trans)
    {
        CustomerSurveyRewardIfc customerSurveyReward = null;
        // Ensure we have the Customer Survey / Reward manager
        try
        {
            customerSurveyReward = (CustomerSurveyRewardIfc)ManagerFactory.create(CustomerSurveyRewardIfc.MANAGER_NAME);
        }
        catch (ManagerException e)
        {
            // default to product version
            customerSurveyReward = new CustomerSurveyReward();
        }
        catch (Throwable t)
        {
            // assume no survey
            return false;
        }

        // Do the work
        return customerSurveyReward.isSurveyExpected(bus, trans);
    }

    /**
     * Method for creating an instance of the AlterationReceiptParameterBeanIfc.
     *
     * @param bus
     * @param receiptParameters
     * @return An instance of the AlterationReceiptParameterBeanIfc.
     */
    protected AlterationReceiptParameterBeanIfc getAlterationParameterBeanInstance(SessionBusIfc bus, ReceiptParameterBeanIfc receiptParameters)
    {
        AlterationReceiptParameterBeanIfc instance = new AlterationReceiptParameterBean();
        instance.setTransaction(receiptParameters.getTransaction());
        instance.setAutoPrintCustomerCopy(receiptParameters.isAutoPrintCustomerCopy());
        instance.setAutoPrintGiftReceiptGiftRegistry(receiptParameters.isAutoPrintGiftReceiptGiftRegistry());
        instance.setAutoPrintGiftReceiptItemSend(receiptParameters.isAutoPrintGiftReceiptItemSend());
        instance.setDiscountEmployeeNumber(receiptParameters.getDiscountEmployeeNumber());
        instance.setDuplicateReceipt(receiptParameters.isDuplicateReceipt());
        instance.setPrintAlterationReceipt(receiptParameters.isPrintAlterationReceipt());
        instance.setPrintGiftReceipt(receiptParameters.isPrintGiftReceipt());
        instance.setPrintItemTax(receiptParameters.isPrintItemTax());
        return instance;
    }

    /**
     * Method to print gift receipts
     * @param bus
     * @param pda
     * @param factory
     * @param receiptParameters
     * @throws PrintableDocumentException
     * @deprecated as of 13.1 use {@link #printReceipt(SessionBusIfc, PrintableDocumentParameterBeanIfc)} instead
     */
    protected void printGiftReceipts(SessionBusIfc bus,
                                     POSDeviceActions pda,
                                     PrintableDocumentFactoryIfc factory,
                                     ReceiptParameterBeanIfc receiptParameters)
        throws PrintableDocumentException
    {
        logger.debug("Entered PrintableDocumentManager.printGiftReceipts()");
        // Get the lineItems from the transaction.
        SaleReturnTransactionIfc srTrans = (SaleReturnTransactionIfc) receiptParameters.getTransaction();
        AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
        GiftReceiptParameterBeanIfc giftReceiptParameterBean = this.getGiftReceiptParameterBeanInstance(bus, receiptParameters);

        if(srTrans.isTransactionGiftReceiptAssigned())
        {
            ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();
            for(int i=0; i<lineItems.length; i++)
            {
                if(lineItems[i] instanceof SaleReturnLineItemIfc)
                {
                    srliList.add((SaleReturnLineItemIfc)lineItems[i]);
                }
            }
            // set only the salereturnlineitems
            giftReceiptParameterBean.setSaleReturnLineItems(srliList.toArray(new SaleReturnLineItemIfc[srliList.size()]));
            // print the transaction
            printGiftReceipt(bus, giftReceiptParameterBean);
        }
        else // else not transaction receipt, print a receipt for each line item
        {
            Map<CustomerIfc,List<SaleReturnLineItemIfc>> mapSendGifts = new HashMap<CustomerIfc,List<SaleReturnLineItemIfc>>(0);
            for (int i = 0; i < srTrans.getLineItemsSize(); i++)
            {
                if (lineItems[i] instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

                    if (!hasDamageDiscounts(srli))
                    {
                        // Get gift registry for this item
                        RegistryIDIfc giftRegistry = srli.getRegistry();

                        // If item is marked for a gift receipt, or the item is linked to a gift
                        // registry and the AutoPrintGiftReceipt parameter is set, then print a gift receipt.
                        if (srli.isGiftReceiptItem()
                                || (giftRegistry != null && receiptParameters.isAutoPrintGiftReceiptGiftRegistry())
                                || (srli.getItemSendFlag() && receiptParameters.isAutoPrintGiftReceiptItemSend()))
                        {
                            if (srli.getItemSendFlag())
                            {
                                SendPackageLineItemIfc spli = srTrans.getSendPackage(srli.getSendLabelCount() - 1);
                                // add the item to list mapped by send destination
                                List<SaleReturnLineItemIfc> list = mapSendGifts.get(spli.getCustomer());
                                if (list == null)
                                {
                                    list = new ArrayList<SaleReturnLineItemIfc>();
                                    mapSendGifts.put(spli.getCustomer(), list);
                                }
                                list.add(srli);
                            }
                            else // if not send gift, print normal gift receipt
                            {
                                // set only the current lineitem
                                giftReceiptParameterBean.setSaleReturnLineItems(new SaleReturnLineItemIfc[] { srli });
                                printGiftReceipt(bus, giftReceiptParameterBean);
                            }
                        }
                    } // end if no damage discounts
                }
            } // end for
            // if any send gifts were found, print them in groups
            if (mapSendGifts.size() > 0)
            {
                for (List<SaleReturnLineItemIfc> items : mapSendGifts.values())
                {
                    giftReceiptParameterBean.setSaleReturnLineItems(items.toArray(new SaleReturnLineItemIfc[items.size()]));
                    printSendGiftReceipt(bus, pda, factory, giftReceiptParameterBean);
                }
            }
        }
        logger.debug("Exiting PrintableDocumentManager.printGiftReceipts()");
    }

    /**
     * Method to print send gift receipt
     * @param bus
     * @param pda
     * @param factory
     * @throws PrintableDocumentException
     * @deprecated as of 13.1 use {@link #printReceipt(SessionBusIfc, PrintableDocumentParameterBeanIfc)} instead
     */
    protected void printSendGiftReceipt(SessionBusIfc bus,
                                     POSDeviceActions pda,
                                     PrintableDocumentFactoryIfc factory,
                                     GiftReceiptParameterBeanIfc giftReceiptParameterBean)
        throws PrintableDocumentException
    {
        // create the GiftReceiptParameterBeanIfc instance and set options
        EYSPrintableDocumentIfc sendGiftReceipt = factory.createPrintableDocument(giftReceiptParameterBean);
        if(sendGiftReceipt == null)
        {
            throw new PrintableDocumentException("Could not find receipt for document type: " +
                    giftReceiptParameterBean.getDocumentType());
        }
        try
        {
            pda.printDocument(sendGiftReceipt);
        }
        catch(DeviceException de)
        {
            logger.error("DeviceException caught while attempting to print Document Type: " +
                    giftReceiptParameterBean.getDocumentType(), de);
            throw new PrintableDocumentException(
                    "DeviceException caught while attempting to print Document Type: " +
                    giftReceiptParameterBean.getDocumentType(), de);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc#getSendGiftReceiptParameterBeanInstance(oracle.retail.stores.foundation.tour.service.SessionBusIfc, oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc)
     */
    public GiftReceiptParameterBeanIfc[] getSendGiftReceiptParameterBeanInstance(SessionBusIfc bus,
            SaleReturnTransactionIfc srTrans)
    {
        AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
        Map<CustomerIfc,List<SaleReturnLineItemIfc>> mapSendGifts = new HashMap<CustomerIfc,List<SaleReturnLineItemIfc>>(0);
        for (int i = 0; i < srTrans.getLineItemsSize(); i++)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

                if (!hasDamageDiscounts(srli))
                {
                    if (srli.getItemSendFlag())
                    {
                        SendPackageLineItemIfc spli = srTrans.getSendPackage(srli.getSendLabelCount() - 1);
                        // add the item to list mapped by send destination
                        List<SaleReturnLineItemIfc> list = mapSendGifts.get(spli.getCustomer());
                        if (list == null)
                        {
                            list = new ArrayList<SaleReturnLineItemIfc>();
                            mapSendGifts.put(spli.getCustomer(), list);
                        }
                        list.add(srli);
                    }
                } // end if no damage discounts
            }
        } // end for

        // group resulting send items into an array of receipt params
        GiftReceiptParameterBeanIfc[] result = new GiftReceiptParameterBeanIfc[mapSendGifts.size()];
        int i = 0;
        for (List<SaleReturnLineItemIfc> items : mapSendGifts.values())
        {
            result[i] = getGiftReceiptParameterBeanInstance(bus, srTrans, items.toArray(new SaleReturnLineItemIfc[0]));
            i++;
        }

        return result;
    }

    /**
     * Creates an instance of the GiftReceiptParameterBean.
     *
     * @param bus
     * @param receiptParameters
     * @return An instance of the GiftReceiptParameterBeanIfc.
     */
    public GiftReceiptParameterBeanIfc getGiftReceiptParameterBeanInstance(
            SessionBusIfc bus,
            ReceiptParameterBeanIfc receiptParameters)
    {
        GiftReceiptParameterBeanIfc instance = new GiftReceiptParameterBean();
        instance.setTransaction(receiptParameters.getTransaction());
        instance.setAutoPrintCustomerCopy(receiptParameters.isAutoPrintCustomerCopy());
        instance.setAutoPrintGiftReceiptGiftRegistry(receiptParameters.isAutoPrintGiftReceiptGiftRegistry());
        instance.setAutoPrintGiftReceiptItemSend(receiptParameters.isAutoPrintGiftReceiptItemSend());
        instance.setDiscountEmployeeNumber(receiptParameters.getDiscountEmployeeNumber());
        instance.setDuplicateReceipt(receiptParameters.isDuplicateReceipt());
        instance.setPrintAlterationReceipt(receiptParameters.isPrintAlterationReceipt());
        instance.setPrintGiftReceipt(receiptParameters.isPrintGiftReceipt());
        instance.setPrintItemTax(receiptParameters.isPrintItemTax());
        instance.setLocale(receiptParameters.getLocale());
        instance.setDefaultLocale(receiptParameters.getDefaultLocale());
        return instance;
    }

    /**
     * Returns an instance of the GiftReceiptParameterBeanIfc for the given arguments.
     *
     * @param bus
     * @param transaction
     * @param lineItems
     * @return An instance of the GiftReceiptParameterBeanIfc for the given arguments.
     */
    public GiftReceiptParameterBeanIfc getGiftReceiptParameterBeanInstance(
            SessionBusIfc bus,
            SaleReturnTransactionIfc transaction,
            SaleReturnLineItemIfc[] lineItems)
    {
        GiftReceiptParameterBeanIfc instance = new GiftReceiptParameterBean();
        instance.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
        instance.setDefaultLocale(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
        instance.setTransaction(transaction);
        instance.setSaleReturnLineItems(lineItems);
        return instance;
    }

    /**
     * Method for returning the instance of the PrintableDocumentFactoryIfc.
     *
     * @return The PrintableDocumentFactoryIfc instance.
     * @deprecated as of 13.1 since blueprints are used instead of printable documents.
     */
    public PrintableDocumentFactoryIfc getPrintableDocumentFactory()
    {
        if(this.printableDocumentFactory == null)
        {
            try
            {
                this.printableDocumentFactory =
                        (PrintableDocumentFactoryIfc) ReflectionUtility.createClass(
                                this.printableDocumentFactoryName);
            }
            catch(IllegalAccessException iae)
            {
                logger.warn("IllegalAccessException caught trying to create PrintableDocumentFactory", iae);
                this.printableDocumentFactory = new PrintableDocumentFactory();
            }
            catch(ClassNotFoundException cnfe)
            {
                logger.warn("ClassNotFoundException caught trying to create PrintableDocumentFactory", cnfe);
                this.printableDocumentFactory = new PrintableDocumentFactory();
            }
            catch(InstantiationException ie)
            {
                logger.warn("InstantiationException caught trying to create PrintableDocumentFactory", ie);
                this.printableDocumentFactory = new PrintableDocumentFactory();
            }
        }
        return this.printableDocumentFactory;
    }

    /**
     * Method for setting the name of the PrintableDocumentFactory.
     *
     * @param factoryName
     */
    public void setPrintableDocumentFactoryName(String factoryName)
    {
        this.printableDocumentFactoryName = factoryName;
    }

    /**
     * Retrieves receipt text through international text support facility.
     *
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveReceiptText(String propName, String defaultValue)
    {
        String returnValue = defaultValue;
        String bundles[] = { BundleConstantsIfc.COMMON_BUNDLE_NAME, BundleConstantsIfc.RECEIPT_BUNDLE_NAME };
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
        Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText("Receipt", bundles, bestMatchLocale);
        if(props != null)
        {
            returnValue = props.getProperty(propName, defaultValue);
        }
        return returnValue;
    }

    /**
     * Returns the number of (sale) receipts printed since the last customer survey printed.
     *
     * @return
     */
    public int getPrintReceiptCount()
    {
        return PrintableDocumentManager.printReceiptCount;
    }

    /**
     * Sets the number of (sale) receipts printed since the last customer survey.
     *
     * @param count
     */
    public void setPrintReceiptCount(int count)
    {
        PrintableDocumentManager.printReceiptCount = count;
    }

    /**
     * Determine if any of the passed in lineItems has a damage discount.  If
     * they do, return a new array without those items in it.
     *
     *  @param lineItems
     *  @return An array free of damage-discounted items.
     */
    public SaleReturnLineItemIfc[] removeDamageDiscounts(SaleReturnLineItemIfc lineItems[])
    {
        ArrayList<SaleReturnLineItemIfc> list = new ArrayList<SaleReturnLineItemIfc>(lineItems.length);
        for (int i = 0; i < lineItems.length; i++)
        {
            if (!hasDamageDiscounts(lineItems[i]))
            {
                list.add(lineItems[i]);
            }
        }

        return list.toArray(new SaleReturnLineItemIfc[list.size()]);
    }

    /**
     * Determine if line item has any damage discounts applied.
     * <P>
     *
     * @param lineItem Sale Return Line Item with potential Employee discounts
     * @return true if damage discounts applied
     */
    public boolean hasDamageDiscounts(SaleReturnLineItemIfc lineItem)
    {
        boolean foundDamageDiscount = false;

        // Check item discounts by amount
        ItemDiscountStrategyIfc[] discounts = lineItem.getItemDiscountsByAmount();
        for (int y = 0; y < discounts.length; y++)
        {
            if (discounts[y].isDamageDiscount())
            {
                foundDamageDiscount = true;
                break;
            }
        }
        if (!foundDamageDiscount)
        {
            discounts = lineItem.getItemDiscountsByPercentage();
            for (int y = 0; y < discounts.length; y++)
            {
                if (discounts[y].isDamageDiscount())
                {
                    foundDamageDiscount = true;
                    break;
                }
            }
        }

        return foundDamageDiscount;
    }

    /**
     * Retrieves a handle to the bundle properties
     *
     * @param tag bean key name
     * @param bundle bundle in which to search for answer
     * @param locale the locale used to retrieve the bundle
     * @return Properties handle to the bundle
     */
    public Properties getBundleProperties(String tag, String bundle, Locale locale)
    {
    	Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText(tag, bundle, bestMatchLocale);
        return props;
    }


    /**
     * Method to return an instance of the Report Summmary.
     *
     * @return An instance of the Report Summary.
     * @deprecated as of 13.1 use {@link #getParameterBeanInstance(String)} instead.
     */
    public SummaryReport getSummaryReportInstance()
    {
        String key = ReportTypeConstantsIfc.SUMMARY_REPORT;
        return getPrintableDocumentFactory().createReportSummary(key);
    }


    /**
     * Does nothing for this manager. See {@link BlueprintedDocumentManager}
     * instead.
     *
     * @see oracle.retail.stores.foundation.tour.manager.ConfigurableManager#configure(org.w3c.dom.Element)
     */
    @Override
    protected void configure(Element xmlRoot)
    {
    }

    /**
        Returns the string array of the associated parameter - or a string array with
        a single empty string if the parameter doesn't exist.
        @param  bus          The session bus.
        @param parameterName The name of the parameter with string values
        @return              the string array of the associated parameter
    **/
    protected String[] getStringValues(SessionBusIfc bus, String parameterName)
    {
        String[] result = null;
        try
        {
            // get parameter manager
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

            // get receipt footer from parameter
            result = pm.getStringValues(parameterName);

        }
        catch (ParameterException e)
        {
            logger.error(
                    "The " + parameterName +" parameter could not be retrieved "
                    + "from the ParameterManager.  The following exception occurred:\n"
                    + Util.throwableToString(e));

            result = new String[1];
            result[0] = Util.EMPTY_STRING;
        }
        return result;
    }
    
    /**
     * Needs to be overridden in the subclassess.
     */
    public String getPreview(SessionBusIfc bus, PrintableDocumentParameterBeanIfc parameterBean)
            throws PrintableDocumentException
    {
        // do nothing, return empty
        return "";
    }
}
