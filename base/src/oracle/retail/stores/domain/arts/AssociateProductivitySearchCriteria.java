/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/AssociateProductivitySearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   09 Apr 2002 16:58:20   jbp
 * Initial revision.
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields to interact with the
    associate productivity reports.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class AssociateProductivitySearchCriteria implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4876089768490026810L;

    /**
        The store ID
    **/
    protected String storeID = null;

    /**
        The list of reporting periods
    **/
    protected EYSDate startDate = null;
    protected EYSDate endDate = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        @param  storeID             The store ID
        @param  EYSDate             startDate
        @param  EYSDate             endDate
    **/
    //---------------------------------------------------------------------
    public AssociateProductivitySearchCriteria(String storeID,
                                               EYSDate startDate,
                                               EYSDate endDate)
    {
        this.storeID = storeID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store id
        <p>
        @return  the store id
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {
        return(storeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store id
        <p>
        @param  value   The store id
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String value)
    {
        storeID = value;
    }

    //---------------------------------------------------------------------
    /**
        Get start date
        @return EYSDate startDate
    **/
    //---------------------------------------------------------------------
    public EYSDate getStartDate()
    {
        return this.startDate;
    }

    //---------------------------------------------------------------------
    /**
        Sets the startDate
        @param  EYSDate   startDate
    **/
    //---------------------------------------------------------------------
    public void setStartDate(EYSDate startDate)
    {
        this.startDate = startDate;
    }

    //---------------------------------------------------------------------
    /**
        Gets the endDate
        @return  EYSDate   endDate
    **/
    //---------------------------------------------------------------------
    public EYSDate getEndDate()
    {
        return this.endDate;
    }

    //---------------------------------------------------------------------
    /**
        Sets the endDate
        @param  EYSDate endDate
    **/
    //---------------------------------------------------------------------
    public void setEndDate(EYSDate endDate)
    {
        this.endDate = endDate;
    }
}
