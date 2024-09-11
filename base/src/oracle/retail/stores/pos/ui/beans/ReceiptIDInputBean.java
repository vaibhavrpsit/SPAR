/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReceiptIDInputBean.java /main/14 2012/10/29 12:55:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/30 02:53:38  mweis
 *   @scr 5555 Error returning with receipt when store ID less than 5 characters long
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;

import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.foundation.manager.gui.FieldSpec;

//--------------------------------------------------------------------------
/**
    Specialized class that enforces the transaction restrictions per the <code>TransactionID</code>
    class.  Most of the work is handled by the parent class.
    
    @see TransactionID

    $Revision: /main/14 $
    @deprecated in version 14.0; No longer used.
**/
//--------------------------------------------------------------------------
public class ReceiptIDInputBean extends DataInputBean
{
    /**
     *  store number
     */
    public static final String STORE_NUMBER_FIELD = "storeNumberField";

    /**
     * register number
     */
    public static final String REGISTER_NUMBER_FIELD = "registerNumberField";

    /**
     * transaction number
     */
    public static final String TRANS_NUMBER_FIELD = "transactionNumberField";
    //----------------------------------------------------------------------
    /**       
       Creates an empty bean. 
    **/
    //----------------------------------------------------------------------
    public ReceiptIDInputBean()
    {
        super();
    }

    /**
     * Establishes the fields.  Performs most of the work by simply calling the parent method.
     * As a special bonus, will enforce the field limits as per <code>TransactionID</code>.
     * 
     * @param fieldSpecs  The field specifications for the screen.
     */
    public void configureFields(FieldSpec[] fieldSpecs)
    {
        // Let our parent do most of the work
        super.configureFields(fieldSpecs);
        
        // Ensure we have the corrent min/max field length, per restrictions from TransactionID
        for (int i = 0; i < fieldSpecs.length; ++i)
        {
            String fieldName = fieldSpecs[i].getFieldName();
            
            if (fieldName.equals(STORE_NUMBER_FIELD))
            {
                setMinMax(this.components[i], TransactionID.getStoreIDLength());
            }
            else if (fieldName.equals(REGISTER_NUMBER_FIELD))
            {
                setMinMax(this.components[i], TransactionID.getWorkstationIDLength());
            }
            else if (fieldName.equals(TRANS_NUMBER_FIELD))
            {
                setMinMax(this.components[i], TransactionID.getSequenceNumberLength());
            }
        }
    }
    
    /**
     * Assuming the component is a <code>ConstrainedTextField</code>, will force the minimum and
     * maximum length to whatever the <code>limit</code> is.
     * 
     * @param component The component
     * @param limit     What the minimum and maximum length of the component will become
     */
    protected void setMinMax(JComponent component, int limit)
    {
        if (component != null && component instanceof ConstrainedTextField)
        {
            ConstrainedTextField ctf = (ConstrainedTextField) component;
            ctf.setMinLength(limit);
            ctf.setMaxLength(limit);
        }
    }
}
