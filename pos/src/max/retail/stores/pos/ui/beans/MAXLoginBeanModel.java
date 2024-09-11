/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  26 Oct, 2016              Nadia              MAX-POS-LOGIN-FESV1 0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXLoginBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -3543360034693390013L;
    
    /** 
        Indicates whether to clear the date fields, default true.
    **/
    private boolean clearUIFields = true;

    String loginID = "";
    String password = "";
 
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
