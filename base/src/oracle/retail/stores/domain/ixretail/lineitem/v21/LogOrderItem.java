/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/v21/LogOrderItem.java /main/11 2012/10/19 12:46:36 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/16/12 - clean up order item quantities
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse
 *
 *   Revision 1.6  2004/08/10 07:17:12  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.5.2.1  2004/07/09 04:06:18  mwright
 *   Added item reference
 *
 *   Revision 1.5  2004/06/24 09:15:11  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.4.2.1  2004/06/10 10:50:55  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.4  2004/05/06 03:33:07  mwright
 *   POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/04/13 06:49:31  mwright
 *   Removed tabs
 *   Ready for testing
 *
 *   Revision 1.1.2.1  2004/03/21 14:29:40  mwright
 *   Initial revision for POSLog v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.lineitem.v21;

// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;


import oracle.retail.stores.domain.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.domain.ixretail.lineitem.LogOrderItemIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomerOrderForDeliveryIfc;

//--------------------------------------------------------------------------
/**
    This class creates the elements for an OrderItemStatus object.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class LogOrderItem
extends LogLineItem
implements LogOrderItemIfc, IXRetailConstantsV21Ifc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogOrderItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogOrderItem()
    {
    }

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
    {
        // this will throw an exception if the wrong flavour is passed:
        RetailTransactionCustomerOrderForDeliveryIfc element = (RetailTransactionCustomerOrderForDeliveryIfc)el;


        EYSStatusIfc status = orderItem.getStatus();

        element.setOrderStatus(status.statusToString(status.getStatus()));
        element.setPreviousStatus(status.statusToString(status.getPreviousStatus()));
        if (status.getLastStatusChange() != null)
        {
            element.setStatusChange(dateValue(status.getLastStatusChange()));
        }
        element.setQuantityPicked(getSchemaTypesFactory().getPOSLogQuantityInstance().initialize(orderItem.getQuantityPickedUp()));
        element.setQuantityShipped(getSchemaTypesFactory().getPOSLogQuantityInstance().initialize(orderItem.getQuantityShipped()));
        element.setDeposit(currency(orderItem.getDepositAmount()));

        String ref = orderItem.getReference();
        if (ref != null && ref.length() > 0)
        {
            element.setItemReference(ref);
        }

        // there is no "from" information, but the field is required:
        element.setFrom("N/A");

        return element;
    }

}
