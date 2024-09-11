/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
     $
     Revision 1.4  2004/09/23 00:07:16  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.3  2004/02/12 16:50:47  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:22  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 29 2003 16:09:54   baa
 * enable customer add on create layaway
 * 
 *    Rev 1.1   May 27 2003 08:48:14   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:34:34   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:20:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:24   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.layaway.create;

// foundation imports
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.create.LinkCustomerLaunchShuttle;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the layaway cargo.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXLinkCustomerLaunchShuttle
extends LinkCustomerLaunchShuttle
{
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.layaway.create.MAXLinkCustomerLaunchShuttle.class);


	private static final long serialVersionUID = 1L;

	public void load(BusIfc bus)
    {
		super.load(bus);
		
    }
	
	public void unload(BusIfc bus)
    {
        super.unload(bus);
        MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();
        cargo.setTICCustomerRequire(false);
    }


}
