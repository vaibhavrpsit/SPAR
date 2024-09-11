/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ExternalOrderSearchBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/12/10 - Search external orders flow
 *    abondala  05/12/10 - New class to dsiplay advanced search criteria for
 *                         retrieving external orders
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

//----------------------------------------------------------------------------
/**
    This is model for searching the external orders by search criteria.
**/
//----------------------------------------------------------------------------
public class ExternalOrderSearchBeanModel extends ReasonBeanModel
{

    private static final long serialVersionUID = 1L;
    /**
        Revision number
    */
    public static String revisionNumber = "$KW=@(#); $Ver=;  $EKW;";
    /**
        First Name data
    */
    String fieldFirstName = "";
    /**
        Last Name data
    */
    String fieldLastName = "";
    /**
        Account data
     */
    String fieldAccount = "";
    /**
        PhoneNumber data
     */
    String fieldTelephoneNumber = "";
    /**
           OrderNumber data
     */
    String fieldOrderNumber = "";
    /**
         IncludeAllStores data
     */
    boolean fieldIncludeAllStores = true;

    //----------------------------------------------------------------------------
    /**
        Get the value of the FirstName field
        @return the value of FirstName
    **/
    //----------------------------------------------------------------------------
    public String getFirstName()
    {
        return fieldFirstName;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the LastName field
        @return the value of LastName
    **/
    //----------------------------------------------------------------------------
    public String getLastName()
    {
        return fieldLastName;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the Account field
        @return the value of Account
    **/
    //----------------------------------------------------------------------------    
    public String getAccount() 
    {
        return fieldAccount;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the PhoneNumber field
        @return the value of PhoneNumber
    **/
    //----------------------------------------------------------------------------
    public String getTelephoneNumber() 
    {
        return fieldTelephoneNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the OrderNumber field
        @return the value of OrderNumber
    **/
    //----------------------------------------------------------------------------
    public String getOrderNumber() 
    {
        return fieldOrderNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the IncludeAllStores field
        @return the value of IncludeAllStores
    **/
    //----------------------------------------------------------------------------
    public boolean getIncludeAllStores() 
    {
        return fieldIncludeAllStores;
    }    
    //----------------------------------------------------------------------------
    /**
        Sets the FirstName field
        @param firstName the value to be set for FirstName
    **/
    //----------------------------------------------------------------------------
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the LastName field
        @param lastName the value to be set for LastName
    **/
    //----------------------------------------------------------------------------
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the Account field
        @param account the value to be set for account
    **/
    //----------------------------------------------------------------------------    
    public void setAccount(String account) 
    {
        this.fieldAccount = account;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the phoneNumber field
        @param phoneNumber the value to be set for PhoneNumber
    **/
    //----------------------------------------------------------------------------    
    public void setTelephoneNumber(String telephoneNumber) 
    {
        this.fieldTelephoneNumber = telephoneNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the orderNumber field
        @param orderNumber the value to be set for orderNumber
    **/
    //----------------------------------------------------------------------------    
    public void setOrderNumber(String orderNumber) 
    {
        this.fieldOrderNumber = orderNumber;
    }    
    //----------------------------------------------------------------------------
    /**
        Sets the includeAllStores field
        @param includeAllStores the value to be set for includeAllStores
    **/
    //----------------------------------------------------------------------------    
    public void setIncludeAllStores(boolean includeAllStores) 
    {
        this.fieldIncludeAllStores = includeAllStores;
    }    
    


    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: ExternalOrderSearchBeanModel Revision: " + revisionNumber + "\n");
        buff.append("FirstName[" + fieldFirstName + "]\n");
        buff.append("LastName [" + fieldLastName + "]\n");
        buff.append("Account [" + fieldAccount + "]\n");
        buff.append("PhoneNumber [" + fieldTelephoneNumber + "]\n");
        buff.append("OrderNumber [" + fieldOrderNumber + "]\n");
        buff.append("IncludeAllStores [" + fieldIncludeAllStores + "]\n");

        return(buff.toString());
    }
}
