/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupDebitADO.java /rgbustores_13.4x_generic_branch/2 2011/07/28 17:17:16 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/28/11 - Removed code that determined tender limit
 *                         violations. These limits were removed as part of
 *                         13.4 Advanced Payment Foundation.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sbeesnal  01/28/10 - Remove the flow that prompts the user to swipe debit
 *                         card & pin as they are not required in void
 *                         transactions.
 *    sbeesnal  01/22/10 - Reverting the changes made to remove flow which
 *                         prompts the user to swipe debit card & pin as they
 *                         resulted in JUnit failure.
 *    sbeesnal  01/20/10 - Removed flow which prompts the user to swipe debit
 *                         card & pin as they are not required in void
 *                         transaction.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:50 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/31 19:12:35  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.4  2004/04/29 17:30:00  bwf
 *   @scr 3377 Debit Reversal Work
 *
 *   Revision 1.3  2004/04/05 21:21:04  bwf
 *   @scr 4063 Create minimum debit error message.
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.8   Feb 05 2004 13:20:56   rhafernik
 * No change.
 *
 *    Rev 1.7   Jan 19 2004 17:41:24   epd
 * makes use of new parameter VoidDebitToCash
 *
 *    Rev 1.6   Jan 09 2004 15:08:38   epd
 * updates for unit testing
 *
 *    Rev 1.5   Jan 06 2004 13:11:56   epd
 * refactored away references to TenderHelper and DomainGateway
 *
 *    Rev 1.4   Dec 05 2003 09:56:12   epd
 * updated void type to cash
 *
 *    Rev 1.3   Nov 18 2003 17:26:04   epd
 * Updates for Debit
 *
 *    Rev 1.2   Nov 14 2003 11:09:46   epd
 * refactored some void functionality to be more general.
 *
 *    Rev 1.1   Nov 11 2003 16:18:24   epd
 * Updates made to accommodate tender deletion/reversal
 *
 *    Rev 1.0   Nov 04 2003 11:13:56   epd
 * Initial revision.
 *
 *    Rev 1.2   Oct 30 2003 20:39:54   epd
 * removed authorize() method
 *
 *    Rev 1.1   Oct 27 2003 20:49:06   epd
 * renamed interface
 *
 *    Rev 1.0   Oct 17 2003 12:34:32   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

/**
 *
 */
public class TenderGroupDebitADO extends AbstractAuthorizableTenderGroupADO
{

    //----------------------------------------------------------------------
    /**
        This method gets the group type.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getGroupType()
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum getGroupType()
    {
        return TenderTypeEnum.DEBIT;
    }

    //----------------------------------------------------------------------
    /**
        This method gets the void type.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getVoidType()
    **/
    //----------------------------------------------------------------------
    public TenderTypeEnum getVoidType()
    {
        return TenderTypeEnum.DEBIT;
    }

    //----------------------------------------------------------------------
    /**
        This method validates the tender limits.
        @param tenderAttributes
        @param balanceDue
        @throws TenderException
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#validateLimits(java.util.HashMap, oracle.retail.stores.domain.currency.CurrencyIfc)
    **/
    //----------------------------------------------------------------------
    public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue) throws TenderException
    {
        // min/max debit limits were removed in 13.4 - simply set to true
        evaluateTenderLimits = Boolean.TRUE;
    }

    //----------------------------------------------------------------------
    /**
        This method returns if the group type is reversible or not.
        @return
        @see oracle.retail.stores.pos.ado.tender.group.AuthorizableTenderGroupADOIfc#isReversible()
    **/
    //----------------------------------------------------------------------
    public boolean isReversible()
    {
        // debits to not require reversals
        return true;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        // TODO Auto-generated method stub
    }
}
