/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/DiscountComparator.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
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
 *    Rev 1.0   Aug 29 2003 15:34:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:49:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:57:38   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:17:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   31 Oct 2001 08:32:02   pjf
 * deprecated classes.
 * Moved their functionality to renamed classes in new package.
 * oracle/retail/stores/domain/comparators
 * Resolution for POS SCR-245: Domain Refactoring
 *
 *    Rev 1.0   Sep 20 2001 16:12:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

//java imports
import java.io.Serializable;
import java.util.Comparator;

//--------------------------------------------------------------------------
/**
    Comparison class used to sort BestDealGroups.
    Sorts a list of BestDealGroups in descending order by total discount amount.
    @deprecated use oracle.retail.stores.domain.comparators.BestDealDiscountDescending
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DiscountComparator implements Comparator, Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9087318174915667443L;

    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    private static Comparator instance = new DiscountComparator();
    //---------------------------------------------------------------------
    /**
        Constructs the singleton.
    **/
    //---------------------------------------------------------------------
    private DiscountComparator()
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
        Compares two best deal groups and returns -1,0 or 1 if the first
        group's total discount is greater than, equal to or less than the second
        group's total discount amount.
    **/
    //---------------------------------------------------------------------
    public int compare(Object o1, Object o2)
    {
        BestDealGroupIfc group1 = (BestDealGroupIfc)o1;
        BestDealGroupIfc group2 = (BestDealGroupIfc)o2;

        return -(group1.getTotalDiscount().compareTo(group2.getTotalDiscount()));
    }

}
