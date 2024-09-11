/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LoginBeanModel.java /main/2 2014/01/28 11:05:39 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    blarsen   10/21/10 - login bean model
 *    blarsen   05/11/10 - login bean model
 *    
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


public class LoginBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -3543360034693390013L;
    
    /** 
        Indicates whether to clear the date fields, default true.
    **/
    private boolean clearUIFields = true;

    String loginID = "";
    byte[] password = new byte[0];
 
    /**
        Set clearUIFields flag to determine whether to clear the date fields. <P>
        @param boolean.
    **/
    //--------------------------------------------------------------------- 
    public void setclearUIFields(boolean value)
    {
        clearUIFields = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the current valud of clearUIFields.<P>
        @return value of clearUIFields flag.
    **/
    //--------------------------------------------------------------------- 
    public boolean getclearUIFields()
    {
        return(clearUIFields);
    }
    
    public String getLoginID()
    {
        return loginID;
    }

    public void setLoginID(String loginID)
    {
        this.loginID = loginID;
    }

    public byte[] getPassword()
    {
        return password;
    }

    public void setPassword(byte[] password)
    {
        this.password = password;
    }
}
