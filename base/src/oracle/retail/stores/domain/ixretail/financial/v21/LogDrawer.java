/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/v21/LogDrawer.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/10/05 00:46:20  mwright
 *   Changed checkin comment so TO DO does not get filtered into task list
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:48:38  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:11:12  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/05/05 02:26:43  mwright
 *   Removed TO DO: Test case and factory done
 *
 *   Revision 1.1.2.1  2004/04/19 07:06:02  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial.v21;

// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.financial.LogDrawerIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.Drawer360Ifc;

import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a drawer
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogDrawer
extends AbstractIXRetailTranslator
implements LogDrawerIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogDrawer object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogDrawer()
    {
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified drawer object. <P>
       @param drawer drawer reference
       @param doc parent document (unused)
       @param el Drawer elementto update
       @param name element name (unused)
       @return Updated element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(DrawerIfc drawer,
                                 Document  doc,         // unused
                                 Element   el,
                                 String    name)        // unused
    throws XMLConversionException
    {
        Drawer360Ifc drawerElement = (Drawer360Ifc)el;

        drawerElement.setID(drawer.getDrawerID());
        drawerElement.setStatus(AbstractStatusEntityIfc.DRAWER_STATUS_DESCRIPTORS[drawer.getDrawerStatus()]);
        drawerElement.setTillID(drawer.getOccupyingTillID());

        return drawerElement;
    }

    //---------------------------------------------------------------------
    /**
       Creates element for the specified drawer object. <P>
       @param drawer drawer reference
       @param doc parent document (unused)
       @param el Drawer element
       @return Element with drawer details added
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(DrawerIfc drawer,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {
        return createElement(drawer, null, el, null);
    }

}
