package max.retail.stores.domain.gstin;

import java.util.ArrayList;

public class GSTSaveRequest {

	ArrayList<GSTInvoice> data = new ArrayList<GSTInvoice>();

	public ArrayList<GSTInvoice> getData() {
		return data;
	}

	public void setData(ArrayList<GSTInvoice> data) {
		this.data = data;
	}
}
