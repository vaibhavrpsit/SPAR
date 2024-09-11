/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckTransactionLevelSendAssignedSite.java /main/12 2012/04/17 13:33:54 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/08/27 21:07:02  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.2  2004/08/10 15:22:12  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.1  2004/08/10 15:20:34  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

//java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


//------------------------------------------------------------------------------
/**
    Site to check if transaction level send has been assigned
    $Revision: /main/12 $
**/
//------------------------------------------------------------------------------
public class CheckTransactionLevelSendAssignedSite extends PosSiteActionAdapter
{
    /**
       revision number 
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";
 
    //--------------------------------------------------------------------------
    /**
       Checks if send level is transaction
       @param  bus service bus reference
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.NEXT);
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        if(cargo.getTransaction() != null && cargo.getTransaction() instanceof SaleReturnTransaction && 
           ((SaleReturnTransaction)cargo.getTransaction()).isTransactionLevelSendAssigned())
        {
             letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
