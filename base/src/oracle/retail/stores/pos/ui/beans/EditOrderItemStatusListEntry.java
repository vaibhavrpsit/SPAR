/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditOrderItemStatusListEntry.java /main/2 2013/01/10 14:03:55 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         10/29/12 - disable pickup and cancel buttons when not applicable
* sgu         10/26/12 - add new class
* sgu         10/26/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.lineitem.SplitOrderItem;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;

public class EditOrderItemStatusListEntry extends SplitOrderItem
{
    public enum EditActionCode
    {
        FILL,
        PICKUP,
    } 
    private static final long serialVersionUID = -8183839876682574474L;
    private EditActionCode editActionCode = EditActionCode.PICKUP;
    private boolean allowEditItemStatusFlag;
    
    public EditOrderItemStatusListEntry(SplitOrderItemIfc orderItem, EditActionCode editActionCode, boolean allowEditItemStatus)
    {
        super(orderItem.getOriginalOrderLineItem(), orderItem.getQuantity(),
                orderItem.getStatus().getStatus());

        setEditActionCode(editActionCode);
        setAllowEditItemStatusFlag(allowEditItemStatus);
    }
    
    public EditActionCode getEditActionCode() 
    {
        return editActionCode;
    }

    public void setEditActionCode(EditActionCode editActionCode) 
    {
        this.editActionCode = editActionCode;
    }

    public boolean getAllowEditItemStatusFlag()
    {
        return allowEditItemStatusFlag;
    }

    public void setAllowEditItemStatusFlag(boolean allowEditItemStatusFlag)
    {
        this.allowEditItemStatusFlag = allowEditItemStatusFlag;
    }
}

