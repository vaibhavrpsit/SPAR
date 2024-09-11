/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012 - 2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	Tanmaya		    	10/05/2013		Weighted item changes
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.NonZeroDecimalTextField;

public class MAXNonZeroDecimalTextField extends NonZeroDecimalTextField {
	private static final long serialVersionUID = -6869454354612717117L;

	public MAXNonZeroDecimalTextField() {
		this("");
	}

	public MAXNonZeroDecimalTextField(String value) {
		this(value, Integer.MAX_VALUE, 3);
	}

	public MAXNonZeroDecimalTextField(String value, int maxLength, int decLength) {
		super(value, maxLength, decLength);
		setNegativeAllowed(false);
	}
}
