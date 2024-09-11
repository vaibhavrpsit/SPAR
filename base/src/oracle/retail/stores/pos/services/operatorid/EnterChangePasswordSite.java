/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EnterChangePasswordSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         10/6/2006 4:13:43 PM   Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ChangePasswordBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the Alternate Login Change Password Screen that alows two things:
    1. Allows Employee to Enter their Login Id(or Displays the Already Entered Login) and Current Password
    2. Enter New Password and Verify that.
    The Validate Login Check happens first and then only password compliance checks will happen eventually in
    the flow. This takes to ValidateLogin site.<p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterChangePasswordSite extends PosSiteActionAdapter
{    
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the Alternate Login Change Password Screen
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ChangePasswordBeanModel beanModel = new ChangePasswordBeanModel();
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        //If this is coming due to Password Policy Evaluations Employee Id is already there
        if (!Util.isEmpty(cargo.getEmployeeID()))
        {
            beanModel.setLoginID(cargo.getEmployeeID());
        }
        ui.showScreen(POSUIManagerIfc.CHANGE_PASSWORD, beanModel);

    }
  
    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EnterChangePasswordSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
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
}
