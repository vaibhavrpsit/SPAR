/* =============================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returnauthorization/LineNumberDescending.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.returnauthorization;

import java.io.Serializable;
import java.util.Comparator;

import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;

/**
  Comparison class used to sort items by line index.
  Sorts a list of SaleReturnLineItems in descending order by
  line number.

  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class LineNumberDescending implements Comparator, Serializable {

    static final long serialVersionUID = 4570790146970990875L;

    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    private static Comparator instance = new LineNumberDescending();
    //---------------------------------------------------------------------
    /**
        Constructs the singleton.
    **/
    //---------------------------------------------------------------------
    public LineNumberDescending()
    {

    }
    //---------------------------------------------------------------------
    /**
        Returns the singleton.
    **/
    //---------------------------------------------------------------------
    public static Comparator getInstance()
    {
        return instance;
    }
    //---------------------------------------------------------------------
    /**
        Compares two sale return line items and returns -1 or 1 if the first
        item's line number is less than, or greater than the second
        item's line number. This allows collections of line items to
        be sorted in ascending order by line number.
    **/
    //---------------------------------------------------------------------
    public int compare(Object o1, Object o2)
    {
        int value = 0;

        ReturnResponseLineItemIfc item1 = (ReturnResponseLineItemIfc)o1;
        ReturnResponseLineItemIfc item2 = (ReturnResponseLineItemIfc)o2;

        value = item1.getSaleReturnLineItemIndex() < item2.getSaleReturnLineItemIndex() ? 1 : (-1);
        return value;
    }

}
