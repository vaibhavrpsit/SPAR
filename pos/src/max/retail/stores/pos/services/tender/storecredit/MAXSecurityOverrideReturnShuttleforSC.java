/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 29, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/


package max.retail.stores.pos.services.tender.storecredit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;

public class MAXSecurityOverrideReturnShuttleforSC implements ShuttleIfc
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7657224805177320229L;

	// This id is used to tell the compiler not to generate a new serialVersionUID.
    

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(MAXSecurityOverrideReturnShuttleforSC.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "MAXSecurityOverrideReturnShuttleforSC";

    /**
     * The calling service's cargo.
     */
    protected UserAccessCargoIfc callingCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        callingCargo = (UserAccessCargoIfc) bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
    	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
    	 HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
    	 
    	// String SCN = (String) tenderAttributes.get(TenderConstants.AMOUNT);
    	
    	cargo.setLastOperator(callingCargo.getOperator());
    	cargo.setAccessFunctionID(callingCargo.getAccessFunctionID());
    	cargo.setAccessFunctionTitle(callingCargo.getAccessFunctionTitle());
    	cargo.setResourceID(callingCargo.getResourceID());
        
        MAXManagerOverride mgo= new MAXManagerOverride();
		Date date = new Date();
		String BusinessDay= new SimpleDateFormat("yyyy-MM-dd").format(date);
		//mgo.setBusinessDay(((SaleCargo) cargo).getTransaction().getBusinessDay());
		//System.out.println("BusinessDay :" +BusinessDay);
		mgo.setStoreCreditId((String) tenderAttributes.get(TenderConstants.NUMBER));
		mgo.setFeatureId(String.valueOf(cargo.getAccessFunctionID()));
		mgo.setItemId(null);
		if(cargo.getLastOperator()!=null)
			mgo.setManagerId(cargo.getLastOperator().getLoginID());
		mgo.setSequenceNumber("-1");
		mgo.setTransactionNO(cargo.getTransaction().getTransactionSequenceNumber());
		String storeId = Gateway.getProperty("application", "StoreID", null);
		mgo.setStoreID(storeId);
		//System.out.println("storeId :"+storeId);
		String workStationId = Gateway.getProperty("application", "WorkstationID", null);
		mgo.setWsID(workStationId);
		//System.out.println("workStationId :"+workStationId);
		mgo.setCashierID(cargo.getOperator().getLoginID().toString());
		//mgo.setCashierID((cargo).getEmployee().getLoginID().toString());
		mgo.setAmountMO((String) tenderAttributes.get(TenderConstants.AMOUNT));

		MAXSaveManagerOverrideTransaction dbTrans = null;
		dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
		

		try {
			dbTrans.saveManagerOverride(mgo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }
}