/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSStore.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================


     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
     $
     Revision 1.4  2004/09/23 00:30:49  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:21  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:06   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//-------------------------------------------------------------------------
/**
    A container class used by DataTransactions to pass store related
    information to a DataOperation.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ARTSStore implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3846113907254762731L;

    /**
        The POS domain Store
    **/
    protected StoreIfc posStore = null;

    /**
        The financial information
    **/
    protected FinancialTotalsIfc financialTotals = null;

    /**
        The business day
    **/
    protected EYSDate businessDate = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
        <P>
        @param  store       The POS domain store
        @param  businessDate The business day
    **/
    //---------------------------------------------------------------------
    public ARTSStore(StoreIfc store, EYSDate businessDate)
    {
        setPosStore(store);
        setBusinessDate(businessDate);
    }

    //---------------------------------------------------------------------
    /**
        Returns the POS domain Store
        <p>
        @return  the POS domain Store
    **/
    //---------------------------------------------------------------------
    public StoreIfc getPosStore()
    {
        return(posStore);
    }

    //---------------------------------------------------------------------
    /**
        Sets the POS domain Store
        <p>
        @param  store    The POS domain Store
    **/
    //---------------------------------------------------------------------
    public void setPosStore(StoreIfc store)
    {
        posStore = store;
    }

    //---------------------------------------------------------------------
    /**
        Returns the financial totals information
        <p>
        @return  the financial totals information
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getFinancialTotals()
    {
        return(financialTotals);
    }

    //---------------------------------------------------------------------
    /**
        Sets the financial totals information
        <p>
        @param  totals  The financial totals information
    **/
    //---------------------------------------------------------------------
    public void setFinancialTotals(FinancialTotalsIfc financialTotals)
    {
        this.financialTotals = financialTotals;
    }

    //---------------------------------------------------------------------
    /**
        Returns the business day
        <p>
        @return  the business day
    **/
    //---------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {
        return(businessDate);
    }

    //---------------------------------------------------------------------
    /**
        Sets the business day
        <p>
        @param  businessDate The business day
    **/
    //---------------------------------------------------------------------
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }
}
