/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSReportingPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields to interact with the
    reporting period data operations.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSReportingPeriod implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6739685543313382663L;

    /**
        The store ID
    **/
    protected String storeID = null;

    /**
        The start of the time interval
    **/
    protected EYSDate startDate = null;

    /**
        The end of the time interval
    **/
    protected EYSDate endDate = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  startDate   The start of the time interval
        @param  endDate     The end of the time interval
    **/
    //---------------------------------------------------------------------
    public ARTSReportingPeriod(EYSDate startDate, EYSDate endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //---------------------------------------------------------------------
    /**
        Returns the start of the time interval
        <p>
        @return  the start of the time interval
    **/
    //---------------------------------------------------------------------
    public EYSDate getStartDate()
    {
        return(startDate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the start of the time interval
        <p>
        @param  value   The start of the time interval
    **/
    //---------------------------------------------------------------------
    public void setStartDate(EYSDate value)
    {
        startDate = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the end of the time interval
        <p>
        @return  the end of the time interval
    **/
    //---------------------------------------------------------------------
    public EYSDate getEndDate()
    {
        return(endDate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the end of the time interval
        <p>
        @param  value   The end of the time interval
    **/
    //---------------------------------------------------------------------
    public void setEndDate(EYSDate value)
    {
        endDate = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the store ID
        <p>
        @return  the store ID
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {
        return(storeID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the store ID
        <p>
        @param  value   The store ID
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String value)
    {
        storeID = value;
    }
}
