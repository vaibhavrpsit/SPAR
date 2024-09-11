/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/EYSPrintableDocument.java /main/41 2012/08/16 15:30:25 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    sgu       02/03/11 - check in all
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    vapartha  01/28/10 - Added code to write space in the signature image
 *                         files when deletion fails.
 *    vapartha  01/21/10 - Added code to replace the signature with empty files
 *                         if delete fails.
 *    abondala  01/03/10 - update header date
 *    asinton   12/09/09 - Merge
 *    asinton   12/03/09 - Changes to support credit card authorizations on
 *                         returns and voids.
 *    acadar    12/03/09 - changes for EYSPRintableDoc
 *    cgreene   03/16/09 - warp caught exception when printing signature
 *    acadar    02/09/09 - use default locale for display of date and time
 *    glwang    01/30/09 - move deleting singature files into the finally block
 *    cgreene   01/08/09 - remove deprecated pritnCanadianTax method
 *    acadar    11/18/08 - display reason code along with the reason
 *                         description
 *    cgreene   11/11/08 - switch to mail check getPrimaryAddress and Phone
 *                         methods
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    ranojha   10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    ranojha   10/29/08 - Fixed ReturnItem
 *    ranojha   10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    acadar    10/29/08 - merged to tip
 *    ddbaker   10/28/08 - Update for merge
 *    cgreene   10/24/08 - add null check in constructor
 *    sgu       10/09/08 - add locale support for shipping methods
 *    cgreene   10/02/08 - merged with tip
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:
      42   360Commerce 1.41        7/19/2007 12:38:08 AM  Chengegowda Venkatesh
            CR-27308 : Modified Item Description length from 19 to 15 and
           Price Length from 7 to 11 for Kit Components
      41   360Commerce 1.40        7/18/2007 5:56:55 AM   Naveen Ganesh   The
           lable GC Remaining Balance: is modified to GC Available Balance:
      40   360Commerce 1.39        7/16/2007 3:59:42 PM   Ashok.Mondal    CR
           27302 :Correcting allignment of shipping charge on receipt.
      39   360Commerce 1.38        7/12/2007 6:08:17 PM   Ranjan Ojha     Fixed
            Layaway Store Receipt
      38   360Commerce 1.37        7/2/2007 2:37:04 PM    Anda D. Cadar   call
           toGroupFormattedString(locale) on gift card balance
      37   360Commerce 1.36        6/28/2007 8:55:55 AM   Alan N. Sinton  CR
           27240 - Added support for printing alpha-numeric barcodes.
      36   360Commerce 1.35        6/26/2007 5:50:33 PM   Ranjan Ojha
           Removed Original GC Amount from GiftCard Tender Receipt
      35   360Commerce 1.34        6/18/2007 11:06:08 AM  Michael Boyd    One
           more tweak on the Mail Bank Check spacing
      34   360Commerce 1.33        6/14/2007 7:55:49 PM   Michael Boyd
           Reduced white space for printing Mail Bank Check amount
      33   360Commerce 1.32        6/14/2007 6:34:47 PM   Ranjan Ojha     Fix
           for removal of Expiration Date from Receipts
      32   360Commerce 1.31        6/12/2007 8:48:02 PM   Anda D. Cadar   SCR
           27207: Receipt changes -  proper alignment for amounts
      31   360Commerce 1.30        5/30/2007 2:20:09 PM   Rohit Sachdeva
           26364: Set up to use Spring for LocaleMapLoader in Dispatcher
      30   360Commerce 1.29        5/17/2007 3:58:13 PM   Mathews Kochummen
           format dates
      29   360Commerce 1.28        5/15/2007 3:45:19 PM   Peter J. Fierro
           Consolidate cut and past code, print ISO country code for foreign
           tenders.
      28   360Commerce 1.27        5/14/2007 2:32:57 PM   Alan N. Sinton  CR
           26486 - EJournal enhancements for VAT.
      27   360Commerce 1.26        5/4/2007 11:05:34 AM   Alan N. Sinton  CR
           26485 - Preventing IndexArrayOutOfBoundsException
      26   360Commerce 1.25        5/2/2007 6:13:44 PM    Alan N. Sinton  CR
           26485 - VAT support for shipping and kit components.
      25   360Commerce 1.24        4/30/2007 7:01:38 PM   Alan N. Sinton  CR
           26485 - Merge from v12.0_temp.
      24   360Commerce 1.23        4/26/2007 4:04:15 PM   Mathews Kochummen use
            the more appropriate time long format for all locales
      23   360Commerce 1.22        4/25/2007 11:02:26 AM  Anda D. Cadar   I18N
           merge
      22   360Commerce 1.21        4/24/2007 11:27:01 AM  Ashok.Mondal    CR
           4364 :V7.2.2 merge to trunk.
      21   360Commerce 1.20        3/29/2007 1:56:21 PM   Michael Boyd    v8x
           merge to trunk

           CR 24396 - Updated with correct fix for mail bank check amount
           being incorrectly formatted.

           CR#23,274 Alingment modified for Price Item

           Change made to make price for item to line up with subtotal.

           Modified for CR : 24396
           Method modified : protected void
           printMailBankCheckTender(TenderLineItemIfc tli, CurrencyIfc amount)
            throws DeviceException
           Description: Added a new parameter
           LocaleConstantsIfc.CURRENCY_GROUP_FORMAT_NATURAL to for calling the
            method toFormattedString
      20   360Commerce 1.19        2/6/2007 2:46:39 PM    Edward B. Thorne
           Merge from EYSPrintableDocument.java, Revision 1.17
      19   360Commerce 1.18        1/22/2007 4:26:19 PM   Charles D. Baker CR
           16245 - Merge from .v8x
      18   360Commerce 1.17        10/20/2006 4:20:17 PM  Charles D. Baker CR
           22570 - Merge of .v7x CR 21534 - changing subtotal text from
           "Sub-Total" to "Subtotal."
      17   360Commerce 1.16        7/28/2006 5:39:01 PM   Brett J. Larsen
           numerous changes
           v7x->360Commerce merge
      16   360Commerce 1.15        7/24/2006 7:01:58 PM   Brendan W. Farrell
           Recheck in merge to link other CR.
      15   360Commerce 1.14        7/24/2006 7:00:01 PM   Brendan W. Farrell
           Merged fix from v7.x.
      14   360Commerce 1.13        7/20/2006 8:09:56 PM   Keith L. Lesikar Tax
           mode flag retrieval.
      13   360Commerce 1.12        5/12/2006 5:25:27 PM   Charles D. Baker
           Merging with v1_0_0_53 of Returns Managament
      12   360Commerce 1.11        3/29/2006 7:25:18 AM   Akhilashwar K. Gupta
           CR-8190: Updated getFormattedDate() method to set locale specific
            format.
      11   360Commerce 1.10        3/16/2006 12:29:14 PM  Deepanshu       CR
           6161: Code added to fix the failing functional test
      10   360Commerce 1.9         3/8/2006 2:52:26 AM    Akhilashwar K. Gupta
           CR 6161: Modified methods to fix the printing of Redeem and Post
           Void Redeem receipts.
      9    360Commerce 1.8         3/3/2006 2:59:49 AM    Akhilashwar K. Gupta
           CR-6094: Removed code for snippet of Sales Associate ID. As per the
            updated 'POS-UI Guidelines.doc', section 11.2.1 and 'POS-Sale.doc'
            section 15.2, the complete Sales Assoc ID should ne displayed now
           on all receipts.
      8    360Commerce 1.7         2/24/2006 10:44:15 AM  Brett J. Larsen CR
           10575 - tax exempt totals not considering PrintItemTax parameter
           value - wrong text appearing on receipts

           made some low-level formatting methods static so JournalUtilities
           can use them

      7    360Commerce 1.6         2/21/2006 11:19:41 AM  Rohit Sachdeva  6091:
            Using CustomerLabel
      6    360Commerce 1.5         1/25/2006 4:11:00 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:45:03 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:42:37 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:35 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse
      $
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.awt.Point;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import jpos.POSPrinterConst;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ReceiptPrinter;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

import org.apache.log4j.Logger;

/**
 * Class for printing EYSPrintableDocument.
 *
 * @version $Revision: /main/41 $
 */
public abstract class EYSPrintableDocument
    implements EYSPrintableDocumentIfc, DiscountRuleConstantsIfc
{
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(EYSPrintableDocument.class);

    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /main/41 $";

    /**
     * space char
     */
    protected static final char SPACE_CHAR = ' ';

    /**
     * Image scale factor key
     */
    public static final String IMAGE_SCALE_FACTOR_KEY = "imageScaleFactor";

    /**
     * Image scale factor key
     */
    public static final String IMAGE_SCALE_FACTOR_DEFAULT = ".5";

    /**
     * printer interface
     */
    protected ReceiptPrinterIfc printer = null;

    /**
     * retail transaction
     */
    protected TenderableTransactionIfc transaction;

    /**
     * A printer has been provided
     */
    protected boolean printerInitialized;

    /**
     * Item tax modification occurred
     */
    protected boolean taxItemModified = false;

    /**
     * properties for literal text
     */
    protected Properties props = null;

    /**
     * Outgoing tender flag
     */
    protected boolean outgoingTender = false;

    /**
     * Negative Tenders allowed
     */
    protected boolean negativeTenders = false;

    /**
     * send items printed by send count
     */
    protected boolean sendItemsPrintedBySendCount = false;

    /**
     * Whether or not to print tax on an individual tax jurisdiction basis.
     */
    protected boolean printItemTax = false;

    /**
     * Outgoing tender amount
     */
    protected CurrencyIfc outgoingTenderAmount = null;

    /**
     * Electronic signature data
     */
    protected Point[] signatureData = null;

    /**
     * Height of printed Signature in pixels
     */
    protected int signatureHeight;

    /**
     * Width of printed Signature in pixels
     */
    protected int signatureWidth;

    /**
     * Value of printer logo file name.
     */
    protected String receiptLogoFileName;

    /**
     * Duplicate Receipt flag
     */
    protected boolean duplicateReceipt = false;

    /** The system dependent end of line. * */
    protected static final String EOL = System.getProperty("line.separator");

    /** locale */
    protected Locale receiptLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);

    /** employee locale * */
    protected Locale employeeLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

    /**
     * Receipt style. Set to PrintableDocumentManagerIfc.STYLE_NORMAL by
     * default.
     */
    protected String receiptStyle = PrintableDocumentManagerIfc.STYLE_NORMAL;

    /** The marker used to indicate if a price was overriden. */
    protected static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("receipt");

    /**
     * Data wrapper object
     */
    protected PrintableDocumentParameterBeanIfc parameterBean;
    /**
     * Return handle to the PrintableDocumentManager
     */
    private PrintableDocumentManagerIfc printableDocumentManager;

    /**
     * Class constructor
     */
    public EYSPrintableDocument()
    {
        setPrinterInitialized(false);
    }

    /**
     * Sets the printer for the Receipt.
     *
     * @param p printer
     */
    public void setPrinter(ReceiptPrinterIfc p)
    {
        printer = p;
        setPrinterInitialized(true);
    }

    /**
     * Sets the properties for the receipts
     *
     * @param value properties
     */
    public void setProps(Properties value)
    {
        props = value;
    }

    /**
     * Returns whether a printer has been provided.
     *
     * @return true if a printer has been provided, false otherwise.
     */
    public boolean isPrinterInitialized()
    {
        return (printerInitialized);
    }

    /**
     * Sets whether a printer has been provided.
     *
     * @param value true if a printer has been provided, false otherwise.
     */
    public void setPrinterInitialized(boolean value)
    {
        this.printerInitialized = value;
    }

    /**
     * Prints a receipt with a specified header and footer.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>printer initialized
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>receipt printed
     * </UL>
     *
     * @throws DeviceException
     */
    public abstract void printDocument() throws DeviceException;
    
    /**
     * Prints a fiscal receipt
     */
    public void printFiscalReceipt() throws DeviceException
    {
        // Do nothing
    }

    /**
     * Lazily load manager.
     *
     * @return handle to the PrintableDocumentManager
     */
    protected PrintableDocumentManagerIfc getPrintableDocumentManager()
    {
        if (printableDocumentManager == null)
        {
            printableDocumentManager = (PrintableDocumentManagerIfc)Gateway
                        .getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
        }
        return printableDocumentManager;
    }

    /**
     * Returns the name of the logo to pass to the printer. This logo must
     * reside in the bin directory at runtime. By default this returns
     * "ReceiptLogo.jpg".
     *
     * @return
     */
    protected String getReceiptLogoFileName()
    {
        return receiptLogoFileName;
    }

    /**
     * Sets the logo file name. Causes this logo to be set as a bitmap onto the
     * pos printer for later use.
     *
     * @see #getReceiptLogoFileName()
     * @param receiptLogoFileName non-null and relative to bin directory
     * @throws DeviceException if the device could not have the bitmap set
     */
    protected void setReceiptLogoFileName(String receiptLogoFileName)
        throws DeviceException
    {
        if (receiptLogoFileName.equals(this.receiptLogoFileName))
            return; // quick exit

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = null;
        if (classLoader != null)  // check for null to avoid NullPointerException
        {
            url = classLoader.getResource(receiptLogoFileName);
        }
        URI uri = null;
        try
        {
            if (url != null)
            {
                uri = new URI(url.toString());
            }
        }
        catch (URISyntaxException e)
        {
            logger.warn("Could not find the logo:" + receiptLogoFileName);
        }
        File logoFile = null;
        if (uri != null)
        {
            logoFile = new File(uri);
        }
        if (logoFile != null && logoFile.exists())
        {
            this.receiptLogoFileName = receiptLogoFileName;
            printer.setBitmap(1, receiptLogoFileName, POSPrinterConst.PTR_BM_CENTER);
        }
        else
        {
            logger.warn("BitMap Logo " + receiptLogoFileName + " for receipt printing does not exist in the bin directory");
        }
    }

    /**
     * This method determines if the transaction is tax exempt.
     *
     * @return true if transaction is tax exempt
     */
    protected boolean isTaxExemptTransaction()
    {
        TransactionTaxIfc tt = ((RetailTransactionIfc)transaction).getTransactionTax();
        if (tt != null)
        {
            if (tt.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the integer value for the given parameter name.
     * @param parameterName
     * @param defaultValue
     * @return
     */
    protected int getIntegerParameterValue(String parameterName, int defaultValue)
    {
        int returnValue;
        try
        {
            UtilityIfc utility = Utility.createInstance();
            String parameterValue = utility.getParameterValue(parameterName, String.valueOf(defaultValue));
            returnValue = Integer.parseInt(parameterValue);
        }
        catch(Exception pe)
        {
            returnValue = defaultValue;
        }
        return returnValue;
    }

    /**
     * Checks for null and fills with blanks, if necessary.
     *
     * @param text input string
     * @return output string
     */
    protected static String checkNull(String text)
    {
        String output;
        // check for null string
        if (text == null)
        {
            output = "";
        }
        else
        {
            output = text;
        }
        return (output);
    }

    /**
     * Returns gift registry, if it exists, from transaction.
     *
     * @return Transaction GiftRegistry
     */
    protected RegistryIDIfc getTransactionGiftRegistry()
    {
        RegistryIDIfc gr = null;
        if (transaction instanceof SaleReturnTransactionIfc)
        {
            gr = ((SaleReturnTransactionIfc)transaction).getDefaultRegistry();
        }
        return (gr);
    }

    /**
     * Returns formatted percentage string for percent discounts.
     *
     * @param d rule value
     * @return formatted percent string
     */
    protected String getFormattedPercentage(DiscountRuleIfc d)
    {
        return getFormattedPercentage(d, false);
    }

    /**
     * Returns formatted percentage string for percent discounts.
     *
     * @param d rule value
     * @param showZeros shows zero percentages if true
     * @return formatted percent string
     */
    protected String getFormattedPercentage(DiscountRuleIfc d, boolean showZeros)
    {
        String strRate = "";

        if (d.getDiscountRate() != null && (showZeros || d.getDiscountRate().doubleValue() > 0))
        {
            BigDecimal scaleOne = new BigDecimal(1);
            // set the scale to 2 places after decimal, rounding up if last
            // digit >= 5
            BigDecimal rate = d.getDiscountRate().divide(scaleOne, 2);
            strRate = LocaleUtilities.formatPercent(rate, receiptLocale);
        }

        return strRate;
    }

    /**
     * Gets the transaction tender total without accounting for forced cash
     * change tenders (negative cash).
     *
     * @param totals
     * @return
     */
    protected CurrencyIfc getTotalAmount(TransactionTotalsIfc totals)
    {
        CurrencyIfc result = totals.getAmountTender();

        // if we are in a return transaction then we want to count the negative
        // cash with the total tender
        if (!(isReturnTransaction()))
        {
            // we want to subtract out forced cash change (negative cash
            // tenders)
            result = result.subtract(transaction.getNegativeCashTotal());
        }

        return result;
    }

    /**
     * Call this to send a '\n' char to the printer.
     *
     * @throws DeviceException
     */
    protected void lineFeed() throws DeviceException
    {
        printer.write("\n");
    }

    /**
     * Sends line to printer for printing with out line feed
     *
     * @param str String to be printed
     * @exception DeviceException if error occurs printing to printer
     */
    protected void print(String str) throws DeviceException
    {
        printer.write(str);
    }

    /**
     * Call this to cut the paper.
     * <p>
     * Adds five lines of padding for the paper cutter then calls
     * {@link ReceiptPrinterIfc#cutPaper()}.
     *
     * @throws DeviceException
     */
    protected void cut() throws DeviceException
    {
        // feed lines then cut 90%
        printer.write(ReceiptPrinter.escape("90fP"));
    }

    /**
     * Call this at the end of printing.
     * <p>
     * Calls {@link #cut()}. After the paper is cut,
     * {@link ReceiptPrinterIfc#endJob()} is called.
     *
     * @throws DeviceException
     */
    protected void end() throws DeviceException
    {
        cut();
        printer.endJob();
    }

    /**
     * Returns the revision number.
     *      *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Tell whether or not to print detailed tax info in the subtotals and for
     * each line item.
     *
     * @return true or false
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#getPrintItemTax()
     */
    public boolean getPrintItemTax()
    {
        return this.printItemTax;
    }

    /**
     * Set whether or not to print detailed tax info in the subtotals and for
     * each line item.
     *
     * @param value
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#setPrintItemTax(boolean)
     */
    public void setPrintItemTax(boolean value)
    {
        this.printItemTax = value;
    }

    /**
     * Returns whether the transaction is a "return" type transaction.
     *
     * @return Whether the transaction is a "return" type transaction.
     */
    protected boolean isReturnTransaction()
    {
        boolean result = false; // default
        if (transaction != null && transaction.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN)
        {
            result = true;
        }
        return result;
    }

    /**
     * If in a return, redeem, or cancel order we know the negative cash is a
     * refund and not change.
     *
     * @return boolean true = cash is a refund
     */
    protected boolean isCashRefund()
    {
        boolean result = false;
        // if return, redeem, or cancel order transaction then we know cash is a
        // refund not change
        if (transaction != null
                && (isReturnTransaction()
                        || transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL
                        || transaction.getTransactionType() == TransactionConstantsIfc.TYPE_REDEEM || transaction
                        .getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE))

        {
            result = true;
        }

        return result;
    }

    /**
     * Checks for Price Adjusted Items.
     *
     * @return boolean true = there exists at least 1 price adjusted item in the
     *         item array.
     */
    protected boolean hasPriceAdjustedItems()
    {
        boolean hasPriceAdjustedItems = false;
        for (Enumeration<AbstractTransactionLineItemIfc> e = ((RetailTransactionIfc)transaction).getLineItemsVector().elements(); e.hasMoreElements();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)e.nextElement();
            if (srli instanceof PriceAdjustmentLineItemIfc)
            {
                hasPriceAdjustedItems = true;
            }
        }
        return hasPriceAdjustedItems;
    }

    /**
     * Return the transaction.
     *
     * @return the transaction
     */
    public TenderableTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Method to check the Redeemed Transaction.
     *
     * @return boolean (true/false)
     */
    public boolean isRedeemedTransaction()
    {
        Boolean isRedeemed = Boolean.FALSE;

        if (transaction != null && transaction.getTransactionType() == TransactionConstantsIfc.TYPE_REDEEM)
        {
            isRedeemed = Boolean.TRUE;
        }
        return isRedeemed.booleanValue();
    }

    /**
     * To be implemented by subclasses, sets the options (header, footer,
     * PATFooter, etc.) from the PrintableDocumentParameterBean instance.
     *
     * @param parameterBean The parameterBean to set.
     */
    public abstract void setParameterBean(PrintableDocumentParameterBeanIfc parameterBean);

    /**
     * Zero amount of currency
     *
     * @return zero amount of currrency
     */
    public static CurrencyIfc getZeroAmount()
    {
        return DomainGateway.getBaseCurrencyInstance();
    }
} // End class EYSPrintableDocument
