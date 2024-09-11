/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/CompleteReturnProcessingSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     07/05/10 - Latest changes.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/16/2007 5:03:12 PM   Owen D. Horne
 *         CR#24874 - Merged fix from v8.0.1
 *         6    .v8x       1.3.1.1     4/20/2007 10:32:38 AM  Michael Wisbauer
 *          added
 *         new method to get the right original item based on orgiginal line
 *         number for quantities to update.
 *         5    .v8x       1.3.1.0     4/10/2007 10:15:10 AM  Michael Wisbauer
 *         Modified how sequence numbers are being set mostly for the orginal
 *         transactions for refunds so items are updated correclty in the db.
 *    4    360Commerce 1.3         1/22/2006 11:45:20 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:08 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/07 18:17:16  blj
 *   @scr 5966 - resolution
 *
 *   Revision 1.1  2004/03/25 15:07:16  baa
 *   @scr 3561 returns bug fixes
 *
 *   Revision 1.6  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.5  2004/03/18 23:01:56  baa
 *   @scr 3561 returns fixes for gift card
 *
 *   Revision 1.4  2004/03/10 14:16:47  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 10 2003 17:38:12   sfl
 * Coment out the duplicate partial return processing.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:04:32   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:14   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.0   Mar 18 2002 11:46:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:43:24   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:25:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
    This road set the the transfer flag to true and clears the selected list.
**/
//--------------------------------------------------------------------------
public class CompleteReturnProcessingSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -2305595030774382935L;

    //----------------------------------------------------------------------
    /**
       Sets up return data to be transfer out of this service
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Every thing is good to go.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        cargo.setTransferCargo(true);
        cargo.completeReturnProcess();
                
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
