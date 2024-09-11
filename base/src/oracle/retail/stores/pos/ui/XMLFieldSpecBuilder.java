/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/XMLFieldSpecBuilder.java /main/11 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    cgreene   09/10/12 - Popup menu implementation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:45 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 14:45:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import oracle.retail.stores.foundation.manager.gui.FieldSpec;
import oracle.retail.stores.foundation.manager.gui.loader.BuilderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.XMLBuilder;

/**
 * This serves as a builder for FIELD specs.
 * 
 * @version $Revision: /main/11 $
 */
public class XMLFieldSpecBuilder extends XMLBuilder implements BuilderIfc
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Default constructor
     */
    public XMLFieldSpecBuilder()
    {
        super();
    }

    /**
     * Creates the button spec class.
     */
    public void create()
    {
        result = new FieldSpec();
    }

    /**
     * Processes nested objects
     * 
     * @param parent The parent object
     * @param childName The name of the child object
     * @param child The child object
     */
    public void addChild(Object parent, String childName, Object child)
    {
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

    /**
     * @return String a representation of the class.
     */
    @Override
    public String toString()
    {
        StringBuilder tmpString = new StringBuilder("XMLFieldSpecBuilder@");
        tmpString.append(hashCode());
        return tmpString.toString();
    }
}
