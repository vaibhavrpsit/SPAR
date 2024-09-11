/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/KitComponentLineItem.java /main/20 2011/12/05 12:16:30 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   cgreene 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *   acadar  06/10/10 - use default locale for currency display
 *   acadar  06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *   cgreene 05/26/10 - convert to oracle packaging
 *   cgreene 04/28/10 - updating deprecated names
 *   acadar  04/09/10 - optimize calls to LocaleMAp
 *   acadar  04/05/10 - use default locale for currency and date/time display
 *   abondal 01/03/10 - update header date
 *   vikini  02/20/09 - Check for Classcast
 *   deghosh 02/12/09 - Cleaning the deprecated method toJournalString()
 *   vchenge 11/04/08 - Changed deprecated-method calls to the new-method calls
 *                      by passing the Locale.
 *   sswamyg 11/04/08 - Modified to use toJournalString(Locale)
 *   akandru 10/31/08 - EJ Changes_I18n
 *   akandru 10/30/08 - EJ changes
 *   akandru 10/23/08 - new helper class is used
 *   akandru 10/21/08 - new method added to take the client's journal locale.
 *   akandru 10/21/08 -
 *   ddbaker 10/17/08 - Domain portion of I18N ItemIfc description updates.
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
    Line item for a kit component.  <P>
    @version $Revision: /main/20 $
**/
//--------------------------------------------------------------------------
public class KitComponentLineItem extends SaleReturnLineItem
                                  implements KitComponentLineItemIfc
{
    private static final long serialVersionUID = 3206493624181275732L;
    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";
    //---------------------------------------------------------------------
    /**
        Default KitComponentLineItem constructor.
    **/
    //---------------------------------------------------------------------
    public KitComponentLineItem()
    {
        setPLUItem(DomainGateway.getFactory().getKitComponentInstance());
    }
    //---------------------------------------------------------------------
    /**
        Constructs a KitComponentLineItem object, setting item, tax rate, sales
        associate and registry attributes. <P>
        @param item PLU item
        @param tax ItemTax object
        @param pSalesAssociate default sales associate
        @param pRegistry default registry
        @deprecated as of release 6.10 replaced by initialize() method
    **/
    //---------------------------------------------------------------------
    public KitComponentLineItem(KitComponentIfc item,
                                ItemTaxIfc tax,
                                EmployeeIfc employee,
                                RegistryIDIfc registry)
    {
        initialize(item, BigDecimal.ONE,tax, employee, registry, null);
    }
    //---------------------------------------------------------------------
    /**
        Initializes a KitComponentLineItem object, setting item, tax rate, sales
        associate and registry attributes. <P>
        @param item PLU item
        @param tax ItemTax object
        @param pSalesAssociate default sales associate
        @param pRegistry default registry
    **/
    //---------------------------------------------------------------------
    public void initialize(KitComponentIfc item,
                                ItemTaxIfc tax,
                                EmployeeIfc employee,
                                RegistryIDIfc registry)
    {
        super.initialize(item, BigDecimal.ONE, tax, employee, registry, null);
    }

    //---------------------------------------------------------------------
    /**
        Clones a KitComponentLineItem object <P>
        @return instance of KitComponentLineItem
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        KitComponentLineItem item = new KitComponentLineItem();
        setCloneAttributes(item);
        return item;
    }
    //----------------------------------------------------------------------------
    /**
        Returns true if this item is a kit component item, false otherwise.
        A KitComponentLineItem must have a valid itemKitID value to be considered
        a KitComponent, else it is treated like a regular SaleReturnLineItem.
        @return boolean
    **/
    //----------------------------------------------------------------------------
    public boolean isKitComponent()
    {
        //check for valid kitID
        String id = getItemKitID();
        return ( (id != null) && (!id.equals("")) );
    }
    //----------------------------------------------------------------------------
    /**
        Retrieves the kit header ID for this kit component. <P>
        @return String ID
    **/
    //----------------------------------------------------------------------------
    public String getItemKitID()
    {
      //Certain conditions throw class cast expn, so checking instance
      if(pluItem instanceof KitComponentIfc)
      {
        return pluItem == null ?
            null : ((KitComponentIfc)pluItem).getItemKitID();
      }
      else
      {
        return null;
      }

    }

    /**
     * Sets the kit ID for this kit component. This is the item id of the kit
     * header item.
     * 
     * @param String ID
     */
    public void setItemKitID(String id)
    {
        if (pluItem != null)
        {
            ((KitComponentIfc)pluItem).setItemKitID(id);
        }
        else
        {
            throw new NullPointerException("Null pluItem reference in KitComponentLineItem.java");
        }
    }

    /**
     * Returns journal string
     * 
     * @return journal string
     * @deprecated Changed in 6.0 due to changes in manual discount journaling
     */
    public String toComponentJournalString(int discountType)
    {
        return toComponentJournalString();
    }

    /**
     * Returns journal string
     * 
     * @return journal string
     * @deprecated new method added to take the client's journal locale.
     */
    public String toComponentJournalString()
    {
        return toComponentJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Returns journal string
     * 
     * @param journalLocale locale received from the client
     * @return journal string
     */
    public String toComponentJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        ItemPriceIfc ip = getItemPrice();

        int taxMode = ip.getItemTax().getTaxMode();
        int taxScope = ip.getItemTax().getTaxScope();

        // Item number
        CurrencyIfc itemPrice = ip.getExtendedSellingPrice();
        int signum =  itemPrice.getDecimalValue().signum();



        String priceString = itemPrice.toGroupFormattedString();
        strResult.append(Util.EOL)
                 .append(pluItem.getItemID())
                 .append(Util.EOL);

        BigDecimal quantity = BigDecimalConstants.ONE_AMOUNT;
        quantity = quantity.setScale(2);

        // If it's a NOT a UoM item, display Qty as an integer
        if (pluItem.getUnitOfMeasure() == null  ||
            pluItem.getUnitOfMeasure().getUnitID().compareTo(UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS) == 0)
        {
            if( quantity.intValue() == quantity.doubleValue() )
            {
                quantity = quantity.setScale(0);
            }
        }


        String quantityString = quantity.toString();
        if(quantityString.startsWith("-"))
        {
            quantityString = quantityString.replace('-','(');
            quantityString = quantityString + ")";
            if (signum >= 0 )
            {
                priceString = itemPrice.negate().toGroupFormattedString();
            }
        }

        //price
        strResult.append(Util.EOL);
        strResult.append(priceString);

        // Tax Mode
        String taxFlag = "T";
        if (taxMode == TaxIfc.TAX_MODE_STANDARD
            && pluItem.getTaxable() == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
        }
        strResult.append(" ").append(taxFlag);

        // Item description
        strResult.append(Util.EOL)
                 .append(pluItem.getDescription(journalLocale));

        Object[] dataArgs = new Object[]{quantityString, ip.getSellingPrice()};
        // Item Quantity and Unit Price
        strResult.append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        		JournalConstantsIfc.QUANTITY_LABEL, dataArgs,
        		journalLocale));

        // Item serial number
        if (itemSerial != null)
        {
        	Object[] dataArgs2 = new Object[]{itemSerial};
            strResult.append(Util.EOL)
                     .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                     		JournalConstantsIfc.SERIAL_NUMBER_LABEL, dataArgs2,
                    		journalLocale)).append(Util.EOL);
        }

        // if the PLUItem is a GiftCardPLUItem journal gift card information
        if (pluItem instanceof GiftCardPLUItemIfc)
        {
            strResult.append(((GiftCardPLUItemIfc)pluItem).getGiftCard().toJournalString(journalLocale));
        }

        // journal non-standard tax
        if (taxMode != TaxIfc.TAX_MODE_STANDARD)
        {
            if (taxScope == TaxIfc.TAX_SCOPE_ITEM)  // tax overirde is at the item level
            {
                strResult.append(ip.getItemTax().toJournalString(journalLocale));
            }
        }
        return strResult.toString();
    }

}//end class KitComponentLineItem


