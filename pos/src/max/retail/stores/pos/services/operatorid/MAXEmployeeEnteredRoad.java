/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  12/April/2013               Himanshu              MAX-POS-LOGIN-FESV1 0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.operatorid;

// foundation imports
import java.io.UnsupportedEncodingException;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import org.apache.commons.codec.binary.Base64;

//--------------------------------------------------------------------------
/**
    This road is traveled when the employee ID has been entered.
    It stores the employee ID in the cargo.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MAXEmployeeEnteredRoad extends LaneActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1182425878243627175L;
    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores the employee ID in the cargo.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        /*
         * Get the input value from the UI Manager
         */
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_IDENTIFICATION);
        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
        
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        
        // the following is here only so that testing can be done on 
        // swiping of credit cards as employee login cards.  this 
        // can be removed once we have real employee login cards.
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
        
            if(pAndRModel.isSwiped()) 
            {
                if(pm.getStringValue("AutomaticEntryID").equals("User"))
                {
                    String loginID = null;
                    UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);  
                    loginID = util.getEmployeeFromModel(pAndRModel);
                    cargo.setEmployeeID(loginID);
                }
                else
                {
                    MSRModel msrModel = pAndRModel.getMSRModel();
                    String employeeID = null;
                    try
                    {
                        KeyStoreEncryptionManagerIfc encryptionManager =
                            (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
                        byte[] eID = encryptionManager.decrypt(Base64.decodeBase64(msrModel.getEncipheredCardData().getEncryptedAcctNumber().getBytes()));
                        // What if operator mistakenly swipes a credit card on this screen.  We can only
                        // assume that what was swiped is an employee card. Since employee ID are 10 chars
                        // or less we can chop off and throw away the excess.
                        if(eID.length > 10)
                        {
                            // not using System.arraycopy() since this is potentially sensitive data.
                            byte[] tmpID = new byte[10];
                            for(int i = 0; i < tmpID.length; i++)
                            {
                                tmpID[i] = eID[i];
                            }
                            employeeID = new String(tmpID);
                            // clear eID
                            Util.flushByteArray(eID);
                            eID = null;
                            // clear tmpID
                            Util.flushByteArray(tmpID);
                            tmpID = null;
                        }
                        else
                        {
                            employeeID = new String(eID);
                        }
                        cargo.setEmployeeID(employeeID);
                    }
                    catch(EncryptionServiceException ese)
                    {
                        logger.error("Could not decrypt employee ID", ese);
                    }
                }
            }
            else
            {
                // the following line will probably be only line needed when using real employee login cards
            	//<!-- MAX Rev 1.0 Change : Start -->
            	
            	String scanId=ui.getInput();
            	
            	String employeeId="";
            	String password="";
            		
            	
            		
            	if(scanId.indexOf('_')!=-1 && scanId.indexOf('@')!=-1 )
            	{
            	int beginIndex=scanId.indexOf('_');
            	int endIndex=scanId.indexOf('@');
            	
                employeeId=scanId.substring(beginIndex+1, endIndex);
                password=scanId.substring(endIndex+1);
            	}
            	
            	 cargo.setEmployeeID(employeeId);
            	 try
                 {
                     cargo.setEmployeePasswordBytes(password.getBytes(EmployeeIfc.PASSWORD_CHARSET));
                 }
            	 
                 catch (UnsupportedEncodingException e)
                 {
                     logger.error("Unable to use correct password character set", e);
                     if (logger.isDebugEnabled())
                         logger.debug("Defaulting to system character set: " + ui.getInput());
                     cargo.setEmployeePasswordBytes(ui.getInput().getBytes());
                 }
                 
            }
            
          //<!-- MAX Rev 1.0 Change : end -->
                     
        }
        catch (ParameterException pe)
        {
            System.out.println("*** error getting parameter AutomaticEntryID");
        }
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
        String strResult = new String("Class:  EmployeeEnteredRoad (Revision " +
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
        MAXEmployeeEnteredRoad obj = new MAXEmployeeEnteredRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
