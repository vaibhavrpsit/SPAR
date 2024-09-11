/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/ReasonCodeGroupComparator.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
 * $
 * Revision 1.1  2004/04/19 18:48:56  awilliam
 * @scr 4374 Reason Code featrure work
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;
//java imports
import java.util.Comparator;
//domain Imports
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
//--------------------------------------------------------------------------
/**
 * This class organizes reason codes in alphabetical order
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ReasonCodeGroupComparator implements Comparator
{
    //----------------------------------------------------------------------
    /**
     * @param arg0 CodeListIfc[]
     * @param arg1 CodeListIfc[]
     * @return @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    //----------------------------------------------------------------------
    public int compare(Object arg0, Object arg1)
    {
        int result = 0;
        if (!(arg0 instanceof CodeListIfc) && !(arg1 instanceof CodeListIfc))
        {
            return result;
        }
        CodeListIfc item0 = (CodeListIfc) arg0;
        CodeListIfc item1 = (CodeListIfc) arg1;
        result = LocaleUtilities.compareValues(item0.getListDescription(), item1.getListDescription());
        return result;
    }
}
