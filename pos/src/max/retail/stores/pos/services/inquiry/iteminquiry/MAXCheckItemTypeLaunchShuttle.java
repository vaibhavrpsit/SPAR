package max.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.CheckItemTypeLaunchShuttle;

public class MAXCheckItemTypeLaunchShuttle extends CheckItemTypeLaunchShuttle {
	
	MAXItemInquiryCargo itemInquiryCargo = null;
	public void load(BusIfc bus)
	{
		super.load(bus);
	    itemInquiryCargo = (MAXItemInquiryCargo) bus.getCargo();
	}
	public void unload(BusIfc bus)
    {
        super.unload(bus);
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo) bus.getCargo();

        //**
        // Set Store ID
        //cargo.setStoreID(this.cargo.getRegister().getWorkstation().getStoreID());
        cargo.setRegister(itemInquiryCargo.getRegister());

        cargo.setPLUItem(itemInquiryCargo.getPLUItem());
        cargo.setTransaction(itemInquiryCargo.getTransaction());
        cargo.setStoreStatus(itemInquiryCargo.getStoreStatus());
        cargo.setOperator(itemInquiryCargo.getOperator());
        cargo.setCustomerInfo(itemInquiryCargo.getCustomerInfo());
        cargo.setTenderLimits(itemInquiryCargo.getTenderLimits());
        cargo.setItemList(itemInquiryCargo.getItemList());
        cargo.setSalesAssociate(itemInquiryCargo.getSalesAssociate());
        cargo.setApplyBestDeal(itemInquiryCargo.isApplyBestDeal());
        cargo.setInitialOriginLetter(itemInquiryCargo.getInitialOriginLetter()); // added by sakshi
        //Changes added by Prateek
        cargo.setSingleBarCodeData(itemInquiryCargo.getSingleBarCodeData());
        if (itemInquiryCargo.getPLUItem() != null && itemInquiryCargo.getPLUItem().isKitHeader())
        {
            ((ItemKitIfc)cargo.getPLUItem()).setindex(-1);
        }

        cargo.setModifiedFlag(false);

    }
}
