/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ejournal/SalesLog.java /main/20 2012/08/21 16:40:58 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/17/12 - get correct discount amount
 *    sgu       08/16/12 - add ItemDiscountAudit discount rule
 *    jswan     06/29/12 - Removed references to tax classes that have been
 *                         depricated and are now deleted.
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    mjwallac  03/09/12 - Fortify: synchronize calls to parse() and format()
 *                         methods in java.text.Format class to fix race
 *                         condition.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   01/08/09 - commented line that used removed deprecated code
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    7    360Commerce 1.6         8/2/2007 6:29:23 AM    Naveen Ganesh
 *         Corrected the message Sales Assoc. for CR 27977
 *    6    360Commerce 1.5         4/25/2007 8:52:35 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         7/21/2006 4:14:15 PM   Brendan W. Farrell
 *         Merge from v7.x.  Use ifc so that it is extendable.
 *    4    360Commerce 1.3         1/22/2006 11:45:04 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:02 PM  Robert Pearse
 *
 *   Revision 1.4  2004/05/19 18:33:32  cdb
 *   @scr 5103 Updating to more correctly handle register reports.
 *
 *   Revision 1.3  2004/02/12 16:48:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:07  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:52:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Apr 16 2003 10:34:42   bwf
 * Fixed old deprecations and marked class as deprecated because it doesnt seem to be used anywhere.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.1   Sep 18 2002 15:17:24   kmorneau
 * changed calls from getCardTypeInstance() to utility manager getConfiguredCardTypeInstance()
 * Resolution for 1815: Credit Card Types Accepted
 *
 *    Rev 1.0   Apr 29 2002 15:40:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:03:10   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:18:38   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 17 2002 16:27:36   mpm
 * Modified to use object factory to implement CardType.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   Feb 05 2002 16:42:12   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:12:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.ejournal;

// Java imports
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.Customer;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.giftregistry.GiftRegistry;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderCharge;
import oracle.retail.stores.domain.tender.TenderGiftCertificate;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTax;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;


//------------------------------------------------------------------------------
/**
    Class for logging sale/return transaction. <P>
    @version $Revision: /main/20 $
    @deprecated as of 6.0.0 - class does not seem to be used anywhere
**/
//------------------------------------------------------------------------------
public class SalesLog
{                                       // Begin class SalesLog()

    /**
        revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /main/20 $";
    /**
        format of date line
    **/
    protected static SimpleDateFormat DATE_LINE_FORMAT =
      new SimpleDateFormat("MM/dd/yyyy               hh:mm:ss a z");
    /**
        length of quantity-unit-price display
    **/
    protected static int QUANTITY_UNIT_PRICE_LENGTH = 18;
    /**
        format of quantity field
    **/
    protected static String QUANTITY_FORMAT = "#";
    /**
        length of quantity field (as defined in screen data specs)
    **/
    protected static int QUANTITY_LENGTH = 6;
    /**
        length of plu item id
    **/
    protected static int PLU_ITEM_ID_LENGTH = 14;
    /**
        length of plu description
    **/
    protected static int PLU_DESCRIPTION_LENGTH = 24;
    /**
        item price length
    **/
    protected static int ITEM_PRICE_LENGTH = 13;
    /**
        extended price length
    **/
    protected static int EXTENDED_PRICE_LENGTH = 15;
    /**
        employee ID length
    **/
    protected static int EMPLOYEE_ID_LENGTH = 10;
    /**
        length of discount literal
    **/
    protected static int DISCOUNT_LITERAL_LENGTH = 19;
    /**
        tax rate rate length (10.0875%)
    **/
    protected static int TAX_RATE_LENGTH = 8;
    /**
        tender type descriptor length
    **/
    protected static int TENDER_TYPE_DESCRIPTOR_LENGTH = 16;
    /**
        spaces buffer
    **/
    protected static String SPACES = "                                        ";
    /**
        employee spaces buffer
    **/
    protected static String EMP_SPACES = "    ";
    /**
        underline buffer
    **/
    protected static String UNDERLINE = "________________________________________";
    /**
        retail transaction
    **/
    protected RetailTransactionIfc transaction;
    /**
        standard line length
    **/
    public static final int LINE_LENGTH = 40;

    /**
        The file to write sales and header logs
    **/
    protected RandomAccessFile file = null;

    /**
        Buffer to hold data until written out
    **/
    protected StringBuffer bufferedData = null;

    /**
        Starting and Ending positions for each transaction
    **/
    protected long startPos = 0;
    protected long endPos = 0;

    /**
        Log files
    **/
    public static final String HEADER_LOG_NAME = "Header.log";
    public static final String SALES_LOG_NAME = "Sales.log";

    /**
        Line length for header data
    **/
    public static final int HEADER_LINE_SIZE = 54;


    //---------------------------------------------------------------------
    /**
        Class constructor
     **/
    //---------------------------------------------------------------------
    public SalesLog()
    {
    }

    protected void writeBuf(String s)
    {
        bufferedData.append(s + "\n");
    }

    /**
     This will open the header log and write the header info
     Each line represents a transaction in this format:
     nnnnnnnnnnnn mm/dd/yyyy HH:mm xxx yyy nnnnnnn nnnnnnn

     TRANID=nnnnnnnnnnnn
     DATE=mm/dd/yyyy
     TIME=HH:mm
     CASHIER=xxx
     SALESASSOC=yyy
     Data start and end in sales.log = nnnnnnn nnnnnnn
    **/
    protected void writeHeader()
    {
        try
        {
            file = new RandomAccessFile(HEADER_LOG_NAME, "rw");
            // Go to the end and start writing
            file.seek(file.length());

            StringBuffer data=new StringBuffer();
            data.append(transaction.getFormattedStoreID());
            data.append(transaction.getFormattedWorkstationID());
            data.append(transaction.getFormattedTransactionSequenceNumber());
            data.append(" ");

            Date now = new Date();
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
            String dateString = dateTimeService.formatDate(now, defaultLocale, DateFormat.SHORT);
            String timeString = dateTimeService.formatTime(now, defaultLocale, DateFormat.SHORT);
            data.append(dateString).append(" ").append(timeString).append(" ");

            if (transaction.getCashier() != null)
            {
                String emp = transaction.getCashier().getEmployeeID();
                data.append(emp);
                // Pad with spaces
                data.append(EMP_SPACES.substring(emp.length()));
            }
            else
            {
                data.append(EMP_SPACES);
            }

            // get sales associate
            try
            {
                /*
                 * Find out if salesAssociate is in the transaction.
                 * An exception will be raised if it isn't.
                 */
                EmployeeIfc employee;
                employee =
                  (EmployeeIfc) ReflectionUtility.getAttribute(transaction,
                                                        "salesAssociate");
                if (employee != null)
                {
                    String emp = employee.getEmployeeID();
                    data.append(emp);
                    data.append(EMP_SPACES.substring(emp.length()));
                }
                else
                    data.append(EMP_SPACES);
            }
            catch (Exception e)
            {
                data.append(EMP_SPACES);
            }

            DecimalFormat df = new DecimalFormat("0000000");
            FieldPosition p = new FieldPosition(NumberFormat.INTEGER_FIELD);
            df.format(startPos,data,p);
            data.append(" ");
            df.format(endPos,data,p);
            data.append("\n");
            file.writeBytes(data.toString());

            file.close();

        }
        catch (IOException e)
        {

        }
        finally
        {

        }
    }

    protected void startLog()
    {
        bufferedData = new StringBuffer();
        try
        {
            file = new RandomAccessFile(SALES_LOG_NAME, "rw");
            startPos = file.length();
            file.seek(startPos);
        }
        catch (IOException e)
        {
        }
    }

    protected void endLog()
    {
        try
        {
            endPos = startPos + bufferedData.length();
            file.writeBytes(bufferedData.toString());
            file.close();
        }
        catch (IOException e)
        {
        }
    }

    //---------------------------------------------------------------------
    /**
        saveToLog. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>transaction logged
        </UL>
        @exception none
    **/
    //---------------------------------------------------------------------
    public void saveToLog(RetailTransactionIfc transaction)
    {                                   // begin saveToLog()

        this.transaction = transaction;
        startLog();
        // Dump Header info in sales log
        writeBuf(getFormattedDate(new Date()));

        // write transaction id
        writeBuf(getFormattedTransactionID());

        // write cashier information
        writeBuf(getFormattedCashier());
        // write customer information
        writeCustomer();
        writeBuf("");

        // write transaction type
        writeTransactionType();
        writeBuf("");
        // write full transaction ID (barcode)
//        writeBufCentered(checkNull(transaction.getTransactionID()));
        writeUnderLine();
        writeBuf("");

        // write receipt body
        writeLineItems();
        writeBuf("");
        writeTransactionTotals();
        if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
        {
            writeTenderAmounts();
        }
        writeTenderTotals();
        writeBuf("");
        writeUnderLine();

        endLog();
        writeHeader();
    }                                   // end saveToLog()

    //---------------------------------------------------------------------
    /**
        Method checks for null and fills with blanks, if necessary. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param text input string
        @return output string
    **/
    //---------------------------------------------------------------------
    protected String checkNull(String text)
    {                                   // begin checkNull()
        String output;
        // check for null string
        if (text == null)
        {
            output = new String();
        }
        else
        {
            output = text;
        }
        return(output);
    }                                   // end checkNull()

    //---------------------------------------------------------------------
    /**
        writes underline. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @exception Jpos exception
    **/
    //---------------------------------------------------------------------
    protected void writeUnderLine()
    {                                   // begin writeUnderLine()
        writeBuf(UNDERLINE);

    }                                   // end writeUnderLine()

    //---------------------------------------------------------------------
    /**
        Returns formatted date string. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>Date format object initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param date Date object
        @return formatted date string
    **/
    //---------------------------------------------------------------------
    protected synchronized String getFormattedDate(Date date)
    {                                   // begin getFormattedDate()
        return(DATE_LINE_FORMAT.format(date));
    }                                   // end getFormattedDate()

    //---------------------------------------------------------------------
    /**
        Returns formatted transaction identifier. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>transaction ID is non null
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return formatted line with transaction identifier data
    **/
    //---------------------------------------------------------------------
    protected String getFormattedTransactionID()
    {                                   // begin getFormattedTransactionID()
        String returnString;
        // if no transaction identifier
        if (transaction.getTransactionID() == null)
        {
            // write blank line
            returnString = " ";
        }
        else
        {
            StringBuffer sb = new StringBuffer(LINE_LENGTH);
            sb.append("Transaction ");
            sb.append(transaction.getFormattedTransactionSequenceNumber());
            sb.append(":Store ");
            sb.append(transaction.getFormattedStoreID());
            sb.append("    Reg. ");
            sb.append(transaction.getFormattedWorkstationID());
            returnString = sb.toString();
        }
        return(returnString);
    }                                   // end getFormattedTransactionID()

    //---------------------------------------------------------------------
    /**
        Returns formatted cashier, sales associate. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>cashier, sales associate exist
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return formatted line with cashier, sales associate data
    **/
    //---------------------------------------------------------------------
    protected String getFormattedCashier()
    {                                   // begin getFormattedCashier()
        String returnString;
        String strCashierID = null;
        String strSalesAssociateID = null;
        if (transaction.getCashier() != null)
        {
            strCashierID = transaction.getCashier().getEmployeeID();
        }
        // get sales associate
        try
        {
            /*
             * Find out if salesAssociate is in the transaction.
             * An exception will be raised if it isn't.
             */
            EmployeeIfc employee;
            employee =
              (EmployeeIfc) ReflectionUtility.getAttribute(transaction,
                                                        "salesAssociate");
            if (employee != null)
            {
                strSalesAssociateID = employee.getEmployeeID();
            }
        }
        catch (Exception e)
        {
            /*
             * Don't worry if there is no sales associate,
             * not all transactions have one.
             */
        }
        // if no cashier, skip line
        if (strCashierID == null)
        {
            returnString = " ";
        }
        else
        {
            // build cashier string
            StringBuffer sb = new StringBuffer(LINE_LENGTH);
            sb.append("Cashier: ");
            // if cashier ID too long, it will be overwritten
            sb.append(strCashierID);
            sb.append(pad(EMPLOYEE_ID_LENGTH,
                          strCashierID.length(),
                          2));
            // if sales associate ID exists, add it to string
            if (strSalesAssociateID != null)
            {
                StringBuffer s = new StringBuffer("Sales: ");
                s.append(strSalesAssociateID);
                rightJustifyStringBuffer(s,
                                         19);
                sb.append(s.toString());
            }
            returnString = sb.toString();
        }

        // pass back string
        return(returnString);

    }                                   // end getFormattedCashier()



    //---------------------------------------------------------------------
    /**
        write customer identifier. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @exception Jpos exception
    **/
    //---------------------------------------------------------------------
    protected void writeCustomer()
    {                                   // begin writeCustomer()
        try
        {                               // begin write customer try block
            /*
             * See if the transaction has customer.
             * If it doesn't, an exception will be raised.
             */
            Object obj = ReflectionUtility.getAttribute(transaction,
                                                        "Customer");
            Customer c = (Customer) obj;
            writeBuf("Customer: " +
                      c.getCustomerID());

        }                               // end write customer try block
        catch (Exception e)
        {
            /*
             * Not all transactions have a customer
            */
        }
    }                                   // end writeCustomer()

    //---------------------------------------------------------------------
    /**
        writes transaction type.  This is a kludge for Release 2.0.
        In Release 2.5, exchanges and returns will be differentiated.
        <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @exception Jpos exception
    **/
    //---------------------------------------------------------------------
    protected void writeTransactionType()
    {                                   // begin writeTransactionType()
        int tt = transaction.getTransactionType();
        if (tt == TransactionIfc.TYPE_NO_SALE ||
            tt == TransactionIfc.TYPE_VOID)
        {
            writeBufCentered(TransactionIfc.TYPE_DESCRIPTORS[tt]);
        }
        else if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            writeBufCentered("CANCELED TRANSACTION");
        }
        else
        {                               // begin handle sale, return or exchange
            try
            {                           // begin write line items try block
                /*
                 * See if the transaction has line items.
                 * If it doesn't, an exception will be raised.
                 */
                Vector lineItems;
                lineItems =
                  (Vector) ReflectionUtility.getAttribute(transaction,
                                                          "lineItems");
                // derive transaction type based on items
                tt = TransactionIfc.TYPE_UNKNOWN;
                for (int i = 0; i < lineItems.size(); i++)
                {                       // begin loop through line items
                    // line items
                    SaleReturnLineItemIfc srli =
                      (SaleReturnLineItemIfc) lineItems.elementAt(i);
                    BigDecimal qty = srli.getItemQuantityDecimal();

                    // reset type based on new qty
                    switch (tt)
                    {                   // begin evaluate temp type
                        // initialize tt
                        case TransactionIfc.TYPE_UNKNOWN:
                            if (qty.longValue() >= 0)
                            {
                                tt = TransactionIfc.TYPE_SALE;
                            }
                            else
                            {
                                tt = TransactionIfc.TYPE_RETURN;
                            }
                            break;
                        // if sale, check for exchange
                        case TransactionIfc.TYPE_SALE:
                            if (qty.longValue() < 0)
                            {
                                tt = TransactionIfc.TYPE_EXCHANGE;
                                // force exit
                                i = lineItems.size();
                            }
                            break;
                        // if return, check for exchange
                        case TransactionIfc.TYPE_RETURN:
                            if (qty.longValue() > 0)
                            {
                                tt = TransactionIfc.TYPE_EXCHANGE;
                                // force exit
                                i = lineItems.size();
                            }
                            break;
                        // should never get here
                        case TransactionIfc.TYPE_EXCHANGE:
                        default:
                            i = lineItems.size();
                            break;
                    }                   // end evaluate temp type
                }                       // end loop through line items
                writeBufCentered(TransactionIfc.TYPE_DESCRIPTORS[tt]);
            }                           // end write line items try block
            catch (Exception e)
            {
                // if we get here, there's a problem, but we'll work around
                // it for now
                writeBufCentered("SALE");
            }
        }                               // end handle sale, return or exchange


    }                                   // end writeTransactionType()

    //---------------------------------------------------------------------
    /**
        Returns formatted quantity string. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param qty long quantity value
        @return formatted line with quantity, sales associate data
    **/
    //---------------------------------------------------------------------
    protected String getFormattedQuantity(long qty)
    {                                   // begin getFormattedQuantity()
        DecimalFormat df = new DecimalFormat(QUANTITY_FORMAT);
        FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
        StringBuffer result = new StringBuffer();
        result = df.format(qty, result, fp);
        // prepend spaces to get eight
        int offset = QUANTITY_LENGTH - fp.getEndIndex();
        if (offset < 0)
        {
           offset = 0;
        }
        result = result.insert(0, SPACES.substring(QUANTITY_LENGTH - offset));
        return(result.toString());
    }                                   // end getFormattedQuantity()

    //---------------------------------------------------------------------
    /**
        Returns formatted quantity string. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param longPrice cents value of price
        @param qty long quantity value
        @return formatted string with quantity, sales associate data
    **/
    //---------------------------------------------------------------------
/* PENDING A DECISION ON WHETHER WE SHOULD DO THIS
    protected String getFormattedQuantityUnitPrice(long longPrice,
                                                 long qty)
    {                                   // begin getFormattedQuantityUnitPrice()
        long unitPrice = 0;
        long useQty = 0;
        if (qty == 1)
        {
            unitPrice = longPrice;
            useQty = qty;
        }
        else
        {
            unitPrice = longPrice / qty;
            if (qty < 0)
            {
                useQty = -qty;
            }
            else
            {
                useQty = qty;
            }
        }
        StringBuffer result = new StringBuffer(new Long(useQty).toString());
        result.append("@");
        result.append((new CurrencyIfc(unitPrice)).toDisplayString());
        rightJustifyStringBuffer(result,
                                 QUANTITY_UNIT_PRICE_LENGTH);
        return(result.toString());
    }                                   // end getFormattedQuantityUnitPrice()
*/
    //---------------------------------------------------------------------
    /**
        write line items. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    protected void writeLineItems()
    {                                   // begin writeLineItems()
        try
        {                               // begin write line items try block
            /*
             * See if the transaction has line items.
             * If it doesn't, an exception will be raised.
             */
            Vector lineItems;
            lineItems =
              (Vector) ReflectionUtility.getAttribute(transaction,
                                                      "lineItems");

            Enumeration e = lineItems.elements();
            while (e.hasMoreElements())
            {                           // begin write line items
                writeLineItem((SaleReturnLineItemIfc) e.nextElement());
            }                           // end write line items

        }                               // end write line items try block
        catch (Exception e)
        {
            /*
             * Not all transactions have line items
             */
        }
    }                                   // end writeLineItems()


    //---------------------------------------------------------------------
    /**
        write a single sale return line item. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param srli SaleReturnLineItem
        @exception jpos exception
    **/
    //---------------------------------------------------------------------
    protected void writeLineItem(SaleReturnLineItemIfc srli)
    {                                   // begin writeLineItem()
        ItemPriceIfc ip = srli.getItemPrice();
        PLUItemIfc plu = srli.getPLUItem();
        StringBuffer sb = new StringBuffer();
        // build description, price line
        // truncate description, if necessary
        String desc = plu.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
        int len = desc.length();
        if (len > PLU_DESCRIPTION_LENGTH)
        {
            desc = desc.substring(0, PLU_DESCRIPTION_LENGTH);
            len = PLU_DESCRIPTION_LENGTH;
        }
        sb.append(desc);
        sb.append(pad(PLU_DESCRIPTION_LENGTH,
                      len,
                      1));
        // insert price two spaces from end
        CurrencyIfc extendedDiscountedSellingPrice =
          ip.getExtendedDiscountedSellingPrice();
        String price =
          extendedDiscountedSellingPrice.toFormattedString();
        sb.append(pad(ITEM_PRICE_LENGTH,
                      price.length(),
                      0));
        sb.append(price);
        sb.append(" ");
        // insert tax mode at end
        int taxMode = ip.getItemTax().getTaxMode();
        String taxFlag = new String ("T");
        if (taxMode == TaxIfc.TAX_MODE_STANDARD &&
            plu.getTaxable() == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
        }
        sb.append(taxFlag);
        // write line
        writeBuf(sb.toString());

        // build item number, quantity line
        sb = new StringBuffer();
        sb.append("  ");
        sb.append(plu.getItemID());
        sb.append(pad(PLU_ITEM_ID_LENGTH,
                      plu.getItemID().length(),
                      1));
        BigDecimal qty = srli.getItemQuantityDecimal();
        //long longPrice = extendedDiscountedSellingPrice.getLongValue();
        // if multiple quantities but price not divisible, we won't do the
        // breakdown
/* PENDING A DECISION ON WHETHER WE SHOULD DO THIS
        if (longPrice % qty == 0 &&
            qty != 0)
        {                               // begin write qty, unit price
            sb.append(getFormattedQuantityUnitPrice(longPrice,
                                                    qty));
        }                               // end write qty, unit price
        else
*/
        {
            sb.append(SPACES.substring(0, 7));
            String qtyString = qty.toString();
            sb.append(pad(QUANTITY_LENGTH,
                          qtyString.length(),
                          0));
            sb.append(qtyString);
        }
        writeBuf(sb.toString());
        // handle discounts, if they exist
        if (ip.getItemDiscounts() != null)
        {
            if (ip.getItemDiscounts().length > 0)
            {
                writeDiscounts(ip);
            }
        }
        // write gift registry if necessary
        RegistryIDIfc gr = srli.getRegistry();
        // if not found, check for transaction gift registry
        if (gr == null)
        {
            gr = getTransactionGiftRegistry();
            if (gr != null)
            {
                writeItemGiftRegistry(gr);
            }
        }
        // use item gift registry
        else
        {
            writeItemGiftRegistry(gr);
        }
        // if sales associate modified, write it
        if (srli.getSalesAssociateModifiedFlag())
        {
            writeItemSalesAssociate(srli.getSalesAssociate());
        }
        else
        {
            ReturnItemIfc ri = srli.getReturnItem();
            // if return, get sales associate
            if (qty.signum() < 0 &&
                ri  != null)
            {
                writeItemSalesAssociate(ri.getSalesAssociate());
            }
        }
        // write a blank line
        writeBuf("");
    }                                   // end writeLineItem()

    //---------------------------------------------------------------------
    /**
        Retrieves gift registry, if it exists, from transaction. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return gr GiftRegistry object
    **/
    //---------------------------------------------------------------------
    protected GiftRegistry getTransactionGiftRegistry()
    {                                   // begin getTransactionGiftRegistry()
        GiftRegistry gr = null;
        try
        {                               // begin get attribute try block
            gr = (GiftRegistry) ReflectionUtility.getAttribute(transaction,
                                                               "defaultGiftRegistry");
        }                               // end get attribute try block
        catch (Exception e)
        {
            /*
             * Not all transactions have gift registries
             */
        }
        return(gr);
    }                                   // end getTransactionGiftRegistry()

    //---------------------------------------------------------------------
    /**
        write discount data. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param ip ItemPriceIfc object
    **/
    //---------------------------------------------------------------------
    protected void writeDiscounts(ItemPriceIfc ip)
    {                                   // begin writeDiscounts()

        ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
        StringBuffer sb;
        String price;
        int numDiscounts = 0;
        if (discounts != null)
        {
            numDiscounts = discounts.length;
        }
        for (int i = 0; i < numDiscounts; i++)
        {                               // begin handle discounts
            // build original price line (on iteration 0 only)
            if (i == 0)
            {                           // begin write original price line
                sb = new StringBuffer();
                sb.append("  Orig. Price         ");
                price = ip.getExtendedSellingPrice().toFormattedString();
                sb.append(pad(ITEM_PRICE_LENGTH,
                              price.length(),
                              0));
                sb.append(price);
                writeBuf(sb.toString());
            }                           // end write original price line
            // build discount line(s)
            DiscountRuleIfc d = discounts[i];
            int scope = d.getDiscountScope();
            int method = d.getDiscountMethod();
            sb = new StringBuffer();
            sb.append("  ");
            CurrencyIfc c;
            String reasonCodeDescription = null;
            // get discount amount, reason code
            if (d instanceof ItemTransactionDiscountAudit)
            // if transaction discount
            {
                ItemTransactionDiscountAudit it = (ItemTransactionDiscountAudit) d;
                // reset scope to build descriptor
                scope = DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION;
                c = it.getDiscountAmount();
                // retrieve transaction discount reason code description
                reasonCodeDescription =
                  getTransactionDiscountReasonCodeDescription(it);
            }
            // if item discount
            else
            {
                ItemDiscountStrategyIfc id = (ItemDiscountStrategyIfc) d;
                c = id.getItemDiscountAmount();

                // retrieve item discount reason code description
                reasonCodeDescription =
                  getItemDiscountReasonCodeDescription(id);
            }
            String discountDescription = DiscountRuleConstantsIfc.DISCOUNT_SCOPE_DESCRIPTOR[scope] +
                                           " Discount";
            sb.append(discountDescription);
            sb.append(pad(DISCOUNT_LITERAL_LENGTH,
                          discountDescription.length(),
                          1));
            price = c.toFormattedString();
            sb.append(pad(ITEM_PRICE_LENGTH,
                      price.length(),
                      0));
            sb.append(price);
            // add discount amount to line and write
            writeBuf(sb.toString());
            // write reason code description line
            sb = new StringBuffer("  ");
            sb.append(reasonCodeDescription);
            writeBuf(sb.toString());
        }                               // end handle discounts
    }                                   // end writeDiscounts()

    //---------------------------------------------------------------------
    /**
        write item gift registry data. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param gr GiftRegistry object
    **/
    //---------------------------------------------------------------------
    protected void writeItemGiftRegistry(RegistryIDIfc gr)
    {                                   // begin writeItemGiftRegistry()

        StringBuffer sb = new StringBuffer("  Gift Reg. ");
        sb.append(gr.getID());
        writeBuf(sb.toString());
    }                                   // end writeItemGiftRegistry()

    //---------------------------------------------------------------------
    /**
        write item sales associate data. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param sa EmployeeIfc object
    **/
    //---------------------------------------------------------------------
    protected void writeItemSalesAssociate(EmployeeIfc sa)
    {                                   // begin writeItemSalesAssociate()
        StringBuffer sb = new StringBuffer("  Sales Assoc. ");
        sb.append(sa.getEmployeeID());
        writeBuf(sb.toString());
    }                                   // end writeItemSalesAssociate()

    //---------------------------------------------------------------------
    /**
        Retrieves item discount reason code description.  In Release 2.0,
        this will be a kludge which pulls the description from
        hard-coded values.  In Release 2.5, properties will be used
        to divine the description. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>reason code description string returned
        </UL>
        @param id ItemDiscountStrategyIfc object
        @return description String description of reason
    **/
    //---------------------------------------------------------------------
    protected String getItemDiscountReasonCodeDescription(ItemDiscountStrategyIfc id)
    {                                   // begin getItemDiscountReasonCodeDescription()
        String desc;
        int reasonCode = 0;
        try
        {
            reasonCode = Integer.parseInt(id.getReason().getCode());
        }
        catch (Exception e)
        {
            //do nothing
        }
        int method = id.getDiscountMethod();
        if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
        {                               // begin check discount-by-percent reasons
            switch (reasonCode)
            {                           // begin evaluate reason code
                case 1:
                    desc = "Competition Match";
                    break;
                case 2:
                    desc = "EmployeeIfc";
                    break;
                case 3:
                    desc = "Saturday Morning Special";
                    break;
                case 4:
                    desc = "Senior Citizen";
                    break;
                default:
                    desc = "Unknown";
            }                           // end evaluate reason code
        }                               // end check discount-by-percent reasons
        else
        {                               // begin check discount-by-amount reasons
            switch (reasonCode)
            {                           // begin evaluate reason code
                case 2:
                    desc = "Mall Coupon";
                    break;
                case 3:
                    desc = "Promotional Campaign";
                    break;
                default:
                    desc = "Unknown";
            }                           // end evaluate reason code
        }                               // end check discount-by-amount reasons

        // pass back description
        return(desc);
    }                                   // end getItemDiscountReasonCodeDescription()

    //---------------------------------------------------------------------
    /**
        Retrieves transaction discount reason code description.  In Release 2.0,
        this will be a kludge which pulls the description from
        hard-coded values.  In Release 2.5, properties will be used
        to divine the description. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>reason code description string returned
        </UL>
        @param it ItemTransactionDiscountAudit object
        @return description String description of reason
    **/
    //---------------------------------------------------------------------
    protected String getTransactionDiscountReasonCodeDescription(ItemTransactionDiscountAudit it)
    {                                   // begin getTransactionDiscountReasonCodeDescription()
        String desc;
        int reasonCode = Integer.parseInt(it.getReason().getCode());
        int method = it.getDiscountMethod();
        if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
        {                               // begin check discount-by-percent reasons
            switch (reasonCode)
            {                           // begin evaluate reason code

                case 11:
                    desc = "Promotion";
                    break;
                case 33:
                    desc = "EmployeeIfc Purchase";
                    break;
                case 45:
                    desc = "Manager Purchase";
                    break;
                default:
                    desc = "Unknown";
            }                           // end evaluate reason code
        }                               // end check discount-by-percent reasons
        else
        {                               // begin check discount-by-amount reasons
            switch (reasonCode)
            {                           // begin evaluate reason code

                case 11:
                    desc = "Saturday Morning Special";
                    break;
                case 12:
                    desc = "Competition Special";
                    break;
                case 33:
                    desc = "EmployeeIfc";
                    break;
                case 45:
                    desc = "Senior Citizen";
                    break;
                default:
                    desc = "Unknown";
                    break;
            }                           // end evaluate reason code
        }                               // end check discount-by-amount reasons

        // pass back description
        return(desc);
    }                                   // end getTransactionDiscountReasonCodeDescription()

    //---------------------------------------------------------------------
    /**
        write transaction totals. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    protected void writeTransactionTotals()
    {                                   // begin writeTransactionTotals()
        TransactionTotalsIfc totals =
          transaction.getTransactionTotals();

        // get pre-tax total
        StringBuffer sb = new StringBuffer();
        sb.append("Sub-Total          ");
        CurrencyIfc preTaxTotal =
            DomainGateway.getBaseCurrencyInstance(totals.getSubtotal().getStringValue());
        preTaxTotal = preTaxTotal.subtract(totals.getDiscountTotal());
        String strAmount = preTaxTotal.toFormattedString();
        sb.append(pad(EXTENDED_PRICE_LENGTH,
                  strAmount.length(),
                  0));
        sb.append(strAmount);
        writeBuf(sb.toString());
        writeBuf("");
        // if canceled, write simple tax total summary line
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {                               // begin write tax summary line
            sb = new StringBuffer();
            sb.append("Tax                ");
            strAmount = totals.getTaxTotal().toFormattedString();
            sb.append(pad(EXTENDED_PRICE_LENGTH,
                      strAmount.length(),
                      0));
            sb.append(strAmount);
            writeBuf(sb.toString());
            writeBuf("");
        }                               // end write tax summary line
        // get transaction tax (to determine tax exempt) and write tax data
        else if (totals.getTaxTotal().signum() != CurrencyIfc.ZERO)
        {                               // begin write tax details
            boolean bExempt = false;
            TransactionTax tt = getTransactionTax();
            if (tt != null)
            {                           // begin write tax data
                if (tt.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
                {
                    bExempt = true;
                }
                if (bExempt)
                {
                    writeTaxExempt(tt);
                }
                writeBuf("");
            }                           // end write tax data
        }                               // end write tax detail
        // write total
        sb = new StringBuffer();
        sb.append("Total              ");
        strAmount = totals.getGrandTotal().toFormattedString();
        sb.append(pad(EXTENDED_PRICE_LENGTH,
                  strAmount.length(),
                  0));
        sb.append(strAmount);
        writeBuf(sb.toString());
        writeBuf("");

    }                                   // end writeTransactionTotals()

    //---------------------------------------------------------------------
    /**
        Retrieves transaction tax data, if it exists, from transaction. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return tt TransactionTax object
    **/
    //---------------------------------------------------------------------
    protected TransactionTax getTransactionTax()
    {                                   // begin getTransactionTax()
        TransactionTax tt = null;
        try
        {                               // begin get attribute try block
            tt = (TransactionTax) ReflectionUtility.getAttribute(transaction,
                                                                 "transactionTax");
        }                               // end get attribute try block
        catch (Exception e)
        {
            /*
             * Not all transactions have transaction tax stuff
             */
        }
        return(tt);
    }                                   // end getTransactionTax()

    //---------------------------------------------------------------------
    /**
        write tax exempt data. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param tt TransactionTax object
    **/
    //---------------------------------------------------------------------
    protected void writeTaxExempt(TransactionTax tt)
    {                                   // begin writeTaxExempt()
        StringBuffer sb = new StringBuffer("  Tax Exempt Certificate   ");
        sb.append(tt.getTaxExemptCertificateID());
        writeBuf(sb.toString());
    }                                   // end writeTaxExempt()

    //---------------------------------------------------------------------
    /**
        write tender amounts. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    protected void writeTenderAmounts()
    {                                   // begin writeTenderAmounts()
        Enumeration e = transaction.getTenderLineItemsVector().elements();
        int size = transaction.getTenderLineItemsVector().size();
        TenderLineItemIfc tli;
        CurrencyIfc amount;
        StringBuffer sb;
        boolean writeCreditData = false;
        boolean writeGiftCertificateData = false;
        String amountString;
        while (e.hasMoreElements())
        {                               // begin loop through tender lines
            tli = (TenderLineItemIfc) e.nextElement();
            amount = tli.getAmountTender();
            String desc = tli.getTypeDescriptorString();
            // if credit card, write credit data, credit slip
            if (tli instanceof TenderCharge)
            {
                Dispatcher d = Dispatcher.getDispatcher();
                UtilityManager utility = (UtilityManager) d.getManager(UtilityManagerIfc.TYPE);
                CardTypeIfc cardType =
                    utility.getConfiguredCardTypeInstance();
                TenderCharge charge = (TenderCharge)tli;
                desc = cardType.identifyCardType(charge.getEncipheredCardData(), TenderTypeEnum.CREDIT.toString());
                writeCreditData = true;
            }
            else if (tli instanceof TenderGiftCertificate)
            {
                writeGiftCertificateData = true;
            }
            // do not write if amount zero
            if (amount.signum() != CurrencyIfc.ZERO)
            {                           // begin write tender line item
                sb = new StringBuffer("  ");
                int len = desc.length();
                if (len > TENDER_TYPE_DESCRIPTOR_LENGTH)
                {
                    desc = desc.substring(0, TENDER_TYPE_DESCRIPTOR_LENGTH);
                    len = TENDER_TYPE_DESCRIPTOR_LENGTH;
                }
                sb.append(desc);
                sb.append(pad(TENDER_TYPE_DESCRIPTOR_LENGTH,
                              len,
                              1));

                amountString = amount.toFormattedString();
                sb.append(pad(EXTENDED_PRICE_LENGTH,
                            amountString.length(),
                            0));
                sb.append(amountString);
                writeBuf(sb.toString());
            }                           // end write tender line item
            // if credit data, write it
            if (writeCreditData)
            {                           // begin write credit card data
                // write account string
                sb = new StringBuffer("    Account    ");
                String tliString = new String(tli.getNumber());
                int len = tliString.length();
                sb.append(tliString.substring(len - 4, len));
                sb.append("  Exp ");
                TenderCharge tc = (TenderCharge) tli;
                sb.append(tc.getExpirationDateString());
                writeBuf(sb.toString());
                // write authorization string
                String auth = tc.getAuthorizationCode();
                if (auth != null)
                {
                    sb = new StringBuffer("    Auth.      ");
                    sb.append(auth);
                    writeBuf(sb.toString());
                }
                writeCreditData = false;
            }                           // end write credit card data
            // if gift certificate data, write it
            else if (writeGiftCertificateData)
            {                           // begin write gift certificate data
                // write account string
                sb = new StringBuffer("    Cert. # ");
                sb.append(tli.getNumber());
                writeBuf(sb.toString());
                writeGiftCertificateData = false;
            }                           // end write gift certificate data
        }                               // end loop through tender lines
        // write space (if tender lines exist)
        if (size > 0)
        {
            writeBuf("");
        }
    }                                   // end writeTenderAmounts()

    //---------------------------------------------------------------------
    /**
        write tender totals. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    protected void writeTenderTotals()
    {                                   // begin writeTenderTotals()
        TransactionTotalsIfc totals =
          transaction.getTransactionTotals();
        // write tender totals (only if not cancelled and not an even exchange)
        if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED &&
            totals.getGrandTotal().signum() != CurrencyIfc.ZERO)
        {                               // begin write totals
            StringBuffer sb = new StringBuffer("Total Tender       ");
            String strAmount = totals.getAmountTender().toFormattedString();
            sb.append(pad(EXTENDED_PRICE_LENGTH,
                      strAmount.length(),
                      0));
            sb.append(strAmount);
            writeBuf(sb.toString());
            writeBuf("");
            sb = new StringBuffer("Change Due         ");
            strAmount = totals.getBalanceDue().toFormattedString();
            sb.append(pad(EXTENDED_PRICE_LENGTH,
                      strAmount.length(),
                      0));
            sb.append(strAmount);
            writeBuf(sb.toString());
            writeBuf("");
        }                               // end write totals

    }                                   // end writeTenderTotals()

    //---------------------------------------------------------------------
    /**
        write an item two spaces from the right margin. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>sales log selected and initialized
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param description text
        @param amount string
    **/
    //---------------------------------------------------------------------
    protected void writeItem(String description, String amount)
    {                                   // begin writeItem()
        String value = description;
        int numSpaces = LINE_LENGTH - description.length() - amount.length();
        for (int i = 0; i < numSpaces; i++ )
        {
            value += " ";
        }
        value += amount;
        writeBuf(value);
    }                                   // end writeItem()

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // Begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // End getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        Centers string and writes line. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>line centered and writeed
        </UL>
        @param str String to be writeed
    **/
    //---------------------------------------------------------------------
    protected void writeBufCentered(String str)
    {                                   // begin writeBufCentered()
        int length = str.length();
        // if line less than line length
        if (length <= LINE_LENGTH)
        {
            StringBuffer sb = new StringBuffer(LINE_LENGTH);
            int insertPoint = ((LINE_LENGTH - length) / 2);
            for (int i = 0; i < insertPoint; i++)
            {
                sb.append(" ");
            }
            sb.append(str);
            writeBuf(sb.toString());
        }
        // if line too long, just write it
        else
        {
            writeBuf(str);
        }
    }                                   // end writeBufCentered()

    //---------------------------------------------------------------------
    /**
        Right justify string buffer. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>buffer right justified
        </UL>
        @param buf StringBuffer to be right-justified
        @param len length of buffer in which to right-justify string
    **/
    //---------------------------------------------------------------------
    protected void rightJustifyStringBuffer(StringBuffer buf,
                                          int len)
    {                                   // begin rightJustifyStringBuffer()
        int offset = len - buf.length();
        if (offset < 0)
        {
           offset = 0;
        }
        buf = buf.insert(0, SPACES.substring(0, offset));
    }                                   // end rightJustifyStringBuffer()

    //---------------------------------------------------------------------
    /**
        Creates string of spaces to pad string buffer on right. </P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>string of spaces created
        </UL>
        @param maxLength maximum length of field
        @param dataLength length of data in field
        @param numSpaces number of spaces to follow field
    **/
    //---------------------------------------------------------------------
    protected String pad(int maxLength,
                       int dataLength,
                       int numSpaces)
    {                                   // begin pad()
        int offset = maxLength - dataLength + numSpaces;
        if (offset < 0)
        {
            offset = 0;
        }
        return(SPACES.substring(0, offset));
    }                                   // end pad()

    //---------------------------------------------------------------------
    /**
        Main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param args
        <UL>
        <LI>
        <LI>
        </UL>
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // check for argument in main
        if (args.length == 0)
        {                               // begin print to printer
        }                               // end print to printer
        // if this is a test, print to stdout
        else if (args[0].equals("console"))
        {                               // begin print to stdout
        }                               // end print to stdout
        else
        {
            System.out.println("Unknown parameter found.");
        }
    }                                   // end main()

}                                       // End class Receipt

