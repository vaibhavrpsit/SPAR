/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/LinkOrDoneSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:32:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:52   msg
 * Initial revision.
 * 
 *    Rev 1.3   10 Dec 2001 15:56:10   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 * 
 *    Rev 1.2   28 Nov 2001 17:46:36   baa
 * No change.
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 * 
 *    Rev 1.1   16 Nov 2001 10:34:16   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:16:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks to see if the customer should be linked and mails the
    appropriate letter.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkOrDoneSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Mails a Continue letter if the customer is to be linked, otherwise
       mail a Retry letter. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        String letterName = CommonLetterIfc.RETRY;

        if (cargo.isLink() && cargo.getCustomer() != null)
        {
            letterName = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
