/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/LookupEmailByOrderIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    deghosh   02/18/09 - Removed the parameters
 *                         'EmailReplyURL','EmailFromAddress' and
 *                         'EmailMaximumMatches'
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
 * $
 * Revision 1.7  2004/06/03 14:47:43  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.6  2004/04/20 13:13:09  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.5  2004/04/12 18:49:35  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.4  2004/03/03 23:15:13  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:50:12  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:48:23  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:16
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 15:58:56 CSchellenger Initial revision.
 *
 * Rev 1.1 Jul 31 2002 11:18:30 jriggins Replaced Integer.parseInt() to
 * LocaleUtilities.parseNumber()
 *
 * Removed unnecessary imports Resolution for POS SCR-1740: Code base
 * Conversions
 *
 * Rev 1.0 Apr 29 2002 15:24:50 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:31:46 msg Initial revision.
 *
 * Rev 1.0 Sep 24 2001 11:17:30 MPM Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;
// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EMessageReadDataTransaction;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;

//------------------------------------------------------------------------------
/**
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------

public class LookupEmailByOrderIDSite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * site name constant
     */
    public static final String SITENAME = "LookupEmailByOrderIDSite";

    /**
     * default email maximum matches constant
     */
    public static final int DEFAULT_EMAIL_MAXIMUM_MATCHES = 50;

    //--------------------------------------------------------------------------
    /**
     * @param bus
     *            the bus arriving at this site
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        EmailCargo cargo = (EmailCargo) bus.getCargo();
        cargo.setSearchMethod(EmailCargo.SEARCH_BY_ORDER_ID);
        EMessageIfc[] emsgList = null;
        Letter result = new Letter(CommonLetterIfc.SUCCESS);

        EMessageReadDataTransaction eTransaction = null;

        eTransaction = (EMessageReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.EMESSAGE_READ_DATA_TRANSACTION);

        int maxMatchesInt = DEFAULT_EMAIL_MAXIMUM_MATCHES;

        int[] statusList = new int[1];
        statusList[0] = 0;

        OrderIfc order = DomainGateway.getFactory().getOrderInstance();
        order.setOrderID(cargo.getOrderID());

        EYSDate beginDate = null;
        EYSDate endDate = null;

        // if we are doing a narrow search, get the dates
        if (cargo.getDateRange())
        {
            beginDate = cargo.getStartDate();
            endDate = cargo.getEndDate();
        }

        // retrieve messages through a query
        try
        {
            emsgList =
                eTransaction.retrieveEMessagesByOrderID(
                    order,
                    beginDate,
                    endDate);

            if (logger.isInfoEnabled())
                logger.info("EMessages found: ");

            int listSize = emsgList.length;

            if (listSize > maxMatchesInt)
            {
                result = new Letter(CommonLetterIfc.TOO_MANY);
                // too many for status search
                logger.error(
                    "Error:  too many matches (> "
                        + Integer.toString(maxMatchesInt)
                        + ")");
            }
            else
            {
                for (int i = 0; i < listSize; i++)
                {
                    if (logger.isInfoEnabled())
                        logger.info(emsgList[i].toString());
                }
                cargo.setEMessageList(emsgList);
            }

            // if there is only one match, send a one match letter
            if (listSize == 1)
            {
                result = new Letter(CommonLetterIfc.ONEMATCH);
                cargo.setSelectedMessage(emsgList[0]);
            }
        }
        catch (DataException de)
        {
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());

            // if no matches were found, on add that's not an error. Go on.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(CommonLetterIfc.NOT_FOUND);
                logger.warn(" email by order id:  No MATCHES !!!");
            }
            else
            { // take care of database errors

                result = new Letter(CommonLetterIfc.DB_ERROR);
                logger.error(" DB error: " + de.getMessage() + "");
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
        }

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of the object.
     * <P>
     *
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String(
                "Class: "
                    + SITENAME
                    + " (Revision "
                    + getRevisionNumber()
                    + ")"
                    + hashCode());

        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}
