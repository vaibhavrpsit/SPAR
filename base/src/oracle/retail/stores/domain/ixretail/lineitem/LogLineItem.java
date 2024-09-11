/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/LogLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:44  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:29  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 09:58:24   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:48   msg
 * Initial revision.
 * 
 *    Rev 1.4   May 27 2002 16:59:06   mpm
 * Modified naming convention for type constants.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   May 13 2002 19:04:08   mpm
 * Added more columns to order; add support for deleted items (line voids).
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   Apr 28 2002 13:32:04   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 26 2002 07:49:00   mpm
 * Modified to set line-item-type attribute.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 22 2002 19:33:46   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.lineitem;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the basic elements for a line item.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogLineItem
extends AbstractIXRetailTranslator
implements LogLineItemIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogLineItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogLineItem()
    {                                   // begin LogLineItem()
    }                                   // end LogLineItem()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified LineItem.  The element is not
       appended to the parent. <P>
       @param doc parent document
       @param el parent element
       @param itemType line item type name
       @param sequenceNumber sequence number
       @param voidFlag flag indicating line item is a void
       @return Element representing LineItem
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(Document doc,
                                 Element el,
                                 String itemType,
                                 boolean voidFlag,
                                 int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element lineItemElement = doc.createElement
          (IXRetailConstantsIfc.ELEMENT_LINE_ITEM);

        // set item type name
        // This attribute facilitates translation from XML by identifying the
        // type of the line item.
        lineItemElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_LINE_ITEM_TYPE,
                                     itemType);
        // set element as a 360 type
        lineItemElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           IXRetailConstantsIfc.TYPE_LINE_ITEM_360);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SEQUENCE_NUMBER,
           Integer.toString(sequenceNumber),
           lineItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_VOID_FLAG,
           voidFlag,
           lineItemElement);

        return(lineItemElement);

    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified LineItem.  The element is not
       appended to the parent. <P>
       @param doc parent document
       @param el parent element
       @param itemType line item type name
       @param sequenceNumber sequence number
       @return Element representing LineItem
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(Document doc,
                                 Element el,
                                 String itemType,
                                 int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createElement()
        return(createElement(doc,
                             el,
                             itemType,
                             false,
                             sequenceNumber));
    }
}
