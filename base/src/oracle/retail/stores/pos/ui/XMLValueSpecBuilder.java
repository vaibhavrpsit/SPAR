/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/XMLValueSpecBuilder.java /main/11 2013/06/24 12:03:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/24/13 - corrected toString method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:27:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:16:09 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.loader.BuilderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.XMLBuilder;

/**
 * This serves as a builder for button specs.
 * 
 * @version $Revision: /main/11 $
 */
public class XMLValueSpecBuilder extends XMLBuilder implements BuilderIfc
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Store instance of logger here
     */
    protected static final Logger logger = Logger.getLogger(XMLValueSpecBuilder.class);

    /**
     * Default constructor
     */
    public XMLValueSpecBuilder()
    {
        super();
    }

    /**
     * Creates temporary storage for a string
     */
    public void create()
    {
        result = new String();
    }

    /**
     * Sets an attribute. Check for the screen text attribute and call the
     * setScreenText method explicitly.
     * 
     * @param attrName The name of the attribute to be set.
     * @param attrValue The value of the attribute to be set.
     */
    public void setAttribute(String attrName, String attrValue)
    {
        if (attrName.equals("value"))
        {
            result = new String(attrValue);
        }
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}