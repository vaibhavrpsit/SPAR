/* ===========================================================================
 *  Copyright (c) 2019 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  5th May 2020	Karni Singh POS REQ: Register CRM customer with OTP
 * Initial revision.
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.tic;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.log.SysoCounter;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
// Foundation imports
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.dailyoperations.register.registeropen.RegisterOpenCargo;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;

//--------------------------------------------------------------------------
/**
 * This site checks to see if the current operator has access to the specified
 * function.
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXLMREnrolmentOverrideAccessSite extends SiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8300401564324649108L;
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	// ----------------------------------------------------------------------
	/**
	 * Check access and mail appropriate letter.
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// return variable to indicate whether the user has access to the
		// code function or not.
		boolean access = false;

		// Default letter assumes that the operator will need to override the
		// access credentials of the current user.
		String letter = CommonLetterIfc.OVERRIDE;

	
			// get the cargo for the information needed to check the access.
			UserAccessCargoIfc cargo = (UserAccessCargoIfc) bus.getCargo();		
									
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.OTP_CANCEL);
			cargo.getAccessFunctionID();
		
			
			//cargo.setResourceID("SecurityErrorNotice");
			
			// get the security manager to be able to check the access to the
			// code
			// function of the current user.
			SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher().getManager(SecurityManagerIfc.TYPE);
			//changes for manager override Kajal Nautiyal start
		//	RetailTransactionIfc trans =cargo.getRetailTransaction();
		/*
		 * MAXManagerOverride mgo= new MAXManagerOverride();
		 * //mgo.setBusinessDay(((SaleCargo) cargo).getTransaction().getBusinessDay());
		 * mgo.setStoreCreditId("LMR Enrolment OTP"); mgo.setFeatureId("326");
		 * mgo.setItemId(null);
		 * mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
		 * mgo.setSequenceNumber("-1"); mgo.setTransactionID(((SaleCargo)
		 * cargo).getRetailTransaction().getTransactionID());
		 * //mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		 * //mgo.setWsID(((SaleCargo)
		 * cargo).getRetailTransaction().getWorkstation().getWorkstationID().toString())
		 * ; //mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		 * //mgo.setStoreID(((SaleCargo)
		 * cargo).getRetailTransaction().getWorkstation().getStoreID().toString());
		 * String storeId = Gateway.getProperty("application", "StoreID", null);
		 * mgo.setStoreID(storeId); String workStationId =
		 * Gateway.getProperty("application", "WorkstationID", null);
		 * mgo.setWsID(workStationId); mgo.setCashierID(((SaleCargo)
		 * cargo).getEmployee().getLoginID().toString()); //
		 * mgo.setAmountMO(trans.getTransactionTotals().getGrandTotal().toString());
		 * mgo.setAmountMO(null);
		 */
			//mgo.setAmountMO("786");
			//System.out.println("LMR register");
		//	MAXSaveManagerOverrideTransaction dbTrans = null;
		//	dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
		/*
		 * try { dbTrans.saveManagerOverride(mgo); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
				/*
				 * if(null != MAXUtilityManager.getManagerOverrideMap()) { if(null != trans &&
				 * trans instanceof MAXSaleReturnTransactionIfc) {
				 * ((MAXSaleReturnTransactionIfc)trans).setManagerOverrideMap(MAXUtilityManager.
				 * getManagerOverrideMap()); } }
				 */
		  	//Changes for Manager Override Report Requirement - End
		 
		//	MAXManagerOverrideIfc managerOverride = ((MAXDomainObjectFactoryIfc) DomainGateway
				//	.getFactory()).getMAXManagerOverrideInstance();			
			//	managerOverride.setAdditionalInfo("LMR Enrolment");	
				
				String transactionId="";
				if(bus.getCargo() instanceof MAXCustomerMainCargo && ((MAXCustomerMainCargo) bus.getCargo()).getTransaction() != null)
				{
					System.out.println("if Cargo Type:"+bus.getCargo());
					transactionId= ((MAXCustomerMainCargo) bus.getCargo()).getTransaction().getTransactionID();					
				 
				}
				else if(bus.getCargo() instanceof MAXSaleCargo && ((MAXSaleCargo) bus.getCargo()).getTransaction() != null)
				{
					transactionId= ((MAXSaleCargo) bus.getCargo()).getTransaction().getTransactionID();					
					
				}
				System.setProperty("TransactionID", (transactionId) != null ? transactionId : "NA");
			
				MAXManagerOverride mgo= new MAXManagerOverride();
				Date date = new Date();
				String BusinessDay= new SimpleDateFormat("yyyy-MM-dd").format(date);
				//mgo.setBusinessDay(((SaleCargo) cargo).getTransaction().getBusinessDay());
				System.out.println("BusinessDay :" +BusinessDay);
				mgo.setStoreCreditId("LMR Enrolment OTP");
				mgo.setFeatureId("326");
				mgo.setItemId(null);
				mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
				mgo.setSequenceNumber("-1");
				mgo.setTransactionID(transactionId);
				String storeId = Gateway.getProperty("application", "StoreID", null);
				mgo.setStoreID(storeId);
				System.out.println("storeId :"+storeId);
				String workStationId = Gateway.getProperty("application", "WorkstationID", null);
				mgo.setWsID(workStationId);
				System.out.println("workStationId :"+workStationId);
				mgo.setCashierID(cargo.getOperator().getLoginID().toString());
				//mgo.setCashierID((cargo).getEmployee().getLoginID().toString());
				mgo.setAmountMO(null);

				MAXSaveManagerOverrideTransaction dbTrans = null;
				dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
				

				try {
					dbTrans.saveManagerOverride(mgo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				
			// Check the access of the user to the code function
			access = securityManager.checkAccess(cargo.getAppID(), cargo.getAccessFunctionID());

			// if access has been granted then need to send a CONTINUE letter.
			if (access) {
				letter = CommonLetterIfc.CONTINUE;
			}
		
		bus.mail(new Letter(letter), BusIfc.CURRENT);
	}
	

	// ----------------------------------------------------------------------
	/**
	 * Returns the revision number of the class.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}
}
