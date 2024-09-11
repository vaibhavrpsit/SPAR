/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/EvaluateSearchCriteriaSite.java /main/13 2013/03/01 17:09:39 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  03/01/13 - updated the default search criteria letter to
 *                         receipt.. added a condition to post Tender letter..
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Pre code reveiw clean up.
 *    jswan     05/11/10 - Returns flow refactor: modified to support entry of
 *                         transaction ID on the ReturnOptions screen.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:56 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 29 2003 15:36:08   baa
 * return enhancements
 * 
 *    Rev 1.0   Dec 17 2003 11:31:24   baa
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:06:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 14 2003 09:26:04   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 * 
 *    Rev 1.1   Aug 14 2002 14:16:08   jriggins
 * Switched call from displayInvalidTransaction() to displayInvalidTransactionNoSellItems().
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:05:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:42   msg
 * Initial revision.
 * 
 *    Rev 1.3   Mar 10 2002 18:01:16   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Jan 22 2002 17:40:08   dfh
 * initial support for order partial
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 10 2001 10:06:14   dfh
 * added check to include order complete transactions
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 21 2001 11:24:58   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:12:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

// java imports
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    Evaluate which transaction lookup path to take:
        1. Enter transaction ID (Receipt)
        2. Lookup using previously entered transaction ID (TransactionID)
        3. Lookup using other previously entered search criteria (Tender) 
**/
//--------------------------------------------------------------------------
public class EvaluateSearchCriteriaSite extends PosSiteActionAdapter 
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8039907230015854221L;

    //----------------------------------------------------------------------
    /**
       Read the transaction.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Initialize the letter to the default value.
        String letter = "Receipt";
        
        // Get the cargo and check for available search criteria 
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        
        // It the transaction ID has already been entered, search using that data element.
        if (cargo.getOriginalTransactionId() != null && 
            !Util.isEmpty(cargo.getOriginalTransactionId().getTransactionIDString()))
        {
            letter = "TransactionID";
        }
        else if (cargo.getSearchCriteria() != null)
        {
            letter = "Tender";
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
