package max.retail.stores.pos.ui.beans;

import java.util.ArrayList;
import java.util.Iterator;

import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

public class MAXCouponLocalNavigationButtonBeanModel extends
		NavigationButtonBeanModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1917172475531373753L;

	public void setButtonLabel(String actionName, String label) {
		if (modifyButtons == null) {
			modifyButtons = new ArrayList();
		}
		ButtonSpec spec = new ButtonSpec();

		Iterator iterator = newButtons.iterator();
		while (iterator.hasNext()) {
			ButtonSpec next = (ButtonSpec) iterator.next();

			if (next.getLabel().equals(label)) {
				spec = next;
				break;
			}
		}
		if (spec != null) {
			spec.setActionName(actionName);
			spec.setLabel(label);
		}
		modifyButtons.add(spec);
	}
}
