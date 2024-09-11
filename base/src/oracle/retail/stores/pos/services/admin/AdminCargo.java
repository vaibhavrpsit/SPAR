/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/AdminCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/8/2008 7:28:31 PM    Sameer Thajudin The
 *         AdminCargo tended to return RoleFunctionIfc.ADMIN always when
 *         getAccessFunctionID was called.
 *
 *         A new local variable functionID is initalized to RoleFunctionIfc.
 *         The getAccessFunctionID() and setAccessFunctionID(int) methods are
 *         also implemented for functionID.
 *
 *         In this way, it can be controlled what getAccessFunctionID returns.
 *
 *         
 *    4    360Commerce 1.3         11/22/2007 10:59:03 PM Naveen Ganesh   PSI
 *         Code checkin
 *    3    360Commerce 1.2         3/31/2005 4:27:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:25 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:48:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:15  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:02:46   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   19 Nov 2001 09:39:08   pdd
 * Deprecated old override methods.
 * Added getAccessFunctionID().
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * 
 *    Rev 1.0   Sep 21 2001 11:10:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

// foundation imports
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

//--------------------------------------------------------------------------
/**
    This class provides the state information for the Aministration Service.    

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AdminCargo extends AbstractFinancialCargo
{
	/**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        false if no override is requested, true is override is needed
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected boolean securityOverrideFlag = false;
    /** 
        employee granting Security override
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected EmployeeIfc securityOverrideEmployee;
    /** 
        employee attempting Security override
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/    
    protected EmployeeIfc securityOverrideRequestEmployee;
    /** 
        employee attempting Access
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/    
    protected EmployeeIfc accessEmployee;
    /**
        Security override Return Letter 
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected String securityOverrideReturnLetter = "Default";

    /**
    This flag indicates if training mode is currently on.
    **/
    protected boolean trainingMode = false;
    
    private RegisterADO registerADO = null;
    
    /**
     This register holds the training mode register
    *  @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead.     
    **/
    private RegisterADO trainingRegisterADO = null;
    
    /**
     * By default this value is RoleFunctionIfc.ADMIN
     **/
    private int functionID = RoleFunctionIfc.ADMIN;
    
    
    
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  AdminCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
    //--------------------------------------------------------------------------
    /**
        Returns the securityOverrideFlag boolean. <P>
        @return The securityOverrideFlag boolean.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public boolean getSecurityOverrideFlag()
    {                                   // begin getSecurityOverrideFlag()
        return securityOverrideFlag;
    }                                   // end getSecurityOverrideFlag()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideFlag boolean. <P>
        @param  value  The ssecurityOverrideFlag boolean.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideFlag(boolean value)
    {                                   // begin setSecurityOverrideFlag()
        securityOverrideFlag = value;
                                        // end setSecurityOverrideFlag()
    } 
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideEmployee object. <P>
        @return The securityOverrideEmployee object.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideEmployee()
    {                                   // begin getSecurityOverrideEmployee()
        return securityOverrideEmployee;
    }                                   // end getSecurityOverrideEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the security override employee object. <P>
        @param  value  The security override employee object.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideEmployee()
        securityOverrideEmployee = value;
    }                                   // end setSecurityOverrideEmployee()
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideRequestEmployee object. <P>
        @return The securityOverrideRequestEmployee object.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {                                   // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    }                                   // end getSecurityOverrideRequestEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideRequestEmployee object. <P>
        @param  value  securityOverrideRequestEmployee object.
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    }                                   // end setSecurityOverrideRequestEmployee()
        //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently 
        logged on cashier or an Override Security Employee
        <P>
        @return the void
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }
    //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently 
        logged on cashier or an Override Security Employee
        <P>
        @return the EmployeeIfc value
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }
    
    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return 
        <P>
        @return the void
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }
    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return 
        <P>
        @return the String value
        @deprecated Deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }
    
    //  ----------------------------------------------------------------------
    /**
     Sets the trainingMode flag.
     @param value boolean
     **/
    //----------------------------------------------------------------------
    public void setTrainingMode(boolean value)
    {
        trainingMode = value;
    }

    //----------------------------------------------------------------------
    /**
     Returns the trainingMode flag.
     @return boolean
     **/
    //----------------------------------------------------------------------
    public boolean isTrainingMode()
    {
        return trainingMode;
    }
    
    public void setRegisterADO(RegisterADO register)
    {
       this.registerADO = register;
    }
    
    public RegisterADO getRegisterADO()
    {
       return registerADO;
    }
    
    //---------------------------------------------------------------------
    /**
     Sets training mode Register ADO. <P>
     @param trainingRegister  new training mode Register ADO
     *  @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead.     
     **/
    //---------------------------------------------------------------------
    public void setTrainingRegisterADO(RegisterADO trainingRegister)
    {
        this.trainingRegisterADO = trainingRegister;
    }
    
    //---------------------------------------------------------------------
    /**
     Returns training mode Register ADO. <P>
     @return training mode Register ADO
     *  @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead.     
     **/
    //---------------------------------------------------------------------
    public RegisterADO getTrainingRegisterADO()
    {
        RegisterIfc register = (RegisterIfc)registerADO.toLegacy();
        RegisterIfc trainingRegister = (RegisterIfc)register.clone();
        StoreStatusIfc ss = (StoreStatusIfc)registerADO.getStoreADO().toLegacy().clone();
        trainingRegister.getWorkstation().setTrainingMode(true);
        trainingRegisterADO.fromLegacy(trainingRegister);
        trainingRegisterADO.getStoreADO().fromLegacy(ss);
        register.setOtherRegister(trainingRegister);
        trainingRegister.setOtherRegister(register);
        return trainingRegisterADO;
    }
    
    
  //--------------------------------------------------------------------- 
    /**
      Sets the access function id.    
      @param a functionID from the set of values in RoleFunctionIfc
     **/
  //---------------------------------------------------------------------  
    
    public void setAccessFunctionID(int functionID)
    {
    	this.functionID = functionID;
    	
    }
    
  //--------------------------------------------------------------------- 
    /**
      Gets the access function id.    
      @return Returns a functionID. This value has to be a member of the set of values 
       in RoleFunctionIfc
     **/
  //---------------------------------------------------------------------  
  
    
    public int getAccessFunctionID()
    {
    	return this.functionID;
    }
}
    
   
    
 
