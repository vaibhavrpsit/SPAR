/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DiscAmountBeanModel.java /main/16 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         1/22/2006 11:45:23 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:57 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:35 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:49:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:24   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *  
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.util.Vector;

import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
 *  This model controls Discount Amount screen.
 *  @see oracle.retail.stores.pos.ui.beans.DiscAmountBean.
 */
//--------------------------------------------------------------------------
public class DiscAmountBeanModel extends POSBaseBeanModel
{
    //--------------------------------------------------------------------------
    /**
     *  Revision Number supplied by TeamConnection.
     */
    //--------------------------------------------------------------------------
    protected static final String revisionNumber = "$Revision: /main/16 $";
    //--------------------------------------------------------------------------
    /**
     *  The amount of discount.
     */
    //--------------------------------------------------------------------------
    protected BigDecimal fieldAmount = BigDecimal.ZERO;
    //--------------------------------------------------------------------------
    /**
     *  Is the field selected.
     */
    //--------------------------------------------------------------------------
    protected boolean fieldSelected = false;
    //--------------------------------------------------------------------------
    /**
     *  Reason selected.
     */
    //--------------------------------------------------------------------------
    protected String fieldSelectedReason = new String();
    //--------------------------------------------------------------------------
    /**
     *  Container of the possible Reason codes.
     */
    //--------------------------------------------------------------------------
    protected Vector fieldReasonCodes = new Vector();
    //---------------------------------------------------------------------
    /**
     *  Constructor
     */
    //---------------------------------------------------------------------
    public DiscAmountBeanModel()
    {
        super();
    }
    //---------------------------------------------------------------------
    /**
     *  This method returns the amount
     *  @param String propertyName
     *  @return BigDecimal
     */
    //---------------------------------------------------------------------
    public BigDecimal getAmount()
    {
        return fieldAmount;
    }
    //---------------------------------------------------------------------
    /**
     *  Returns a vector of ReasonCodes
     *  @return Vector
    */
    //---------------------------------------------------------------------
    public Vector getReasonCodes()
    {
        return fieldReasonCodes;
    }
    //---------------------------------------------------------------------
    /**
     *  This method returns Reason.
     *  @return java.beans.PropertyChangeSupport
     *  @see #setSelectedReason
     */
    //---------------------------------------------------------------------
    public String getSelectedReason()
    {
        return fieldSelectedReason;
    }
    //---------------------------------------------------------------------
    /**
     *  This method returns fieldSelected field.
     *  @return boolean
     *  @see #setSelected
     */
    //---------------------------------------------------------------------
    public boolean isSelected()
    {
        return fieldSelected;
    }
    //---------------------------------------------------------------------
    /**
     *  Sets the Amount given BigDecimal
     *  @param BigDecimal amount
     *  @see #getAmount
     */
    //---------------------------------------------------------------------
    public void setAmount(BigDecimal amount)
    {
        fieldAmount = amount;
    }
    //---------------------------------------------------------------------
    /**
     *  Set the ReasonCodes vector
     *  @param reasonCodes The new value for the property.
     *  @see #getReasonCodes
     */
    //---------------------------------------------------------------------
    public void setReasonCodes(Vector reasonCodes)
    {
        fieldReasonCodes = reasonCodes;
    }
    //---------------------------------------------------------------------
    /**
     *  Set fieldSelected to boolean value.
     *  @param selected The new value for the property.
     */
    //---------------------------------------------------------------------
    public void setSelected(boolean selected)
    {
        fieldSelected = selected;
    }
    //---------------------------------------------------------------------
    /**
     *  Sets the SelectionReason
     *  @param selectedReason The new value for the property.
     */
    //---------------------------------------------------------------------
    public void setSelectedReason(String selectedReason)
    {
        fieldSelectedReason = selectedReason;
    }
}
