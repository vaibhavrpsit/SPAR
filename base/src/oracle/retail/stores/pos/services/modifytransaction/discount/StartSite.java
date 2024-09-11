/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/StartSite.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   04/11/12 - Changed cargo type to DiscountCargoIfc so site could
 *                         be used from mobile POS transdiscount tour.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:23 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:10  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:02:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:16:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:52   msg
 * Initial revision.
 *
 *    Rev 1.1   17 Jan 2002 14:14:22   pjf
 * Modified to use new security access service, deprecated previous security classes, corrected SCRs 404,405.
 * Resolution for POS SCR-404: Security Override continually loops in Trans Disc Amt
 *
 *    Rev 1.0   Sep 21 2001 11:30:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.discount;
// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;

//--------------------------------------------------------------------------
/**
    Sets reason codes. <P>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class StartSite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
       Sets reason codes. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // retrieve cargo
        DiscountCargoIfc cargo = (DiscountCargoIfc) bus.getCargo();

        String letterName = "Percent";

        // check to see what type of discount this is
        if ((cargo.getDiscountType()) == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
        {
            // if this is an amount discount - mail a DiscountDollar letter
            letterName = "Dollar";
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
