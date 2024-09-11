/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Nitesh		4/Jan/2013		Changes for Till Reconcilation FES
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;

//------------------------------------------------------------------------------
/**


    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPosCountTillReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1574975048054992052L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.dailyoperations.till.tillreconcile.MAXPosCountTillReturnShuttle.class);

    public static final String SHUTTLENAME = "PosCountTillReturnShuttle";

    protected FinancialTotalsIfc posCountTotal;

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        MAXPosCountCargo cargo = (MAXPosCountCargo) bus.getCargo();
        posCountTotal = cargo.getFinancialTotals();
        if(posCountTotal instanceof MAXFinancialTotals)
        {
        	((MAXFinancialTotals)posCountTotal).setCouponDenominationCount(cargo.getCouponCargo());
        	((MAXFinancialTotals)posCountTotal).setAcquirerBankDetails(cargo.getAcquirerBankDetails());
        	((MAXFinancialTotals)posCountTotal).setGiftCertificateDenomination(cargo.getGiftCertList());
        	((MAXFinancialTotals)posCountTotal).setCashDenomination(cargo.getCashDenomination());
			//changes for rev 1.1 starts
        	((MAXFinancialTotals)posCountTotal).setEnteredTotals(cargo.getEnteredTender());
			/*ArrayList arrayList = new ArrayList(Arrays.asList(cargo.getFinancialTotals().getCombinedCount().getEntered().getTenderItems()));
        	((MAXFinancialTotals)posCountTotal).setEnteredTotals(arrayList);*/
			//changes for rev 1.1 ends
        }

    }

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        //register.getTillByID(cargo.getTillID()).setTotals(posCountTotal);
        cargo.setTillTotals(posCountTotal);

    }
}
