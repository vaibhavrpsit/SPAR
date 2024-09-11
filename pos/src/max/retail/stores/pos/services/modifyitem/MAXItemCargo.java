/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.2		Kritica		22/05/2017		Change for GST
  Rev. 1.1 		Tanmaya		05/04/2013		Change for Scan and void
  Rev 1.0		Prateek		23/03/2013		Initial Draft: Changes for Quantity Button
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

public class MAXItemCargo extends ItemCargo{

	/**
     * Determines which letter to use to go to the start site in the new sub tour.
     */
    protected String parentLetter = null;
	//----------------------------------------------------------------------
    
    boolean scanNVoidFlow = true;

	public boolean isScanNVoidFlow() {
		return scanNVoidFlow;
	}

	public void setScanNVoidFlow(boolean scanNVoidFlow) {
		this.scanNVoidFlow = scanNVoidFlow;
	}
	
	
    /**
         Gets the sub Tour letter.
         @return parentLetter String
     **/
    //----------------------------------------------------------------------
    public String getParentLetter()
    {
        return parentLetter;
    }
    //----------------------------------------------------------------------
    /**
         Sets the sub tour letter.
         @param parentLetter The parentLetter to set.
     **/
    //----------------------------------------------------------------------
    public void setParentLetter(String parentLetter)
    {
        this.parentLetter = parentLetter;
    }
    public boolean isSend = false;

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}
	
}
