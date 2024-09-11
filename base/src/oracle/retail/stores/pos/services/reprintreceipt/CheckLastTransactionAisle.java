/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/CheckLastTransactionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 04 2002 11:02:20   dfh
 * set cargo flag, user did not enter trans id to reprint
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 21 2001 11:23:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This road is traveled when the last transaction option has been 
    selected.  It is used to determine if the last transaction ID is
    available. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckLastTransactionAisle extends LaneActionAdapter
{                                       // begin CheckLastTransactionAisle        
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Retrieves the transaction ID from the user interface and stores 
        it in the cargo.   A Continue letter is mailed. <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        // if no reprintable last transaction is available, issue not found letter
        String letterName = CommonLetterIfc.NEXT;
        if (Util.isEmpty(cargo.getLastReprintableTransactionID()))
        {
            letterName = ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LETTER;
            cargo.setNonReprintableErrorID
              (ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LAST_TRANSACTION_NOT_AVAILABLE);
        }
        else
        {
            cargo.setTransactionID(cargo.getLastReprintableTransactionID());
            cargo.setTransactionIDEntered(false);
        }
        
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }                                   // end traverse()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = 
           Util.classToStringHeader("CheckLastTransactionAisle",
                                    revisionNumber,
                                    hashCode()).toString();
        return(strResult);
    }                                   // end toString()

}                                       // end class CheckLastTransactionAisle
