/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/v21/LogLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:24 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:11  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/10 10:50:55  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:15:08  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.1  2004/04/13 07:26:34  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.lineitem.v21;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;


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
    {
        
        RetailTransactionLineItemIfc lineItemElement = (RetailTransactionLineItemIfc)el;

        lineItemElement.setVoidFlag(new Boolean(voidFlag));
        lineItemElement.setSequenceNumber(Integer.toString(sequenceNumber));

        return lineItemElement;

    }

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
