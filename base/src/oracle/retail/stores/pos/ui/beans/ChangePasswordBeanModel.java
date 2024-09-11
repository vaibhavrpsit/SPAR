/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ChangePasswordBeanModel.java /main/15 2014/01/28 11:05:39 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         10/4/2006 10:50:53 AM  Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//----------------------------------------------------------------------------
/**
 * This is model for Change Password Screen.
 * 
 * @version $Revision: /main/15 $
 */
//----------------------------------------------------------------------------
public class ChangePasswordBeanModel extends POSBaseBeanModel
{
    /**
     * Revision number
     */
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
     * employee login id 
     */
    protected String fieldLoginID = "";
    /**
     * Password
     */
    protected byte[] fieldPassword = new byte[0];
    /**
     * New Password
     */
    protected byte[] fieldNewPassword = new byte[0];
    /**
     * Verify to Re-enter Password
     */
    protected byte[] fieldVerifyPassword = new byte[0];
   

  
    //----------------------------------------------------------------------------
    /**
     * Get the value of the LoginID field
     * 
     * @return the value of LoginID
     */
    //----------------------------------------------------------------------------
    public String getLoginID()
    {
        return fieldLoginID;
    }
    //----------------------------------------------------------------------------
    /**
     * Get the value of the Password field
     * 
     * @return the value of Password
     */
    //----------------------------------------------------------------------------
    public byte[] getPassword()
    {
        return fieldPassword;
    }
    
    //----------------------------------------------------------------------------
    /**
     * Get the value of the Password field
     * 
     * @return the value of Password
     */
    //----------------------------------------------------------------------------
    public byte[] getNewPassword()
    {
        return fieldNewPassword;
    }
    //----------------------------------------------------------------------------
    /**
     * Get the value of the Verify Re-enter Password field
     * 
     * @return the value of Verify Password
     */
    //----------------------------------------------------------------------------
    public byte[] getVerifyPassword()
    {
        return fieldVerifyPassword;
    }

    //----------------------------------------------------------------------------
    /**
     * Sets the LoginID field
     * 
     * @param loginID
     *            the value to be set for loginID
     */
    //----------------------------------------------------------------------------
    public void setLoginID(String loginID)
    {
        fieldLoginID = loginID;
    }

    //----------------------------------------------------------------------------
    /**
     * Sets the Password field
     * 
     * @param password
     *            the value to be set for Password
     */
    //----------------------------------------------------------------------------
    public void setPassword(byte[] password)
    {
        fieldPassword = password;
    }
    
    //----------------------------------------------------------------------------
    /**
     * Sets the Password field
     * 
     * @param newPassword
     *            the value to be set for Password
     */
    //----------------------------------------------------------------------------
    public void setNewPassword(byte[] newPassword)
    {
        fieldNewPassword = newPassword;
    }
    
    //----------------------------------------------------------------------------
    /**
     * Sets the VerifyPassword field
     * 
     * @param verifyPassword
     *            the value to be set for VerifyPassword
     */
    //----------------------------------------------------------------------------
    public void setVerifyPassword(byte[] verifyPassword)
    {
        fieldVerifyPassword = verifyPassword;
    }

    //---------------------------------------------------------------------
    /**
     * Converts to a string representing the data in this Object @returns
     * string representing the data in this Object
     * @return String 
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append(
            "Class: ChangePasswordBeanModel Revision: "
                + revisionNumber
                + "\n");
        buff.append("LoginID [" + fieldLoginID + "]\n");
        buff.append("Password [" + fieldPassword + "]\n");
        buff.append("NewPassword [" + fieldNewPassword + "]\n");
        buff.append("VerifyPassword [" + fieldVerifyPassword + "]\n");
        return (buff.toString());
    }
}
