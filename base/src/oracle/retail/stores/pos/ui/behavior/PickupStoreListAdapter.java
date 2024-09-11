/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/PickupStoreListAdapter.java /main/4 2013/03/08 10:08:57 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * abhinavs    03/07/13 - Fix to display correct pickup date on the basis of
 *                        item availability
 * abhinavs    02/28/13 - Fix to enable and disable pickup buttons on qty and
 *                        date availability
 * abhinavs    12/31/12 - Fix to prevent null pointer or number format
 *                        exception
 * abhinavs    12/24/12 - Fix to enable and disable store pickup and print
 *                        buttons
 * abhinavs    12/24/12 - initial version
 * abhinavs    12/24/12 - Creation
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.behavior;

import java.math.BigDecimal;

import javax.swing.JList;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.pos.ui.beans.AvailableToPromiseInventoryLineItemModel;
import oracle.retail.stores.pos.ui.beans.EYSList;

public class PickupStoreListAdapter  extends AbstractListAdapter {


	/** Constants for item action.  */
	private static final String INVALID_SELECTION = "Pickup[false],Print[true]";
	private static final String VALID_SELECTION   = "Pickup[true],Print[true]";



	@Override
	public String determineButtonState(JList list) {

		EYSList multiList = (EYSList)list;
		int rowIndex=multiList.getSelectedRow();
		int[] indices = {rowIndex};

		// default is to disable buttons
		String result = INVALID_SELECTION;

		// Only one store is selected at a time
		if (indices.length > 0)
		{    
			boolean isButtonEnabled = false;
			for (int index : indices)
			{
				AvailableToPromiseInventoryLineItemModel pickupStore=(AvailableToPromiseInventoryLineItemModel)list.getModel().getElementAt(index);
				if(pickupStore.isAvailableNow() || pickupStore.isAvailableInFuture())
				{
					isButtonEnabled=true;
					break;
				}
			}
			if(isButtonEnabled){
				result=VALID_SELECTION;
			}

		}

		return result;
	}

}
