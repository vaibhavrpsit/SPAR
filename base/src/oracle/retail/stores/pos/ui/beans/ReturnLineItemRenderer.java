/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnLineItemRenderer.java /main/20 2013/10/18 14:13:53 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  10/18/13 - return item message display requires return line
 *                         item to be there.
 *    jswan     12/13/12 - Modified to prorate discount and tax for returns of
 *                         order line items.
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    yiqzhao   05/08/12 - add Send and leave a space between Send and index
 *                         for Transaction Detail screen.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     06/30/09 - Fixed issues with reprint/gift receipt and order
 *                         line items.
 *    jswan     05/21/09 - Code Review.
 *    jswan     05/21/09 - Modified to prevent the pickup/delivery images from
 *                         displaying for return items.
 *    sgu       02/09/09 - remove number formatter from return quantity msg
 *                         constant
 *    sgu       02/06/09 - fix available quantity to not have formatting info
 *                         in its bundle
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce1.5         3/30/2007 6:04:07 AM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         6    .v8x      1.4.1.0     2/23/2007 1:33:03 PM   Rohit Sachdeva
 *         24878:
 *         CTR Multi Store with Price Adjustment not working
 *    5    360Commerce1.4         5/13/2006 3:55:36 AM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce1.3         1/22/2006 11:15:28 PM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce1.2         4/1/2005 2:59:45 AM    Robert Pearse
 *    2    360Commerce1.1         3/10/2005 9:54:52 PM   Robert Pearse
 *    1    360Commerce1.0         2/11/2005 11:43:54 PM  Robert Pearse
 *
 *   Revision 1.7  2004/07/30 00:18:01  jdeleau
 *   @scr 6530 Don't let the quantity available for return appear
 *   for returns without a receipt.
 *
 *   Revision 1.6  2004/06/03 13:29:21  lzhao
 *   @scr 4670: delete send item.
 *
 *   Revision 1.5  2004/05/28 20:09:02  lzhao
 *   @scr 4670: add index for send in line items.
 *
 *   Revision 1.4  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:18:30   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:57:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:16   msg
 * Initial revision.
 *
 *    Rev 1.5   04 Mar 2002 13:04:08   sfl
 * Implemented the new requirements to display
 * return item modifiers in the Select Item screen.
 * Resolution for POS SCR-1456: Gift Receipt item modifier missing from Select Item screen during a retrieved Return
 *
 *    Rev 1.4   Feb 05 2002 16:43:58   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   28 Jan 2002 14:20:44   jbp
 * Merged fix into new ui changes
 * Resolution for POS SCR-137: Exchanges retrieved for return show neg qty avail for returns
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.text.NumberFormat;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;


/**
 * This is the renderer for the Return Table. It displays SaleReturnLineItems
 * and makes them look like it is a table.
 * 
 * @version $Revision: /main/20 $
 */
public class ReturnLineItemRenderer extends SaleLineItemRenderer
{
    private static final long serialVersionUID = -6830778369218578484L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * return qty msg
     */
    public static final String RETURN_QTY_MSG = "Return Quantity Available ({0})";

    /**
     * Constructor
     */
    public ReturnLineItemRenderer()
    {
        super();
    }

    /**
     * sets the visual components of the cell
     * 
     * @param value Object
     */
    public void setData(Object value)
    {
        // The pickup/delivery image should not be displayed for return items.
        displayPickupDeliveryImage = false;
        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;
        if(lineItem.getReturnItem()==null)
        {
            lineItem.setReturnItem(DomainGateway.getFactory().getReturnItemInstance());
        }
        super.setData(value);

        // If there are no items left to return, disable the component.
        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) value;
        BigDecimal numberReturnable = srli.getQuantityReturnable();

        // this check is done so that Return Quantity Available is set to "0.00"
        // when an exchange transaction is displayed and the item has a negative
        // number returnable.
        if (numberReturnable.compareTo(BigDecimalConstants.ZERO_AMOUNT) == -1)
        {
            numberReturnable = BigDecimalConstants.ZERO_AMOUNT;
        }

        int i = 0;
        String returnQty = UIUtilities.retrieveText("Common",
                                                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                                                    "ReturnAvailableQty",
                                                    RETURN_QTY_MSG);
        NumberFormat nf = NumberFormat.getInstance(getLocale());
        Object[] parm = {nf.format(numberReturnable)};

        returnQty = LocaleUtilities.formatComplexMessage(returnQty, parm);
        if(srli.isFromTransaction())
        {
            optionalFields[i].setText(returnQty);
        }
        else
        {
            optionalFields[i].setText("");
        }
        sizeOptionalField(optionalFields[i]);

        // Check if Gift receipt and display "Gift Receipt"
        if (srli.isGiftReceiptItem())
        {
            i = i + 1;
            optionalFields[i].setText(giftReceiptLabel);
            sizeOptionalField(optionalFields[i]);
        }

        //If the item is a gift card, then sale info field within the line item should
        //display "Gift Card ID: card #"
        if(srli.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard =
                ((GiftCardPLUItemIfc)(srli.getPLUItem())).getGiftCard();

            if(giftCard != null &&
               giftCard.getCardNumber() != null &&
               !(giftCard.getCardNumber().equals("")))
            {
                i = i + 1;
                optionalFields[i].setText(giftCardLabel + giftCard.getEncipheredCardData().getTruncatedAcctNumber());
                sizeOptionalField(optionalFields[i]);
            }
        }
        //If an item has a serial number associated with it, then the sale info field
        //within the line item should display 'Serialized'
        else if(srli.getItemSerial() != null && !"".equals(srli.getItemSerial()))
        {
            i = i + 1;
            optionalFields[i].setText(serialLabel + srli.getItemSerial());
            sizeOptionalField(optionalFields[i]);
        }
        // Check to see if it is a send item and display "Send" if yes
        if (srli.getItemSendFlag() || srli.isShippingCharge())
        {
            i = i + 1;
            optionalFields[i].setText(new StringBuffer(sendLabel).append(" ").append(srli.getSendLabelCount()).toString());
            sizeOptionalField(optionalFields[i]);
        }

        if (srli.getRegistry() != null)
        {
            i = i + 1;
            optionalFields[i].setText(giftRegLabel + srli.getRegistry().getID().toString());
            sizeOptionalField(optionalFields[i]);
        }

        if (srli.getSalesAssociate() != null && srli.getSalesAssociateModifiedFlag())
        {
        	if(srli.getSalesAssociate().getPersonName() != null)
        	{
                i = i + 1;
                optionalFields[i].setText(salesAssocLabel + srli.getSalesAssociate().getPersonName().getFirstLastName());
                sizeOptionalField(optionalFields[i]);
        	}
        }

        if (numberReturnable.equals(BigDecimal.ZERO))
        {
           setEnabled(false);
        }
        else
        {
           setEnabled(true);
        }

        // Reset the pickup/delivery indicator for the next time through.
        displayPickupDeliveryImage = true;
    }
}
