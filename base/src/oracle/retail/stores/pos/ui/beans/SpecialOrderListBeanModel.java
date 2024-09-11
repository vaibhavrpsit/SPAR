/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderListBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:51:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:04   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 10 2001 19:24:46   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 *  This model provides the data used by the 
 *  SpecialOrderDetailListBean.
 */
//------------------------------------------------------------------------------
public class SpecialOrderListBeanModel extends POSBaseBeanModel
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** a list of layaway transaction objects */
    protected OrderSummaryEntryIfc[] orderList = null;
    
    /** the index of the row selected in the list */
    protected int selectedRow = -1;
    
//------------------------------------------------------------------------------
/**
 *  Default constructor.
 */
    public SpecialOrderListBeanModel() 
    {
    }

//------------------------------------------------------------------------------
/**
 *  Returns the list of special order objects.
 *  @return OrderSummaryEntryIfc[]
 */
    public OrderSummaryEntryIfc[] getOrderList()
    {
        return orderList;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the the list of special order objects. 
 *  @param list a list of special order objects
 */
    public void setOrderList(OrderSummaryEntryIfc[] list)
    {
        orderList = list;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the selected row.
 *  @return index of the selected row.
 */
    public int getSelectedRow()
    {
        return selectedRow;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the selected row. 
 *  @param row index of the selected row.
 */
    public void setSelectedRow(int row)
    {
        selectedRow = row;
    }

//------------------------------------------------------------------------------
/**
 *  Returns a string representation of this object.
 *  @return String representation of object
 */
    public String toString()
    {                                   
        // result string
        String strResult = new String("Class:  SpecialOrderListBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   

//------------------------------------------------------------------------------
/**
 *  Returns the revision number of the class.
 *  @return String representation of revision number
 */
    public String getRevisionNumber()
    {                                   
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   
}
