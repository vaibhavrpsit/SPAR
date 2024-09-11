/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/CheckTransactionListLengthSite.java /main/11 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 07 2003 13:10:16   sfl
 * Rechecking
 * Resolution for POS SCR-1889: Retrieval of Suspended Transactions while DB offline
 * 
 *    Rev 1.0   Jan 07 2003 12:46:34   sfl
 * Initial revision.
 * Resolution for POS SCR-1889: Retrieval of Suspended Transactions while DB offline
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Check the length of suspended transaction list and mail different letters. So
 * that when database is offline, no retrieved suspended transaction situation
 * will be handled
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class CheckTransactionListLengthSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "CheckTransactionListLengthSite";

    /**
     * Check the length of suspended transaction list. So that when database is
     * offline, no retrieved suspended transaction situation will be handled.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean r_c = false;

        // get the cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        if (cargo.getSuspendList() != null)
        {
            if (cargo.getSuspendList().length > 1)
            {                               // Begin more than one transaction in the list
                r_c = true;
            }                              // End more than one transaction in the list

            if (cargo.getSuspendList().length == 1)
            {                               // Begin only one transaction in the list
                r_c = true;
            }                              // End only one transaction in the list

        }


        // if one or more transactions, send ok letter. If no transaction, send DBError
        if (r_c)
        {
            bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);
        }
        // if transaction is in progress, display error message
        else
        {
            bus.mail(new Letter(CommonLetterIfc.FAILURE), BusIfc.CURRENT);
        }


    } // end arrive()

}
