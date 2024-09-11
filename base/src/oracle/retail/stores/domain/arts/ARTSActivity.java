/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSActivity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    03/13/09 - added support for localized department names
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;

//-------------------------------------------------------------------------
/**
    A container class that contains data fields to interact with the
    activity reports.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSActivity implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1844486888843641313L;

    /**
        The store ID
    **/
    protected String storeID = null;

    /**
        The list of reporting periods
    **/
    protected ReportingPeriodIfc[] reportingPeriods = null;

    /**
    	The locales for any localized text
     **/
    protected LocaleRequestor locales = null;
  
	/**
     *  Class constructor.
     * @param storeID The store ID
     * @param reportingPeriods The list of reporting periods
     */
    public ARTSActivity(String storeID, ReportingPeriodIfc[] reportingPeriods)
    {
    	this(storeID, reportingPeriods, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT))); 
    }
    
    /**
     *  Class constructor.
     * @param storeID The store ID
     * @param reportingPeriods The list of reporting periods
     */
    public ARTSActivity(String storeID, ReportingPeriodIfc[] reportingPeriods, LocaleRequestor localeRequestor)
    {
        this.storeID = storeID;
        this.reportingPeriods = reportingPeriods;
        this.locales = localeRequestor;
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
        Returns the list of reporting periods
        <p>
        @return  the list of reporting periods
    **/
    //---------------------------------------------------------------------
    public ReportingPeriodIfc[] getReportingPeriods()
    {
        return(reportingPeriods);
    }

    //---------------------------------------------------------------------
    /**
        Sets the list of reporting periods
        <p>
        @param  value   The list of reporting periods
    **/
    //---------------------------------------------------------------------
    public void setEndDate(ReportingPeriodIfc[] value)
    {
        reportingPeriods = value;
    }

	/**
	 * @return the locales
	 */
	public LocaleRequestor getLocales()
	{
		return locales;
	}

	/**
	 * @param locales the locales to set
	 */
	public void setLocales(LocaleRequestor locales) 
	{
		this.locales = locales;
	}
    
    
}
