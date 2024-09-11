/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/CheckForSerializedItemSite.java /main/17 2013/01/02 11:55:37 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       12/27/12 - no need to set pre split index
 *    cgreene   08/10/11 - quickwin - implement dialog for trying to enter
 *                         multiple qty of serialized item
 *    nkgautam  08/11/10 - do not prompt for serial number for order initiate
 *                         transactions
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  02/03/10 - do not prompt for serial numbers for layaway
 *                         initiate transactions
 *    nkgautam  01/22/10 - fix for imei lookup flow
 *    nkgautam  01/13/10 - change to prompt for serial number when not already
 *                         entered
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Serialisation Code changes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:18:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:52   msg
 * Initial revision.
 *
 *    Rev 1.2   16 Jan 2002 13:01:32   baa
 * allow for adding serial item to non serialized items
 * Resolution for POS SCR-579: Unable to manually enter a serial number to an item
 *
 *    Rev 1.1   07 Dec 2001 12:51:58   pjf
 * Code review updates.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   14 Nov 2001 06:44:44   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

//foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.domain.transaction.OrderTransaction;

/**
 * Checks the items in cargo for a serial number and mails a letter to continue
 * or exit the service.
 * 
 * @version $Revision: /main/17 $
 */
@SuppressWarnings("serial")
public class CheckForSerializedItemSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * This method loops on the line items in cargo until an item requiring a
     * serial number is found or all the items in the cargo have been checked.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();

        String letter = CommonLetterIfc.OK;

        while (cargo.hasMoreLineItems())
        {
            cargo.nextLineItem();
            if (cargo.isKitHeader())
            {
                if (cargo.getItem().isSerializedItem() && cargo.getItem().getItemIMEINumber() == null)
                {
                    letter = CommonLetterIfc.CONTINUE;
                    break;
                }
                else
                {
                    cargo.getItem().setItemSerial(cargo.getItem().getItemIMEINumber());
                    letter = CommonLetterIfc.OK;
                    break;
                }
            }
            else
            {
                // if it is a regular item Check for IMEI
                if (cargo.getItem().getItemIMEINumber() != null)
                {
                    cargo.getItem().setItemSerial(cargo.getItem().getItemIMEINumber());
                    letter = CommonLetterIfc.OK;
                }
                else
                {
                    // For Layaway initiate transactions, serial number should
                    // not be prompted to enter
                    if (cargo.getTransaction() instanceof LayawayTransactionIfc)
                    {
                        LayawayTransaction transaction = (LayawayTransaction)cargo.getTransaction();
                        if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
                        {
                            letter = CommonLetterIfc.OK;
                        }
                    }
                    // For special order initiate transactions, serial number should not
                    // be prompted to enter. For pickup/delivery order, serial number 
                    // station should be executed to prompt for pickup or delivery.
                    else if (cargo.getTransaction() instanceof OrderTransaction)
                    {
                        OrderTransaction transaction = (OrderTransaction)cargo.getTransaction();
                        if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE
                                && transaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
                        {
                            letter = CommonLetterIfc.OK;
                        }
                        else
                        {
                            letter = CommonLetterIfc.CONTINUE;
                        }
                    }
                    else
                    {
                        letter = CommonLetterIfc.CONTINUE;
                    }
                }
                break;
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);

    }// end arrive()
}
