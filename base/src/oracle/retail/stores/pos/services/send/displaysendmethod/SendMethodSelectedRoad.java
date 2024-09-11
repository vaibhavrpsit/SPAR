/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/SendMethodSelectedRoad.java /main/19 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    rsnayak   05/12/11 - APf changes for send
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/18/09 - do not process selected shipping method if it is
 *                         null
 *    deghosh   02/10/09 - EJ i18n defect fixes
 *
 * ===========================================================================
 * $Log:
 * 14   360Commerce 1.13        5/21/2007 9:16:22 AM   Anda D. Cadar   EJ
 *      changes
 * 13   360Commerce 1.12        5/8/2007 5:22:00 PM    Alan N. Sinton  CR 26486
 *       - Refactor of some EJournal code.
 * 12   360Commerce 1.11        5/1/2007 12:15:40 PM   Brett J. Larsen CR 26474
 *       - Tax Engine Enhancements for Shipping Carge Tax (for VAT feature)
 *
 * 11   360Commerce 1.10        4/25/2007 8:51:34 AM   Anda D. Cadar   I18N
 *      merge
 * 10   360Commerce 1.9         3/29/2007 7:21:14 PM   Michael Boyd    CR 26172
 *       - v8x merge to trunk
 *
 *      11   .v8x      1.8.1.1     3/3/2007 2:32:20 PM    Maisa De Camargo
 *      Replaced
 *      "Sub-Total" to "Subtotal" to match receipt
 *      10   .v8x      1.8.1.0     3/3/2007 1:59:11 PM    Maisa De Camargo
 *      Replaced
 *      "Sub-Total" to "Subtotal" to match receipt
 * 9    360Commerce 1.8         8/9/2006 9:00:36 PM    Robert Zurga    Merge
 *      4159 Country Name appearing incorrectly, defect fixed.
 * 8    360Commerce 1.7         3/16/2006 5:54:30 AM   Akhilashwar K. Gupta
 *      CR-3995: Updated "getCustomerInfo()" method as per Code review
 *      comment.
 * 7    360Commerce 1.6         3/2/2006 4:08:44 AM    Akhilashwar K. Gupta
 *      CR-3995: Updated to remove duplicate setting of Customer Name
 * 6    360Commerce 1.5         2/24/2006 2:10:40 PM   Brett J. Larsen CR 10575
 *       - incorrect tax amount in e-journal for tax exempt transactions
 *
 *      replaced faulty code w/ new helper method in JournalUtilities
 *
 * 5    360Commerce 1.4         1/25/2006 4:11:46 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 4    360Commerce 1.3         1/22/2006 11:45:21 AM  Ron W. Haight   removed
 *      references to com.ibm.math.BigDecimal
 * 3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:11 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:09 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     12/23/2005 17:17:52    Rohit Sachdeva  8203: Null
 *      Pointer Fix for Business Customer Info
 * 3    360Commerce1.2         3/31/2005 15:29:55     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:11     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:09     Robert Pearse
 *
 *Revision 1.16.2.1  2004/10/18 18:20:54  jdeleau
 *@scr 7381 Correct printing of tax in the e-journal for when printItemTax
 *is turned off.
 *
 *
 *Revision 1.16  2004/09/30 20:21:51  jdeleau
 *@scr 7263 Make printItemTax apply to e-journal as well as receipts.
 *
 *Revision 1.15  2004/09/03 14:30:44  rsachdeva
 *@scr  6791 Transaction Level Send
 *
 *Revision 1.14  2004/09/01 15:34:38  rsachdeva
 *@scr 6791 Transaction Level Send
 *
 *Revision 1.13  2004/08/27 14:30:08  rsachdeva
 *@scr 6791 Item Level Send  to Transaction Level Send Update Flow
 *
 *Revision 1.12  2004/08/10 16:58:21  rsachdeva
 *@scr 6791 Transaction Level Send Journal
 *
 *Revision 1.11  2004/06/21 13:16:07  lzhao
 *@scr 4670: cleanup
 *
 *Revision 1.10  2004/06/19 14:06:14  lzhao
 *@scr 4670: integrate with capture customer
 *
 *Revision 1.9  2004/06/17 14:26:14  rsachdeva
 *@scr 4670 Send: Multiple Sends Journal Customer
 *
 *Revision 1.8  2004/06/11 19:10:34  lzhao
 *@scr 4670: add customer present feature
 *
 *Revision 1.7  2004/06/04 20:23:44  lzhao
 *@scr 4670: add Change send functionality.
 *
 *Revision 1.6  2004/06/03 13:29:21  lzhao
 *@scr 4670: delete send item.
 *
 *Revision 1.5  2004/06/02 19:06:51  lzhao
 *@scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *Revision 1.4  2004/05/28 20:10:07  lzhao
 *@scr 4670: shippingMethod is deprecated.
 *
 *Revision 1.3  2004/05/27 14:37:06  rsachdeva
 *@scr 4670 Send: Multiple Sends
 *
 *Revision 1.2  2004/05/26 19:28:52  lzhao
 *@scr 4670: clean up send.
 *
 *Revision 1.1  2004/05/26 16:37:47  lzhao
 *@scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *Revision 1.5  2004/05/13 19:47:24  rsachdeva
 *@scr 4670 Send: Journal Send Information for Each Send
 *
 *Revision 1.4  2004/05/05 16:13:53  rsachdeva
 *@scr 4670 Send: Multiple Sends
 *
 *Revision 1.3  2004/02/12 16:51:55  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:52:29  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:06:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   13 Nov 2002 17:31:14   sfl
 * Adjust the tax amount decimal point digit length before
 * sending it for display.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.1   04 Nov 2002 15:21:34   sfl
 * Make sure the tax is re-calculated after manual
 * tax rate override during send.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:04:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:10   msg
 * Initial revision.
 *
 *    Rev 1.13   13 Feb 2002 09:22:44   sfl
 * Added shipping method information right after
 * the shipping charge in the E-Journal.
 * Resolution for POS SCR-1264: Send - ejournal entry for send item not complete
 *
 *    Rev 1.12   Feb 05 2002 16:43:28   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.11   28 Jan 2002 11:25:34   sfl
 * Make an error message in E-journal more concise.
 * Resolution for POS SCR-879: Send - item weight is not returned with the item, an error should be written to the ejournal
 *
 *    Rev 1.10   25 Jan 2002 19:04:54   sfl
 * Fix for SCR879. Added error message logging in
 * E-Journal when the item weigh is zero during
 * the shipping charge calculation basted on weight.
 * Resolution for POS SCR-879: Send - item weight is not returned with the item, an error should be written to the ejournal
 *
 *    Rev 1.9   21 Jan 2002 09:42:20   sfl
 * Removed the extra "r" from the special instruction
 * header printing in the E-Journal.
 * Resolution for POS SCR-787: Send - ejournal entry correction
 *
 *    Rev 1.8   14 Jan 2002 19:45:22   sfl
 * E-Journal fixing for send items.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.7   14 Jan 2002 12:59:22   baa
 * updates from code review
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.6   11 Jan 2002 12:12:20   sfl
 * Took away one extra space before the second address line
 * display in E-journal.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.5   09 Jan 2002 10:26:02   baa
 * fix totals
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.4   03 Jan 2002 14:23:24   baa
 * cleanup code
 * Resolution for POS SCR-520: Prepare Send code for review
 *
 *    Rev 1.3   20 Dec 2001 18:31:12   baa
 * fix sale type when balance is + after returns
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   12 Dec 2001 17:25:42   baa
 * updates for  journaling send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   07 Dec 2001 18:56:44   sfl
 * Added the shipping charge to transaction totals so that
 * the tender, receipt, and e-journal will have the new grandtotal
 * = calculatedShippingCharge + normal grandtotal.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   06 Dec 2001 18:52:32   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   04 Dec 2001 17:23:02   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.displaysendmethod;

// java imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;

/**
 * Retrieves send method selected, adds to transaction totals, journals current
 * send information
 * 
 */
public class SendMethodSelectedRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 6546630921720179222L;
    
    /**
     * Default Shipping Charge Service Item ID
     */
    public static final String DEFAULT_SHIPPING_CHARGE_ITEM_ID = "ShippingChargeItemID";

    /**
     * Retrieves selected shipping method and calculate totals.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
    	
    	SendCargo cargo = (SendCargo) bus.getCargo();
    	SaleReturnTransactionIfc transaction = cargo.getTransaction();

    	TransactionTotalsIfc totals = transaction.getTransactionTotals();
    	if ( cargo.getOperator() == null )
    		journalCurrentSend(bus, transaction, transaction.getSalesAssociateID());
    	else
    		journalCurrentSend(bus, transaction, cargo.getOperator().getLoginID());
    }


    /**
     * Journal current send information
     * 
     * @param bus service bus
     * @param transaction sale return transaction reference
     * @param loginID login id
     */
    public void journalCurrentSend(BusIfc bus, SaleReturnTransactionIfc transaction, String loginID)
    {
       // print journal
       JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

       if (journal != null)
       {
    	   journal.journal(loginID, transaction.getTransactionID(), journalShippingInfo(bus, transaction));
           if (logger.isInfoEnabled()) logger.info( "Journal Send msg");
       }
       else
       {
           logger.error( "No JournalManager found");
       }
   }

   /**
     * Prints ship to info for the current send
     * 
     * @param bus service bus
     * @param transaction sale return transaction reference
     * @return String shipping to info
     */
    protected String journalShippingInfo(BusIfc bus,
                                         SaleReturnTransactionIfc transaction)
    {
        JournalFormatterManagerIfc formatterManager =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        SendCargo cargo = (SendCargo) bus.getCargo();
        ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        SaleReturnLineItemIfc[] items = cargo.getLineItems();
        // Retrieve shipping charge parameter
        StringBuffer journalBuffer =
            new StringBuffer(formatterManager.journalShippingInfo(transaction, items, parameterManager));

        //journal sub totals for transaction level send
        if (transaction.isTransactionLevelSendAssigned())
        {
            journalBuffer.append(journalSubTotalsForTransactionLevelSend(transaction,
                                                                         bus));
        }
        return journalBuffer.toString();
    }
    
    /**
     * Journal sub totals for transaction level send. Journalling for Subtotals
     * is being done now since now we have the total shipping charges for
     * transaction level send
     * 
     * @param transaction sale return transaction reference
     * @param bus service bus reference
     * @return String journal string
     */
    public String journalSubTotalsForTransactionLevelSend(SaleReturnTransactionIfc transaction,
                                                         BusIfc bus)
    {
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)bus.getManager(JournalFormatterManagerIfc.TYPE);
        ParameterManagerIfc parameterManager =
            (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        return formatter.journalSubTotalsForTransactionLevelSend(transaction, parameterManager);
   }    
}
