package max.retail.stores.pos.services.admin.security.common;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;

/**
 * This shuttle transfers the Security Override data.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class MAXSecurityOverrideReturnShuttle implements ShuttleIfc
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7657224805177320229L;

	// This id is used to tell the compiler not to generate a new serialVersionUID.
    

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(MAXSecurityOverrideReturnShuttle.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * class name constant
     */
    public static final String SHUTTLENAME = "MAXSecurityOverrideReturnShuttle";

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
    	MAXPricingCargo calledCargo = (MAXPricingCargo) bus.getCargo();
    	calledCargo.setLastOperator(callingCargo.getOperator());
        calledCargo.setAccessFunctionID(callingCargo.getAccessFunctionID());
        calledCargo.setAccessFunctionTitle(callingCargo.getAccessFunctionTitle());
        calledCargo.setResourceID(callingCargo.getResourceID());
        
        //Added by Kamlesh
        RetailTransactionIfc trans = calledCargo.getTransaction();
        MAXManagerOverride mgo= new MAXManagerOverride();
		 mgo.setBusinessDay(calledCargo.getTransaction().getBusinessDay());
		mgo.setStoreCreditId(null);
		mgo.setFeatureId("111");
		mgo.setItemId(null);
		if(calledCargo.getLastOperator()!=null) {
			mgo.setManagerId(calledCargo.getLastOperator().getLoginID());
		}
		mgo.setSequenceNumber("-1");
		//mgo.setTransactionID(calledCargo.getTransaction().getTransactionID());
		mgo.setTransactionNO(calledCargo.getTransaction().getTransactionSequenceNumber());
		mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		mgo.setCashierID(calledCargo.getOperator().getLoginID().toString());
		MAXSaveManagerOverrideTransaction dbTrans = null;
		dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
		try {
			dbTrans.saveManagerOverride(mgo);
		} catch (DataException | com.extendyourstore.foundation.manager.data.DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
