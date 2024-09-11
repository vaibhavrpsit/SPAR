/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/PrepareBusinessDateSite.java /main/10 2011/02/16 09:13:26 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:31  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;

import java.util.Calendar;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site checks the date list passed in through the shuttle. If only one
 * date is in the list, it sets the next default date, as needed.
 * <P>
 * The appropriate letter is mailed to present a selection list or an entry
 * screen to determine the business date.
 * 
 * @version $Revision: /main/10 $
 */
public class PrepareBusinessDateSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5808257725857468666L;

    /**
     * revision number supplied by source-code-control system
     **/
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * site name constant
     **/
    public static final String SITENAME = "PrepareBusinessDateSite";

    /**
     * Displays store-open screen, using store-status business date in cargo as
     * the default business date.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to be mailed at conclusion of method
        Letter letter;

        // pull business date list from cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        EYSDate[] dateList = cargo.getBusinessDateList();

        if (dateList.length > 1)
        {
            letter = new Letter("MultipleMatches");
        }
        else
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);

            // first date is default date
            EYSDate defaultDate = dateList[0];

            // if date needs to be rolled, do it
            if (cargo.isAdvanceDateFlag())
            {
                defaultDate = setNextBusinessDate(defaultDate);
            }
            if (logger.isInfoEnabled())
                logger.info("Default business date: [" + defaultDate + "]");

            // set default in cargo
            cargo.setDefaultBusinessDate(defaultDate);

        }

        // mail appropriate letter
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Sets next business date. In some implementations, this may be
     * accomplished by reading from a database. Always increments the calendar
     * date. If the current date is the last day of the year (12/31), increments
     * the year. If the current day is the last day of the month, increments the
     * month and resets the day to 1.
     * 
     * @param date EYSDate object
     **/
    protected EYSDate setNextBusinessDate(EYSDate date)
    {
        // clone business date and roll date by one, increment business day
        // per calendar constraints - update pieces (month, day, year)
        EYSDate nextBusinessDate = (EYSDate) date.clone();

        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();

        boolean rollMonthSetDayOne = false;
        boolean rollYear = false;

        // last day of the year, roll the year
        if (month == 12 && day == 31)
        {
            rollYear = true;
        }

        // are we at the last day of a month, roll the month
        switch (month)
        {
        // check februrary
        case 2:
            // leap years have 29
            // century years don't have leap years except every 400 years
            if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
            {
                if (day == 29)
                {
                    rollMonthSetDayOne = true; // increment month set day to 1
                }
            }
            // check non-leap years
            else
            {
                if (day == 28)
                {
                    rollMonthSetDayOne = true; // increment month set day to 1
                }
            }
            break;

        // check 30-day months
        case 4:
        case 6:
        case 9:
        case 11:
            if (day == 30)
            {
                rollMonthSetDayOne = true; // increment month set day to 1
            }
            break;

        // check 31-day months
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            if (day == 31)
            {
                rollMonthSetDayOne = true; // increment month set day to 1
            }
            break;

        } // end switch

        // determine if we need to roll the year
        if (rollYear)
        {
            nextBusinessDate.roll(Calendar.YEAR, true);
        }

        // determine if we need to roll the month and reset day to 1
        if (rollMonthSetDayOne)
        {
            nextBusinessDate.roll(Calendar.MONTH, true);
            nextBusinessDate.setDay(1);
        }

        else
        // only roll the day not last day of month
        {
            nextBusinessDate.roll(Calendar.DATE, true);
        }

        // set default business date in cargo and return
        // cargo.setDefaultBusinessDate(nextBusinessDate);
        return (nextBusinessDate);
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}