/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/InBoxSearchSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:23 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:23  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 08 2002 17:00:28   dfh
 * remove log info
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 24 2001 11:17:30   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EMessageReadDataTransaction;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;

//------------------------------------------------------------------------------
/** Searches for all new emails.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class InBoxSearchSite extends PosSiteActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "InBoxSearchSite";

    //--------------------------------------------------------------------------
    /**
       Searches for all new emails.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        EmailCargo cargo = (EmailCargo)bus.getCargo();
        cargo.setSearchMethod(EmailCargo.SEARCH_FOR_NEW_EMAILS); // In Box search

        EMessageIfc[] emsgList = null;
        Letter result = new Letter (CommonLetterIfc.SUCCESS);
        
        EMessageReadDataTransaction eTransaction = null;
        
        eTransaction = (EMessageReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.EMESSAGE_READ_DATA_TRANSACTION);

        int[] statusList = new int[1];
        statusList[0] = 0;

        String storeID = cargo.getStoreID();

        EYSDate beginDate = null;
        EYSDate endDate = null;

        // retrieve messages with a status of "New"
        try
        {
            emsgList = eTransaction.retrieveEMessagesByStatus(
                                                              statusList, storeID, beginDate, endDate);

            if (logger.isInfoEnabled()) logger.info( "EMessages found");
            int listSize = emsgList.length;

            cargo.setEMessageList(emsgList);

            // if there is only one match, send a one match letter
            if (listSize == 1)
            {
                // make it the selected email
                cargo.setSelectedMessage(emsgList[0]);
                result = new Letter("OneMatch");
            }
        }
        catch (DataException de)
        {
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            // if no matches were found, on add that's not an error.  Go on.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(CommonLetterIfc.NOT_FOUND);
                logger.warn( " No new Emails found :  No MATCHES !!!");
            }
            else
            {    // take care of database errors

                result = new Letter(CommonLetterIfc.DB_ERROR);
                logger.error( " DB error: " + de.getMessage() + "");
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
        }

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);
    }
}
