/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/financial/LogDrawer.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:57:28   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   May 06 2002 19:39:44   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.financial;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
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
    {                                   // begin LogDrawer()
    }                                   // end LogDrawer()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified drawer object. <P>
       @param drawer drawer reference
       @param doc parent document
       @param el parent element
       @param name element name
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(DrawerIfc drawer,
                                 Document doc,
                                 Element el,
                                 String name)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element drawerElement = parentDocument.createElement(name);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DRAWER_ID,
           drawer.getDrawerID(),
           drawerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           AbstractStatusEntityIfc.DRAWER_STATUS_DESCRIPTORS[drawer.getDrawerStatus()],
           drawerElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TILL_ID,
           drawer.getOccupyingTillID(),
           drawerElement);

        parentElement.appendChild(drawerElement);

        return(drawerElement);
    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified drawer object. <P>
       @param drawer drawer reference
       @param doc parent document
       @param el parent element
       @return Element representing quantity
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(DrawerIfc drawer,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        return(createElement(drawer,
                             doc,
                             el,
                             IXRetailConstantsIfc.ELEMENT_DRAWER));
    }                                   // end createElement()

}
