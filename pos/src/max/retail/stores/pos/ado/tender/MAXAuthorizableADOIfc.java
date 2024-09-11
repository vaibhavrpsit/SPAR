package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;

public interface MAXAuthorizableADOIfc extends AuthorizableADOIfc{
	
	public void authorize(HashMap authAttributes) throws AuthorizationException;

}
