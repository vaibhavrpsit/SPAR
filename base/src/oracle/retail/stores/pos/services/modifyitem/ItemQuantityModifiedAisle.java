/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemQuantityModifiedAisle.java /main/18 2012/12/11 14:33:10 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/07/12 - Fixing HP Fortify redundant null check issues
 *    blarsen   06/18/12 - Adding quantity > 1 serialized item check for MPOS.
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       10/10/11 - journal the cloned item with VAT recalculated
 *    cgreene   05/23/11 - change code to only clone transaction if vat
 *                         journalling is needed
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/12/10 - use default locale for display of currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       02/17/09 - donot convert Number to double it will loose
 *                         precision
 *    sgu       02/17/09 - use formatNumber and parseNumber
 *    sgu       02/16/09 - reponse text must be localized
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         8/7/2007 6:13:05 AM    Manikandan Chellapan
 *         Updated Comment
 *    8    360Commerce 1.7         8/7/2007 5:42:12 AM    Manikandan Chellapan
 *         CR28143 Fixed VAT EJ Issue
 *    7    360Commerce 1.6         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *         26486 - Changes per review comments.
 *    6    360Commerce 1.5         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    5    360Commerce 1.4         5/12/2006 5:25:30 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
 *
 *   Revision 1.6  2004/08/11 14:11:21  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.5  2004/06/09 17:12:52  lzhao
 *   @scr 4670: set quantity for send item to calculate shipping charge.
 *
 *   Revision 1.4  2004/06/09 14:24:17  lzhao
 *   @scr 4670: add shipping method for update quantity for send item.
 *
 *   Revision 1.3  2004/02/12 16:51:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 17 2003 06:47:24   jgs
 * Modifed journaling for item discounts.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.0   Apr 29 2002 15:17:04   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:02   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.0   Mar 18 2002 11:37:12   msg
 * Initial revision.
 *
 *    Rev 1.2   26 Feb 2002 14:42:38   jbp
 * do not allow zero quantitys
 * Resolution for POS SCR-1386: Can add a UoM item from Item Inquiry with O quantity 0 price to the sell item
 *
 *    Rev 1.1   Feb 05 2002 16:42:40   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:29:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Get UI input Put it into cargo update the modified item. Mail a final letter
 *
 */
public class ItemQuantityModifiedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 8461554343242000992L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * lane name constant
     */
    public static final String LANENAME = "ItemQuantityModifiedAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        BigDecimal newQuantity;               // new quantity to set the line item to
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String quantity = ui.getInput();
        BigDecimal tmp = null;
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        try
        {
        	tmp = new BigDecimal(LocaleUtilities.parseNumber(quantity, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)).toString());
            if (tmp.scale() == 0)
            {
                tmp = tmp.multiply(BigDecimalConstants.ONE_AMOUNT);
            }
            else if (tmp.scale() == 1)
            {
                tmp = tmp.multiply(BigDecimalConstants.ONE_AMOUNT);
            }
        }
        catch  (Exception e)
        {
            tmp = BigDecimalConstants.ONE_AMOUNT;//what do we do when invalid???
        }

        ItemCargo cargo = (ItemCargo)bus.getCargo();

        // if quantity entered is zero, reenter quantity.
        if(tmp.compareTo(BigDecimal.ZERO) == 0)
        {
            DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("QuantityCannotBeZero");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
        // POS disables quantity button for serialized items.  MPOS does not.  So, need to check this condition.
        else if (cargo != null && cargo.getItem() != null && cargo.getItem().isSerializedItem() && tmp.compareTo(BigDecimal.ONE) == 1)
        {
            DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("SerialNotAllowedWithMultipleQuantity");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
            dModel.setArgs(new String[] { cargo.getItem().getItemID() });
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
        else if(null != cargo && null != cargo.getItem())
        {
            // check if this item is a sale or return
            if ( cargo.getItem().getItemQuantityDecimal().signum() >= 0)
            {
                newQuantity = tmp;
            }
            else
            {
                newQuantity = tmp.negate();
            }

            //save original item in StringBuilder for journal
            StringBuilder sb = new StringBuilder();
            SaleReturnLineItemIfc item = cargo.getItem();
            sb.append(formatter.toJournalRemoveString(item));
            ItemDiscountStrategyIfc[] itemDiscounts =
                item.getItemPrice().getItemDiscounts();
            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
                        sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i],true));
                    }
                }
            }

            //set the quantity of the line item
            item.modifyItemQuantity(newQuantity);

            // journal it here
            JournalManagerIfc journal =
                (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

            if (journal != null)
            {

                // CR28143: For VAT the line item's tax amounts are reported in the EJournal.
                // In order to retrieve the right values, they must be recalculated.  The
                // actual update to the transaction doesn't occur until leaving ModifyItemReturnShuttle.
                // To fix this calculate the tax for the price overriden items in a clone of the transaction.
                // This fix is copied from ItemPriceModifiedAisle - Mani
                // NOTE: The journaling will be done against a line item clone. CMG
                boolean taxInclusiveFlag = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
                RetailTransactionIfc transaction = cargo.getTransaction();
                if(taxInclusiveFlag && transaction instanceof SaleReturnTransactionIfc)
                {
                    SaleReturnTransactionIfc srTransaction =
                        (SaleReturnTransactionIfc)transaction.clone();
                    // line item is now a clone
                    item = (SaleReturnLineItemIfc)item.clone();
                    srTransaction.replaceLineItem(item, item.getLineNumber());
                    srTransaction.updateTransactionTotals();
                }

                //save new item info in StringBuilder for journal
                sb.append(Util.EOL);
                sb.append(formatter.toJournalString(item, null, null));
                itemDiscounts = item.getItemPrice().getItemDiscounts();
                if((itemDiscounts != null) && (itemDiscounts.length > 0))
                {
                    for (int i = 0; i < itemDiscounts.length; ++i)
                    {
                        if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                        {
                            sb.append(Util.EOL);
                            sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i],false));
                        }
                    }
                }

                //write the journal
                journal.journal(cargo.getCashier().getEmployeeID(),
                                cargo.getTransactionID(),
                                sb.toString());
            }
            else
            {
                logger.warn( "No journal manager found!");
            }
            if (item.getSendLabelCount() == 0
                ||(cargo.getTransaction() != null &&
                   ((SaleReturnTransactionIfc)cargo.getTransaction()).isTransactionLevelSendAssigned()))
            {
                // Done modifying quantity, mail a final letter.
                bus.mail(new FinalLetter("Next"), BusIfc.CURRENT);
            }
            else
            {
                cargo.setItemQuantity(newQuantity);
                bus.mail(new Letter("ShippingMethod"), BusIfc.CURRENT);
            }
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return String representation of class
     */
    @Override
    public String toString()
    { // begin toString()
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append(LANENAME).append(" (Revision ").append(getRevisionNumber()).append(") @").append(hashCode());
        // pass back result
        return (strResult.toString());
    } // end toString()

    /**
     * Returns the revision number of this class.
     *
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}
