/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/ExitServiceSite.java /main/13 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:58 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/15 16:27:07  jdeleau
 *   @scr 6281 Make sure that receipts print out with the correct
 *   Locale.
 *
 *   Revision 1.4  2004/04/07 14:36:08  jdeleau
 *   @scr 4090 Set up the LocaleMaps for DEVICES where necessary
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 21 2003 09:35:32   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jan 31 2003 17:35:10   baa
 * change pole display locale and receipt to match link customer locale preferences
 * Resolution for POS SCR-1843: Multilanguage support
 * 
 *    Rev 1.0   Apr 29 2002 15:32:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:34:12   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// java imports
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    This site performs any necessary processing when the service is
    terminating.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class ExitServiceSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
        Writes the text "Exiting Customer" to the electronic journal.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // retrieve cargo
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();

        // save current letter
        LetterIfc currentLetter = bus.getCurrentLetter();

        //if a customer was not link reset remove the customer instance.
        if (!cargo.isLink())
        {
          cargo.setCustomer(null);
        }
        else
        {
            // Use customer locale preferrences for the 
            // pole display and receipt  subsystems
            Locale customerLocale = cargo.getCustomer().getPreferredLocale();
            
            if (customerLocale != null)
            {
                UIUtilities.setUILocaleForCustomer(customerLocale);
            }
            else
            {
            	 Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            	 UIUtilities.setUILocaleForCustomer(defaultLocale);
            }
        }
            
        bus.mail(currentLetter, BusIfc.CURRENT);
    }

}
