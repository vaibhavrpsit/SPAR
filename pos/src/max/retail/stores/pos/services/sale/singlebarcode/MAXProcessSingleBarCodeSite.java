package max.retail.stores.pos.services.sale.singlebarcode;

import java.util.Vector;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXProcessSingleBarCodeSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc)bus.getCargo();
		Vector mposLineItem = cargo.getSingleBarCodeVector();
		if(mposLineItem!=null)
		{
			generateSellLineItem(mposLineItem, bus);
		}
	}
	private void generateSellLineItem(Vector result, BusIfc bus)
	{
		MAXSaleCargo saleCargo = (MAXSaleCargo)bus.getCargo();
		int lineItem = saleCargo.getSingleBarCodeLineItem();
		SingleBarCodeData data = null; 
		if(lineItem > result.size()-1)
		{
			data = saleCargo.getSingleBarCodeData();
			saleCargo.setSingleBarCodeData(null);
			saleCargo.setSingleBarCodeVector(null);
			bus.mail("Done");
		}
		else
		{
			data = (SingleBarCodeData)result.get(lineItem);
			saleCargo.setSingleBarCodeData(data);
			lineItem++;
			saleCargo.setSingleBarCodeLineItem(lineItem);		
			bus.mail("Next");
		}
	}
}
