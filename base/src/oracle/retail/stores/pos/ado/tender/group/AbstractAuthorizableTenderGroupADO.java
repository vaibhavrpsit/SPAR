/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/AbstractAuthorizableTenderGroupADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/28 22:55:53  blj
 *   @scr 6650 - removed change from original 6650 scr
 *
 *   Revision 1.1  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;

/**
 * Abstract class to provide functionality for all authorizable tender groups.  
 */
public abstract class AbstractAuthorizableTenderGroupADO extends AbstractTenderGroupADO implements AuthorizableTenderGroupADOIfc
{
    
    /**
     * Returns an array of all tenders still requiring authorization.
     * 
     * @return voidTenders List of authorizable void tender groups
     */
    public List getVoidAuthPendingTenderLineItems()
    {
        TenderADOIfc[] tenders = getTenders();
        List voidTenders = new ArrayList();
        for (int i = 0; i < tenders.length; i++)
        {            
            if (!((ReversibleTenderADOIfc) tenders[i]).isVoided())
            {
                voidTenders.add(tenders[i]);
            }
        }
        
        return voidTenders;
    }
}
