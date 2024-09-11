/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/PosCountFloatLaunchShuttle.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/19/12 - do not add till if register already has it
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:06 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;

import org.apache.log4j.Logger;

/**
 * @version $Revision: /main/11 $
 */
public class PosCountFloatLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 6659352827521270192L;

    /**
     * The logger to which log messages will be sent.
     **/
    protected static final Logger logger = Logger.getLogger(PosCountFloatLaunchShuttle.class);

    public static final String SHUTTLENAME = "PosCountFloatLaunchShuttle";

    private TillOpenCargo opencargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        opencargo = (TillOpenCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        RegisterIfc register = (RegisterIfc)opencargo.getRegister().clone();

        addOpenTillToRegister(register, opencargo.getTill());

        // Setup poscount cargo
        cargo.setRegister(register);
        cargo.setTillID(opencargo.getTillID());
        cargo.setCountType(PosCountCargo.START_FLOAT);

        if (opencargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
        {
            cargo.setSummaryFlag(true);
        }
        else
        {
            cargo.setSummaryFlag(false);
        }
    }

    /**
     * Add till to the register.
     *
     * @param register
     * @param openTill
     */
    protected void addOpenTillToRegister(RegisterIfc register, TillIfc openTill)
    {
        for (TillIfc till : register.getTills())
        {
            if (openTill.equals(till))
            {
                // do not add till if register already has it.
                return;
            }
        }
        register.addTill(openTill);
    }
}
