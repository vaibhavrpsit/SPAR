/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/ItemTaxModController.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/28/08 - localization for item tax reason codes
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         4/25/2007 8:52:23 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:34 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:33 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse
 * $
 * Revision 1.17.2.1  2004/10/22 21:27:23  jdeleau
 * @scr 7429 Move the TAX_ALREADY_APPLIED dialog to appear
 * after the override amount or override % dialog is selected.
 *
 * Revision 1.17  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.16  2004/08/03 20:51:09  dcobb
 * @scr 4654 Tax Override REQ, v1 does not address multi-item select with some items not eligible for Tax Override
 *
 * Revision 1.15  2004/07/30 15:15:52  jdeleau
 * @scr 6623 Fixed the name of the dialog being returned on a tax exception
 *
 * Revision 1.14  2004/07/27 00:07:45  jdeleau
 * @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on
 *
 * Revision 1.13  2004/06/23 21:59:45  jdeleau
 * @scr 5158 Property files changes to correct a dialog to be in line with requirements.
 *
 * Revision 1.12  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * Revision 1.11  2004/05/07 01:51:57  dcobb
 * @scr 4702 Tax Override - When selecting multiple items and some but not all are non-taxable, the wrong message appears
 *
 * Revision 1.10  2004/05/06 15:59:01  dcobb
 * @scr 4709 Tax Override is changing tax on non-taxable items and kit header items in multiselect.
 *
 * Revision 1.9  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.8  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.7  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.6  2004/03/11 00:32:01  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.5  2004/03/09 21:46:08  bjosserand
 * @scr 3954 Tax Override
 *
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;

public class ItemTaxModController implements ItemTaxModControllerIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1382549924050941516L;

    /**
     * The logger to which log messages will be sent
     */
    protected static Logger logger = Logger.getLogger(ItemTaxModController.class);

    /**
     * Validate whether any items are invalid for tax override
     *
     * @param items  The selection list
     * @throws TaxErrorException
     * @throws TaxWarningException
     */
    public void validateItemsForOverride(SaleReturnLineItemIfc[] items) throws TaxErrorException, TaxWarningException
    {
        boolean nonTaxable = false;
        boolean invalid = false;
        int invalidCount = 0;

        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (!(items[i].getTaxable()))
                {
                    nonTaxable = true;
                    invalidCount++;
                }
                else if (items[i].isKitHeader())
                {
                    invalid = true;
                    invalidCount++;
                }
            }
        }

        if (nonTaxable && items.length == 1)
        {
            TaxErrorException taxError = new TaxErrorException("item is non-taxable");
            taxError.setErrorTextResourceName("TaxOverrideNotAllowed");
            throw (taxError);
        }
        else if (invalid || nonTaxable)
        {
            TaxWarningException taxWarning = new TaxWarningException("at least one item is invalid");
            taxWarning.setErrorTextResourceName("INVALID_MULTIPLE");
            throw (taxWarning);
        }
    }

    /**
     * Validate whether any items are invalid for tax toggle
     *
     * @param items  The selection list
     * @throws TaxWarningException
     */
    public void validateItemsForToggle(SaleReturnLineItemIfc[] items) throws TaxWarningException
    {
        boolean invalid = false;

        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                // check for invalid item
                if (items[i].getPLUItem() instanceof GiftCardPLUItemIfc
                    || items[i].getPLUItem() instanceof GiftCertificateItemIfc
                    || items[i].isKitHeader())
                {
                    invalid = true;
                }
            }
        }

        if (invalid)
        {
            TaxWarningException taxWarning = new TaxWarningException("at least one item is invalid");
            taxWarning.setErrorTextResourceName("INVALID_MULTIPLE");
            throw (taxWarning);
        }
    }



    /**
     * Validate whether any already overridden (tax modified) items are in the list for tax modification.
     *
     * @param items TaxableLineItemsIfc[]
     * @throws TaxWarningException
     */
    public void validateOverride(TaxableLineItemIfc[] items) throws TaxWarningException
    {
        if (items != null)
        {
            for (int i = 0; i < items.length; i++)
            {
                if(items[i].getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT ||
                        items[i].getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_RATE)

                {
                    TaxWarningException taxWarning =
                        new TaxWarningException("at least one item has already been overridden");
                    taxWarning.setErrorTextResourceName("TAX_ALREADY_APPLIED");
                    throw (taxWarning);
                }
            }
        }
    }

    /**
     * Validate that the user has entered a valid tax rate.
     *
     * @param bus BusIfc
     * @throws TaxErrorException
     */
    public void validateTaxRate(BusIfc bus) throws TaxErrorException
    {
        // get cargo handle
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get bean model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_TAX_OVERRIDE_RATE);

        // retrieve amount, reason
        BigDecimal rate = beanModel.getValue();
        rate = rate.setScale(4, BigDecimal.ROUND_HALF_UP);

        String maxRate = new String("1.0000");
        if (rate.abs().compareTo(new BigDecimal(maxRate).abs()) == 1)
        {
            TaxErrorException taxError = new TaxErrorException("tax rate error");
            taxError.setErrorTextResourceName("InvalidTaxOverride");
            throw (taxError);
        }
    }

    /**
     * Validate that the user has entered a valid tax amount.
     *
     * @param bus BusIfc
     * @throws TaxErrorException
     */
    public void validateTaxAmount(BusIfc bus) throws TaxErrorException
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // get bean model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_TAX_OVERRIDE_AMOUNT);

        // retrieve amount, reason
        BigDecimal amount = beanModel.getValue();

        // retrieve pre-tax item price amount string
        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])cargo.getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getTaxable()
                && !items[i].isKitHeader())
            {
                String PreTaxItemPrice = items[i].getFinalPreTaxAmount().getStringValue();
                if (amount.abs().compareTo(new BigDecimal(PreTaxItemPrice).abs()) == 1)
                {
                    TaxErrorException taxError = new TaxErrorException("tax rate error");
                    taxError.setErrorTextResourceName("InvalidTaxOverride");
                    throw (taxError);
                }
            }
        }
    }

    /**
     * Final processing for a user entered Tax Rate.
     *
     * @param bus BusIfc
     */
    public void processTaxRate(BusIfc bus)
    {
        // get cargo handle

        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get bean model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_TAX_OVERRIDE_RATE);
        // retrieve amount, reason
        BigDecimal rate = beanModel.getValue();
        
        //UI allow up to 4 after decimal point. With division of 100, round it up to 6 decimal places.
        rate = rate.setScale(6, BigDecimal.ROUND_HALF_UP);  
        // get reason text with the selected index
        String reason = beanModel.getSelectedReasonKey();

        CodeListIfc list = cargo.getLocalizedOverrideRateReasons();

        // get reason text with the selected index
        LocalizedCodeIfc reasonEntry = DomainGateway.getFactory().getLocalizedCode();
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode(reason);
            reasonEntry.setCode(reason);
            reasonEntry.setText(entry.getLocalizedText());

        }
        else
        {
            reasonEntry.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }


        // log results
        if (logger.isInfoEnabled())
        {
            StringBuffer sb = new StringBuffer("***** ItemTaxModController.processTaxRate() - received input [");
            sb.append(rate);
            sb.append("] reason [");
            sb.append(reasonEntry.getCode());
            sb.append("].");
            logger.info(sb.toString());
        }



        // set values in item tax object

        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])cargo.getItems();
        if ((items != null) && (items.length > 0) && (items[0] != null))
        {
            ItemTaxIfc tax = DomainGateway.getFactory().getItemTaxInstance();
            tax.setExternalTaxEnabled(items[0].getItemTax().getExternalTaxEnabled());
            tax.setOverrideRate(rate.doubleValue());
            tax.setReason(reasonEntry);
            tax.setOriginalTaxMode(tax.getTaxMode());
            tax.setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_RATE);
            tax.setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
            cargo.setItemTax(tax);

            // kit header has no associated tax, only the components of the kit have tax
            for (int i = 0; i < items.length; i++)
            {
                if (!items[i].getTaxable()
                    || items[i].isKitHeader())
                {
                    items[i].setTaxChanged(false);
                }
                else
                {
                    cargo.setDirtyFlag(true);
                    items[i].setTaxChanged(true);
                }
            }
        }
    }

    /**
     * Final processing for a user entered Tax Amount.
     *
     * @param bus BusIfc
     */
    public void processTaxAmount(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();


        // get bean model
        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_TAX_OVERRIDE_AMOUNT);
        // retrieve amount, reason
        BigDecimal amount = beanModel.getValue();

//      retrieve reason code list
        CodeListIfc list = cargo.getLocalizedOverrideAmountReasons();
        String reasonCodeKey = beanModel.getSelectedReasonKey();
        // get reason text with the selected index
        LocalizedCodeIfc reasonEntry = DomainGateway.getFactory().getLocalizedCode();
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode(reasonCodeKey);
            reasonEntry.setCode(reasonCodeKey);
            reasonEntry.setText(entry.getLocalizedText());

        }
        else
        {
            reasonEntry.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }


        // log results
        if (logger.isInfoEnabled())
        {
            StringBuffer sb = new StringBuffer("**** ItemTaxModController.processAmount() -  received input [");
            sb.append(amount);
            sb.append("] reason [");
            sb.append(reasonEntry.getCode());
            sb.append("].");
            logger.info(sb.toString());
        }

        // set values in item tax object

        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])cargo.getItems();
        if ((items != null) && (items.length > 0) && (items[0] != null))
        {
            ItemTaxIfc tax = DomainGateway.getFactory().getItemTaxInstance();
            CurrencyIfc cAmount = DomainGateway.getBaseCurrencyInstance(amount.toString());
            tax.setOverrideAmount(cAmount);
            tax.setReason(reasonEntry);
            tax.setExternalTaxEnabled(items[0].getItemTax().getExternalTaxEnabled());
            tax.setOriginalTaxMode(tax.getTaxMode());
            tax.setTaxMode(TaxIfc.TAX_MODE_OVERRIDE_AMOUNT);
            tax.setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
            cargo.setItemTax(tax);

            // Kit header has no associated tax; only the components of the kit have tax.
            for (int i = 0; i < items.length; i++)
            {
                if (!items[i].getTaxable()
                    || items[i].isKitHeader())
                {
                    items[i].setTaxChanged(false);
                }
                else
                {
                    cargo.setDirtyFlag(true);
                    items[i].setTaxChanged(true);
                }
            }
        }
    }
}
