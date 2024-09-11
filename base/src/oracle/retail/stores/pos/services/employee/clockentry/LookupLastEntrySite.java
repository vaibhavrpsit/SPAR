/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/LookupLastEntrySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/12 18:52:57  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:15  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   07 May 2002 15:24:18   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 * 
 *    Rev 1.0   Apr 29 2002 15:24:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   28 Oct 2001 17:55:52   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.clockentry;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    Retrieves the last clock entry for the entered employee and updates
    the cargo values for the entry code In, Out, and label strings from the 
    property file .
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class LookupLastEntrySite
extends PosSiteActionAdapter
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       employee clock entry spec property constant
    **/
    public static String EMPLOYEE_CLOCK_ENTRY_SPEC = "EmployeeClockEntrySpec";    
    /**
       entry type code in property name constant
    **/
    public static String ENTRY_TYPE_CODE_IN = "TypeCodeIn";
    /**
       entry type code out property name constant
    **/
    public static String ENTRY_TYPE_CODE_OUT = "TypeCodeOut";
    /**
       entry type code label property name constant
    **/
    public static String ENTRY_TYPE_CODE_LABEL = "TypeCodeLabel";
    /**
       entry type in default string value
    **/   
    public static String TYPE_IN = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_IN];
    /**
       entry type out default string value
    **/
    public static String TYPE_OUT = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_OUT];
    /**
       entry type label default string value
    **/
    public static String ENTRY_TYPE = "Entry Type:";

    //--------------------------------------------------------------------------
    /**
        Retrieves the last clock entry for the entered employee and sets the
        cargo values for the entry code In, Out, and label strings from the 
        property file.
        @param bus  the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
        cargo.setTypeCodeInString(utility.retrieveText(EMPLOYEE_CLOCK_ENTRY_SPEC,
                                             BundleConstantsIfc.EMPLOYEE_BUNDLE_NAME,
                                             ENTRY_TYPE_CODE_IN,
                                             TYPE_IN));

        cargo.setTypeCodeOutString(utility.retrieveText(EMPLOYEE_CLOCK_ENTRY_SPEC,
                                             BundleConstantsIfc.EMPLOYEE_BUNDLE_NAME,
                                             ENTRY_TYPE_CODE_OUT,
                                             TYPE_OUT));
        cargo.setEntryTypeString(utility.retrieveText(EMPLOYEE_CLOCK_ENTRY_SPEC,
                                             BundleConstantsIfc.EMPLOYEE_BUNDLE_NAME,
                                             ENTRY_TYPE_CODE_LABEL,
                                             ENTRY_TYPE));
        try
        {
            // retrieve last clock entry for employee
            EmployeeTransaction readDataTransaction = null;
            readDataTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
            
            EmployeeClockEntryIfc lastEntry =
              readDataTransaction.readEmployeeLastClockEntry
                (cargo.getStoreStatus().getStore().getStoreID(),
                 cargo.getClockingEmployee().getLoginID());

            // put clock entry time in cargo
            cargo.setLastEntry(lastEntry.getClockEntry());
            cargo.setClockEntry(lastEntry);
        }
        // eat the data exception
        catch (DataException de)
        {
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
