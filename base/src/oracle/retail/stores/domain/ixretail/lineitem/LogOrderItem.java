/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/LogOrderItem.java /main/12 2012/10/19 12:46:35 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/16/12 - clean up order item quantities
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse
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
 *    Rev 1.1   Jan 22 2003 09:58:22   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:50   msg
 * Initial revision.
 *
 *    Rev 1.1   May 28 2002 10:46:54   mpm
 * Added reference for order line items.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 28 2002 13:33:32   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.lineitem;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the elements for an OrderItemStatus object.
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class LogOrderItem
extends LogLineItem
implements LogOrderItemIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogOrderItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogOrderItem()
    {                                   // begin LogOrderItemStatus()
    }                                   // end LogOrderItemStatus()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified OrderItemStatus. <P>
       @param orderItem OrderItemStatus object
       @param doc parent document
       @param el parent element
       @return Element representing OrderItemStatus
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(OrderItemStatusIfc orderItem,
                                 Document doc,
                                 Element el)
    throws XMLConversionException
    {                                   // begin createElement()
        setParentDocument(doc);
        setParentElement(el);

        Element orderItemElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_ORDER_ITEM);

        EYSStatusIfc status = orderItem.getStatus();

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           status.statusToString(status.getStatus()),
           orderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PREVIOUS_STATUS,
           status.statusToString(status.getPreviousStatus()),
           orderItemElement);

        if (status.getLastStatusChange() != null)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_STATUS_CHANGE,
               status.getLastStatusChange(),
               orderItemElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_QUANTITY_PICKED,
           orderItem.getQuantityPickedUp(),
           orderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_QUANTITY_SHIPPED,
           orderItem.getQuantityShipped(),
           orderItemElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DEPOSIT_AMOUNT,
           orderItem.getDepositAmount(),
           orderItemElement);

        el.appendChild(orderItemElement);

        return(orderItemElement);
    }                                   // end createElement()

}
