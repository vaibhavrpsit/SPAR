/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
     $
     Revision 1.6  2004/07/28 16:06:53  rsachdeva
     @scr 4865 Transaction Sales Associate

     Revision 1.5  2004/04/09 16:56:01  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.4  2004/03/17 16:00:15  epd
     @scr 3561 Bug fixing and refactoring

     Revision 1.3  2004/02/12 16:48:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.1   Nov 07 2003 12:37:36   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
 * <p>
 * 
 * @version $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXModifyTransactionReturnShuttle extends FinancialCargoShuttle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 808338943386796350L;

	/**
	 * The logger to which log messages will be sent.
	 **/
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.sale.MAXModifyTransactionReturnShuttle.class);

	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	protected ModifyTransactionCargo modifyTransactionCargo = null;
	
	ModifyTransactionResumeCargo cargo = null;
	/**
	 * sales associate set using modify transaction sales associate
	 **/
	protected boolean salesAssociateAlreadySet = false;

	// ----------------------------------------------------------------------
	/**
	 * Takes the cargo from the child
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>Cargo will contain the selected item
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>
	 * </UL>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {

		super.load(bus);

		// retrieve cargo from the child(ModifyTransaction Cargo)
		modifyTransactionCargo = (ModifyTransactionCargo) bus.getCargo();
		salesAssociateAlreadySet = modifyTransactionCargo
				.isAlreadySetTransactionSalesAssociate();
		//System.out.println("103 modifyTransactionCargo :"+modifyTransactionCargo);
		//System.out.println("104 salesAssociateAlreadySet :"+salesAssociateAlreadySet);
		
	}

	// ----------------------------------------------------------------------
	/**
	 * Takes the cargo from the temp area into the parent cargo
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>Cargo will contain the selected item
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>
	 * </UL>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {

		super.unload(bus);

		/**
		 * This array contains a list of SaleReturnTransacions on which returns
		 * have been completed. It will be used if a transaction with returned
		 * lineitems is retrieved.
		 **/
		SaleReturnTransactionIfc[] originalReturnTransactions = null;

		RetailTransactionIfc transaction = null;
		EmployeeIfc salesAssociate = null;

		// update transaction if modified
		if (modifyTransactionCargo.getUpdateParentCargoFlag() == true) {

			transaction = modifyTransactionCargo.getTransaction();

			originalReturnTransactions = modifyTransactionCargo
					.getOriginalReturnTransactions();
			salesAssociate = modifyTransactionCargo.getSalesAssociate();
		}

		// retrieve cargo from the parent(Sales Cargo)
		SaleCargoIfc saleCargo = (SaleCargoIfc) bus.getCargo();
		
		saleCargo.setCustomerInfo(modifyTransactionCargo.getCustomerInfo());
		//saleCargo.getCustomerInfo().getPhoneNumber().getPhoneNumber();
		
		
			//System.out.println("152 Return shuttle :"+transaction);
		// set the temp storage into the parent cargo(Sales Cargo) if not null

		if (transaction != null) {
			saleCargo.setTransaction((SaleReturnTransactionIfc) transaction);
			if(modifyTransactionCargo.getTransaction().getCustomer()!=null)
			{
				saleCargo.getCustomerInfo().getPhoneNumber().setPhoneNumber(modifyTransactionCargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber());
		
			}
		}
		//System.out.println("159 modifyTransactionCargo.getTransaction() :"+modifyTransactionCargo.getTransaction());
		
		if (modifyTransactionCargo.getTransaction() != null) {
			CustomerIfc customer = modifyTransactionCargo.getTransaction()
					.getCustomer();
			//System.out.println("164 customer :"+customer);
			if (customer != null) {
				saleCargo.getTransaction().setCustomer(customer);
				//saleCargo.getTransaction().getCustomerInfo().setPhoneNumber( customer.getPrimaryPhone());
				//transaction.getCustomerInfo().getPhoneNumber()
				//System.out.println("167 saleCargo.getTransaction().setCustomer(customer) :"+saleCargo);
				
			}
		}
		// add all original return transactions to the parent cargo list
		if (originalReturnTransactions != null) {
			for (int i = 0; i < originalReturnTransactions.length; i++) {
				saleCargo
						.addOriginalReturnTransaction(originalReturnTransactions[i]);
			}
		}

		if (salesAssociate != null) {
			if (logger.isInfoEnabled())
				logger.info("ModifyTransactionReturnShuttle: setting sales associate");
			saleCargo.setEmployee(salesAssociate);
		}
		if (salesAssociateAlreadySet) {
			saleCargo.setAlreadySetTransactionSalesAssociate(true);
		}

		saleCargo.setRefreshNeeded(true);
	}
}
