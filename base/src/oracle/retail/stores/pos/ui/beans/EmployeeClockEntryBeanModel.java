/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeClockEntryBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc
     $Log:
      4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:17 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse
     $
     Revision 1.3  2004/03/16 17:15:17  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:10:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   07 May 2002 15:24:26   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 14:51:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:54:20   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 19 2002 10:30:06   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   28 Oct 2001 17:53:02   mpm
 * Tweaked beans.
 *
 *    Rev 1.0   28 Oct 2001 13:02:04   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;
// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This is the bean model for the EmployeeClockEntryBean
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class EmployeeClockEntryBeanModel
extends ReasonBeanModel
{                                       // begin class EmployeeClockEntryBeanModel
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -2313918559641784584L;
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        last date entry for this employee
    **/
    protected EYSDate lastEntry = null;
    /**
        current date entry
    **/
    protected EYSDate currentEntry = null;
    /**
        reason code list index
        @deprecated as of 13.1 Use ReasonCodeModel.fieldSelectedIndex
    **/
    protected int selectedReasonIndex = -1;
    /**
        type code list index
    **/
    protected int selectedTypeIndex = -1;
    /**
        type code value
    **/
    protected int typeCode = -1;

    /**
        type code strings to select
    **/
    protected String[] typeCodes;

    /**
        type code string to display
    **/
    protected String typeCodeString = "";

    /**
        type code entry type label
    **/
    protected String entryTypeLabel = "";

    //----------------------------------------------------------------------------
    /**
        Constructs EmployeeClockEntryBeanModel object. <P>
    **/
    //----------------------------------------------------------------------------
    public EmployeeClockEntryBeanModel()
    {                                   // begin EmployeeClockEntryBeanModel()
    }                                   // end EmployeeClockEntryBeanModel()

    //----------------------------------------------------------------------------
    /**
        Retrieves last date entry for this employee. <P>
        @return last date entry for this employee
    **/
    //----------------------------------------------------------------------------
    public EYSDate getLastEntry()
    {                                   // begin getLastEntry()
        return(lastEntry);
    }                                   // end getLastEntry()

    //----------------------------------------------------------------------------
    /**
        Sets last date entry for this employee. <P>
        @param value  last date entry for this employee
    **/
    //----------------------------------------------------------------------------
    public void setLastEntry(EYSDate value)
    {                                   // begin setLastEntry()
        lastEntry = value;
    }                                   // end setLastEntry()

    //----------------------------------------------------------------------------
    /**
        Retrieves current date entry. <P>
        @return current date entry
    **/
    //----------------------------------------------------------------------------
    public EYSDate getCurrentEntry()
    {                                   // begin getCurrentEntry()
        return(currentEntry);
    }                                   // end getCurrentEntry()

    //----------------------------------------------------------------------------
    /**
        Sets current date entry. <P>
        @param value  current date entry
    **/
    //----------------------------------------------------------------------------
    public void setCurrentEntry(EYSDate value)
    {                                   // begin setCurrentEntry()
        currentEntry = value;
    }                                   // end setCurrentEntry()

    //----------------------------------------------------------------------------
    /**
        Sets current date entry to current date/time. <P>
    **/
    //----------------------------------------------------------------------------
    public void setCurrentEntry()
    {                                   // begin setCurrentEntry()
        currentEntry = DomainGateway.getFactory().getEYSDateInstance();
    }                                   // end setCurrentEntry()

    //----------------------------------------------------------------------------
    /**
        Retrieves index into reason list. <P>
        @return index into reason list
        @deprecated as of 13.1 Use super.getSelectedReasonIndex
    **/
    //----------------------------------------------------------------------------
    public int getSelectedReasonIndex()
    {                                   // begin getSelectedReasonIndex()
        return(super.getSelectedIndex());
    }                                   // end getSelectedReasonIndex()

    //----------------------------------------------------------------------------
    /**
        Sets index into reason list. <P>
        @param value  index into reason list
        @deprecated as of 13.1 Use super.setSelectedreasonCode
    **/
    //----------------------------------------------------------------------------
    public void setSelectedReasonIndex(int value)
    {                                   // begin setSelectedReasonIndex()
        super.setSelectedReasonCode(value);
    }                                   // end setSelectedReasonIndex()

    //----------------------------------------------------------------------------
    /**
        Retrieves index into type list. <P>
        @return index into type list
    **/
    //----------------------------------------------------------------------------
    public int getSelectedTypeCodeIndex()
    {                                   // begin getSelectedTypeCodeIndex()
        return(selectedTypeIndex);
    }                                   // end getSelectedTypeCodeIndex()

    //----------------------------------------------------------------------------
    /**
        Sets index into type list. <P>
        @param value  index into type list
    **/
    //----------------------------------------------------------------------------
    public void setSelectedTypeCodeIndex(int value)
    {                                   // begin setSelectedTypeCodeIndex()
        selectedTypeIndex = value;
    }                                   // end setSelectedTypeCodeIndex()

    //----------------------------------------------------------------------------
    /**
        Retrieves type code. <P>
        @return type code value
    **/
    //----------------------------------------------------------------------------
    public int getTypeCode()
    {                                   // begin getTypeCode()
        return(typeCode);
    }                                   // end getTypeCode()

    //----------------------------------------------------------------------------
    /**
        Sets type code. Current values are 0 and 1.<P>
        @param value  type code
    **/
    //----------------------------------------------------------------------------
    public void setTypeCode(int value)
    {                                   // begin setTypeCode()
        typeCode = value;
    }                                   // end setTypeCode()

    //----------------------------------------------------------------------------
    /**
        Retrieves the entry type code strings. <P>
        @return entry type code strings for selection
    **/
    //----------------------------------------------------------------------------
    public String[] getTypeCodes()
    {
        return typeCodes;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the entry type code strings as set in the property file. <P>
        @param entry type code strings
    **/
    //----------------------------------------------------------------------------
    public void setTypeCodes(String[] types)
    {
        typeCodes = types;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the last clock entry type code string. <P>
        @param last clock entry type code string
    **/
    //----------------------------------------------------------------------------
    public void setTypeCodeString(String value)
    {
        typeCodeString = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves the last clock entry type code string for display. <P>
        @return last clock entry type code string
    **/
    //----------------------------------------------------------------------------
    public String getTypeCodeString()
    {
        return typeCodeString;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the entry type code label string. <P>
        @param entry type code label string
    **/
    //----------------------------------------------------------------------------
    public void setEntryTypeLabelString(String value)
    {
        entryTypeLabel = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves the entry type code label. <P>
        @return entry type code label string
    **/
    //----------------------------------------------------------------------------
    public String getEntryTypeLabelString()
    {
        return entryTypeLabel;
    }

    //----------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("EmployeeClockEntryBeanModel",
                                    getRevisionNumber(),
                                    hashCode());
        // add attributes to string
        strResult.append(Util.formatToStringEntry("lastEntry",
                                                  getLastEntry()))
                 .append(Util.formatToStringEntry("currentEntry",
                                                  getCurrentEntry()))
                 .append(super.toString());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class EmployeeClockEntryBeanModel
