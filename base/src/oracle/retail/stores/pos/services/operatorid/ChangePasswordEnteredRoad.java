/* ===========================================================================
* Copyright (c) 2006, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/ChangePasswordEnteredRoad.java /main/10 2014/01/28 11:05:40 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     3    360Commerce 1.2         10/13/2006 2:57:12 PM  Rohit Sachdeva
 *          21237: Change Password Updates
 *     2    360Commerce 1.1         10/11/2006 4:48:21 PM  Rohit Sachdeva
 *          21237: Change Password Updates
 *     1    360Commerce 1.0         10/6/2006 3:17:39 PM   Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;


import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ChangePasswordBeanModel;

//--------------------------------------------------------------------------
/**
    This road stores the login info in the cargo for Validation.
    <p>
     @version $Revision: /main/10 $
**/
//--------------------------------------------------------------------------
public class ChangePasswordEnteredRoad extends LaneActionAdapter
{
	/**
       revision number
    **/
    public static String revisionNumber = "$Revision: /main/10 $";
    /**
     * parameter manual entry id. Value=Employee implies we should enter
     * id as employee id number such as '20027'. 
     * Value=User implies employee login id such as 'pos.
     * The screen label states User ID in both cases.
     * This is the same behaviour in normal login process also.
     */
    private static final String MANUAL_ENTRY_ID = "ManualEntryID";

    //----------------------------------------------------------------------
    /**
       Stores the password in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ChangePasswordBeanModel beanModel =
            (ChangePasswordBeanModel) ui.getModel(POSUIManagerIfc.CHANGE_PASSWORD);
        String userId = beanModel.getLoginID();
        cargo.setEmployeeID(userId);
        cargo.setEmployeePasswordBytes(beanModel.getPassword());
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String idParm = MANUAL_ENTRY_ID;
        try
        {
            cargo.setIDType(pm.getStringValue(idParm));
        }
        catch (ParameterException pe)
        {
        	logger.warn("*** error getting parameter" + idParm);
        }
        cargo.setLoginValidationChangePassword(true);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ChangePasswordEnteredRoad (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        PasswordEnteredRoad obj = new PasswordEnteredRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
