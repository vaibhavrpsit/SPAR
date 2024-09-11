/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BooleanWithReasonBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:36 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//-------------------------------------------------------------------------
/**
    This bean model contains a BigDecimal and a reason code. It is used by
        the following beans:
    <UL>
    <LI>ItemTaxOnOffBean
    </UL>
    @see oracle.retail.stores.pos.ui.beans.ItemTaxOnOffBean
    @author  $KW=@(#); $Own=michaelm; $EKW;
    @version $KW=@(#); $Ver=rapp.q_2.6.1:2; $EKW;
 */
//-------------------------------------------------------------------------
public class BooleanWithReasonBeanModel extends ReasonBeanModel
{
    //--------------------------------------------------------------------------
    /**
     *  fieldValue represents the boolean value of the Model.
     */
    //--------------------------------------------------------------------------
    protected boolean fieldValue = false;
    //--------------------------------------------------------------------------
    /**
        Revision Number supplied by TeamConnection.
     */
    //--------------------------------------------------------------------------
    protected static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    //--------------------------------------------------------------------------
    /**
     *  DiscPercentBeanModel constructor comment.
     */
    //--------------------------------------------------------------------------
    public BooleanWithReasonBeanModel() 
    {
        super();
    }
    //--------------------------------------------------------------------------
    /**
     *  Gets the value property (boolean) value.
     *  @return The value property value.
     *  @see #setValue
     */
    //--------------------------------------------------------------------------
    public boolean getValue() 
    {
        return fieldValue;
    }
    //--------------------------------------------------------------------------
    /**
     *  Sets the value property (boolean) value.
     *  @param value The new value for the property.
     *  @see #getValue
     */
    //--------------------------------------------------------------------------
    public void setValue(boolean value) 
    {
        boolean oldValue = fieldValue;
        fieldValue = value;
    }
}
