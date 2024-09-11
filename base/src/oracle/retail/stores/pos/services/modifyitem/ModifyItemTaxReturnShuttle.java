/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemTaxReturnShuttle.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 * $
 * Revision 1.12  2004/09/23 00:07:12  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.11  2004/05/11 22:43:47  dcobb
 * @scr 4922 Update service to work with Tax Override multi-item select.
 *
 * Revision 1.10  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.9  2004/03/16 18:30:46  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.8  2004/03/12 19:22:15  fkane
 * @scr 3977
 * Changed the logging line to make sure it had an item in hte cargo so when
 * multiselect option is chosen from taxmodify it didnt null pointer
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:03 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:39:28 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:01:46 CSchellenger Initial revision.
 * 
 * Rev 1.1 24 May 2002 18:54:36 vxs Removed unncessary concatenations from log statements. Resolution for POS SCR-1632:
 * Updates for Gap - Logging
 * 
 * Rev 1.0 Apr 29 2002 15:17:44 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:37:32 msg Initial revision.
 * 
 * Rev 1.1 06 Mar 2002 16:29:38 baa Replace get/setAccessEmployee with get/setOperator Resolution for POS SCR-802:
 * Security Access override for Reprint Receipt does not journal to requirements
 * 
 * Rev 1.0 Sep 21 2001 11:29:10 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import java.text.DecimalFormat;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifyitem.tax.ModifyItemTaxCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * Return shuttle class for ModifyItemTax service.
 * 
 * @version $Revision: /main/12 $
 */
public class ModifyItemTaxReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8041275429488022463L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyItemTaxReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
     * dirty flag (indicates update needs to be performed)
     */
    protected boolean dirtyFlag = false;
    /**
     * incoming cargo object
     */
    protected ModifyItemTaxCargo taxCargo;

    /**
     * Load from child (ModifyItemTax) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ModifyItemTaxCargo class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo loaded
     * </UL>
     * 
     * @param bus  The service bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // retrieve cargo
        taxCargo = (ModifyItemTaxCargo) bus.getCargo();
    }

    /**
     * Unload to parent (ModifyItem) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ModifyItem class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo unloaded
     * </UL>
     * 
     * @param bus  The service bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        // retrieve cargo
        ItemCargo cargo = (ItemCargo) bus.getCargo();

        // if dirty flag set, perform updates
        TaxableLineItemIfc[] items = taxCargo.getItems();
        dirtyFlag = taxCargo.getDirtyFlag();

        if (dirtyFlag)
        {
            updateItemTax(bus, cargo);
            
            // Set the tax modification data for Kit Component item
            if (items != null && items.length == 1 && items[0] instanceof KitComponentLineItemIfc)
            {
                ItemPriceIfc price = ((KitComponentLineItemIfc)items[0]).getItemPrice();
                SaleReturnLineItemIfc theItem = cargo.getItem();
                theItem.setItemPrice(price);
                theItem.setTaxable(items[0].getTaxable());
            }
            
            if ((logger.isInfoEnabled() ) && ( cargo.getItem()!= null ))
                logger.info(
                    "ModifyItemTaxReturnShuttle unload:  ItemTaxIfc:" + cargo.getItems()[0].getItemTax().toString() + "");
        }

    } // end unload()

    /**
     * Updates item with tax changes.
     *
     * @param newTax new item tax settings
     */
    public void updateItemTax(BusIfc bus, ItemCargo cargo)
    {
        ItemTaxIfc newTax = taxCargo.getItemTax();
        SaleReturnLineItemIfc[] items = cargo.getItems();
        int taxMode = newTax.getTaxMode();
        LocalizedCodeIfc reasonCode = newTax.getReason();
        CurrencyIfc overrideAmount = newTax.getOverrideAmount();
        double overrideRate = newTax.getOverrideRate();
        if (items != null)
        {
            // update tax based on tax mode
            for (int i = 0; i < items.length; i++)
            {
                if (items[i].isTaxChanged())
                {
                    ItemPriceIfc ip = items[i].getItemPrice();
                    //ItemPriceIfc ip = item.getItemPrice();
                    switch (taxMode)
                    { // begin evaluate new tax mode
                        case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT :
                            if (logger.isInfoEnabled())
                                logger.info("Overriding item tax amount ...");
                            // override tax amount on item
                            ip.overrideTaxAmount(overrideAmount, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_OVERRIDE_RATE :
                            if (logger.isInfoEnabled())
                                logger.info("Overriding item tax rate ...");
                            // override tax rate on item
                            ip.overrideTaxRate(overrideRate, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_TOGGLE_ON :
                            if (logger.isInfoEnabled())
                                logger.info("Setting item tax toggle on ...");
                            ip.toggleTax(true, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_TOGGLE_OFF :
                            if (logger.isInfoEnabled())
                                logger.info("Setting item tax toggle off ...");
                            ip.toggleTax(false, reasonCode);
                            break;
                        case TaxIfc.TAX_MODE_STANDARD :
                        default :
                            if (logger.isInfoEnabled())
                                logger.info("Resetting standard tax ...");
                            // reset standard
                            ip.clearTaxOverride();
                            break;
                    } // end evaluate new tax mode
                    // Set the original tax code
                    items[i].getItemPrice().getItemTax().setOriginalTaxMode(newTax.getOriginalTaxMode());
                    // make journal entry
                    JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

                    int taxScope = newTax.getTaxScope();
                    String reasonString;
                    String taxAmount;
                    String reasonCodeString;
                    StringBuilder sb = new StringBuilder();

                    // tailor output to mode
                    if (taxScope == TaxIfc.TAX_SCOPE_ITEM) //scope must be item
                    {
                        if(taxMode == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE)
                        {
                            // Make the percentage 2 digits after decimal
                            String formatPattern = "#0.00%";
                            DecimalFormat formatter = new DecimalFormat();
                            formatter.applyPattern(formatPattern);
                            taxAmount = formatter.format(overrideRate);
                            reasonString = reasonCode.getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                            reasonCodeString = reasonCode.getCode();
                            Object dataObject[]={items[i].getItemID()};


                            String taxDesc = TaxConstantsIfc.TAX_MODE_DESCRIPTOR[items[i].getItemTax().getTaxMode()];
                            String taxModeFlag =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxflag."+taxDesc,null);
                            String taxModeDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxdesc."+TaxConstantsIfc.TAX_MODE_DESCRIPTOR[taxMode],null);

                            String journalItem = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LOWER,dataObject);


                            Object taxModeDataArgs[]={taxModeDesc};
                            String journalTaxMode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAX_MODE,taxModeDataArgs);

                            Object taxRateDataArgs[]={taxAmount};
                            String journalTaxRate = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAX_RATE,taxRateDataArgs);

                            sb.append(Util.EOL);
                            sb.append(journalItem + " ("+taxModeFlag+")");
                            sb.append(Util.EOL);
                            sb.append(journalTaxMode);
                            sb.append(Util.EOL);
                            sb.append(journalTaxRate);
                            sb.append(Util.EOL);

                            Object dataArgs[] = new Object[]{reasonString};
                            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_REASON_CODE, dataArgs));
                            sb.append(" (" + reasonCodeString + ")");
                        }
                        else if(taxMode == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT)
                        {
                            taxAmount = overrideAmount.toGroupFormattedString();
                            reasonString = reasonCode.getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                            reasonCodeString = reasonCode.getCode();
                            Object dataObject[]={items[i].getItemID()};

                            String taxDesc = TaxConstantsIfc.TAX_MODE_DESCRIPTOR[items[i].getItemTax().getTaxMode()];
                            String taxModeFlag =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxflag."+taxDesc,null);
                            String taxModeDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxdesc."+TaxConstantsIfc.TAX_MODE_DESCRIPTOR[taxMode],null);

                            String journalItem = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LOWER,dataObject);

                            Object taxModeDataArgs[]={taxModeDesc};
                            String journalTaxMode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAX_MODE,taxModeDataArgs);

                            Object taxRateDataArgs[]={taxAmount};
                            String journalTaxAmt = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAX_AMT,taxRateDataArgs);

                            sb.append(Util.EOL);
                            sb.append(journalItem + " ("+taxModeFlag+")");
                            sb.append(Util.EOL);
                            sb.append(journalTaxMode);
                            sb.append(Util.EOL);
                            sb.append(journalTaxAmt);
                            sb.append(Util.EOL);
                            Object dataArgs[] = new Object[]{reasonString};
                            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_REASON_CODE, dataArgs));
                            sb.append(" (" + reasonCodeString + ")");
                        }
                        else if(taxMode == TaxIfc.TAX_MODE_TOGGLE_ON || taxMode == TaxIfc.TAX_MODE_TOGGLE_OFF)
                        {
                            taxAmount = items[i].getItemTax().getTaxInformationContainer().getTaxAmount().toString();
                            reasonString = reasonCode.getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                            reasonCodeString = reasonCode.getCode();
                            Object dataObject[]={items[i].getItemID()};


                            String taxDesc = TaxConstantsIfc.TAX_MODE_DESCRIPTOR[items[i].getItemTax().getTaxMode()];
                            String taxModeFlag =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxflag."+taxDesc,null);
                            String taxModeDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.taxdesc."+TaxConstantsIfc.TAX_MODE_DESCRIPTOR[taxMode],null);

                            String journalItem = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LOWER,dataObject);

                            Object taxModeDataArgs[]={taxModeDesc};
                            String journalTaxMode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAX_MODE,taxModeDataArgs);

                            sb.append(Util.EOL);
                            sb.append(journalItem + " ("+taxModeFlag+")");
                            sb.append(Util.EOL);
                            sb.append(journalTaxMode);
                            sb.append(Util.EOL);
                            Object dataArgs[] = new Object[]{reasonString};
                            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_REASON_CODE, dataArgs));
                            sb.append(" (" + reasonCodeString + ")");
                        }
                        sb.append(Util.EOL);

                        journal.journal(cargo.getCashier().getLoginID(), cargo.getTransactionID(), sb.toString());
                    }
                }
            }
        }
    } // end updateItemTax()
} // end class ModifyItemTaxReturnShuttle
