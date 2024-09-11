/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/OperatorRequiredSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse   
 *
 *Revision 1.4  2004/07/19 15:38:10  nrao
 *@scr 6162 Added check to see if Operator ID had been
 *previously captured. If so, bypassed Identify Operator
 *use case. Otherwise, executed Identify Operator use case.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 24 2003 19:46:56   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OperatorRequiredSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        
        if(cargo.getOperator()!= null && cargo.getOperator().getLoginID() != null)
        {
            bus.mail(new Letter("Bypass"), BusIfc.CURRENT);
        }
        else
        {
            bus.mail(new Letter("OperatorID"), BusIfc.CURRENT);
        }
    }
}
