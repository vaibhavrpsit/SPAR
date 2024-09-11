/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerSelectBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/04/27 17:24:31  cdb
 *  @scr 4166 Removed unintentional null pointer exception potential.
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//java imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.utility.Util;

//---------------------------------------------------------------------
/**
    This bean model is used by CustomerSelectBean
    @see CustomerSelectBean
    @version $KW=@(#); $Ver; $EKW;
    @deprecated as of release 5.0.0 replaced by @link{oracle.retail.stores.pos.ui.beans.DualListBeanModel.java}
 */
//---------------------------------------------------------------------
public class CustomerSelectBeanModel extends POSBaseBeanModel
{

    /**
        revision number
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver; $EKW;";

    /**
        Customer query field
    **/
    protected CustomerIfc fieldCustomerQuery = null;

    /**
        Customer matches field
    **/
    protected java.util.Vector fieldCustomerMatches = new java.util.Vector();

    /**
        Customer Match field
    **/
    protected int fieldSelectedCustomerMatch = -1;

    //---------------------------------------------------------------------
    /**
    * CustomerSelectBeanModel constructor comment.
    */
    //---------------------------------------------------------------------
    public CustomerSelectBeanModel()
    {
        super();
        fieldCustomerQuery = DomainGateway.getFactory().getCustomerInstance();
    }

    //---------------------------------------------------------------------
    /**
    * Gets the customerMatches property (java.util.Vector) value.
    * @return The customerMatches property value.
    * @see #setCustomerMatches
    */
    //---------------------------------------------------------------------
    public java.util.Vector getCustomerMatches()
    {
        return fieldCustomerMatches;
    }

    //---------------------------------------------------------------------
    /**
    * Gets the customerQuery property (oracle.retail.stores.domain.customer.Customer) value.
    * @return The customerQuery property value.
    * @see #setCustomerQuery
    */
    //---------------------------------------------------------------------
    public CustomerIfc getCustomerQuery()
    {
        return fieldCustomerQuery;
    }

    //---------------------------------------------------------------------
    /**
    * Gets the selectedCustomerMatch property (int) value.
    * @return The selectedCustomerMatch property value.
    * @see #setSelectedCustomerMatch
    */
    //---------------------------------------------------------------------
    public int getSelectedCustomerMatch()
    {
        return fieldSelectedCustomerMatch;
    }


    //---------------------------------------------------------------------
    /**
    * Sets the customerMatches property (java.util.Vector) value.
    * @param customerMatches The new value for the property.
    * @see #getCustomerMatches
    */
    //---------------------------------------------------------------------
    public void setCustomerMatches(java.util.Vector customerMatches)
    {
        java.util.Vector oldValue = fieldCustomerMatches;
        fieldCustomerMatches = customerMatches;
    }

    //---------------------------------------------------------------------
   /**
    * Sets the customerQuery property (Customer) value.
    * @param customerQuery The new value for the property.
    * @see #getCustomerQuery
    */
    //---------------------------------------------------------------------
    public void setCustomerQuery(CustomerIfc customerQuery)
    {
        CustomerIfc oldValue = fieldCustomerQuery;
        fieldCustomerQuery = customerQuery;

    }

    //---------------------------------------------------------------------
    /**
    * Sets the selectedCustomerMatch property (int) value.
    * @param selectedCustomerMatch The new value for the property.
    * @see #getSelectedCustomerMatch
    */
    //---------------------------------------------------------------------
    public void setSelectedCustomerMatch(int selectedCustomerMatch)
    {
        int oldValue = fieldSelectedCustomerMatch;
        fieldSelectedCustomerMatch = selectedCustomerMatch;
     }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
    //---------------------------------------------------------------------
    /**
    * This returns a printable version of this bean model.
    * @return java.lang.String
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        if (getCustomerQuery() != null)
        {
            s.append(getCustomerQuery().toString());
        }
        else
        {
            s.append("null");
        }
        s.append(" selected match=").append(Integer.toString(getSelectedCustomerMatch()));
        if (getCustomerMatches() != null)
        {
            s.append(" {").append(getCustomerMatches().toString()).append("}");
        }
        else
        {
            s.append(" {null}");
        }
        return s.toString();
    }
}
