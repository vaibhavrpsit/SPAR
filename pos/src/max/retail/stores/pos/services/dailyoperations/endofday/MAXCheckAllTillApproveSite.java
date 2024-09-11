/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.
  Rev 1.1  27/May/2013	Nitesh		Changes for till reconcilation
  Rev 1.0  27/May/2013	Prateek		Initial Draft: Changes done to block eod if till not approved
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.dailyoperations.endofday;

import max.retail.stores.domain.arts.MAXTransactionReadDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckAllTillApproveSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		//Changes for rev 1.1 starts
		//MAXTransactionReadDataTransaction trxn = new MAXTransactionReadDataTransaction();
		MAXTransactionReadDataTransaction trxn = null;
		//Changes for rev 1.1 ends
		trxn = (MAXTransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);
		int count = 0;
		try {
			count = trxn.getUnapproveTillReconcileCount();
		} catch (DataException e) {
			if(e.getErrorCode() ==3 )
				count=0;
			else
				count =-1;
		}
		
		if(count==0)
			bus.mail("Success");
		else
			showErrorDialogue(bus);
	}
	protected void showErrorDialogue(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("UnapprovedTill");        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}
