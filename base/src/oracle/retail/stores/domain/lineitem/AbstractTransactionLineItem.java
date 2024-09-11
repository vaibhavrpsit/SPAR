/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/AbstractTransactionLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:45 mszekely Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 08/05/14 - remove deprecated methods
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    djenni 03/28/09 - Enter a single line comment: creating
 *                      isSalesAssociateModifiedAtLineItem(), which is similar
 *                      to getSalesAssociateModified(), and using it at receipt
 *                      to determine whether to print the SalesAssociate at the
 *                      line item. Jack warned against modifying the existing
 *                      method as it is used for something else.
 *    akandr 10/31/08 - EJ Changes_I18n
 *    akandr 10/30/08 - EJ changes
 *    ddbake 10/28/08 - Update for merge
 *    acadar 10/28/08 - removed old deprecated methods
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================     $Log:

     $Log:
      4    360Commerce 1.3         4/25/2007 10:00:40 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:29 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse
     $
     Revision 1.4  2004/09/23 00:30:54  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:37:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:57:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 12:23:38   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:35:44   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.1   Feb 03 2002 14:01:32   mpm
 * Changes to support inventory movement in order transactions.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.0   Sep 20 2001 16:16:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:38:16   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.lineitem;

import java.util.Locale;

import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.employee.Employee;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

/**
 * This class defines those attributes and methods that are common to all
 * transaction line items. Line item implementations are to extend this class.
 * The intent is to allow transactions to handle different types of line items
 * (like sale of merchandise and sale of gift certificates) together and to
 * provide a more generic interface for line items.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public abstract class AbstractTransactionLineItem
                          implements AbstractTransactionLineItemIfc,
                          DiscountSourceIfc,
                          DiscountTargetIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -2333420357255579754L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * line number
     */
    protected int lineNumber = 1;

    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate = null;

    /**
     * sales-associate-modified flag - transaction level
     */
    protected boolean salesAssociateModifiedFlag = false;

    /**
     * sales-associate-modified flag - line item level
     */
    protected boolean salesAssociateModifiedAtLineItem = false;

    /**
     * Force implementation of clone by subclasses.
     * 
     * @return Object the clone of this object
     */
    public abstract Object clone();

    /**
     * Clone the abstract attributes. This is to be called by the clone of the
     * children with an new instance of this class.
     * 
     * @param AbstractTransactionLineItem new instance
     */
    protected void setCloneAttributes(AbstractTransactionLineItem newItem)
    {
        newItem.lineNumber = lineNumber;
        // clone employee, if valid
        if (salesAssociate != null)
        {
            newItem.salesAssociate = (EmployeeIfc)salesAssociate.clone();
        }
        newItem.salesAssociateModifiedFlag = salesAssociateModifiedFlag;
    }

    /**
     * Force implementation of getLineItemAmount by subclasses
     */
    public abstract CurrencyIfc getLineItemAmount();

    /**
     * Force implementation of getFinancialTotals by subclasses
     * 
     * @param isSale boolean
     */
    public abstract FinancialTotalsIfc getFinancialTotals(boolean isSale);

    /**
     * Force implementation of getFinancialTotals by subclasses
     */
    public abstract FinancialTotalsIfc getFinancialTotals();

    /**
     * Force implementation of getItemID by subclasses
     */
    public abstract String getItemID();

    /**
     * Force implementation of getItemDescription by locale by subclasses
     * 
     * @param locale The locale of the item description being sought
     * @return The localized description
     */
    public abstract String getItemDescription(Locale locale);

    /**
     * Sets sales associate.
     * 
     * @param value Employee sales associate
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
        // set new sales associate, modified flag
        setSalesAssociateModifiedFlag(true);
        setSalesAssociateModifiedAtLineItem(true);
    }

    /**
     * Retrieves sales associate.
     * 
     * @return Employee sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Sets sales associate modified flag.
     * 
     * @param value modified flag
     */
    public void setSalesAssociateModifiedFlag(boolean value)
    {
        salesAssociateModifiedFlag = value;
    }

    /**
     * Retrieves sales associate-modified flag.
     * 
     * @return sales associate-modified flag
     */
    public boolean getSalesAssociateModifiedFlag()
    {
        return (salesAssociateModifiedFlag);
    }

    /**
     * Sets sales associate modified flag for this specific line item.
     * 
     * @param value modified flag
     */
    public void setSalesAssociateModifiedAtLineItem(boolean value)
    {
        salesAssociateModifiedAtLineItem = value;
    }

    /**
     * Retrieves sales associate-modified flag for this specific line item for
     * this specific line item.
     * 
     * @return sales associate-modified flag
     */
    public boolean isSalesAssociateModifiedAtLineItem()
    {
        return (salesAssociateModifiedAtLineItem);
    }

    /**
     * Set line number.
     * 
     * @param num line number
     */
    public void setLineNumber(int num)
    {
        lineNumber = num;
    }

    /**
     * Retrieves line number.
     * 
     * @return line number
     */
    public int getLineNumber()
    {
        return (lineNumber);
    }

    /**
     * Returns true if item is a SaleReturnLineItemIfc and the given item is of
     * item type STOCK, false otherwise.
     * 
     * @return true if sale/return stock item, false otherwise
     */
    public boolean isSaleReturnItemTypeStock()
    {
        boolean returnValue = false;
        if (this instanceof SaleReturnLineItemIfc)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) this;
            if (srli.getPLUItem().getItem().getItemClassification().getItemType() ==
                  ItemClassificationConstantsIfc.TYPE_STOCK)
            {
                returnValue = true;
            }
        }
        return (returnValue);
    }

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     * 
     * @param converter is the conversion utility
     * @exception XMLConversionException if translation fails
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        try
        {
            Element top = converter.getCurrentElement();
            Element[] properties =
                converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("lineNumber".equals(name))
            {
                lineNumber = Integer.parseInt(converter.getElementText(element));
            }
            else if ("salesAssociateModifiedFlag".equals(name))
            {
                salesAssociateModifiedFlag = Boolean.valueOf(converter.getElementText(element)).booleanValue();
            }
            else if ("salesAssociate".equals(name))
            {
                Element[] children = converter.getChildElements(element);
                if (children.length == 1)
                {
                    salesAssociate = (Employee)converter.getObject(children[0]);
                }
            }
        }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        try
        {
            // downcast the input object
            AbstractTransactionLineItem li = (AbstractTransactionLineItem) obj;

            // compare all the attributes of AbstractTransactionLineItem
            if (lineNumber != li.lineNumber)
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(salesAssociate, li.salesAssociate))
            {
                isEqual = false;
            }
            else
            {
                isEqual = true;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }

        return(isEqual);
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult =
            new String("\n\tClass:  AbstractTransactionLineItem (Revision " +
                       getRevisionNumber() +
                       ") @" +
                       hashCode());

        strResult +=     "\n\tLine number:            [" + lineNumber + "]";

        if (salesAssociate == null)
        {
            strResult += "\n\tSales Associate:        [null]";
        }
        else
        {
            strResult += "\n\tSales Associate:\n\t" + salesAssociate.toString();
        }

        strResult +=     "\n\tSales Assoc. Modified:  ["
                         + salesAssociateModifiedFlag + "]";

        // pass back result
        return(strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}