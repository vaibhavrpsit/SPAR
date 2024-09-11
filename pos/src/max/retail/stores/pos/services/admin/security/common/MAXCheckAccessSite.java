/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
 rev 1.1.	Ashish Yadav	14/09/2016	Changes done for code merging	
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.admin.security.common;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;

//--------------------------------------------------------------------------
/**
 * This site checks to see if the current operator has access to the specified
 * function.
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXCheckAccessSite extends SiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7807115655184761095L;
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
		// Rev 1.0 changes Start here
		cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		cargo.getAccessFunctionID();
		// Rev 1.0 changes end here

		// get the security manager to be able to check the access to the code
		// function of the current user.
		SecurityManagerIfc securityManager = (SecurityManagerIfc) Gateway.getDispatcher().getManager(SecurityManagerIfc.TYPE);

		// Check the access of the user to the code function
		// Changes starts for rev 1.1
	//	access = securityManager.checkAccess(cargo.getAppID(), cargo.getAccessFunctionID());
access = securityManager.checkAccess(cargo.getAppID(),
               ((UserAccessCargoIfc) cargo).getAccessFunctionID());
//fix for override access on invoice rules
        if(bus.getCargo() instanceof MAXModifyTransactionDiscountCargo){
        if(((MAXModifyTransactionDiscountCargo)cargo).isGrantAccessforInvoicerules())
        	access=true;
        }
        //end
		// changes ends for rev 1.1
		// if access has been granted then need to send a CONTINUE letter.
		if (access) {
			letter = CommonLetterIfc.CONTINUE;
			if (cargo instanceof SaleCargo) {
				SaleCargo saleCargo = (SaleCargo) cargo;
				JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
				EmployeeIfc employee = saleCargo.getOperator();
				if (employee != null) {
					journal.setCashierID(employee.getLoginID());
					journal.setSalesAssociateID(employee.getLoginID());
				}
				RegisterIfc register = saleCargo.getRegister();
				if (register != null) {
					WorkstationIfc workstation = register.getWorkstation();
					if (workstation != null) {
						journal.setRegisterID(workstation.getWorkstationID());
						StoreIfc store = workstation.getStore();
						if (store != null) {
							journal.setStoreID(store.getStoreID());
						}
					}
				}
			}
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
