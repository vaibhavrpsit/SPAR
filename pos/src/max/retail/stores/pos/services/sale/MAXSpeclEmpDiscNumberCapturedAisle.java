/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved. 
  Rev 1.0   Kamlesh Pant		08/12/2022		SpecialEmpDiscount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.employee.MAXEmployeeIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.services.pricing.employeediscount.MAXEmployeeNumberCapturedAisle;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXEmployeeDiscountBeanModel;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle reads in the employee number.
    @version $Revision: 5$
**/
//--------------------------------------------------------------------------
public class MAXSpeclEmpDiscNumberCapturedAisle extends MAXEmployeeNumberCapturedAisle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4956788268919304222L;
	/**
       revision number
    **/
    public static String revisionNumber = "$Revision: 5$";
    /**
	minimum accepted employee id length
	 **/
	public static final int EMPLOYEE_ID_MIN_LENGTH = 3;
	/**
		maximum accepted employee id length
	 **/
	public static final int EMPLOYEE_ID_MAX_LENGTH = 10;
	/**
		employee number label tag
	 **/
	public static final String EMPLOYEE_ID_TAG = "EmployeeID";
	/**
	    employee number label
	 **/
	public static final String EMPLOYEE_ID = "Employee ID";
	/**
	    empty label
	 **/
	public static final String EMPTY_LABEL = "";
	/**
	    constant to identify the number of fields on the ui
	 **/
	public static final int FIELD_COUNT = 4;
    
    //----------------------------------------------------------------------
    /**
     *   Reads in the employee number from the UI. <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {   
    	//System.out.println("MAXSpeclEmpDiscNumberCapturedAisle");
        MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();  //Rev 1.0 changes
        //cargo.setEmpID(true);
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
     //   POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.EMPLOYEE_NUMBER);
        //String empModel=ui.getInput();
       // PromptAndResponseModel pAndRModel = empModel.getPromptAndResponseModel();
        MAXEmployeeDiscountBeanModel empModel=(MAXEmployeeDiscountBeanModel)ui.getModel(MAXPOSUIManagerIfc.EMPLOYEE_ID);
        
        UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        
        String employeeID = null;
         
        employeeID=empModel.getEmpId();
        String selectedCompanyName=empModel.getSelectedReason();
        //System.out.println("selectedCompanyName======= "+selectedCompanyName);
     
        if (isEmpNumberLengthValid(employeeID))
        {
        	boolean isEmpNumberValid = true;
			boolean isCentralConnected = true;

			MAXCentralEmployeeTransaction centralEmployeeTransaction = null;
			MAXEmployeeIfc employee = null;
			
			centralEmployeeTransaction = (MAXCentralEmployeeTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_TRANSACTION);
			try {
				
				employee = centralEmployeeTransaction.getEmployeeNumber(employeeID,selectedCompanyName);
				if(employee!=null){
					if (employee.getLoginStatus() == 0)
						isEmpNumberValid = false;
					else
						isEmpNumberValid = true;
				}
				if(employee == null){
					logger.error("CO Returned employee null : MAXEmployeeNumberCapturedAisle ");
					DialogBeanModel dialogModel = new DialogBeanModel();
					dialogModel.setResourceID("InactiveEmployee");
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				}
				// Changes ends for Rev 1.0 ( Ashish : Emplyee Discount)
			} catch (DataException e) {
				
				logger.error("Error Accessing the CO DB: ", e);
				if(e.getErrorCode()==DataException.CONNECTION_ERROR || e.getErrorCode()==DataException.UNKNOWN)
					isCentralConnected = false;
				else
					isEmpNumberValid = false;
			}
			
			if(employee!=null)
			{	
				MAXEmployee.availAmount=Integer.parseInt(employee.getAvailableAmount());
				if(employee.getSpecialEmployeeDiscountValue()=="1"||employee.getSpecialEmployeeDiscountValue().equals("1")){
//					MAXEmployee.specialDiscountFlag=true; 
				}
			}
			// Changes start for rev 1.0 (Ashish : Employee Discount)
			if (employee!=null && Integer.parseInt(employee.getStatusCode())!= 0){
				//Insufficient Fund Notice
				
			
			if (employee!=null && Integer.parseInt(employee.getAvailableAmount())< 1){
				//Insufficient Fund Notice
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("InsufficientBalance");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			// Changes start for Rev 1.0 (Ashish : Employee Discount : Added one more condition employee!=null)
			else if (isEmpNumberValid && isCentralConnected && employee!=null) {
								DialogBeanModel dialogModel = new DialogBeanModel();

				String msg[] = new String[7];
                 /** Changes for Rev 1.2 : Starts **/
				msg[0] = employee.getEmployeeID();//Id
				msg[1] = employee.getName().getFirstName() + " " + employee.getName().getLastName();//Name
				msg[2] = employee.getName().getFullName();//Department
				msg[3] = employee.getName().getMiddleName();//Location
				
				//msg[4] = employee.getEligibleAmount();//Eligible Amount
				//msg[5] = employee.getAvailableAmount();//Available Amount
				// Changes For Rev 1.2 Start
				msg[6]=employee.getCompanyName().trim().toString();
				cargo.setEmpID(true);
				// Changes For Rev 1.2 End
				/** Changes for Rev 1.2 : Ends **/
				dialogModel.setResourceID("SPECIAL_EMPLOYEE_Discount_DETAIL_DISPLAY");
				dialogModel.setArgs(msg);
				dialogModel.setType(DialogScreensIfc.YES_NO);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Continue");
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Back");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				/** Changes for Rev 1.2 : Starts **/
			}
				else if(!isCentralConnected){
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("StoreNotConnected");
				dialogModel.setType(DialogScreensIfc.ERROR);
//				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
				/** Changes for Rev 1.2 : Ends **/
			 else {
				logger.error("Unknown error accessing the CO DB: ");
					// initialize model bean
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("InactiveEmployee");
				dialogModel.setType(DialogScreensIfc.ERROR);
//				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
                 	/** Changes for Rev 1.2 : Starts **/
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
					/** Changes for Rev 1.2 : Ends **/
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			}
			else if (employee!=null && Integer.parseInt(employee.getStatusCode())== 0){
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("NotactiveEmployee");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			else if(!isCentralConnected){
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("StoreNotConnected");
				dialogModel.setType(DialogScreensIfc.ERROR);
//				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			
			// Changes ends for rev 1.0 (Ashish : Employee Discount)
		
        }
        /**
    	 * Rev 1.0 changes end here
    	 */
        else
        {
        	String msg[] = new String[FIELD_COUNT];
            // initialize model bean
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("INVALID_DATA");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
            
            msg[0] = util.retrieveDialogText(EMPLOYEE_ID_TAG,EMPLOYEE_ID);
            
            for (int i=1; i < FIELD_COUNT ; i++)
            {
                msg[i] = EMPTY_LABEL;
            }
            
            dialogModel.setArgs(msg);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        }
    }
    
    /*
     * Check whether the length of the employee login id entered is valid
     * and is not more than 10 characters and not less than 3 characters. 
     * This is to ensure that employee login id swiped in the application 
     * is validated for length in order to avoid any exception while saving the transaction.
     */
    protected boolean isEmpNumberLengthValid(String employeeId)
    {
        	if (employeeId != null && 
    			(employeeId.length() < EMPLOYEE_ID_MIN_LENGTH || employeeId.length() > EMPLOYEE_ID_MAX_LENGTH))
    		return false;
    	else
    		return true;
    }
  /**
   * Rev 1.0 changes start here
   * @param employeeId
   * @return
   */
    // below method is modified by atul shukla 
	protected boolean isEmpNumberValid(String employeeId, String comapanyName) {
//		EmployeeTransaction empTransaction = null;
		/** Changes for Rev 1.2 : Starts **/
		MAXCentralEmployeeTransaction empTransaction = null;
		EmployeeIfc employee = null;

//		empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
		empTransaction = (MAXCentralEmployeeTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_TRANSACTION);
		try {
//			employee = empTransaction.getEmployeeNumber(employeeId);
			empTransaction.getEmployeeNumber(employeeId, comapanyName);
			/** Changes for Rev 1.2 : Ends **/
		} catch (DataException e) {

			return false;

		}
		return true;
	}
	/**
	 * Rev 1.0 changes end here
	 */
}
