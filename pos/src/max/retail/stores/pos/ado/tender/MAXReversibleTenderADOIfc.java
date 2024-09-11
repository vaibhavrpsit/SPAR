package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.ReversibleTenderADOIfc;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;

public interface MAXReversibleTenderADOIfc extends ReversibleTenderADOIfc{
	public void reverse(HashMap authAttributes) throws AuthorizationException;
	public void voidAuth(HashMap authAttributes) throws AuthorizationException;

}
