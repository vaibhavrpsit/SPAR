/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SetAccessSelectBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/** 
    This is the bean model that is used by the SetAccessSelectBean. <P>
    This bean model is used to access an array of Role Functions,
    handle their function names and also their boolean access values.
    @see oracle.retail.stores.pos.ui.beans.SetAccessSelectBean
    @version $KW=; $Ver=; $EKW;
 */
//--------------------------------------------------------------------------
public class SetAccessSelectBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=; $Ver; $EKW;";

    /**
        functions array size
    **/
    protected int functionsArraySize = 0;
    
    /**
        role function title
    **/
    protected String[] roleFunctionTitle = null;
    
    /**
        role function access
    **/
    protected String[] roleFunctionAccess = null;

    //----------------------------------------------------------------------------
    /**
        SetAccessSelectBeanModel constructor comment.
    **/
    //----------------------------------------------------------------------------    
    public SetAccessSelectBeanModel()
    {
    }

    //----------------------------------------------------------------------------
    /**
        Gets the functionsArraySize property value.
        @return int representing thhe functionsArraySize property value.
        @see #setFunctionsArraySize
    **/
    //----------------------------------------------------------------------------
    public int getFunctionsArraySize()
    {
        return functionsArraySize;           
    }

    //----------------------------------------------------------------------------
    /**
        Sets the functionsArraySize property value.
        @param value the functionsArraySize value property.
        @see #getFunctionsArraySize
    */
    //----------------------------------------------------------------------------
    public void setFunctionsArraySize(int value)
    {
        // get the int value obtained from the role function        
        functionsArraySize = value;
    }
    
    //----------------------------------------------------------------------------
    /**
        Gets the roleFunctionTitle property value.
        @return String[] the roleFunctionTitle property value.
        @see #setRoleFunctionTitle
    **/
    //----------------------------------------------------------------------------
    public String[] getRoleFunctionTitle()
    {
        return roleFunctionTitle;           
    }
    
    //----------------------------------------------------------------------------
    /**
        Sets the roleFunctionTitle property value.
        @param functionTitle the roleFunctionTitle property value.
        @see #getRoleFunctionTitle
    */
    //----------------------------------------------------------------------------
    public void setRoleFunctionTitle(String[] functionTitle)
    {
        // copy the String array of function titles obtained from the
        // site into this bean model's String titles array
        roleFunctionTitle = new String[functionTitle.length];
        
        System.arraycopy(functionTitle, 0, roleFunctionTitle, 0, 
            functionTitle.length);        
    }
    
    //----------------------------------------------------------------------------
    /**
        Gets the roleFunctionAccess property value.
        @return String[] the roleFunctionAccess property value.
        @see #setRoleFunctionAccess
    **/
    //----------------------------------------------------------------------------
    public String[] getRoleFunctionAccess()
    {
        return roleFunctionAccess;           
    }    
  
    //----------------------------------------------------------------------------
    /**
        Sets the roleFunctionAccess property value.
        @param functionAccess the roleFunctionAccess property value.
        @see #getRoleFunctionAccess
    */
    //----------------------------------------------------------------------------
    public void setRoleFunctionAccess(String[] functionAccess)
    {
        // copy the String array of function Access values obtained from the
        // site into this bean model's String Access array
        roleFunctionAccess = new String[functionAccess.length];
        
        System.arraycopy(functionAccess, 0, roleFunctionAccess, 0, 
            functionAccess.length);        
    }
    
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                
        // result string
        String strResult = new String("Class: SetAccessSelectBeanModel"
            + "(Revision " + getRevisionNumber() + ")" + hashCode());
            
        // pass back result
        return(strResult);
    }                                  

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  
}
