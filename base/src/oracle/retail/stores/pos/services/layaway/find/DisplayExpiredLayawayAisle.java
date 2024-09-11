/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/DisplayExpiredLayawayAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/18/2007 8:25:49 AM   Peter J. Fierro Fix
 *         the import.
 *    4    360Commerce 1.3         4/17/2007 3:25:28 PM   Peter J. Fierro Merge
 *          3849 fix from 7.2.2: Layaway: Active status not changing to
 *         Expired when as_ly.dt_ly_ep date is reached or passed
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 30 2002 12:53:34   jriggins
 * I18N formatting the grace period days remaining string.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

//foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the expired layaway screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplayExpiredLayawayAisle extends LaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "DisplayExpiredLayawayAisle";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        expired layaway screen name
    **/
    private static final String RESOURCE_ID = "ExpiredLayaway";

    //--------------------------------------------------------------------------
    /**
       Displays the layaway Expired Layaway screen.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();

        LayawayIfc layaway = cargo.getLayaway();
        EYSDate gracePeriod = layaway.getGracePeriodDate();
        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);

        String days =
            LocaleUtilities.formatNumber(new Integer(differenceInDays(currentDate,gracePeriod)),
                                        LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        // set arg strings to layaway
        String args[] = new String[1];
        args[0] = days;

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog
        // Set button and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);

        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    public int differenceInDays(EYSDate currentDate, EYSDate targetDate)
    {
        long SECONDS_IN_DAY = 86400000;

        int targetDays = (int)(targetDate.dateValue().getTime()/SECONDS_IN_DAY);
        int currentDays = (int)(currentDate.dateValue().getTime()/SECONDS_IN_DAY);

        return (targetDays - currentDays);
    }
}
