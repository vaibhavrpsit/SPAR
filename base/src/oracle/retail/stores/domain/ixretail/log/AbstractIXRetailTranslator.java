/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/AbstractIXRetailTranslator.java /main/19 2012/05/14 09:43:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/10/12 - Remove TransactionTax for store send item since the
 *                         tax will be specified at ShippingCharge line
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    jkoppolu  08/03/10 - Fixes for BUG#9955722, added 'NULL' checks
 *    mchellap  06/21/10 - BillPay Changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   12/04/08 - fix possible NPE in createTextNodeElement with
 *                         useElement logging
 *    acadar    11/14/08 - forward port for: BUG 7114838
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         6/5/2007 2:04:43 PM    Ranjan X Ojha   Code
 *         Review updates to POSLog for VAT
 *    7    360Commerce 1.6         5/22/2007 9:14:25 AM   Sandy Gu        Check
 *          in PosLog enhancement for VAT
 *    6    360Commerce 1.5         4/25/2007 10:00:49 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/22/2006 11:41:35 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 *
 *   Revision 1.15  2004/08/10 07:17:10  mwright
 *   Merge (3) with top of tree
 *
 *
 *   Revision 1.14  2004/07/29 20:44:29  cdb
 *   @scr 5863 Protect POSLog from null pointer exceptions.
 *
 *   Revision 1.13  2004/07/29 20:35:23  cdb
 *   @scr 5863 Protect POSLog from null pointer exceptions.
 *
 *   Revision 1.12  2004/07/29 20:17:12  cdb
 *   @scr 5863 Protect POSLog from null pointer exceptions.
 *
 *   Revision 1.11  2004/07/09 21:32:01  cdb
 *   @scr 5863 Added null checking for createNodeTextElement(String,String,Element)
 *
 *
 *   Revision 1.10.2.3  2004/08/06 02:28:44  mwright
 *   Added shipping method ID
 *
 *   Revision 1.10.2.2  2004/08/01 22:40:29  mwright
 *   Set discount employee and damage flag
 *
 *   Revision 1.10.2.1  2004/07/29 01:13:34  mwright
 *   Moved setDeliveryDetails() and addCustomerDetails() here, so they can be shared by loggers that only share teh base class
 *
 *
 *   Revision 1.10  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.9.2.1  2004/06/10 10:53:33  mwright
 *   Updated to use schema types in commerce services, providing date and currency translation service to loggers
 *
 *   Revision 1.9  2004/05/06 03:36:06  mwright
 *   POSLog v2.1 merge with top of tree
 *   Effectively reverted to early version of this file, with no v2.1 extensions.
 *   This class is no longer used by v2.1
 *
 *   Revision 1.8  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.7  2004/04/08 16:45:59  cdb
 *   @scr 4204 Removing tabs - again.
 *
 *   Revision 1.6  2004/04/08 06:34:18  smcgrigor
 *   Merge of Kintore POSLog v2.1 code from branch to TopOfTree
 *
 *   Revision 1.5.2.3  2004/03/28 10:34:16  mwright
 *   Added methods to create child elements in the extension namespace instead of the default
 *
 *   Revision 1.5.2.2  2004/03/18 02:20:43  mwright
 *   Implemented use of schema type factory
 *
 *   Revision 1.5.2.1  2004/03/17 04:13:49  mwright
 *   Initial revision for POSLog v2.1
 *
 *   Revision 1.5  2004/02/17 17:57:41  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:51  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 22 2003 09:58:52   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.Currency;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionBillPayment360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionDelivery360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSPaymentHistory360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.SchemaTypesFactoryIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.customer.LogIRSCustomerIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentHistoryIfc;
import oracle.retail.stores.domain.ixretail.financial.v21.LogBillPaymentIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.utility.EYSBoolean;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * This class creates the TLog in IXRetail format for the Retail Transaction
 * View.
 *
 * @version $Revision: /main/19 $
 */
public abstract class AbstractIXRetailTranslator implements AbstractIXRetailTranslatorIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    public static Logger logger = Logger
            .getLogger(oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/19 $";

    /**
     * document object
     */
    protected Document parentDocument = null;

    /**
     * parent element
     */
    protected Element parentElement = null;

    /**
     * element type attribute
     */
    protected String typeName = null;

    /**
     * IXRetail Schema Type Element Factory
     */
    public SchemaTypesFactoryIfc schemaTypesFactory = null;

    /**
     * Gets a cached instance of the ixRetail schema type element factory
     *
     * @return The factory object
     */
    public SchemaTypesFactoryIfc getSchemaTypesFactory()
    {
        if (schemaTypesFactory == null)
        {
            schemaTypesFactory = IXRetailGateway.getFactory().getSchemaTypesFactoryInstance();
        }
        return schemaTypesFactory;
    }

    /**
     * Sets parent document object.
     *
     * @param value parent document object
     */
    public void setParentDocument(Document value)
    {
        parentDocument = value;
    }

    /**
     * Returns parent document object.
     *
     * @return parent document object
     */
    public Document getParentDocument()
    {
        return (parentDocument);
    }

    /**
     * Sets parent element object.
     *
     * @param value parent element object
     */
    public void setParentElement(Element value)
    {
        parentElement = value;
    }

    /**
     * Returns parent element object.
     *
     * @return parent element object
     */
    public Element getParentElement()
    {
        return (parentElement);
    }

    /**
     * Sets type name attribute.
     *
     * @param value type name attribute
     */
    public void setTypeName(String value)
    {
        typeName = value;
    }

    /**
     * Returns type name attribute.
     *
     * @return type name attribute
     */
    public String getTypeName()
    {
        return (typeName);
    }

    /**
     * Sets element type. This is used when an extended type is employed.
     * <P>
     * To use this, the type name attribute must be set.
     * <P>
     *
     * @param el element
     */
    protected void setElementType(Element el) throws XMLConversionException
    {
        if (!Util.isEmpty(getTypeName()))
        {
            el.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG, getTypeName());
        }
    }

    /**
     * Creates a text node element.
     *
     * @param nodeName node name
     * @param nodeText node text
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, String nodeText)
            throws XMLConversionException
    {
        testParentDocumentElement();
        // create element
        Element el = parentDocument.createElement(nodeName);
        // create text node
        if (nodeText == null)
        {
            nodeText = "";
        }
        Text elementText = parentDocument.createTextNode(nodeText);
        // append node to element
        el.appendChild(elementText);
        // append child to parent
        parentElement.appendChild(el);
    }

    /**
     * Creates a text node element and appends it to a specified element.
     *
     * @param nodeName node name
     * @param nodeText node text
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, String nodeText, Element useElement)
            throws XMLConversionException
    {
        testParentDocumentElement();
        // create element
        Element el = parentDocument.createElement(nodeName);
        // create text node
        if (nodeText == null)
        {
            nodeText = "";
        }
        Text elementText = parentDocument.createTextNode(nodeText);
        // append node to element
        el.appendChild(elementText);
        if (useElement == null)
        {
            throw new XMLConversionException("Specified element [" + nodeName + "] is null.");
        }

        useElement.appendChild(el);
    }

    /**
     * Creates a text node element for a boolean object.
     *
     * @param nodeName node name
     * @param value boolean value
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, boolean value) throws XMLConversionException
    {
        createTextNodeElement(nodeName, new Boolean(value).toString());
    }

    /**
     * Creates a text node element for a boolean object and appends it to a
     * specified element.
     *
     * @param nodeName node name
     * @param value boolean value
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, boolean value, Element useElement)
            throws XMLConversionException
    {
        createTextNodeElement(nodeName, new Boolean(value).toString(), useElement);
    }

    /**
     * Creates a text node element for a int object.
     *
     * @param nodeName node name
     * @param value int value
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, int value)
            throws XMLConversionException
    {
        createTextNodeElement(nodeName, Integer.toString(value));
    }

    /**
     * Creates a text node element for a int object and appends it to a
     * specified element.
     *
     * @param nodeName node name
     * @param value int value
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, int value, Element useElement)
            throws XMLConversionException
    {
        createTextNodeElement(nodeName, Integer.toString(value), useElement);
    }

    /**
     * Creates a text node element for a long object.
     *
     * @param nodeName node name
     * @param value long value
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, long value) throws XMLConversionException
    {
        createTextNodeElement(nodeName, Long.toString(value));
    }

    /**
     * Creates a text node element for a long object and appends it to a
     * specified element.
     *
     * @param nodeName node name
     * @param value long value
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, long value, Element useElement)
            throws XMLConversionException
    {
        createTextNodeElement(nodeName, Long.toString(value), useElement);
    }

    /**
     * Creates a text node element for a BigDecimal object.
     *
     * @param nodeName node name
     * @param value BigDecimal value
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, BigDecimal value)
            throws XMLConversionException
    {
        if (value != null)
        {
            createTextNodeElement(nodeName, value.toString());
        }
    }

    /**
     * Creates a text node element for a BigDecimal object and appends it to a
     * specified element.
     *
     * @param nodeName node name
     * @param value BigDecimal value
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, BigDecimal value, Element useElement)
            throws XMLConversionException
    {
        if (value != null)
        {
            createTextNodeElement(nodeName, value.toString(), useElement);
        }
    }

    /**
     * Creates a text node element for a CurrencyIfc object.
     *
     * @param nodeName node name
     * @param value CurrencyIfc value
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, CurrencyIfc value)
            throws XMLConversionException
    {
        if (value != null)
        {
            createTextNodeElement(nodeName, value.toString());
        }
    }

    /**
     * Creates a text node element for a CurrencyIfc object and appends it to a
     * specified element.
     *
     * @param nodeName node name
     * @param value CurrencyIfc value
     * @param useElement element to which node is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTextNodeElement(String nodeName, CurrencyIfc value, Element useElement)
            throws XMLConversionException
    {
        if (value != null)
        {
            createTextNodeElement(nodeName, value.toString(), useElement);
        }
    }

    /**
     * Creates a date text node element from an EYSDate. This method assumes
     * parent document and element are valid. If the date object is null and the
     * bypassNull flag is set, no element is created.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param bypassNull flag indicating null date object should be ignored
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createDateTextNodeElement(String nodeName, EYSDate date, boolean bypassNull)
            throws XMLConversionException
    {
        if (bypassNull == false || date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd"));
        }
    }

    /**
     * Creates a date text node element from an EYSDate. This method assumes
     * parent document and element are valid.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createDateTextNodeElement(String nodeName, EYSDate date)
            throws XMLConversionException
    {
        if (date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd"));
        }
    }

    /**
     * Creates a date text node element from an EYSDate and append to a
     * specified element. This method assumes parent document and element are
     * valid.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param el element to which text node element is to be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createDateTextNodeElement(String nodeName, EYSDate date, Element el)
            throws XMLConversionException
    {
        if (date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd"), el);
        }
    }

    /**
     * Creates a date text node element from an EYSDate and append to a
     * specified element. This method assumes parent document and element are
     * valid.
     * <P>
     * If the date object is null and the bypassNull flag is set, no element is
     * created.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param el element to which text node element is to be appended
     * @param bypassNull flag indicating null date object should be ignored
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createDateTextNodeElement(String nodeName, EYSDate date, Element el, boolean bypassNull)
            throws XMLConversionException
    {
        if (bypassNull == false && date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd"), el);
        }
    }

    /**
     * Creates a timestamp text node element from an EYSDate. This method
     * assumes parent document and element are valid.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTimestampTextNodeElement(String nodeName, EYSDate date)
            throws XMLConversionException
    {
        if (date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd'T'HH:mm:ss"));
        }
    }

    /**
     * Creates a timestamp text node element from an EYSDate. This method
     * assumes parent document and element are valid. If the date object is null
     * and the bypassNull flag is set, no element is created.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param bypassNull flag indicating null date object should be ignored
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTimestampTextNodeElement(String nodeName, EYSDate date, boolean bypassNull)
            throws XMLConversionException
    {
        if (bypassNull == false || date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd'T'HH:mm:ss"));
        }
    }

    /**
     * Creates a timestamp text node element from an EYSDate and appends to the
     * specified element.
     * <P>
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param useElement element to which data should be appended
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTimestampTextNodeElement(String nodeName, EYSDate date, Element el)
            throws XMLConversionException
    {
        if (date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd'T'HH:mm:ss.SSS"), el);
        }
    }

    /**
     * Creates a timestamp text node element from an EYSDate and appends to the
     * specified element.
     * <P>
     * If the date object is null and the bypassNull flag is set, no element is
     * created.
     *
     * @param nodeName node name
     * @param date EYSDate object
     * @param useElement element to which data should be appended
     * @param bypassNull flag indicating null date object should be ignored
     * @exception XMLConversionException thrown if parent document or parent
     *                element are not valid.
     */
    public void createTimestampTextNodeElement(String nodeName, EYSDate date, Element el, boolean bypassNull)
            throws XMLConversionException
    {
        if (bypassNull == false || date != null)
        {
            createTextNodeElement(nodeName, date.toFormattedString("yyyy-MM-dd'T'HH:mm:ss"), el);
        }
    }

    /**
     * Appends additional elements. This method is used to facilitate
     * extensibility.
     *
     * @param object object from which elements are to be derived
     * @param el element to which elements are to be added
     */
    public void appendElements(Object object, Element el) throws XMLConversionException
    {
    }

    /**
     * Tests parent document and element to ensure they exist before attempting
     * to use them.
     *
     * @exception XMLConversionException is thrown if parent document or element
     *                are null
     */
    protected void testParentDocumentElement() throws XMLConversionException
    {
        if (parentDocument == null)
        {
            throw new XMLConversionException("Parent document is null.");
        }
        if (parentElement == null)
        {
            throw new XMLConversionException("Parent element is null.");
        }
    }

    /**
     * Create a free element.
     */
    public Element createElement(String name)
    {
        return parentDocument.createElement(name);
    }

    /**
     * Add a text node to an existing element
     */
    public void appendText(Element el, String text)
    {
        Text elementText = parentDocument.createTextNode(text);
        el.appendChild(elementText);
    }

    /**
     * Create a free element with a text node.
     */
    public Element createElement(String name, String text)
    {
        Element el = createElement(name);
        appendText(el, text);
        return el;
    }

    /**
     * Create a free element with a text node using a currency.
     */
    public Element createElement(String name, CurrencyIfc text)
    {
        return createElement(name, text.toString());
    }

    /**
     * Set a boolean attribute
     *
     * @param element The element to get the attribute
     * @param attribute The name of the attribute
     * @param value The boolean value to set
     */
    public void setAttribute(Element element, String attribute, EYSBoolean value)
    {
        setAttribute(element, attribute, value.getFlag());
    }

    /**
     * Set a string attribute. This method is included to get parameter
     * consistency in all the attribute setting methods.
     * <p>
     *
     * @param element The element to get the attribute
     * @param attribute The name of the attribute
     * @param value The string value to set
     */
    public void setAttribute(Element element, String attribute, String value)
    {
        element.setAttribute(attribute, value);
    }

    /**
     * Set a boolean attribute
     *
     * @param element The element to get the attribute
     * @param attribute The name of the attribute
     * @param value The boolean value to set
     */
    public void setAttribute(Element element, String attribute, boolean value)
    {
        if (value)
        {
            element.setAttribute(attribute, "true");
        }
        else
        {
            element.setAttribute(attribute, "false");
        }
    }

    /**
     * Creates a text node element for an EYSBoolean object and appends it to a
     * specified element.
     *
     * @param nodeName The name of the node to create
     * @param flag the EYSBoolean object
     * @param useElement element to add the new one to
     * @throws XMLConversionException
     */
    public void createTextNodeElement(String nodeName, EYSBoolean value, Element useElement)
            throws XMLConversionException
    {
        if (value != null)
        {
            boolean flag = value.getFlag();
            createTextNodeElement(nodeName, flag, useElement);
        }
    }

    /**
     * Creates a text node element for an EYSBoolean object and appends it to
     * the parent element.
     *
     * @param nodeName The name of the node to create
     * @param flag the EYSBoolean object
     * @throws XMLConversionException
     */
    public void createTextNodeElement(String nodeName, EYSBoolean value) throws XMLConversionException
    {
        if (value != null)
        {
            boolean flag = value.getFlag();
            createTextNodeElement(nodeName, flag, parentElement);
        }
    }

    /**
     * This method is used to convert CurrencyIfc (from domain objects) to
     * Currency objects (for commerce services SchemaTtypes package).
     */
    public Currency currency(CurrencyIfc value)
    {
        String str;
        if (value == null)
        {
            str = "0.00";
        }
        else
        {
            str = value.toString();
        }
        return new Currency(str);
    }

    /**
     * This method is used to convert EYSDate (from domain objects) to java Date
     * objects (for commerce services SchemaTtypes package).
     */
    public Date dateValue(EYSDate value)
    {
        if (value == null)
        {
            return null;
        }
        return value.dateValue();
    }

    protected void addCustomerDetails(RetailTransactionCustomer360Ifc customerElement, CustomerIfc customer)
            throws XMLConversionException
    {
        LogCustomerIfc logCustomer = IXRetailGateway.getFactory().getLogCustomerInstance();
        logCustomer.createElement(customer, null, customerElement);
    }

    /**
     * This method is used to add irs customer details
     *
     * @param customerElement retail transaction irs customer
     * @param irsCustomer irs customer reference
     * @throws XMLConversionException xml conversion exception
     */
    protected void addIRSCustomerDetails(RetailTransactionIRSCustomer360Ifc customerElement, IRSCustomerIfc irsCustomer)
            throws XMLConversionException
    {
        LogIRSCustomerIfc logIrsCustomer = IXRetailGateway.getFactory().getLogIRSCustomerInstance();
        logIrsCustomer.createElement(irsCustomer, null, customerElement);
    }

    /**
     * This method is used to add payment history details
     *
     * @param paymentHistoryElement
     * @param paymentHistoryInfoCollection
     * @param typeCode
     * @throws XMLConversionException
     */
    protected void addPaymentHistoryDetails(RetailTransactionIRSPaymentHistory360Ifc paymentHistoryElement,
            List paymentHistoryInfoCollection,
            String typeCode)
        throws XMLConversionException
    {
        LogPaymentHistoryIfc logPaymentHistory = IXRetailGateway.getFactory().getLogPaymentHistoryInstance();
        logPaymentHistory.createElement(parentDocument, paymentHistoryElement, paymentHistoryInfoCollection, typeCode);
    }

    /**
     * This method is used to add bill payment details
     *
     * @param paymentHistoryElement
     * @param paymentHistoryInfoCollection
     * @throws XMLConversionException
     */
    protected void addBillPaymentDetails(RetailTransactionBillPayment360Ifc paymentElement, BillPayIfc billPayment)
            throws XMLConversionException
    {
        LogBillPaymentIfc logBillPayment = IXRetailGateway.getFactory().getLogBillPaymentInstance();
        logBillPayment.createElement(billPayment, parentDocument, paymentElement);
    }

    protected RetailTransactionDelivery360Ifc setDeliveryDetails(ShippingMethodIfc method,
            CustomerIfc customer,
            String sendLabel)
        throws XMLConversionException
    {
        RetailTransactionDelivery360Ifc deliveryElement = null;
        // write out shipping type in default locale in POSLOG
    String shippingType = null;
    if (method != null)
    {
      // write out shipping type in default locale in POSLOG
      shippingType = method.getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }
        if (shippingType != null)
        {
            // write out shipping type in default locale in POSLOG
            String shippingCarrier = method.getShippingCarrier(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
            String shippingInstructions = method.getShippingInstructions();
            CurrencyIfc shippingCharge = method.getCalculatedShippingCharge();
            // CurrencyIfc shippingCharge =
            // retailTrans.getTransactionTotals().getCalculatedShippingCharge();

            deliveryElement = getSchemaTypesFactory().getRetailTransactionDeliveryInstance();
            RetailTransactionCustomer360Ifc customerElement = getSchemaTypesFactory()
                    .getRetailTransactionCustomerInstance();

            addCustomerDetails(customerElement, customer); // uses LogCustomer object...sets address using Log as well, eventually same as in v1.0
            deliveryElement.setCustomer(customerElement);

            deliveryElement.setNotes(shippingInstructions);
            deliveryElement.setAmountToCollect(currency(shippingCharge));
            deliveryElement.setMethod(shippingType);
            deliveryElement.setMethodID(Integer.toString(method.getShippingMethodID()));

            deliveryElement.setSendLabelCount(sendLabel); // This forms the key (with the transaction key) of the shipping info
            deliveryElement.setCourier(shippingCarrier);
            deliveryElement.setShippingChargeFlatRate(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(method.getFlatRate())));
            deliveryElement.setShippingChargeBase(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(method.getBaseShippingCharge())));
            deliveryElement.setShippingChargeByWeight(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(method.getShippingChargeRateByWeight())));

        }
        return deliveryElement;
    }
}
