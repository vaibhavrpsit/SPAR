/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.ado.tender.group;

import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupStoreCreditADO;

//-------------------------------------------------------------------------
/**
    @author Himanshu
**/
//-------------------------------------------------------------------------
public class MAXTenderGroupStoreCreditADO extends TenderGroupStoreCreditADO
{
	// MAX Changes for Rev 1.0 - Start
	//-------------------------------------------------------------------------
	/**
	  * MFL Customizations
	  * The tender will be added to the list	    
	  * Added by Himanshu
	**/
	//-------------------------------------------------------------------------
    public void addTender(TenderADOIfc tender) throws TenderException
    {
        // add the tender to the list;
        //tenderList.add(tender);
        super.addTender(tender);
    }
    

}
