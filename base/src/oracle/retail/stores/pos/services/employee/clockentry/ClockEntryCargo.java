/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/ClockEntryCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc

  $Log:
   3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
   2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse
   1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse
  $
  Revision 1.4  2004/09/27 22:32:03  bwf
  @scr 7244 Merged 2 versions of abstractfinancialcargo.

  Revision 1.3  2004/02/12 16:50:15  mcs
  Forcing head revision

  Revision 1.2  2004/02/11 21:49:15  rhafernik
  @scr 0 Log4J conversion and code cleanup

  Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
  updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:59:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   May 07 2003 14:56:12   adc
 * Added security access check
 * Resolution for 2327: jkl (no access employee) able to enter Clock In/Out, never prompted for Manger Override
 *
 *    Rev 1.3   Feb 12 2003 18:14:32   crain
 * Refactored getReasonCodes()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.2   Feb 07 2003 18:40:22   crain
 * Replaced getCodeListMap()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.1   07 May 2002 15:24:16   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 15:24:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:54   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Oct 2001 17:55:48   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.employee.clockentry;
// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

//------------------------------------------------------------------------------
/**
    This is the cargo for the clock entry service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ClockEntryCargo
extends AbstractFinancialCargo
implements CargoIfc
{
    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        last time entry for the user
    **/
    protected EYSDate lastEntry = null;
    /**
        employee
    **/
    protected EmployeeIfc clockingEmployee = null;
    /**
        clock entry
    **/
    protected EmployeeClockEntryIfc clockEntry = null;
    /**
        clock entry type In string set from property file
    **/
    protected String typeCodeInString = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_IN];
    /**
        clock entry type Out string set from property file
    **/
    protected String typeCodeOutString = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_OUT];
    /**
        clock entry type label string set from property file
    **/
    protected String entryTypeString = "";
    /**
        reason codes
    **/
    protected CodeListIfc reasonCodes = null;

    //---------------------------------------------------------------------
    /**
        Sets the last clock entry. <P>
        @param value  the last clock entry
    **/
    //---------------------------------------------------------------------
    public void setLastEntry(EYSDate value)
    {
        lastEntry = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the last clock entry. <P>
        @return the last clock entry
    **/
    //---------------------------------------------------------------------
    public EYSDate getLastEntry()
    {
        return(lastEntry);
    }

    //---------------------------------------------------------------------
    /**
        Sets the clock entry. <P>
        @param value  the clock entry
    **/
    //---------------------------------------------------------------------
    public void setClockEntry(EmployeeClockEntryIfc value)
    {
        clockEntry = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the clock entry. <P>
        @return the clock entry
    **/
    //---------------------------------------------------------------------
    public EmployeeClockEntryIfc getClockEntry()
    {
        return(clockEntry);
    }

    //---------------------------------------------------------------------
    /**
        Sets the clocking employee. <P>
        @param value  the clocking employee
    **/
    //---------------------------------------------------------------------
    public void setClockingEmployee(EmployeeIfc value)
    {
        clockingEmployee = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the clocking employee. <P>
        @return the clocking employee
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getClockingEmployee()
    {
        return(clockingEmployee);
    }

    //---------------------------------------------------------------------
    /**
        Returns the reason code list. <P>
        @return the reason code list
    **/
    //---------------------------------------------------------------------
    public CodeListIfc getReasonCodes()
    {
        return reasonCodes;
    }

    /**
     * Sets the Reason Codes
     * @param reasonCodes
     */
    public void setReasonCodes (CodeListIfc reasonCodes)
    {
        this.reasonCodes = reasonCodes;
    }

    //---------------------------------------------------------------------
    /**
        Sets the entry type code In string value. <P>
        @param value  entry type code In string
    **/
    //---------------------------------------------------------------------
    public void setTypeCodeInString(String value)
    {
        typeCodeInString = value;
    }

    //---------------------------------------------------------------------
    /**
        Sets the entry type code Out string value. <P>
        @param value  entry type code Out string
    **/
    //---------------------------------------------------------------------
    public void setTypeCodeOutString(String value)
    {
        typeCodeOutString = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the entry type code In string. <P>
        @return the entry type code In string
    **/
    //---------------------------------------------------------------------
    public String getTypeCodeInString()
    {
        return typeCodeInString;
    }

    //---------------------------------------------------------------------
    /**
        Returns the entry type code Out string. <P>
        @return the entry type code Out string
    **/
    //---------------------------------------------------------------------
    public String getTypeCodeOutString()
    {
        return typeCodeOutString;
    }

    //---------------------------------------------------------------------
    /**
        Sets the entry type code label string from the property file. <P>
        @param value  entry type code labe string
    **/
    //---------------------------------------------------------------------
    public void setEntryTypeString(String value)
    {
        entryTypeString = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the entry type code label string. <P>
        @return the entry type code label string
    **/
    //---------------------------------------------------------------------
    public String getEntryTypeString()
    {
        return entryTypeString;
    }

    //----------------------------------------------------------------------
    /**
        Returns the appropriate function ID.
        @return int RoleFunctionIfc.ADMIN
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.CLOCK_IN_OUT;
    }

}
