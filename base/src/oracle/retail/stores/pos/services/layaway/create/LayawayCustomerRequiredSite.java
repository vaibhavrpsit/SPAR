/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/LayawayCustomerRequiredSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    Determines if a customer is already linked to the transaction and mails 
    the appropriate letter to proceed.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LayawayCustomerRequiredSite extends PosSiteActionAdapter
{
    /** 
        class name constant 
    **/ 
    public static final String SITENAME = "LayawayCustomerRequiredSite"; 
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

   //----------------------------------------------------------------------
    /**
        Determines if the customer is currencly linked to the
        transaction. Mails Continue letter is customer is linked or
        Link if customer is not linked.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // need to determine if customer is already linked to this transaction
        // if not linked then call customer service
        // if linked, then mail continue letter to customer layaway screen
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();

                if (cargo.getSaleTransaction() != null &&
           cargo.getSaleTransaction().getCustomer() != null)
                {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.LINK), BusIfc.CURRENT);   
        }     
    }
}
