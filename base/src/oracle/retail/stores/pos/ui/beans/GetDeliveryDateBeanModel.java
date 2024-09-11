/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GetDeliveryDateBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - get delivery date bean model
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public class GetDeliveryDateBeanModel extends POSBaseBeanModel
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     constant for list view.
     this constant defines the length of the pull-down list
     **/
    protected static final String SPACES = "                    "; // 20 spaces

    /**
     the list of service item values, business defined
     **/
    protected String[] nonMerchandiseList = null;

    /**
     the selected item
     **/
    protected EYSDate selectedDate = null;

    /**
     the list of service items
     **/
    protected PLUItemIfc[] serviceItemList = null;

    //----------------------------------------------------------------------
    /**
     Class Constructor.
     **/
    //----------------------------------------------------------------------
    public GetDeliveryDateBeanModel()
    {
        super();
    }

    //----------------------------------------------------------------------
    /**
     Returns the Selected delivery date.
     <p>
     @return the Selected delivery date.
     **/
    //----------------------------------------------------------------------
    public EYSDate getSelectedDeliveryDate()
    {
        return (selectedDate);
    }

    //----------------------------------------------------------------------
    /**
     Sets the Selected delivery date.
     <p>
     @param value the value to be set for selectedDate
     **/
    //----------------------------------------------------------------------
    public void setSelectedDeliveryDate(EYSDate value)
    {
        selectedDate = value;
    }
}
