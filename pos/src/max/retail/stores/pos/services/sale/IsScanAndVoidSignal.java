/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

// foundation imports
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.modifyitem.MAXItemCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal checks to see if the till is suspended.
    <p>
    @version $Revision: 4$
    @deprecated as of release 7.0 The complete pos service was replaced by the sale service under _360commerce
**/
//--------------------------------------------------------------------------
public class IsScanAndVoidSignal implements TrafficLightIfc
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1014736519795831359L;

	/**
        revision number
    **/
    public static String revisionNumber = "$Revision: 4$";

    /**
     * The logger to which log messages will be sent.
     **/
    private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.IsScanAndVoidSignal.class);
   
    public boolean roadClear(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug("IsScanAndVoidSignal.roadClear()");

        boolean result = false;
        if(bus.getCargo() instanceof MAXSaleCargo && ((MAXSaleCargo)bus.getCargo()).isScanNVoidFlow())
        	result=true;
        if(bus.getCargo() instanceof MAXItemCargo && ((MAXItemCargo)bus.getCargo()).isScanNVoidFlow())
        	result=true;
        
        
        if (logger.isDebugEnabled()) logger.debug("IsScanAndVoidSignal.roadClear()");
        return(result);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  IsScanAndVoidSignal (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
