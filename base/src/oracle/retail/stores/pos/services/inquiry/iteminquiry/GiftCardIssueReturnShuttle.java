/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/GiftCardIssueReturnShuttle.java /main/12 2011/12/05 12:16:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vikini    01/20/10 - Setting undo to cancel
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:25:29 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:10 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Jan 09 2004 12:54:30   lzhao
 * set transaction back, remove comments, add date
 * Resolution for 3666: Eltronic Journal for Gift Card Issue  and Reload not Correct
 * 
 *    Rev 1.2   Dec 19 2003 15:21:46   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Dec 18 2003 09:40:28   lzhao
 * fix return problem
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Dec 12 2003 14:30:56   lzhao
 * Initial revision.
 * 
 *    Rev 1.0   Dec 08 2003 09:11:10   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

/**
 * This shuttle copies information from the cargo used in the GiftCardOption
 * service to the cargo used in the Sale service.
 * 
 * @version $Revision: /main/12 $
 */
public class GiftCardIssueReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2939709116763560956L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(GiftCardIssueReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Gift card cargo
     */
    protected GiftCardCargo giftCardCargo = null;

    /**
     * Loads cargo from GiftCardOption service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        giftCardCargo = (GiftCardCargo) bus.getCargo();
    }

    /**
     * Loads cargo for Sale service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        if (giftCardCargo.getItemQuantity().compareTo(BigDecimal.ONE) == 0)
        {
            cargo.setPLUItem(giftCardCargo.getPLUItem());
            cargo.setItemQuantity(giftCardCargo.getItemQuantity());
            cargo.setTransaction(giftCardCargo.getTransaction());
        }
        else
        {
            cargo.setPLUItem(null);
            cargo.setTransaction(giftCardCargo.getTransaction());
        }
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
      // result string
        String strResult = new String("Class:  GiftCardIssueReturnShuttle (Revision " + revisionNumber + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    }
}