/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/ValidateDiscountsSite.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/23/12 - Tour hangs if transaction on on the cargo. Should
 *                         never happen. (But with initial experimental version
 *                         of MPOS it was.)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   08 Nov 2003 01:27:26   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 17:40:36   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;


// foundation imports
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
    This site checks if the transaction has alteration item(s).
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class ValidateDiscountsSite extends PosSiteActionAdapter
{
    /**
        Revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";


    //----------------------------------------------------------------------
    /**
        Check if the transaction is null and mail a proper letter
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Default the letter value to Continue
        String                    letter      = "CheckDiscounts";
        SaleCargoIfc              cargo       = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc  transaction = cargo.getTransaction();

        if (transaction != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        {
            logger.error("transaction is null!");
            bus.mail(new Letter("Failure"), BusIfc.CURRENT);
        }
    }

}
