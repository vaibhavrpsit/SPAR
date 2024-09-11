/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/DisplayPossibleMatchesSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/06/04 13:28:29  tmorris
 *   @scr 5246 -Too Many Matches error screen was not appearing when needed.
 *
 *   Revision 1.4  2004/06/02 13:29:01  tmorris
 *   @scr 3935 -Added the feature of sorting employees in descending order by last name.
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   31 Jan 2002 15:37:46   baa
 * fix select employe screens
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:23:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * The DisplayPossibleMatches site displays one or more matches to allow the
 * user to select or confirm the employee.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DisplayPossibleMatchesSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1428892181353474446L;

    public static final String SITENAME = "DisplayPossibleMatchesSite";

    /**
     * The DisplayPossibleMatches site displays one or more matches to allow the
     * user to select or confirm the employee.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Retrieve the data from cargo
        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();

        String letterName = "";

        // Display the possible matches
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // setup model to display data using new UI format
        DualListBeanModel model = new DualListBeanModel();
        Vector<EmployeeIfc> topList = new Vector<EmployeeIfc>();
        Vector<EmployeeIfc> bottomList = new Vector<EmployeeIfc>();

        topList.add(cargo.getEmployee());
        bottomList = cargo.getEmployeeList();

        // Get the param. Employe Maximum Matches value and if the employee list
        // is greater than display employee's otherwise display error screen
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        int maximumMatches = EmployeeUtilities.getMaximumMatches(pm, SITENAME, cargo.getOperatorID());
        int topLength = topList.size();
        int bottomLength = bottomList.size();

        if (topLength > maximumMatches || bottomLength > maximumMatches)
        {
            letterName = "TooMany";
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
        else
        {
            // Sort the list with a comparator
            Collections.sort(bottomList, new EmployeeAlphabeticOrderComparator());

            model.setTopListModel(topList);
            model.setListModel(bottomList);

            ui.setModel(POSUIManagerIfc.EMPLOYEE_SELECT_MODIFY, model);
            ui.showScreen(POSUIManagerIfc.EMPLOYEE_SELECT_MODIFY);
        }
    }

    /**
     * This class organizes employee names in descending alphabetical order
     */
    public static class EmployeeAlphabeticOrderComparator implements Comparator
    {
        /**
         * @param o1 EmployeeIfc[]
         * @param o2 EmployeeIfc[]
         * @return @see java.util.Comparator#compare(java.lang.Object,
         *         java.lang.Object)
         */
        public int compare(Object o1, Object o2)
        {

            int result = 0;

            // If not an instance of EmployeeIfc return 0
            if (!(o1 instanceof EmployeeIfc) || !(o2 instanceof EmployeeIfc))
            {
                return result;
            }

            String personNameOne = "";
            String personNameTwo = "";

            EmployeeIfc item0 = (EmployeeIfc)o1;
            EmployeeIfc item1 = (EmployeeIfc)o2;

            // This comparator sorts by last name
            personNameOne = ((PersonNameIfc)item0.getPersonName()).getLastName();
            personNameTwo = ((PersonNameIfc)item1.getPersonName()).getLastName();

            // Descending order -> To change to ascending remove the "-"
            result = -(LocaleUtilities.compareValues(personNameOne, personNameTwo));

            return result;

        }
    }
}