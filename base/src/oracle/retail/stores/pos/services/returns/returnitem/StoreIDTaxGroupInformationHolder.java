/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/StoreIDTaxGroupInformationHolder.java /main/14 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:30:12 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:25:33 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:14:28 PM  Robert Pearse   
 *
Revision 1.2  2004/06/11 13:08:01  jriggins
@scr 2775 Removed unused import which was causing a build error under the POS product team's Eclipse configs
 *
Revision 1.1  2004/06/11 12:37:56  mkp1
@scr 2775 More Tax - Returns
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;



/**
 * @author mkp1
 *
 */
public class StoreIDTaxGroupInformationHolder
{
    private HashMap<String, HashMap<Integer, TaxGroupInformation>> storeGroupHolder = new HashMap<String, HashMap<Integer, TaxGroupInformation>>(0);
    
    public String[] getStoreNumbers()
    {
        return (String[]) storeGroupHolder.keySet().toArray(new String[0]);
    }
    
    public Integer[] getGroupIDs(String storeNumber)
    {
        Integer[] returnValue = new Integer[0];
        HashMap<Integer, TaxGroupInformation> taxGroups = storeGroupHolder.get(storeNumber);
        if(taxGroups != null)
        {
            returnValue = (Integer[]) taxGroups.keySet().toArray(returnValue);
        }
        return returnValue;
    }
    
    public SaleReturnLineItemIfc[] getReturnItems(String storeNumber, int groupNumber)
    {
        return getReturnItems(storeNumber, new Integer(groupNumber));
    }
    
    public SaleReturnLineItemIfc[] getReturnItems(String storeNumber, Integer groupNumber)
    {
        SaleReturnLineItemIfc[] returnValue = null;
        HashMap<Integer, TaxGroupInformation> taxGroups = storeGroupHolder.get(storeNumber);
        if(taxGroups != null)
        {
            TaxGroupInformation taxGroupInformation = (TaxGroupInformation) taxGroups.get(groupNumber);
            if( taxGroupInformation != null)
            {
                returnValue = taxGroupInformation.getReturnItems();
            }
        }
        
        if(returnValue == null)
        {
            returnValue = new SaleReturnLineItemIfc[0];
        }
        return returnValue;
    }
    
    public void addReturnItem(SaleReturnLineItemIfc item)
    {
        ReturnItemIfc returnItem = item.getReturnItem();
        if( returnItem != null && returnItem.getStore() != null)
        { 
            String storeID = returnItem.getStore().getStoreID();
            
            if( storeGroupHolder.containsKey(storeID))
            {
                TaxGroupInformation taxGroupInformation = null;
                HashMap<Integer, TaxGroupInformation> taxGroups = storeGroupHolder.get(storeID);
                
                Integer key = new Integer(item.getPLUItem().getTaxGroupID());
                
                if(taxGroups.containsKey(key))
                {
                    taxGroupInformation = (TaxGroupInformation) taxGroups.get(key);
                    taxGroupInformation.addReturnItem(item);
                }
                else
                {
                    taxGroups.put(key, new TaxGroupInformation(item));
                }
            }
            else 
            {
                //create a new one
                if(item.getPLUItem() != null)
                {
                    HashMap<Integer, TaxGroupInformation> taxGroups = new HashMap<Integer, TaxGroupInformation>(0);
                    TaxGroupInformation taxGroupInformation = new TaxGroupInformation(item);
                    taxGroups.put(new Integer(item.getPLUItem().getTaxGroupID()), taxGroupInformation);
                    storeGroupHolder.put(storeID, taxGroups);
                }
                else
                {
                    
                }
            }
        }
        
    }
    
    
    public class TaxGroupInformation
    {
        private ArrayList<SaleReturnLineItemIfc> returnItems = new ArrayList<SaleReturnLineItemIfc>();
        
        public TaxGroupInformation(SaleReturnLineItemIfc returnItem)
        {
            returnItems.add(returnItem);
        }
        
        public int getGroupId()
        {
            return ((SaleReturnLineItemIfc) returnItems.get(0)).getPLUItem().getTaxGroupID();
        }
        
        public SaleReturnLineItemIfc[] getReturnItems()
        {
            return (SaleReturnLineItemIfc[]) returnItems.toArray(new SaleReturnLineItemIfc[0]);
        }
        
        public boolean isSameGroup(int groupID)
        {
            return groupID == ((SaleReturnLineItemIfc) returnItems.get(0)).getPLUItem().getTaxGroupID();
        }
        
        public boolean isSameGroup(SaleReturnLineItemIfc returnItem)
        {
            return isSameGroup(returnItem.getPLUItem().getTaxGroupID()); 
        }
        
        public boolean addReturnItem(SaleReturnLineItemIfc returnItem)
        {
            boolean returnValue = isSameGroup(returnItem);
            if(returnValue)
            {
                returnItems.add(returnItem);
            }
            return returnValue;
        }
        
    }
    
}
