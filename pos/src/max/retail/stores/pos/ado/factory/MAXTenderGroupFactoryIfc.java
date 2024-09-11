/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ado.factory;

import oracle.retail.stores.pos.ado.factory.TenderGroupFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;

/**
 * @author Himanshu
 *
 */
public interface MAXTenderGroupFactoryIfc extends TenderGroupFactoryIfc 
{
	public TenderGroupADOIfc createTenderGroup(TenderTypeEnum tenderType);
}
