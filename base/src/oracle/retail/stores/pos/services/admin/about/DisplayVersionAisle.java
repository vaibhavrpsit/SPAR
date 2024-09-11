/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/about/DisplayVersionAisle.java /main/12 2011/12/05 12:16:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         11/8/2006 8:53:29 AM   Keith L. Lesikar 
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.about;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.PosVersion;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class DisplayVersionAisle extends LaneActionAdapter 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 
	    class name constant 
	**/ 
	public static final String LANENAME = "DisplayVersionAisle"; 
	/** 
	    revision number for this class 
	**/ 
	public static final String revisionNumber = "$Revision: /main/12 $";
	/** 
	    expired layaway screen name 
	**/ 
	private static final String RESOURCE_ID = "VersionInformation";
	
    //-------------------------------------------------------------------------- 
    /** 
       Displays the Version Information screen.
       <P> 
       @param bus the bus arriving at this site 
    **/ 
    //-------------------------------------------------------------------------- 
    public void traverse(BusIfc bus) 
    {  
    	// set arg strings to version information 
        String args[] = new String[4];
    	PosVersion posVersion = new PosVersion();
    	
        String buildString = posVersion.getBuild();
        String buildDateString = "Mon Jan 01 00:00:01 CST 2006";
        if (buildString == null)
        {
        	buildString = "development.build.0.development";
        }
        else
        {
        	buildDateString = posVersion.getBuildTimestamp();
        }


        // the date format is specified in application assembly build.xml as follows
        // <format property="anthill.build.date_time" pattern="EEE MMM dd HH:mm:ss z yyyy"/>
        // this corresponds to the way the date and time are passed into the official build
        SimpleDateFormat buildDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        // if the tokens were replaced we've got work to do
        if (buildString.indexOf("version") == -1)
        {
            if (buildString.equals("development build"))
            {
            	args[0] = "development";
            	args[1] = "development";
            }
            // we assume the build number is formated <VERSION NUMBER>.<YYMMDD.HHMM>
            // and YYMMDD.HHMM is build number.
            int lastPeriod = buildString.lastIndexOf(".");            
            if (lastPeriod >= 0)
            {
        		lastPeriod = buildString.substring(0,lastPeriod).lastIndexOf(".");
        		if (lastPeriod >= 0)
        		{
        			// at least one period
        			args[0] = buildString.substring(0,lastPeriod);
        			args[1] = buildString.substring(lastPeriod + 1);
        		}
        	}

        	try
            {
                Date buildDate = buildDateFormatter.parse(buildDateString);
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, getLocale());
                DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.LONG, getLocale());
                args[2] = dateFormat.format(buildDate);
                args[3] = timeFormat.format(buildDate);
            }
            catch (ParseException e)
            {
                // log that we couldn't parse the date and keep the defaults
                logger.info("Unable to parse date string for version date. " + buildDateString);
            } 
        }
       
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);

        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    //----------------------------------------------------------------------
    /**
        This method gets the Locale so that it can be override in the
        unit tests.
        @return Locale
    **/
    //----------------------------------------------------------------------
    protected Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    } 

}
