/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/MailValidExitLetterSite.java /main/11 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         5/7/2008 8:55:11 PM    Alan N. Sinton  CR
 *        30295: Code modified to present  Function Unavailable dialog for
 *        House Account and Instant Credit when configured with ISD.  Code
 *        reviewed by Anda Cadar.
 *   3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:24 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:31 PM  Robert Pearse   
 *
 *  Revision 1.2  2004/05/20 22:54:58  cdb
 *  @scr 4204 Removed tabs from code base again.
 *
 *  Revision 1.1  2004/05/07 13:11:49  tfritz
 *  @scr 3909 The exit of the enrollment use case now returns to the sell item screen.
 *  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Changes some of the letters to reduce number of letters that needs to be
 * handles in the calling xml.
 * 
 * @version $Revision: /main/11 $
 */
public class MailValidExitLetterSite extends PosSiteActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -6045362980497782588L;

    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String name = bus.getCurrentLetter().getName();

        if (name.equals(CommonLetterIfc.SUCCESS) || name.equals(CommonLetterIfc.DONE))
        {
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        else if (name.equals("Noswipe") || name.equals("NotSupported"))
        {
            bus.mail(CommonLetterIfc.FAILURE);
        }
    }
}
