/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ExternalOrderItemReturnStatusElement.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
*===========================================================================*/

package oracle.retail.stores.pos.services.returns.returncommon;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;


/**
 * This class tracks of the return state of an external order item. 
 */
public class ExternalOrderItemReturnStatusElement implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 5595845500030685363L;
    /**
     * External Order Item for return
     */
    protected ExternalOrderItemIfc externalOrderItem;
    /**
     * Indicates if the return for the external order item has been completed.
     */
    protected boolean returned = false;

    /**
     * Indicates if the external order item has been selected for return.
     */
    protected boolean selected = false;

    /**
     * If the external order item has a serial number, this indicates if it
     * has already been matched to a serial number entered by the operator.
     */
    protected boolean serialNumberMatched = false;

    /**
     * @return the serialNumberMatched
     */
    public boolean isSerialNumberMatched()
    {
        return serialNumberMatched;
    }

    /**
     * @param serialNumberMatched the serialNumberMatched to set
     */
    public void setSerialNumberMatched(boolean serialNumberMatched)
    {
        this.serialNumberMatched = serialNumberMatched;
    }

    /**
     * @param sets the externalOrderItem.
     */
    public void setExternalOrderItem(ExternalOrderItemIfc externalOrderItem)
    {
        this.externalOrderItem = externalOrderItem;
    }

    /**
     * @return Returns the externalOrderItem.
     */
    public ExternalOrderItemIfc getExternalOrderItem()
    {
        return externalOrderItem;
    }

    /**
     * @return Returns the returned.
     */
    public boolean isReturned()
    {
        return returned;
    }

    /**
     * @param returned The returned to set.
     */
    public void setReturned(boolean returned)
    {
        this.returned = returned;
    }

    /**
     * @return Returns the selected.
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected The selected to set.
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

}
