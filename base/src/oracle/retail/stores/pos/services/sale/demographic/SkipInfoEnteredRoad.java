/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/demographic/SkipInfoEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:18 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   08 Nov 2003 01:22:46   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.1   Nov 07 2003 07:45:18   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 13:58:44   jriggins
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.demographic;

// foundation imports
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Sets that the customerInfo has been skipped.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SkipInfoEnteredRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by PVCS
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets that the customerInfo has been skipped
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

        // get customerInfo from cargo
        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();
        customerInfo.setSkipped(true);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()
}
