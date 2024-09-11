  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  26 Oct, 2016              Nadia              MAX-POS-LOGIN-FESV1 0.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.operatorid;
import max.retail.stores.pos.ui.beans.MAXLoginBeanModel;
import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site displays the LOGIN screen.
    <p>
     @version $Revision: 8$
**/
//--------------------------------------------------------------------------
public class MAXEnterEmployeeIDSite extends PosSiteActionAdapter
{                                       // begin class class EnterEmployeeIDSite
	
	/**
       revision number
    **/
    public static String revisionNumber = "$Revision: 8$";
    /**
     * Change Password Button
     */
    private static final String CHANGE_PASSWORD_BUTTON = "ChangePassword";

    //----------------------------------------------------------------------
    /**
       Displays the LOGIN screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //reset what is required
        resetCargo(bus);
        //check if we should even enable the change password button or not
    	NavigationButtonBeanModel nbbModel = checkChangePasswordAllowed(bus);    	
    	MAXLoginBeanModel model = new MAXLoginBeanModel();
    	/*PromptAndResponseModel pModel =new PromptAndResponseModel();*/
    	boolean enableResponse = true;
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
    	try {
			enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	/*pModel.setResponseEnabled(enableResponse);*/
    	/*model.setPromptAndResponseModel(pModel);*/
    	model.setLocalButtonBeanModel(nbbModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.OPERATOR_LOGIN, model);

        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        try
        {
        	// Changes starts for cod emerging(depricated class so using 
            //CIDAction showAction = new CIDAction(IngenicoLogo.SCREEN_NAME, CIDAction.SHOW);
        	CIDAction showAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SHOW);
        // Changes ends for code merging
            pda.cidScreenPerformAction(showAction);
        } 
        catch (DeviceException e)
        {
            logger.warn(
                        "Error using customer interface device: " + e.getMessage() + "");
        }
    }

   //----------------------------------------------------------------------
    /**
     * This resets cargo for settings that will be set again
     * @param bus reference to bus
     */
    //----------------------------------------------------------------------
	private void resetCargo(BusIfc bus) 
	{
		OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
    	cargo.setLoginValidationChangePassword(false);
    	cargo.setLockOut(false);
    	cargo.setEmployeeID("");
        cargo.setSelectedEmployee(null);
        cargo.setEvaluateStatusEnum(EmployeeStatusEnum.ACTIVE);
	}
 
    /**
     * This checks if Change Password needs to be enabled
     * @param bus reference to bus
     * @return  NavigationButtonBeanModel navigation bean model
     */
	private NavigationButtonBeanModel checkChangePasswordAllowed(BusIfc bus) 
	{
		NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
		//default button is enabled
		nbbModel.setButtonEnabled(CHANGE_PASSWORD_BUTTON, true);
		PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();
		boolean employeeComplianceAllowed = tdo.checkEmployeeComplianceEvaluationAllowed(bus);
		if (!employeeComplianceAllowed)
		{
			nbbModel.setButtonEnabled(CHANGE_PASSWORD_BUTTON, false);
			return nbbModel;	
		}
        boolean passwordRequired = tdo.checkPasswordParameter(bus);
        if(!passwordRequired)
        {
        	nbbModel.setButtonEnabled(CHANGE_PASSWORD_BUTTON, false);
			return nbbModel;	
        }	
		return nbbModel;
	}


    //----------------------------------------------------------------------
    /**
       Calls <code>arrive</code>
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void reset(BusIfc bus)
    {
        arrive(bus);
    }
	
   //----------------------------------------------------------------------
    /**
       Creates Instance of Password Policy TDO.
       @return PasswordPolicyTDOIfc instance of Password Policy TDO
    **/
    //----------------------------------------------------------------------
	private PasswordPolicyTDOIfc getPasswordPolicyTDO() 
	{
		PasswordPolicyTDOIfc tdo = (PasswordPolicyTDOIfc)TDOFactory.createBean(PasswordPolicyTDOIfc.PASSWORD_POLICY_TDO_BEAN_KEY);
		return tdo;
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
        String strResult = new String("Class:  EnterEmployeeIDSite (Revision " +
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

}                                       // end class EnterEmployeeIDSite
