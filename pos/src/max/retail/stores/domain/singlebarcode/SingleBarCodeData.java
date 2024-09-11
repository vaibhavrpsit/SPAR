/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *	Rev 1.1  05/July/2013	Prateek			Change done for BUG: 6851
 *  Rev 1.0  10/June/2013	Prateek			Initial Draft: Single Bar Code FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.singlebarcode;

import java.io.Serializable;
import java.math.BigDecimal;

/** MAX Rev 1.1 Change : Start **/
public class SingleBarCodeData implements Serializable {
	/** MAX Rev 1.1 Change : End **/
	protected String itemId;
	protected BigDecimal quantity;

	public SingleBarCodeData() {
		super();
	}

	public SingleBarCodeData(String itemId, BigDecimal quantity) {
		super();
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
}
