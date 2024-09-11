/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/KitHeaderLineItem.java /main/22 2012/07/02 10:12:51 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreen    08/05/14 - remove deprecated methods
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *    vchengeg  02/27/09 - To format EJournal entries
 *    deghosh   02/12/09 - Cleaning the deprecated method toJournalString()
 *    vchengeg  11/04/08 - Changed deprecated-method calls to the new-method
 *                         calls by passing the Locale.
 *    akandru   10/31/08 - EJ Changes_I18n
 *    akandru   10/30/08 - EJ changes
 *    akandru   10/23/08 - new helper class is used.
 *    akandru   10/21/08 - new method added to take the client's journal
 *                         locale.
 *    akandru   10/21/08 - new method added to take the client's journal
 *                         locale.
 *    akandru   10/21/08 -
 *    ddbaker   10/17/08 - Domain portion of I18N ItemIfc description updates.
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      9    360Commerce 1.8         8/2/2007 6:31:26 AM    Naveen Ganesh
           Corrected the message Sales Assoc. for CR 27977
      8    360Commerce 1.7         6/19/2007 4:18:52 PM   Alan N. Sinton  CR
           27232 - Enabled line item discounts for kit headers.
      7    360Commerce 1.6         5/21/2007 9:17:03 AM   Anda D. Cadar   Ej
           changes and cleanup
      6    360Commerce 1.5         5/8/2007 11:32:34 AM   Anda D. Cadar
           currency changes for I18N
      5    360Commerce 1.4         4/25/2007 10:00:40 AM  Anda D. Cadar   I18N
           merge
      4    360Commerce 1.3         1/22/2006 11:41:40 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:00 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:14 PM  Robert Pearse
     $
     Revision 1.6  2004/07/29 18:29:39  mweis
     @scr 6040 Ensure Kit headers have a "journal delete string" for the EJ.

     Revision 1.5  2004/07/29 17:03:23  mweis
     @scr 6040 EJ not showing kit header, kit components, nor serialized items.

     Revision 1.4  2004/06/10 20:25:37  jdeleau
     @scr 2775 Handle kits in sent items

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Dec 10 2003 10:25:36   rrn
 * In toJournalString method, change quantity line to single digit.
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   Oct 22 2003 09:59:42   baa
 * refactoring- get salereturnlineitem from factory
 *
 *    Rev 1.0.1.0   Oct 22 2003 09:54:20   baa
 * refactoring - get saleReturnlineitem instance from factory
 *
 *    Rev 1.0   Aug 29 2003 15:38:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 26 2003 12:01:00   sfl
 * Log the kit component items in the EJ.
 * Resolution for POS SCR-3253: Layway E. Journal is not Correct for Layayway Delete.
 *
 *    Rev 1.0   Jun 03 2002 16:58:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:04:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:24:14   msg
 * Initial revision.
 *
 *    Rev 1.20   13 Mar 2002 15:21:52   pjf
 * Enhancements for kits.
 * Resolution for POS SCR-1554: After returning one item from a kit you cannot retrieve the remaing kit items
 * Resolution for POS SCR-1555: System stuck on Invalid Quantity Error after attempting a second return.
 *
 *    Rev 1.19   11 Mar 2002 09:21:38   pjf
 * Journal cleanup.
 * Resolution for POS SCR-1042: Kit items that doubled (see SRC1039) EJournal has 4 more items displaying
 * Resolution for POS SCR-1257: Layaway Delete EJ entry has kit items listed twice
 *
 *    Rev 1.18   10 Mar 2002 11:49:48   pjf
 * Maintain kit inventory at header level.
 * Resolution for POS SCR-1444: Selling then returning a kit does not upadate the inventory count
 * Resolution for POS SCR-1503: When all kit items are returned and attempt to retrieve trans no error displays
 *
 *    Rev 1.17   Feb 23 2002 10:31:26   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF.
 * Resolution for Domain SCR-35: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.16   22 Feb 2002 12:06:06   pjf
 * 2nd revision of getTaxStatusDescriptor()
 * Resolution for POS SCR-1288: Kit header and componets not displaying transaction tax modification designation 'R'
 *
 *    Rev 1.15   18 Feb 2002 17:06:36   pjf
 * Modified getTaxStatusDescriptor()
 * Resolution for POS SCR-1288: Kit header and componets not displaying transaction tax modification designation 'R'
 *
 *    Rev 1.14   12 Feb 2002 12:18:44   pjf
 * Fun with kit cloning.
 * Resolution for POS SCR-1188: Tax indicator incorrect on receipt for Tax Exmept trans with 2 kits, added TE status after items
 *
 *    Rev 1.13   06 Feb 2002 18:49:12   pjf
 * Associate multiple identical kit headers with their component items.
 * Resolution for POS SCR-1039: Entered kit, susp, retrv & added same kit, susp & retrv each kit doubled items
 * Resolution for POS SCR-1042: Kit items that doubled (see SRC1039) EJournal has 4 more items displaying
 * Resolution for POS SCR-1044: Void kit trans that dbld items from susp & retrv. dbld again + 8 more on receipt
 *
 *    Rev 1.11   01 Feb 2002 10:36:36   pjf
 * Overrode isSerialized() to test component item attributes.
 * Resolution for POS SCR-991: Adding non-serialized item through item inquiry asks for serial number.
 *
 *    Rev 1.10   31 Jan 2002 09:39:50   pjf
 * Modified getTaxStatusDescriptor() to check for tax exempt mode.
 * Resolution for POS SCR-968: Kit header tax status indicator does not reflect Tax Exempt status
 *
 *    Rev 1.9   24 Jan 2002 17:04:20   pjf
 * Refactoring, Updates to correct defects introduced by UI conversion.
 * Resolution for POS SCR-832: Kit quantity is displaying +1 in the total region,  quantity
 * Resolution for POS SCR-837: Kit discount amount not showing on sell item screen.
 * Resolution for POS SCR-838: Kit Header tax flag displayed incorrectly on sell item screen.
 *
 *    Rev 1.8   21 Nov 2001 14:28:02   pjf
 * Updates for jouraling kit items.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.7   13 Nov 2001 07:03:20   mpm
 * Installed support for ItemContainerProxy.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.6   06 Nov 2001 13:32:44   pjf
 * Changes to recreate kit state from database for suspend and retrieve, returns.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.5   30 Oct 2001 11:45:10   pjf
 * Added getKitDiscountTotal().
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.4   27 Oct 2001 11:15:54   pjf
 * Overrode modifyItemRegistry() and modifyItemSalesAssociate() in order to propogate these actions on the kit header to its components.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.3   26 Oct 2001 09:29:08   pjf
 * Added clone support, new ifcs.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.2   22 Oct 2001 14:11:44   pjf
 * Added pricing rules for kit items.
 * Removed pricing rules that are never applied (3010,5060) due to competing rules that give a better deal (3040,5070).
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.1   20 Oct 2001 11:18:24   pjf
 * Kit updates.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   19 Oct 2001 14:24:48   pjf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
 * Line item for kit header.
 * <P>
 * 
 * @version $Revision: /main/22 $
 **/
// --------------------------------------------------------------------------
public class KitHeaderLineItem extends SaleReturnLineItem implements KitHeaderLineItemIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = -1004998185731855419L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * collection for the child line items of this kit header this will contain
     * KitComponentLineItem objects
     */
    protected ArrayList<KitComponentLineItemIfc> components = new ArrayList<KitComponentLineItemIfc>();

    /**
     * Default KitHeaderLineItem constructor.
     * <P>
     */
    public KitHeaderLineItem()
    {
        setKitHeaderReference(hashCode());
    }

    /**
     * Returns an iterator over the KitComponentLineItems held by this header
     * line item.
     * 
     * @return Iterator
     */
    public Iterator<KitComponentLineItemIfc> getKitComponentLineItems()
    {
        return components.iterator();
    }

    /**
     * Returns an array containing the KitComponentLineItems held by this header
     * line item.
     * 
     * @return KitComponentLineItem[]
     */
    public KitComponentLineItemIfc[] getKitComponentLineItemArray()
    {
        KitComponentLineItemIfc[] lineItemArray = new KitComponentLineItemIfc[components.size()];
        components.toArray(lineItemArray);
        return lineItemArray;
    }

    /**
     * Returns true to indicate this item is a kit header item.
     * 
     * @return boolean true
     */
    public boolean isKitHeader()
    {
        return true;
    }

    /**
     * Returns true to indicate this item has associated component items.
     * 
     * @return boolean
     */
    public boolean hasKitComponentLineItems()
    {
        return !components.isEmpty();
    }

    /**
     * Returns true if any of this kit header item's components are serialized.
     * 
     * @return boolean true
     */
    public boolean isSerializedItem()
    {
        boolean value = false;
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            if ((i.next()).isSerializedItem())
            {
                value = true;
                break;
            }
        }
        return value;
    }

    /**
     * Adds a KitComponentLineItem to this header and sets the component items
     * reference to the header.
     * 
     * @param item to add
     */
    public void addKitComponentLineItem(KitComponentLineItemIfc item)
    {
        item.setKitHeaderReference(getKitHeaderReference());
        components.add(item);
    }

    /**
     * Associates a KitComponentLineItem with this header.
     * 
     * @param Iterator - an iterator over a collection of SaleReturnLineItemIfcs
     * @throws ClassCastException if the collection contains a
     *             non-SaleReturnLineItemIfc type
     */
    public void associateKitComponentLineItems(Iterator saleReturnLineItems)
    {
        KitComponentLineItemIfc component;

        components.clear();
        while (saleReturnLineItems.hasNext())
        {
            Object o = saleReturnLineItems.next();
            if (((SaleReturnLineItemIfc)o).isKitComponent())
            {
                component = (KitComponentLineItemIfc)o;
                String kitID = component.getItemKitID();
                if (kitID != null &&
                    kitID.equals(getItemID()) &&
                    kitHeaderReference == component.getKitHeaderReference())
                {
                    components.add(component);
                }
            }
        }
    }

    /**
     * Removes the kit component line items held by this kit header line item
     * from the transaction.
     * 
     * @return boolean true
     */
    public void removeKitComponentLineItems(Iterator transactionLineItems)
    {
        KitComponentLineItemIfc temp = null;

        for (Iterator<KitComponentLineItemIfc> p = getKitComponentLineItems(); p.hasNext();)
        {
            temp = p.next();

            while(transactionLineItems.hasNext())
            {
                if (temp == transactionLineItems.next())
                {
                    transactionLineItems.remove();
                    break;
                }
            }
        }
    }

    /**
     * Tests whether all components in this header's collection are contained in
     * the collection passed in as an argument.
     * 
     * @return boolean true if all component items are in the collection
     */
    public boolean areAllComponentsIn(Collection lineItems)
    {
        return (lineItems == null) ? false : componentsContainsAll(lineItems);

    }

    /**
     * Tests whether all the Objects in the Collection c are contained in the
     * components Collecton maintained by this kit header.
     * 
     * @return boolean true if all items are in the components collection
     */
    public boolean componentsContainsAll(Collection c)
    {
        boolean     value = true;
        Iterator    i     = c.iterator();

        if (c.size() >= components.size())
        {
            while (i.hasNext())
            {
                if(!componentsContains(i.next()))
                {
                    value = false;
                    break;
                }
            }
        }
        else
        {
            value = false;
        }

        return value;
    }

    /**
     * Tests whether an Object is in the components Collection maintained by
     * this kit header.
     * 
     * @return boolean true if an object is in the components collection
     */
    public boolean componentsContains(Object o)
    {
        boolean     value = false;
        Iterator<KitComponentLineItemIfc> i = components.iterator();
        if (o == null)
        {
            while (i.hasNext())
            {
                if (i.next() == null)
                {
                    value = true;
                    break;
                }
            }
        }
        else
        {
            while (i.hasNext())
            {
                if (o == i.next())
                {
                    value = true;
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Returns the original selling price for the kit. This is the sum of the
     * kit component's selling prices.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getSellingPrice()
    {
        CurrencyIfc price = DomainGateway.getBaseCurrencyInstance();
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            price = price.add(
                ((KitComponentLineItem)i.next()).getSellingPrice());
        }
        return price;
    }

    /**
     * Overrides super's getExtendedSellingPrice() method to add up all of the
     * components getExtendedSellingPrice().
     * 
     * @return
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#getExtendedSellingPrice()
     */
    public CurrencyIfc getExtendedSellingPrice()
    {
        CurrencyIfc price = DomainGateway.getBaseCurrencyInstance();
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            price = price.add(
                ((KitComponentLineItem)i.next()).getExtendedSellingPrice());
        }
        if(this.isReturnLineItem())
        {
            price = price.negate();
        }
        return price;
    }

    /**
     * Overrides super's getItemDiscountTotal() method to return the sum of
     * discounts on the component items within this kit.
     *
     * @return
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#getItemDiscountTotal()
     */
    public CurrencyIfc getItemDiscountTotal()
    {
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            discountAmount = discountAmount.add(
                ((KitComponentLineItem)i.next()).getItemDiscountTotal());
        }
        return discountAmount;
    }

    /**
     * Returns the extended discounted selling price for the kit. This is the
     * sum of the kit component's extended discounted selling prices. ((Selling
     * price * item quantity) - item discount total)
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getExtendedDiscountedSellingPrice()
    {
        CurrencyIfc price = DomainGateway.getBaseCurrencyInstance();
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            price = price.add(((KitComponentLineItem)i.next()).getExtendedDiscountedSellingPrice());
        }
        return price;
    }

    /**
     * Modifies item registry and sets modified flag.
     * 
     * @param newGift new registry
     */
    public void modifyItemRegistry(RegistryIDIfc newGift)
    {
        // set new registry, modified flag to true
        super.modifyItemRegistry(newGift, true);
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            ((KitComponentLineItemIfc)i.next()).modifyItemRegistry(newGift, true);
        }
    }

    /**
     * Modifies sales associate and sets modified flag.
     * 
     * @param EmployeeIfc new associate
     */
    public void modifyItemSalesAssociate(EmployeeIfc newEmployee, boolean value)
    {
        // set new employee, modified flag to true
        super.setSalesAssociate(newEmployee);
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext(); )
        {
            ((KitComponentLineItemIfc)i.next()).setSalesAssociate(newEmployee);
        }
    }

    /**
     * Clones a KitHeaderLineItem object
     * 
     * @return instance of KitHeaderLineItem
     * @see ItemContainerProxyIfc.setCloneAttributes(),
     *      associateKitComponentsWithHeaders()
     */
    public Object clone()
    {
        KitHeaderLineItem item = new KitHeaderLineItem();
        this.setCloneAttributes(item);
        return item;
    }

    /**
     * Returns the sum of the kit component discount totals for rendering the
     * total discount for this kit header. The getItemDiscountTotal() method in
     * SaleReturnLineItem is not overridden for kit headers. This is to prevent
     * the total discounts for the kit from being counted twice on the
     * transaction.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getKitDiscountTotal()
    {
        CurrencyIfc price = DomainGateway.getBaseCurrencyInstance();
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            price = price.add(
                ((KitComponentLineItem)i.next()).getItemDiscountTotal());
        }
        return price;
    }

    /**
     * Returns journal string
     * 
     * @param journalLocale locale received from the client
     * @return journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();

        // Item number
        CurrencyIfc itemPrice = getSellingPrice();

        String priceString = "";

        if (!isReturnLineItem())
        {   //journal price for sale items only
          priceString = itemPrice.toGroupFormattedString();
        }

        Object[] dataArgs = new Object[]{pluItem.getItemID(),priceString};
        strResult.append(Util.EOL)
                 .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                 		JournalConstantsIfc.KIT_HEADER_LABEL, dataArgs,
                		journalLocale));

        BigDecimal quantity = BigDecimalConstants.ONE_AMOUNT;
        quantity = quantity.setScale(2);

        if( quantity.intValue() == quantity.doubleValue() )
        {
            quantity = quantity.setScale(0);
        }

        String quantityString = quantity.toString();

        // Item description
        strResult.append(Util.EOL).append(pluItem.getDescription(journalLocale));

        // Item Quantity and Unit Price
        Object dataArgs2[] = new Object[]{quantityString, getSellingPrice()};
        strResult.append(Util.EOL)
                 .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                 		JournalConstantsIfc.QUANTITY_LABEL, dataArgs2,
                		journalLocale));

        if (getRegistry() != null)
        {
        	dataArgs[0] = getRegistry().getID();
            strResult.append(Util.EOL)
                     .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                      		JournalConstantsIfc.GIFT_REG_LABEL, dataArgs,
                    		journalLocale));
        }

        // if sales associate modified, write it
        if (getSalesAssociateModifiedFlag() &&
            getSalesAssociate() != null)
        {
        	dataArgs[0] = getSalesAssociate().getEmployeeID();
            strResult.append(Util.EOL)
                     .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                      		JournalConstantsIfc.SALES_ASSOC_LABEL, dataArgs,
                    		journalLocale));
        }
        if (!isReturnLineItem())
        {  //components are journaled individually on returns
            strResult.append(getComponentsToJournalString(journalLocale));
        }
        // pass back result
        return (strResult.toString());
    }

    /**
     * Overriding to make this a deep clone of the component line items.
     * 
     * @param newSrli
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#setCloneAttributes(oracle.retail.stores.domain.lineitem.SaleReturnLineItem)
     */
    protected void setCloneAttributes(SaleReturnLineItem newSrli)
    {
        super.setCloneAttributes(newSrli);
        KitComponentLineItemIfc[] componentItems = getKitComponentLineItemArray();
        for(int i = 0; i < componentItems.length; i++)
        {
            ((KitHeaderLineItemIfc)newSrli).
                addKitComponentLineItem((KitComponentLineItemIfc)componentItems[i].clone());
        }
    }

    /**
     * Returns journal string
     * 
     * @param journalLocale locale received from the client
     * @return journal string
     */
    public String getComponentsToJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();

        strResult.append(Util.EOL)
                 .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                   		JournalConstantsIfc.COMPONENTS_LABEL, null,
                		journalLocale));
        for (Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems(); i.hasNext();)
        {
            strResult.append(((KitComponentLineItemIfc)i.next()).toComponentJournalString(journalLocale));
        }
        // pass back result
        return(strResult.toString());
    }


    //--------------------------------------------------------------------------
    /**
     *  Determines the tax status descriptor flag.
     *  @return a string value to describe taxability status
     */
    //--------------------------------------------------------------------------
    public String getTaxStatusDescriptor()
    {
        Iterator<KitComponentLineItemIfc> i = getKitComponentLineItems();
        String      taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];

        if (getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            taxFlag = super.getTaxStatusDescriptor();
        }
        else
        {
            SaleReturnLineItemIfc item = null;
            while (i.hasNext())
            {
                item = (SaleReturnLineItemIfc)i.next();
                if (item.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_RATE ||
                    item.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT)
                {
                    //flag is the same for both override types ('R')
                    taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_OVERRIDE_AMOUNT];
                    break;
                }
                else if (item.getTaxable())
                {
                    taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_STANDARD];
                }
            }
        }
        return taxFlag;
    }

    /**
     *  Return a set of taxGroupIds for all components
     *  within the kit.
     *
     *  @since 7.0
     *  @return Collection of Integer taxGroupIds.
     *  @see oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc#getTaxGroupIds()
     */
    public Collection<Integer> getTaxGroupIds()
    {
        HashSet<Integer> taxGroupIds = new HashSet<Integer>();
        KitComponentLineItemIfc[] kitComponents = getKitComponentLineItemArray();
        for(int i=0; i<kitComponents.length; i++)
        {
            taxGroupIds.add(Integer.valueOf(kitComponents[i].getTaxGroupID()));
        }
        return taxGroupIds;
    }

    /**
     *  Given a TaxRuleVO, set the sendTaxRules for each
     *  component of the kit.
     *
     *  @since 7.0
     *  @param taxRulesVO Tax rule value object containing tax
     *  rules for each taxGroupID.
     *  @see oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc#setSendTaxRules(oracle.retail.stores.domain.tax.TaxRulesVO)
     */
    public void setSendTaxRules(TaxRulesVO taxRulesVO)
    {
        KitComponentLineItemIfc[] kitComponents = getKitComponentLineItemArray();
        for(int i=0; i<kitComponents.length; i++)
        {
            TaxRuleIfc[] taxRules = taxRulesVO.getTaxRules(kitComponents[i].getTaxGroupID());
            kitComponents[i].getItemPrice().getItemTax().setSendTaxRules(taxRules);
        }
    }

    /**
     *  Given a tax rule, set it as the sendTaxRule for
     *  every item in the component.  This is only done
     *  when the default tax rule must be used because
     *  the others are inaccessible.
     *
     *  @since 7.0
     *  @param taxRule Taxrule to set for all components.
     *  @see oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc#setSendTaxRule(oracle.retail.stores.domain.tax.TaxRuleIfc)
     */
    public void setSendTaxRule(TaxRuleIfc taxRule)
    {
        KitComponentLineItemIfc[] kitComponents = getKitComponentLineItemArray();
        for(int i=0; i<kitComponents.length; i++)
        {
            kitComponents[i].getItemPrice().getItemTax().setSendTaxRules(new TaxRuleIfc[] {taxRule});
        }
    }

    /**
     * Returns journal string when removing an item.
     * 
     * @param journalLocale locale received from the client
     * @return journal string when removing an item
     */
    public String toJournalDeleteString(Locale journalLocale)
    {
        // What we will ultimately return
        StringBuilder strResult = new StringBuilder();

        // Negate the price
        CurrencyIfc itemPrice = getSellingPrice();
        String priceString = "";
        if (!isReturnLineItem())
        {   // Journal price for sale items only
          priceString = itemPrice.negate().toGroupFormattedString();
        }

        Object[] dataArgs = new Object[]{pluItem.getItemID(),priceString};
        // First EJ line
        strResult.append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          		JournalConstantsIfc.KIT_HEADER_LABEL, dataArgs,
        		journalLocale)).append(Util.EOL);

        // Item description
        strResult.append(Util.EOL)
        .append(pluItem.getDescription(journalLocale));

        // Negate the quantity
        BigDecimal quantity = BigDecimalConstants.ONE_AMOUNT;
        quantity = quantity.setScale(2);
        if( quantity.intValue() == quantity.doubleValue() )
        {
            quantity = quantity.setScale(0);
        }
        String quantityString = quantity.toString();
        quantityString = negate(quantityString);

        // Item Quantity and Unit Price
        Object[] dataArgs2 = new Object[]{quantityString, getSellingPrice()};
        strResult.append(Util.EOL)
        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          		JournalConstantsIfc.QUANTITY_LABEL, dataArgs2,
        		journalLocale)).append(quantityString);

        // Note: Possibly need to add the negated components
        return strResult.toString();
    }


    /**
     * Adds the discount to the component items.
     * @param discount
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#addItemDiscount(oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc)
     */
    public void addItemDiscount(ItemDiscountStrategyIfc discount)
    {
        CurrencyIfc discountAmount = discount.getDiscountAmount();
        CurrencyIfc discountRemaining = discountAmount;
        CurrencyIfc extSellPrice = this.getExtendedSellingPrice();
        KitComponentLineItemIfc[] items = getKitComponentLineItemArray();
        if(items != null && items.length > 0)
        {
            for(int i = 0; i < items.length; i++)
            {
                if(discount.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    ItemDiscountStrategyIfc discountClone = (ItemDiscountStrategyIfc)discount.clone();
                    items[i].addItemDiscount(discountClone);
                }
                else if(discount.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                {
                    // prorate the amount discount over the components
                    ItemDiscountStrategyIfc discountClone = (ItemDiscountStrategyIfc)discount.clone();
                    CurrencyIfc proratedDiscountAmount = null;
                    if(i < items.length - 1)
                    {
                        proratedDiscountAmount =
                            items[i].getExtendedSellingPrice().divide(extSellPrice).multiply(discountAmount);
                        discountRemaining = discountRemaining.subtract(proratedDiscountAmount);
                    }
                    // to avoid rounding issues, just use remaining discount amount on the last item
                    else
                    {
                        proratedDiscountAmount = discountRemaining;
                    }
                    discountClone.setDiscountAmount(proratedDiscountAmount);
                    items[i].addItemDiscount(discountClone);
                }
            }
        }
    }

    /**
     * Overriden to clear discounts on kit component items.
     * @param basis
     * @param damage
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#clearItemDiscountsByAmount(int, boolean)
     */
    public void clearItemDiscountsByAmount(int basis, boolean damage)
    {
        KitComponentLineItemIfc[] items = getKitComponentLineItemArray();
        if(items != null && items.length > 0)
        {
            for(int i = 0; i < items.length; i++)
            {
                items[i].getItemPrice().clearItemDiscountsByAmount(basis, damage);
            }
        }
    }

    /**
     * Overriden to clear discounts on kit component items.
     * 
     * @param typeCode
     * @param basis
     * @param damage
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#clearItemDiscountsByAmount(int,
     *      int, boolean)
     */
    public void clearItemDiscountsByAmount(int typeCode, int basis, boolean damage)
    {
        KitComponentLineItemIfc[] items = getKitComponentLineItemArray();
        if(items != null && items.length > 0)
        {
            for(int i = 0; i < items.length; i++)
            {
                items[i].getItemPrice().clearItemDiscountsByAmount(typeCode, basis, damage);
            }
        }
    }

    /**
     * Overriden to clear discounts on kit component items.
     * 
     * @param basis
     * @param damage
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#clearItemDiscountsByPercentage(int,
     *      boolean)
     */
    public void clearItemDiscountsByPercentage(int basis, boolean damage)
    {
        KitComponentLineItemIfc[] items = getKitComponentLineItemArray();
        if(items != null && items.length > 0)
        {
            for(int i = 0; i < items.length; i++)
            {
                items[i].getItemPrice().clearItemDiscountsByPercentage(basis, damage);
            }
        }
    }

    /**
     * Overriden to clear discounts on kit component items.
     * 
     * @param typeCode
     * @param basis
     * @param damage
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItem#clearItemDiscountsByPercentage(int,
     *      int, boolean)
     */
    public void clearItemDiscountsByPercentage(int typeCode, int basis, boolean damage)
    {
        KitComponentLineItemIfc[] items = getKitComponentLineItemArray();
        if(items != null && items.length > 0)
        {
            for(int i = 0; i < items.length; i++)
            {
                items[i].getItemPrice().clearItemDiscountsByPercentage(typeCode, basis, damage);
            }
        }
    }

    /**
     * Returns journal string when removing an item.
     * 
     * @return journal string when removing an item
     * @param discountType The discount type. Parameter is not used.
     * @param journalLocale locale received from the client
     */
    public String toJournalDeleteString(int discountType, Locale journalLocale)
    {
        return toJournalDeleteString(journalLocale);
    }

    /**
     * Returns journal string when removing an item.
     * 
     * @param journalLocale locale received from the client
     * @return journal string when removing an item
     */
    public String toJournalRemoveString(Locale journalLocale)
    {
        return super.toJournalRemoveString(journalLocale);
    }

    /**
     * Helper method used to manipulate the display of a string.
     * <ul>
     * <li>Given "1.11" will return "(1.11)"
     * <li>Given "-2" will return "2"
     * <li>Given "(3.4)" will return "3.4"
     * </ul>
     * @param s The string to be negated.
     * @return The negated string
     */
    protected static String negate(String s)
    {
        // Defense: Need something to work with.
        if (s == null || s.length() == 0)
        {
            return s;
        }

        // WARNING:  We assume "well-formed" strings from here.
        // Examples of "bad" strings include:  ")" and "()"

        // If positive, twist into negative.
        if (! (s.startsWith("-") || s.startsWith("(")) )
        {
            return "("+ s +")";
        }

        // We have negative: like "-nnnn" or "(nnnn)"
        s = s.substring(1);
        if (s.endsWith(")"))
        {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}


