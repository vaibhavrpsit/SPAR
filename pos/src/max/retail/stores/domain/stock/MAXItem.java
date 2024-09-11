package max.retail.stores.domain.stock;

import com.itextpdf.text.pdf.AcroFields.Item;

public class MAXItem extends Item {

	boolean EmployeeDiscountAllowedFlag = true;
	public void setEmployeeDiscountEligible(boolean EmployeeDiscountAllowedFlag ) {
	    this.EmployeeDiscountAllowedFlag = EmployeeDiscountAllowedFlag;
	  }
}
