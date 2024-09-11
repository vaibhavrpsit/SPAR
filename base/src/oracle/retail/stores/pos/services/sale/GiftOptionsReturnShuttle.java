/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/GiftOptionsReturnShuttle.java /main/13 2011/12/05 12:16:29 cgreene Exp $
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
 *    6    360Commerce 1.5         5/12/2006 5:25:27 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         2/10/2006 11:06:44 AM  Deepanshu       CR
 *         6092: Sales Assoc sould be last 4 digits of Sales Assoc ID and not
 *         of Cashier ID on the recipt
 *    4    360Commerce 1.3         1/22/2006 11:45:01 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/25 15:58:29  epd
 *   @scr 3561 Reversed earlier change.  backed out the change
 *
 *   Revision 1.4  2004/02/23 23:29:39  crain
 *   @scr 3814 Fixed double-adding of the item
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Dec 16 2003 10:15:28   lzhao
 * fix nullpointerexception
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.2   Dec 12 2003 14:06:58   lzhao
 * Fix the problem when transaction is null.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.1   Nov 26 2003 09:12:34   lzhao
 * remove tendering.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   20 Nov 2003 00:14:10   baa
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the GiftCard service to the cargo used in the Sale service. <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class GiftOptionsReturnShuttle implements ShuttleIfc
{                                       // begin class GiftOptionsReturnShuttle()
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7863013627048107274L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.GiftOptionsReturnShuttle.class);

    /**
       revision number supplied by pvcs
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
        the gift card service cargo
    **/
    protected GiftCardCargo  giftcardCargo;

    //----------------------------------------------------------------------
    /**
       Loads cargo from GiftCardActivation service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the retail transaction
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        giftcardCargo = (GiftCardCargo) bus.getCargo();

    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads cargo for POS service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the retail transaction
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        PLUItemIfc               item        = null;
        SaleReturnTransactionIfc transaction = null;
        SaleCargoIfc             cargo       = null;
        
        BigDecimal quantity  = giftcardCargo.getItemQuantity();
        /*
         * The system will not update the SaleCargo if without issue/reload gift card.
         */
        if ( quantity.compareTo(BigDecimal.ONE) == 0 )
        { 
            cargo = (SaleCargoIfc) bus.getCargo();
            item        = giftcardCargo.getPLUItem();
            transaction = giftcardCargo.getTransaction();
            if ( ( item != null ) && ( transaction != null ) )
            {
                cargo.setTransaction(transaction);
                cargo.setPLUItem(item);
                cargo.setLineItem(transaction.addPLUItem(item, quantity));
                cargo.setItemQuantity(quantity);
                cargo.setItemScanned( giftcardCargo.isItemScanned() );
                cargo.setSalesAssociate(giftcardCargo.getSalesAssociate());
            }
            else if (transaction != null)
            {
                // this case is used when transaction sequence number is generated, but then Esc is used
                cargo.setPLUItem(null);
                cargo.setTransaction(transaction);
            }
        }
    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  GiftCardReloadReturnShuttle (Revision " +
                                      revisionNumber +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

}                                       // end class GiftOptionsReturnShuttle
