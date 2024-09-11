/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/XChannelShippingMethodBeanModel.java /main/3 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* abhinavs    06/03/14 - CAE add available shipping date during order create enhancement
* yiqzhao     07/05/12 - Add ship item list on DisplayShippingMethod screen.
* yiqzhao     07/03/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.ui.beans;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * This is the model used to pass customer information
 * 
 * @version $Revision: /main/3 $
 */
public class XChannelShippingMethodBeanModel extends ShippingMethodBeanModel
{
    private static final long serialVersionUID = 1L;
    
    // list of item numbers for the shipping method
    protected ArrayList<String> itemNumbers;
    
    // list of item description which match itemNumbers
    protected ArrayList<String> itemDescriptions;

    /**
     * Estimated available to ship date when shipping to a customer
     * Estimated arrival date when shipping to a store
     */
    protected Calendar estimatedAvailableToShipDate;
    
    /** Store id for shipping to store */
    protected String storeID;
    
    /**
     * @return the itemNumbers
     */
    public ArrayList<String> getItemNumbers() {
        return itemNumbers;
    }

    /**
     * @param the itemNumbers the itemNumbers to set
     */
    public void setItemNumbers(ArrayList<String> itemNumbers) {
         this.itemNumbers = itemNumbers;
    }

    /**
     * @return the itemDescriptions
     */
    public ArrayList<String> getItemDescriptions() {
         return itemDescriptions;
    }

    /**
     * @param itemDescriptions the itemDescriptions to set
     */
    public void setItemDescriptions(ArrayList<String> itemDescriptions) {
        this.itemDescriptions = itemDescriptions;
    }
     
    /**
     * @return the estimatedAvailableToShipDate
     */
    public Calendar getEstimatedAvailableToShipDate()
    {
        return estimatedAvailableToShipDate;
    }

    /**
     * @param estimatedAvailableToShipDate the estimatedAvailableToShipDate to set
     */
    public void setEstimatedAvailableToShipDate(Calendar estimatedAvailableToShipDate)
    {
        this.estimatedAvailableToShipDate = estimatedAvailableToShipDate;
    }

    /**
     * @return the storeID
     */
    public String getStoreID()
    {
        return storeID;
    }

    /**
     * @param storeID the storeID to set
     */
    public void setStoreID(String storeID)
    {
        this.storeID = storeID;
    }

    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel#toString()
     */
    @Override
    public String toString()
    {
        int i=0;
        StringBuilder buff = new StringBuilder();
        for (String itemNo : itemNumbers)
        {
            buff.append(itemNo).append(" ");
            if (itemDescriptions.size()<i)
                buff.append((String)itemDescriptions.get(i++)).append("\n");
            else
                buff.append("\n");
        }
        buff.append(new StringBuilder(super.toString()));
        buff.append("Class: ShippingMethodBeanModel Revision: " + revisionNumber + "\n");
        if (methodsList != null && methodsList.length > 0 && selectedIndex < methodsList.length
                && getSelectedShipMethod().getBaseShippingCharge() != null)
        {
            buff.append("ShippingCharge [" + getSelectedShipMethod().getBaseShippingCharge().toString() + "]\n");
        }
        else
        {
            buff.append("ShippingCharge [null]\n");
        }
        buff.append("Offline [" + offline + "]\n");
        buff.append("Special Instructions[" + instructions + "]\n");
        return (buff.toString());
    }
}
