/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSDepartmentTotals.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
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
 *    2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:46   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields needed to perform
    certain data operations.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSDepartmentTotals implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -41858472196335979L;

    /** The department ID **/
    protected String departmentID = null;

    /** The financial totals information **/
    protected FinancialTotalsIfc financialTotals = null;

    /** The business day **/
    protected EYSDate businessDate = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
    **/
    //---------------------------------------------------------------------
    public ARTSDepartmentTotals()
    {
    }

    //---------------------------------------------------------------------
    /**
        Returns the department ID
        <p>
        @return  the department ID
    **/
    //---------------------------------------------------------------------
    public String getDepartmentID()
    {
        return(departmentID);
    }

    //---------------------------------------------------------------------
    /**
        Sets the department ID
        <p>
        @param  deptID    The department ID
    **/
    //---------------------------------------------------------------------
    public void setDepartmentID(String deptID)
    {
        departmentID = deptID;
    }

    //---------------------------------------------------------------------
    /**
        Returns the financial totals information
        <p>
        @return  the financial totals information
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getFinancialTotals()
    {
        return(financialTotals);
    }

    //---------------------------------------------------------------------
    /**
        Sets the financial totals for the department
        <p>
        @param  totals    The financial totals for the department
    **/
    //---------------------------------------------------------------------
    public void setFinancialTotals(FinancialTotalsIfc totals)
    {
        financialTotals = totals;
    }

    //---------------------------------------------------------------------
    /**
        Returns the business day
        <p>
        @return  the business day
    **/
    //---------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {
        return(businessDate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the business day
        <p>
        @param  businessDate    The new value for business day
    **/
    //---------------------------------------------------------------------
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }
}
