/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BrowserBeanModel.java /main/13 2012/10/29 16:37:48 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/29/12 - deprecating class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *
 *   Revision 1.4.4.1  2004/10/18 19:34:37  jdeleau
 *   @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:54:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:36   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:29:18   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   29 Oct 2001 12:39:58   jbp
 * Added Functionality to enable cookies based on parameter
 * Resolution for POS SCR-238: Update browser to enable cookies
 *
 *    Rev 1.0   Sep 21 2001 11:34:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports

//--------------------------------------------------------------------------
/**
 * This is the bean model that is used by the BrowserBeanModel. <P>
 * This bean model is used to access a single String
 * @see oracle.retail.stores.pos.ui.beans.BrowserBean
 * @version $Revision: /main/13 $
 * @deprecated as of 14.0 Use {@link oracle.retail.stores.pos.ui.beans.BrowserFoundationDisplayBeanModel} instead.
 */
//--------------------------------------------------------------------------
public class BrowserBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
        constant for class name
    **/
    public static final String CLASSNAME = "BrowserBeanModel";
    /**
        Cookies Enabled
    **/
    protected boolean cookiesEnabled = false;

    /**
        Model String
    **/
    public String homeUrl = "www.360Commerce.com";

    public BrowserBeanModel()
    {
    }

    //--------------------------------------------------------------------------
    /**
        Gets the model string property value.
        @return String the model string property value.
        @see #setModelString
    **/
    //--------------------------------------------------------------------------
    public String getHomeUrl()
    {
        return homeUrl;
    }

    //--------------------------------------------------------------------------
    /**
    * Sets the model string property value.
    * @param modelString the new value for the property.
    * @see #getModelString
    */
    //--------------------------------------------------------------------------
    public void setHomeUrl(String homeUrl)
    {
        this.homeUrl = homeUrl;
    }

    //--------------------------------------------------------------------------
    /**
        Checks if cookies are Enabled.
        @return cookiesEnabled.
    **/
    //--------------------------------------------------------------------------
    public boolean isCookiesEnabled()
    {
        return cookiesEnabled;
    }

    //--------------------------------------------------------------------------
    /**
    * Sets cookiesEnabled based on parameter
    * @param boolean true if cookies are enabled
    */
    //--------------------------------------------------------------------------
    public void setCookiesEnabled(boolean value)
    {
        this.cookiesEnabled = value;
    }
    /**
     * Return whether or not a browser is installed.  This
     * only will check for the ICE Browser.  This works by
     * checking for a ProxyManager ICE class which is only
     * in version 6.0+ inside the ice.net.proxy package.
     *  
     *  @return true or false
     */
    public boolean isInstalled()
    {
        boolean result = false;
        try
        {
            Class.forName("ice.net.proxy.ProxyManager");
            result = true;
        }
        catch(ClassNotFoundException cnfe)
        {
            result = false;
        }
        return result;
        
    }
    
    /**
     * Get a string representation of this class
     *  
     * @return String representation of class
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + CLASSNAME + " (Revision " +
                                      getRevisionNumber() + ")" + hashCode());
        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
