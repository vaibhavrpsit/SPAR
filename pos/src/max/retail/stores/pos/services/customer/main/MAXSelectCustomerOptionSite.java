package max.retail.stores.pos.services.customer.main;

import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXSelectCustomerOptionSite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = 1L;

	/**
       revision number
	 **/
	public static final String revisionNumber = "$Revision: 3$";


	public static final String ADDTICCUSTOMEER = "AddTICCustomer";


	//----------------------------------------------------------------------
	/**
       Prompts the operator for action to take on a customer record.
       The operator can select Add, Find, or Delete from this menu. <p>
       @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public void arrive(BusIfc bus)
	{

		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo)bus.getCargo();
		//akhilesh changes for tic customer start
		boolean trainingMode=false;
		boolean reentryMode=false;

		if(cargo.getRegister()!=null && cargo.getRegister().getWorkstation()!=null){
			trainingMode=cargo.getRegister().getWorkstation().isTrainingMode();
			reentryMode=cargo.getRegister().getWorkstation().isTransReentryMode();
		}

		//changes for the tic customer second phase for the check Start
		//For the evaluation of the phone no and to get the customer value from data base during login

		if(((MAXCustomerMainCargo)cargo).isTicCustomerPhoneNoFlag()){
			bus.mail(new Letter("CustomerByPhoneNumber"),BusIfc.CURRENT);
		}else{
			//changes for the tic customer second phase for the check END

			// Call the journalling utility method passing in employee ID and trans ID
			// Since this is Customer functionality, transaction ID may be null.
			// Entry to Customer Service should only be journalled upon the initial entry.
			if (cargo.isInitialEntry())
			{
				if (cargo.getTransactionID() != null)
				{
					CustomerUtilities.journalCustomerEnter(bus,cargo.getOperator().getEmployeeID(),
							cargo.getTransactionID());
				}
				cargo.setInitialEntry(false);
			}

			// show the screen
			POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
			POSBaseBeanModel model = new POSBaseBeanModel();
			NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

			//Check if customer service was call for lookup purposes only
			nModel.setButtonEnabled(CustomerCargo.ADD, cargo.isAddCustomerEnabled() && !cargo.isFindOnlyMode());
			nModel.setButtonEnabled(CustomerCargo.ADDBUS, cargo.isAddBusinessEnabled() && !cargo.isFindOnlyMode());
			nModel.setButtonEnabled(CustomerCargo.DELETE, cargo.isDeleteEnabled() && !cargo.isFindOnlyMode());
			//akhilesh changes for tic customer start
			nModel.setButtonEnabled(MAXSelectCustomerOptionSite.ADDTICCUSTOMEER, cargo.isDeleteEnabled() && !cargo.isFindOnlyMode());
			//akhilesh changes for tic customer End 
			model.setLocalButtonBeanModel(nModel);

			MAXSaleReturnTransactionIfc transaction=null;
			if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){
				transaction=(MAXSaleReturnTransactionIfc)cargo.getTransaction();
			}

			MAXTICCustomerIfc customerIfc=null;
			if(transaction!=null && transaction.getMAXTICCustomer() instanceof MAXTICCustomerIfc){
				customerIfc= (MAXTICCustomerIfc) transaction.getMAXTICCustomer();	
			}

			MAXCustomerIfc customer =null;
			if (transaction!=null && transaction.getCustomer() instanceof MAXCustomerIfc) {
				customer = (MAXCustomerIfc) transaction.getCustomer();
			}

			String TICCustomerButton=Gateway.getProperty("application", "TICCustomerButton", "NO");

			if(!cargo.isTICCustomerRequire()){
				ui.showScreen(MAXPOSUIManagerIfc.CUSTOMER_OPTIONS, model);
			}
			//akhilesh changes for tic customer start
			else if(!(customer != null && customer.getCustomerType()!=null && customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)) && !(customerIfc!=null && customerIfc.getTICCustomerID()!=null && !customerIfc.getTICCustomerID().equalsIgnoreCase("")) && (TICCustomerButton!=null && TICCustomerButton.equalsIgnoreCase("YES")  ) && !reentryMode && !trainingMode){
				ui.showScreen(MAXPOSUIManagerIfc.TIC_CUSTOMER_OPTIONS, model);
			}
			//akhilesh changes for tic customer END

			else{
				ui.showScreen(MAXPOSUIManagerIfc.CUSTOMER_OPTIONS, model);
			}
		}
	}
}
