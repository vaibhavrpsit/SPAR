/* ===========================================================================
* Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxToggleActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/29/08 - cleaned up commented out code
 *    acadar 10/28/08 - localization for item tax reason codes
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse
 * $
 * Revision 1.10  2004/07/27 00:07:45  jdeleau
 * @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on
 *
 * Revision 1.9  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * Revision 1.8  2004/05/06 15:59:01  dcobb
 * @scr 4709 Tax Override is changing tax on non-taxable items and kit header items in multiselect.
 *
 * Revision 1.7  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.6  2004/05/03 19:59:02  dcobb
 * @scr 4381 get "Tax Override Not Allowed" error when try to override tax for an item that was non-taxable but had tax turned on for that item
 *
 * Revision 1.5  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.4  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.3  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/11 00:32:01  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:07 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:51:47 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:02:10 CSchellenger Initial revision.
 *
 * Rev 1.1 Mar 26 2003 15:14:24 RSachdeva Removed use of CodeEntry getCode() method Resolution for POS SCR-2103: Remove
 * uses of deprecated items in POS.
 *
 * Rev 1.0 Apr 29 2002 15:18:20 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:38:14 msg Initial revision.
 *
 * Rev 1.0 Sep 21 2001 11:29:34 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:09:20 msg header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.BooleanWithReasonBeanModel;

//--------------------------------------------------------------------------
/**
 * Site used when tax toggle is entered.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxToggleActionSite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     * Revision Number furnished by TeamConnection.
     * <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * This site is executed when the tax toggle has been executed.
     * <P>
     * @param bus  The service bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // retrieve the items for tax toggle
        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])cargo.getItems();
        // get bean model
        BooleanWithReasonBeanModel beanModel =
            (BooleanWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_TAX_ON_OFF);
        // retrieve toggle setting, reason code
        boolean toggle = beanModel.getValue();
        String reason = beanModel.getSelectedReasonKey();
        // log results
        StringBuffer sb = new StringBuffer("***** TaxToggleEnteredSite received input [");
        sb.append(toggle);
        sb.append("] reason [");
        sb.append(reason);
        sb.append("].");
        if (logger.isInfoEnabled())
        {
            logger.info(sb.toString());
        }

        // use old tax settings to see if anything really happened here
        ItemTaxIfc oldTax = cargo.getItemTax();

        // retrieve reason code list
        CodeListIfc rcl = cargo.getLocalizedToggleReasons();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();

        if (rcl != null)
        {
            CodeEntryIfc entry = rcl.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }



        if (items != null && items.length > 0)
        {
            // set values in item tax object
            ItemTaxIfc tax = DomainGateway.getFactory().getItemTaxInstance();
            tax.setExternalTaxEnabled(oldTax.getExternalTaxEnabled());
            tax.setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
            tax.setTaxToggle(toggle);
            tax.setReason(localizedCode);

            // if toggle on and item was not taxed
            if (toggle == true)
            {
                tax.setOriginalTaxMode(tax.getTaxMode());
                tax.setTaxMode(TaxIfc.TAX_MODE_TOGGLE_ON);
                for (int i = 0; i < items.length; i++)
                {
                    ItemTaxIfc itemTax = items[i].getItemTax();
                    if (itemTax.getTaxMode() == TaxIfc.TAX_MODE_TOGGLE_OFF
                        || itemTax.getTaxMode() == TaxIfc.TAX_MODE_NON_TAXABLE)
                    {
                        // check for invalid item
                        if (items[i].getPLUItem() instanceof GiftCardPLUItemIfc
                            || items[i].getPLUItem() instanceof GiftCertificateItemIfc
                            || items[i].isKitHeader())
                        {
                            items[i].setTaxChanged(false);
                        }
                        else
                        {
                            cargo.setDirtyFlag(true);
                            items[i].setTaxable(true);
                            items[i].setTaxChanged(true);
                        }
                    }
                    else
                    {
                        items[i].setTaxChanged(false);
                    }
                }
            }
            // if toggle off, turn tax off
            else if (toggle == false)
            {
                tax.setOriginalTaxMode(tax.getTaxMode());
                tax.setTaxMode(TaxIfc.TAX_MODE_TOGGLE_OFF);
                for (int i = 0; i < items.length; i++)
                {
                    ItemTaxIfc itemTax = items[i].getItemTax();
                    if (itemTax.getTaxMode() != TaxIfc.TAX_MODE_TOGGLE_OFF
                            && itemTax.getTaxMode() != TaxIfc.TAX_MODE_NON_TAXABLE)
                    {
                        if (items[i].isKitHeader())
                        {
                            items[i].setTaxChanged(false);
                        }
                        else
                        {
                            cargo.setDirtyFlag(true);
                            items[i].setTaxable(false);
                            items[i].setTaxChanged(true);
                        }
                    }
                    else
                    {
                        items[i].setTaxChanged(false);
                    }
                }
            }

            cargo.setItemTax(tax);
        }
        // mail a Success (final) letter
        bus.mail(new FinalLetter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
