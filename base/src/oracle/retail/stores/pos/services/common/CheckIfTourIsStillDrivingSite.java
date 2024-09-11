/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckIfTourIsStillDrivingSite.java /main/11 2014/07/17 09:16:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/16/14 - handle timeout letters by calling timeout on the
 *                         other tour's uimanager
 *    cgreene   01/11/11 - tweak sleeping to help prevent CPU starvation
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   08/06/09 - XbranchMerge cgreene_bug-8737695 from
 *                         rgbustores_13.1x_branch
 *    cgreene   08/03/09 - Made messaging for NPE with site name less severe
 *                         since it can regularly happen under load.
 *    cgreene   06/01/09 - add null check to otherBus
 *    cgreene   03/02/09 - catch potential nullpointer exceptions from other
 *                         bus that may have stopped and not have a site
 *    cgreene   02/05/09 - site required for checking status of Printing tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.TierTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.CheckPointUIManagerIfc;

/**
 * Monitors the bus driving another tour. If it is not yet reached the
 * final site, this site should wait until it has exited. If the other tour
 * is blocked, this site will unblock it. If a {@link CommonLetterIfc#TIMEOUT}
 * is being handled, then this site will tell the other tour's
 * {@link CheckPointUIManagerIfc} to timeout.
 * 
 * @author cgreene
 * @since 13.1
 */
public class CheckIfTourIsStillDrivingSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1069979696591054231L;

    private static final Logger logger = Logger.getLogger(CheckIfTourIsStillDrivingSite.class);

    /**
     * Number of milliseconds to sleep while waiting for printing tour.
     */
    public static final long SLEEP_MILLIS = 300;

    /**
     * Name of tour this site is usually interested in finding. Equals "Printing".
     * @see #getOtherTourName()
     */
    public static final String PRINTING_TOUR_NAME = "Printing";

    /**
     * Name of the last site in most every tour. Equals "Final".
     * @see #getFinalSiteName()
     */
    public static final String FINAL_SITE_NAME = "Final";

    /**
     * Name letter that this tour will mail after it {@link #arrive(BusIfc)}s.
     * Equals "ExitCompleteSale".
     * 
     * @see #getOtherTourName()
     */
    public static final String EXIT_LETTER = "ExitCompleteSale";

    /**
     * Loop through each bus in the tier technician and if the printing service
     * is found then wait for it, or unblock it (and wait). Calls
     * {@link #unblockTour(BusIfc, boolean)}.
     * 
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     * @see #getOtherTourName()
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // check if timeout has occurred
        boolean timeoutHasOccurred = CommonLetterIfc.TIMEOUT.equals(bus.getCurrentLetter().getName());

        // get access to all buses from tier
        TierTechnicianIfc tierTech = bus.getTierTechnician();
        boolean otherServiceFound = true;

        // while the other service bus exists, keep trying to unblock it or wait until it is done.
        while (otherServiceFound)
        {
            otherServiceFound = false; // we haven't actually found the bus yet
            BusIfc[] buses = tierTech.getBuses();
            for (int i = 0; i < buses.length; i++)
            {
                BusIfc otherBus = buses[i];
                if (otherBus != null)
                {
                    String serviceName = otherBus.getServiceName();
                    if (getOtherTourName().equals(serviceName))
                    {
                        otherServiceFound = true;
                        unblockTour(otherBus, timeoutHasOccurred);
                        break; // break for since we're only interested in "other" tour
                    }
                }
            }

            if (otherServiceFound)
            {
                // Sleep a little before checking if the other bus is still running.
                // This gives the tour a chance to end and prevents CPU starvation.
                try
                {
                    Thread.sleep(getPause());
                }
                catch (InterruptedException e)
                {
                }
            }
        }

        /* If there was a timeout, this method has sent the other child tour
         * a Timeout letter, which should result in it exiting with a Timeout
         * letter being delivered to this site. In non-timeout situations, we
         * mail the normal exit letter in order to continue.
         */
        if (!timeoutHasOccurred)
        {
            bus.mail(getExitLetter());
        }
    }

    /**
     * Same as calling {@link #unblockTour(BusIfc, boolean)} with false.
     */
    protected void unblockTour(BusIfc otherBus)
    {
        unblockTour(otherBus, false);
    }

    /**
     * Inspect bus of the other service and if it is not at the final site or
     * exited, then check if it is blocked and unblock it.
     * <p>
     * If <code>timeoutOccurred</code> has occurred, then ignore whether the
     * other tour is blocked and just call {@link CheckPointUIManager#timeout()}.
     * 
     * @param otherBus the bus driving the blocked tour
     * @param timeoutOccurred
     * @return true if the tour is successfully unblocked.
     * @see CheckPointUIManager#isCheckPointBlocked()
     */
    protected void unblockTour(BusIfc otherBus, boolean timeoutOccurred)
    {
        String currentSite = null;
        try
        {
            currentSite = otherBus.getCurrentSiteName();
        }
        catch (NullPointerException e)
        {
            logger.info("NullPointerException caught getting other site's bus name. Reference was likely stale. Tour proceeding.");
        }
        catch (Exception e)
        {
            logger.error("Could not determine site name from bus: " + otherBus, e);
        }

        if (currentSite != null && !getFinalSiteName().equals(currentSite))
        {
            UIManagerIfc otherUI = (UIManagerIfc)otherBus.getManager(UIManagerIfc.TYPE);
            if (otherUI instanceof CheckPointUIManagerIfc)
            {
                CheckPointUIManagerIfc checkPoint = (CheckPointUIManagerIfc)otherUI;
                if (timeoutOccurred)
                {
                    logger.debug("Sending other tour timeout event...");
                    checkPoint.timeout();
                }
                else
                {
                    logger.debug("Attempting to unblock other tour that is still driving...");
                    if (checkPoint.isCheckPointBlocked())
                    {
                        checkPoint.setCheckPointBlocked(false);
                        logger.debug("Unblocked other tour...");
                    }
                }
            }
            else
            {
                logger.debug("Could not unblock other tour because it does not have a CheckPointUIManager.");
            }
        }
    }    

    /**
     * Return the site when this site should consider the other tour over.
     * Returns {@link #FINAL_SITE_NAME}.
     * 
     * @return
     */
    public String getFinalSiteName()
    {
        return FINAL_SITE_NAME;
    }

    /**
     * Return the tour for which this site is monitoring. Returns
     * {@link #PRINTING_TOUR_NAME}.
     * 
     * @return
     */
    public String getOtherTourName()
    {
        return PRINTING_TOUR_NAME;
    }

    /**
     * Return the letter that this tour will mail. Returns {@link #EXIT_LETTER}.
     * 
     * @return
     */
    public String getExitLetter()
    {
        return EXIT_LETTER;
    }

    /**
     * Returns number of milliseconds this this site will pause between checks
     * on the printing service. Returns {@link #SLEEP_MILLIS}.
     * 
     * @return
     */
    public long getPause()
    {
        return SLEEP_MILLIS;
    }
}
