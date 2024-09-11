/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/ReasonCodeSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/05/08 - I18N Reason Code - Refactored the EmployeeClockEntry
 *                         reason field.
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:50:15  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:49:15  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:59:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 26 2003 13:58:48   RSachdeva
 * Removed use of CodeEntry.getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.1   07 May 2002 15:24:20   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 15:24:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:00   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Oct 2001 17:55:52   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.employee.clockentry;
// foundation imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeClockEntryBeanModel;

//------------------------------------------------------------------------------
/**
    Retrieves selected entry type code and reason code from user interface and
    builds entry for saving to database. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ReasonCodeSelectedRoad
extends PosLaneActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 2787415334198181388L;
    /**
        lane name constant
    **/
    public static final String LANENAME = "ReasonCodeSelectedRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Retrieves selected entry type code and reason code from user interface
        and builds entry for saving to database. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // retrieve bean model from user interface
        EmployeeClockEntryBeanModel beanModel = (EmployeeClockEntryBeanModel)
          ui.getModel(POSUIManagerIfc.EMPLOYEE_CLOCK_ENTRY);

        // pull out reason code
        String reasonCode = beanModel.getSelectedReasonKey();
        CodeEntryIfc reasonEntry =
          cargo.getReasonCodes().findListEntryByCode(reasonCode);

        int typeInt = beanModel.getSelectedTypeCodeIndex();

        // set up new clock entry for writing to database
        EmployeeClockEntryIfc clockEntry =
          DomainGateway.getFactory().getEmployeeClockEntryInstance();
        clockEntry.setClockEntry(beanModel.getCurrentEntry());
        clockEntry.setEmployee(cargo.getClockingEmployee());
        clockEntry.setStoreID(cargo.getStoreStatus().getStore().getStoreID());

        LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
        reason.setCode(reasonCode);
        reason.setText(reasonEntry.getLocalizedText());
        clockEntry.setReason(reason);

        // pull out entry type code
        clockEntry.setTypeCode(typeInt);
        cargo.setClockEntry(clockEntry);
    }
}
