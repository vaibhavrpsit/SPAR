/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/capturecustomerinfo/CaptureCustomerInfoCargo.java /main/14 2013/01/10 15:05:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:45 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $
     Revision 1.11  2004/06/25 14:56:32  khassen
     Added comments.

     Revision 1.10  2004/06/23 16:38:50  khassen
     @scr 5780 - modified the setCustomer() method to load in proper values.

     Revision 1.9  2004/06/21 14:22:41  khassen
     @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.

     Revision 1.8  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.7  2004/03/02 04:27:06  khassen
     @scr 0 Capture Customer Info use-case - Modifications to tour script and sites.  Added verification for postal code.

     Revision 1.6  2004/02/27 21:08:36  khassen
     @scr 0 Capture Customer Info use-case

     Revision 1.5  2004/02/27 19:23:02  khassen
     @scr 0 Capture Customer Info use-case


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package max.retail.stores.pos.services.tender.capturecustomerinfo;


import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
/**
 *
 * @author kph
 *
 * Cargo class for the capture customer info use-case.
 */
public class MAXCaptureCustomerInfoCargo extends CaptureCustomerInfoCargo
{
	public boolean isSend = false;

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}


}
