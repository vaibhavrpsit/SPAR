/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/SourceTargetComparator.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:19 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/04/09 16:55:49  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:50:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:58:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:18:38   msg
 * Initial revision.
 * 
 *    Rev 1.2   31 Oct 2001 08:32:02   pjf
 * deprecated classes.
 * Moved their functionality to renamed classes in new package.
 * oracle/retail/stores/domain/comparators
 * Resolution for POS SCR-245: Domain Refactoring
 *
 *    Rev 1.1   31 Oct 2001 06:59:26   pjf
 * Removed instanceof checks in order to throw ClassCastException if arguments are invalid.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.0   Sep 20 2001 16:12:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

//java imports
import java.io.Serializable;
import java.util.Comparator;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
//--------------------------------------------------------------------------
/**
    Comparison class used to sort discount sources and targets.
    Sorts a list of SaleReturnLineItems in descending order by
    extended discounted selling price.
    @deprecated use oracle.retail.stores.domain.comparators.LineItemPriceDescending
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SourceTargetComparator implements Comparator, Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7185685222074108487L;

    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    private static Comparator instance = new SourceTargetComparator();
    //---------------------------------------------------------------------
    /**
        Constructs the singleton.
    **/
    //---------------------------------------------------------------------
    private SourceTargetComparator()
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
        Compares two sale return line items and returns -1,0 or 1 if the first
        item's extended discounted selling price is greater than, equal to or less than the second
        item's extended discounted selling price. This allows collections of line items to
        be sorted in descending order by final pre-tax price.
    **/
    //---------------------------------------------------------------------
    public int compare(Object o1, Object o2)
    {
        int value = 0;

        SaleReturnLineItemIfc item1 = (SaleReturnLineItemIfc)o1;
        SaleReturnLineItemIfc item2 = (SaleReturnLineItemIfc)o2;

        value = -(item1.getExtendedDiscountedSellingPrice().compareTo(
                  item2.getExtendedDiscountedSellingPrice()));

        return value;
    }

}
