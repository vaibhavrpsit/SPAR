/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/CheckDateAisle.java /main/10 2011/02/16 09:13:26 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:56:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterBusinessDateBeanModel;

/**
 * This class validates the input business date and then evaluates the input
 * business date against the current date and spawns the appropriate letter.
 * 
 * @version $Revision: /main/10 $
 */
public class CheckDateAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 8243947067665131484L;

    /**
     * lane name constant
     */
    public static final String LANENAME = "CheckDateAisle";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/10 $";

    /**
     * Performs the traversal functionality for the aisle. In this case, the
     * input date is checked for date validity and is compared to the default
     * business date.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        Letter letter = null;

        // get default date from cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        EYSDate defaultDate = cargo.getDefaultBusinessDate();

        // get input date from UI
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        EnterBusinessDateBeanModel beanModel =
            (EnterBusinessDateBeanModel) ui.getModel(POSUIManagerIfc.ENTER_BUSINESS_DATE);
        EYSDate inputDate = beanModel.getBusinessDate();
        if (logger.isInfoEnabled()) logger.info(
                    "Input date:  [" + inputDate + "] default date: [" + defaultDate + "]");

        // set input date in cargo
        cargo.setInputBusinessDate(inputDate);

        // confirm date is valid
        if (inputDate.isValid())
        {                               // begin handle valid date
            // set date in cargo for handling later
            cargo.setInputBusinessDate(inputDate);
            // if input date after default business date
            if (inputDate.after(defaultDate))
            {
                letter = new Letter("LaterDate");
            }
            // if input date before default business date
            else if (inputDate.before(defaultDate))
            {
                letter = new Letter("PastDate");
            }
            // date is valid
            else
            {
                letter = new Letter(CommonLetterIfc.SUCCESS);
            }
        }                               // end handle valid date
        // if invalid date, set invalid-date-letter
        else
        {
            letter = new Letter("InvalidDate");
        }

        // mail appropriate letter
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}