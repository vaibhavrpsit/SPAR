/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012 - 2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	5/04/2013	Prateek	 		Initial Draft: Changes for Suspended Bills FES.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.modifytransaction.suspend;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXTransactionReadDataTransaction;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.suspend.ModifyTransactionSuspendCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateSuspendLimit extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	site name constant
	**/
	public static final String SITENAME = "CheckTransactionSite";
	public static final String UNKNOW_DB_ERROR = "error";
	public static final String PARAMETER_ERROR = "RegisterParameterError";
	public static final String INVALID_SUSPEND_LIMIT = "InvalidSuspendLimit";
	public static final String SUSPEND_DB_OFFLINE = "SuspendDbOffline";
	
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.modifytransaction.suspend.MAXValidateSuspendLimit.class);
	public void arrive(BusIfc bus)
	{
		int limit =0, count =0, errorCode =-1;
		ParameterManagerIfc pmr = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		try {
			limit = pmr.getIntegerValue("SuspendedTransactionRestrictionCount").intValue();
		} catch (ParameterException e) {
			logger.error(e);
			showErrorDialog(bus, PARAMETER_ERROR);
		}
		MAXTransactionReadDataTransaction readTransaction = null;
        readTransaction = (MAXTransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);
        ModifyTransactionSuspendCargo cargo = (ModifyTransactionSuspendCargo)bus.getCargo();
        TransactionIfc transaction = createTransaction();
        transaction.setBusinessDay(cargo.getTransaction().getBusinessDay());
        transaction.setWorkstation(cargo.getTransaction().getWorkstation());
        transaction.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
        try {
			count = readTransaction.getSuspendedTransactionCount(transaction);
		} catch (DataException e) {
			errorCode =e.getErrorCode();
		}
        if(errorCode ==-1 || errorCode == 6)
        {
        	if(count>=limit)
        		showErrorDialog(bus, INVALID_SUSPEND_LIMIT);
        	else
        		bus.mail("Success");
        }
        else
        {
        	showErrorDialog(bus, SUSPEND_DB_OFFLINE);
        }
        
	}
	public void showErrorDialog(BusIfc bus, String flag)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(flag);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.FAILURE);
        // display the screen
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	protected TransactionIfc createTransaction()
    {                                   // begin createTransactionSummary()
        return(DomainGateway.getFactory().getTransactionInstance());
    } 
}
