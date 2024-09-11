/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/print/PrintOrderSite.java /main/20 2013/01/10 14:04:08 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/07/13 - added order search by status
 *    sgu       05/21/12 - donot change order status after printing the order
 *    sgu       05/21/12 - remove order printed status
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    sgu       02/03/11 - check in all
 *    jkoppolu  09/23/10 - BUG#856, Only 'store copy' of the receipt should be
 *                         printed when the order is reprinted.
 *    npoola    09/08/10 - added the transaction to the orderReceiptParameter
 *                         to print the missing header info
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   03/12/09 - set order status before printing so that the change
 *                         shows up on the receipt
 *    cgreene   12/11/08 - convert to print receipt blueprint
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *         26485 - Merge from v12.0_temp.
 *    8    360Commerce 1.7         5/12/2006 5:25:31 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    7    360Commerce 1.6         2/27/2006 6:15:02 AM   Akhilashwar K. Gupta
 *         CR-8172: Updated as per QA test. Added new method updateStatus() to
 *          set the status of OrderLineItem and Order in case of Print Order.
 *         This CR is opened after fixing CR 3896. Now the code has been
 *         modified to fix both the CRs.
 *    6    360Commerce 1.5         2/21/2006 11:26:09 PM  Akhilashwar K. Gupta
 *         CR-8172: Updated as per code review done by Rohit Sachdeva
 *    5    360Commerce 1.4         2/20/2006 6:34:32 AM   Akhilashwar K. Gupta
 *         Updated method arrive() and placed a check for status update in
 *         case of Filled order.
 *    4    360Commerce 1.3         1/25/2006 4:11:38 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/28/2005 18:16:38    Deepanshu       CR
 *         3896: Set the status as printed for the order
 *    3    360Commerce1.2         3/31/2005 15:29:30     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:23     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:26     Robert Pearse
 *
 *   Revision 1.4.2.1  2004/10/28 16:40:31  kll
 *   @scr 7440: remove service type's impact on the order status
 *
 *   Revision 1.4  2004/09/16 15:24:27  kll
 *   @scr 7149: set order status after printing operation
 *
 *   Revision 1.3  2004/02/12 16:51:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Jan 30 2004 10:59:04   kll
 * removed import and unused variables
 * Resolution for 2390: Print new order from View order function, the order status is changed to Printed
 *
 *    Rev 1.3   Jan 30 2004 10:49:54   kll
 * prevent PENDING status during printing operation
 * Resolution for 2390: Print new order from View order function, the order status is changed to Printed
 *
 *    Rev 1.2   Oct 23 2003 08:27:58   rsachdeva
 * Printer Status Offline
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 *
 *    Rev 1.1   Oct 17 2003 10:06:36   kll
 * SCR-2390: do not alter status after printing
 *
 *    Rev 1.0   Aug 29 2003 16:03:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 06 2003 20:26:32   baa
 * fix receipt printing bundle error
 * Resolution for POS SCR-2303: At Sp. Ord. Cust screen, edit customer phone number, POS crashes
 *
 *    Rev 1.1   Aug 28 2002 10:08:34   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:11:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:54   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 12 2002 14:09:30   mpm
 * Externalized text in receipts and documents.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Jan 23 2002 22:24:52   dfh
 * updates to use abstractionlineitems (salereturnlineitems)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   27 Oct 2001 13:09:56   jbp
 * changed to follow new receipt printing guidelines
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.1   14 Dec 2001 07:52:08   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:22   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.print;

import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * Prints the Order.
 *
 * @version $Revision: /main/20 $
 */
public class PrintOrderSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1589979799511677203L;

    public static final String SITENAME = "PrintOrderSite";

    /**
     * Print the Order and update the order and line item status if applicable.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = null;
        OrderCargo cargo = (OrderCargo)bus.getCargo();
        cargo.setServiceType(OrderCargoIfc.SERVICE_PRINT_TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PrintableDocumentManagerIfc printableDocumentManager = (PrintableDocumentManagerIfc)bus
                .getManager(PrintableDocumentManagerIfc.TYPE);

        POSBaseBeanModel model = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.ORDER_PRINTING, model);
        ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        ui.customerNameChanged(" ");
        // set the EmployeeIfc for this Order to currently logged in operator
        cargo.getOrder().setSalesAssociate(cargo.getOperator());
        // set order status before printing show it show up on receipt
        try
        {

            // print blueprint
            OrderReceiptParameterBeanIfc orderReceiptParameter = printableDocumentManager
                    .getOrderReceiptParameterBeanInstance((SessionBusIfc)bus, cargo.getOrder());
            orderReceiptParameter.setServiceType(cargo.getServiceType());
            if(cargo.getOrder().getOriginalTransaction() instanceof OrderTransaction)
            {
               orderReceiptParameter.setTransaction((OrderTransaction)cargo.getOrder().getOriginalTransaction());
            }
            printableDocumentManager.printOrderReceipt((SessionBusIfc)bus, orderReceiptParameter);

            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (PrintableDocumentException e)
        {
            logger.error("Unable to print receipt.", e);
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            letter = new Letter(CommonLetterIfc.ERROR);
        }
        catch (ParameterException pe)
        {
            logger.error("ParameterException caught in " + SITENAME + ".arrive()", pe);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
