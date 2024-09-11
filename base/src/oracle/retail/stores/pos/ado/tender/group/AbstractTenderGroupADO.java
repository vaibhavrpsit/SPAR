/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/AbstractTenderGroupADO.java /main/18 2014/02/10 11:22:29 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  09/03/14 - changes for mpos gift card refund tender limit override
 *    mkutiana  02/06/14 - Fortify Null Derefernce fix
 *    icole     06/27/12 - Forward port: Overtendering of Check is allowed even
 *                         if Check Tender is configured not be allowed for
 *                         Overtender
 *    icole     12/13/11 - Reset evaluate tender limits to handle case of UNDO
 *                         from tender after a manager override.
 *    blarsen   06/22/11 - refactored validateOvertender() to use new
 *                         UtilityIfc.isOvertenderAllowed() method.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    kulu      02/22/09 - Fix the bug that foreign currency in tender info is
 *                         always CAD
 *    sgu       02/11/09 - fix alternate currency franking amount
 *    vchengeg  01/27/09 - ej defect fixes
 *    vchengeg  01/07/09 - ej defect fixes
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         3/31/2008 1:46:11 PM   Mathews Kochummen
 *       forward port from v12x to trunk
 *  6    360Commerce 1.5         7/9/2007 6:04:18 PM    Alan N. Sinton  CR
 *       27494 - Removed constaint TenderLimitsIfc.TENDER_NO_LIMIT_AMOUNT in
 *       favor of a lazy init value returned by static method
 *       TenderLimits.getTenderNoLimitAmount().
 *  5    360Commerce 1.4         4/25/2007 8:52:52 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  4    360Commerce 1.3         10/12/2006 8:17:49 AM  Christian Greene Adding
 *        new functionality for PasswordPolicy.  Employee password will now be
 *        persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *        rebuild with the new SQL scripts will be required.
 *  3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse
 * $
 * Revision 1.13.2.1  2004/11/12 14:28:53  kll
 * @scr 7337: JournalFactory extensibility initiative
 *
 * Revision 1.13  2004/08/31 19:12:35  blj
 * @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 * Revision 1.12  2004/07/21 22:55:33  bwf
 * @scr 5963 (ServicesImpact) Moved getChangeOptions and calculateMaxCashChange out of
 *                   abstractRetailTransaction and into TenderUtility.  Also made calculateMaxCashChange
 *                   more polymorphic.
 *
 * Revision 1.11  2004/07/14 18:47:09  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.10  2004/07/01 15:09:44  bwf
 * @scr 5929 Create void tender correctly.
 *
 * Revision 1.9  2004/05/02 01:54:05  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.8  2004/04/29 17:30:00  bwf
 * @scr 3377 Debit Reversal Work
 *
 * Revision 1.7  2004/04/22 21:03:53  epd
 * @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 * Revision 1.6  2004/04/15 20:56:18  blj
 * @scr 3871 - updated to fix problems with void and offline.
 *
 * Revision 1.5  2004/04/07 20:19:10  epd
 * @scr 4322 Updates for tender invariant work
 *
 * Revision 1.4  2004/04/01 15:58:17  blj
 * @scr 3872 Added training mode, toggled the redeem button based
 * on transaction==null and fixed post void problems.
 *
 * Revision 1.3  2004/02/12 16:47:56  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:19:47  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:11
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.18 Feb 05 2004 13:20:34 rhafernik log4j conversion
 *
 * Rev 1.17 Jan 19 2004 17:40:42 epd Put calls back in to DomainGateway
 *
 * Rev 1.16 Jan 09 2004 15:08:38 epd updates for unit testing
 *
 * Rev 1.15 Jan 06 2004 13:11:46 epd refactored away references to TenderHelper
 * and DomainGateway
 *
 * Rev 1.14 Jan 06 2004 11:09:48 epd refactoring to remove references to
 * TenderHelper, DomainGateway
 *
 * Rev 1.13 Dec 29 2003 14:27:12 bwf Updated for unit tests. Change use of
 * TenderHelper.parseAmount.
 *
 * Rev 1.12 Dec 18 2003 17:39:26 rsachdeva Alternate Currency Resolution for
 * POS SCR-3551: Tender using Canadian Cash
 *
 * Rev 1.11 Nov 21 2003 15:18:48 epd fixed factory label
 *
 * Rev 1.10 Nov 20 2003 17:13:50 epd updates for ADO Factory Complex
 *
 * Rev 1.9 Nov 20 2003 16:57:18 epd updated to use new ADO Factory Complex
 *
 * Rev 1.8 Nov 14 2003 16:44:36 epd new dev
 *
 * Rev 1.7 Nov 14 2003 11:09:36 epd refactored some void functionality to be
 * more general.
 *
 * Rev 1.6 Nov 13 2003 17:03:08 epd Refactoring: updated to use new method to
 * access context
 *
 * Rev 1.5 Nov 12 2003 10:07:22 rwh Added getContext() method to ADO base class
 * Resolution for Foundation SCR-265: Add ADOContext reference to ADO base
 * class
 *
 * Rev 1.4 Nov 12 2003 09:26:40 rwh Added setChildContexts() method Resolution
 * for Foundation SCR-265: Add ADOContext reference to ADO base class
 *
 * Rev 1.3 Nov 05 2003 18:42:58 epd updates for authorization
 *
 * Rev 1.2 Nov 05 2003 14:48:40 epd oops, fix broke it
 *
 * Rev 1.0 Nov 04 2003 11:13:52 epd Initial revision.
 *
 * Rev 1.4 Oct 30 2003 20:39:32 epd updated remove logic
 *
 * Rev 1.3 Oct 27 2003 20:25:06 epd fixed bug in remove tender code
 *
 * Rev 1.2 Oct 21 2003 10:28:40 epd Added functionality for Delete Tender
 * functionality from Tender Options screens
 *
 * Rev 1.1 Oct 20 2003 15:20:20 epd Added code to check for overtender
 *
 * Rev 1.0 Oct 17 2003 12:34:28 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

//java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.TourADOContext;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.factory.TenderGroupFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalData;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderBaseADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.tender.TenderLimits;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * This abstract tender group provides some functionality common to all tender
 * groups as well as defining a validation contract the concrete tender groups
 * must implement.
 */
public abstract class AbstractTenderGroupADO
    extends TenderBaseADO
    implements TenderGroupADOIfc
{
    /** The list of tenders for a group */
    protected ArrayList tenderList = new ArrayList(3);

    /**
     * Used for override purposes to modify validation behavior when in an
     * override situation.
     */
    protected Boolean evaluateTenderLimits = Boolean.TRUE;

    /**
     * Not all tender groups are overtenderable (controlled by parameter)
     * Assume true, and mark those that aren't based on parameterized value;
     */
    protected Boolean overtenderable = Boolean.TRUE;

    /**
     * The tender will be added to the list assuming it passes validation.
     *
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#addTender(oracle.retail.stores.pos.ado.tender.TenderADOIfc)
     */
    public void addTender(TenderADOIfc tender) throws TenderException
    {
        // validate the tender
        validate(tender);
        // add the tender to the list;
        tenderList.add(tender);
    }

    /**
     * A testTender must be validated before it can be added to the group. This
     * provides the basic implementation for that validation.
     */
    protected void validate(TenderADOIfc testTender) throws TenderException
    {
        testTender.validate();
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#validateOvertender(java.util.HashMap,
     *      oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public void validateOvertender(
        HashMap tenderAttributes,
        CurrencyIfc balanceDue,
        CurrencyIfc overtenderLimit)
        throws TenderException
    {
        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        String tenderType =
                ((TenderTypeEnum) tenderAttributes
                .get(TenderConstants.TENDER_TYPE))
                .toString();
        if (!utility.isOvertenderAllowed(tenderType))
        {
            // mark this group as non-overtenderable
            this.overtenderable = Boolean.FALSE;

            // we found a match, compare tender amount to balance due.
            CurrencyIfc tenderAmount =
                parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
            if (tenderAmount.compareTo(balanceDue) == CurrencyIfc.GREATER_THAN)
            {
                    throw new TenderException(
                            "Overtender not allowed",
                            TenderErrorCodeEnum.OVERTENDER_ILLEGAL);
            }
        }
    }

    /**
     * The tender will be added to the list without validation. This should
     * only be used when validation has already occurred (like in Void).
     */
    public void addTenderNoValidation(TenderADOIfc tender)
    {
        tenderList.add(tender);
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#removeTender(oracle.retail.stores.pos.ado.tender.TenderADOIfc)
     *      The tender passed in is not necessarily object equal, but must at
     *      least be attribute equal.
     */
    public void removeTender(TenderADOIfc tenderToRemove)
    {
        // first try to remove the tender based on object equality
        if (tenderList.contains(tenderToRemove))
        {
            System.out.println(tenderList.remove(tenderToRemove));
        }
        else
        {
            // iterate through the tenders in the group and if we find a match,
            // then remove that tender
            for (Iterator iter = tenderList.iterator(); iter.hasNext();)
            {
                TenderADOIfc tender = (TenderADOIfc) iter.next();
                TenderLineItemIfc tenderRDO =
                    (TenderLineItemIfc) ((ADO) tender).toLegacy();
                TenderLineItemIfc tenderToRemoveRDO =
                    (TenderLineItemIfc) ((ADO) tenderToRemove).toLegacy();
                if (tenderRDO.equals(tenderToRemoveRDO))
                {
                    iter.remove();
                    break;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getTenders()
     */
    public TenderADOIfc[] getTenders()
    {
        TenderADOIfc[] tenders = new TenderADOIfc[tenderList.size()];
        tenders = (TenderADOIfc[]) tenderList.toArray(tenders);
        return tenders;
    }

    /**
     * Attempt to override the tender limit function using the provided
     * employee. This method is hardwired to override tender limits only.
     *
     * @param overrideEmployee
     *            The employee for which we are testing override access
     * @see oracle.retail.stores.pos.ado.security.OverridableIfc#override(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public boolean override(EmployeeIfc overrideEmployee, int roleFunction)
    {
        // Get the context
        TourADOContext context = (TourADOContext)getContext();
        TourContext ctx = TourContext.getInstance();
        String appID = null;
        // Get the security manager
        SecurityManagerIfc sm = (SecurityManagerIfc)context.getManager(SecurityManagerIfc.TYPE);
        boolean result = false;
        try
        {
            BusIfc bus = ctx.getTourBus();
            if (bus != null)
            {
                appID = ((TenderCargo)bus.getCargo()).getAppID();
            }
            else
            {
                appID = context.getApplicationID();
            }
            // Attempt Tender Limit override with this employee
            result =
                sm.override(
                        appID,
                    overrideEmployee.getLoginID(),
                    overrideEmployee.getPasswordBytes(),
                    RoleFunctionIfc.TENDER_LIMIT);

            // journal the override attempt
            JournalData data = new JournalData();
            // add operator, override operator, function, and result to journal
            // data.
            data.putJournalData(
                JournalConstants.OPERATOR,
                ((UserAccessCargo) context.getBus().getCargo())
                    .getOperator()
                    .getEmployeeID());
            data.putJournalData(
                JournalConstants.OVERRIDE_OPERATOR,
                overrideEmployee.getEmployeeID());
            data.putJournalData(
                JournalConstants.FUNCTION,
                Integer.toString(RoleFunctionIfc.TENDER_LIMIT));
            data.putJournalData(
                JournalConstants.BOOLEAN,
                new Boolean(result).toString());
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(
                data,
                JournalFamilyEnum.TENDER,
                JournalActionEnum.OVERRIDE);
        }
        catch (LoginException e)
        {
            // do nothing. Obviously the override didn't work
        }
        
        // If the override succeeded, alter the internal state of this
        // tender group
        if (result)
        {
            evaluateTenderLimits = Boolean.FALSE;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getTenderCount()
     */
    public int getTenderCount()
    {
        return tenderList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#getTenderToal()
     */
    public CurrencyIfc getTenderTotal()
    {
        // get new base currency so we don't return null.
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();

        for (Iterator iter = tenderList.iterator(); iter.hasNext();)
        {
            TenderADOIfc tender = (TenderADOIfc) iter.next();
            total = total.add(tender.getAmount());
        }
        return total;
    }

    /*
     * (non-Javadoc)
     *
     * @see oracle.retail.stores.ado.tender.group.TenderGroupADOIfc#combineGroups(oracle.retail.stores.ado.tender.group.TenderGroupADOIfc)
     */
    public void combineGroups(TenderGroupADOIfc tenderGroup)
    {
        assert(this.getGroupType() == tenderGroup.getGroupType());

        tenderList.addAll(((AbstractTenderGroupADO) tenderGroup).tenderList);
    }

    //----------------------------------------------------------------------
    /**
        Creates a new tender group idential to the current one with the
        exception that the added tenders have negated amounts
        @return
        @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#processVoid()
    **/
    //----------------------------------------------------------------------
    public TenderGroupADOIfc processVoid()
    {
        TenderGroupADOIfc voidGroup = createGroupFactoryAndTenderGroup();

        for (Iterator iter = tenderList.iterator(); iter.hasNext();)
        {
            HashMap tenderAttributes = negateTenderAmount(iter);
            // create new tender
            try
            {
                createAndJournalVoidTender(voidGroup, tenderAttributes);
            }
            catch (ADOException e)
            {
                // TODO: log
            }
            catch (TenderException e)
            {
                // There should NEVER be an exception performing this function
                // TODO: log
            }
        }
        return voidGroup;
    }

    //----------------------------------------------------------------------
    /**
        This method creates and journals a void tender.
        @param voidGroup
        @param tenderLI
        @throws ADOException
        @throws TenderException
    **/
    //----------------------------------------------------------------------
    public void createAndJournalVoidTender(TenderGroupADOIfc voidGroup, HashMap tenderAttributes) throws ADOException, TenderException
    {
        TenderFactoryIfc factory =
            (TenderFactoryIfc) ADOFactoryComplex.getFactory(
            "factory.tender");
        TenderADOIfc voidTender =
            factory.createTender(tenderAttributes);

        voidGroup.addTenderNoValidation(voidTender);

        // journal
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
        registerJournal.journal(
            voidTender,
            JournalFamilyEnum.TENDER,
            JournalActionEnum.VOID);
    }

    //----------------------------------------------------------------------
    /**
        This method gets the tender and negates the amounts.
        @param tender TenderADOIfc original tender
        @return tenderlineitemifc
    **/
    //----------------------------------------------------------------------
    public HashMap negateTenderAmount(Iterator iter)
    {
        TenderADOIfc tender = (TenderADOIfc) iter.next();

        HashMap tenderAttributes = tender.getTenderAttributes();

        // negate the amount
        String amountStr =
            (String) tenderAttributes.get(TenderConstants.AMOUNT);
        CurrencyIfc amount =
            DomainGateway.getBaseCurrencyInstance(amountStr);
        amount = amount.negate();

        // replace the negated amount in the map
        tenderAttributes.put(
            TenderConstants.AMOUNT,
            amount.getStringValue());
        String alternateAmountValue =
            (String) tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
        if (alternateAmountValue != null)
        {
            CurrencyIfc alternateAmount = null;

            try
            {
                alternateAmount =
                    parseAlternateAmount(alternateAmountValue, tenderAttributes);
                alternateAmount = alternateAmount.negate();
                // replace the negated alternate amount in the map
                tenderAttributes.put(
                        TenderConstants.ALTERNATE_AMOUNT,
                        alternateAmount.getStringValue());
            }
            catch (TenderException e)
            {
                logger.error(e.getMessage());
            }
        }
        // replace type with void type
        tenderAttributes.put(TenderConstants.TENDER_TYPE, getVoidType());
        return tenderAttributes;
    }

    /**
     * @return
     */
    public TenderGroupADOIfc createGroupFactoryAndTenderGroup()
    {
        TenderGroupFactoryIfc groupFactory = null;
        TenderGroupADOIfc voidGroup = null;
        try
        {
            groupFactory =
                (TenderGroupFactoryIfc) ADOFactoryComplex.getFactory(
                "factory.tender.group");
            voidGroup = groupFactory.createTenderGroup(getVoidType());
        }
        catch (ADOException e1)
        {
            e1.printStackTrace();
        }
        return voidGroup;
    }

    /**
     * Most tenders do not have refund limits. Implemented here as a
     * convenience.
     */
    public void validateRefundLimits(
        HashMap tenderAttributes,
        boolean hasReceipt,
        boolean retrieved)
        throws TenderException
    {
    }

    /**
     * This method acts as an accessor to the static No Limit tender value.
     * Added to accommodate Unit Testing
     *
     * @return The Tender No limit amount;
     */
    protected CurrencyIfc getTenderNoLimitAmount()
    {
        return TenderLimits.getTenderNoLimitAmount();
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc#getOvertenderLimit()
     */
    public CurrencyIfc getOvertenderLimit()
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance("0.00");
        if (overtenderable != Boolean.FALSE)
        {
            Iterator tenderIter = tenderList.iterator();
            while (tenderIter.hasNext())
            {
                TenderADOIfc tender = (TenderADOIfc) tenderIter.next();
                result = result.add(tender.getAmount());
            }
        }
        return result;
    }

    //----------------------------------------------------------------------
    /**
        This method checks if the tender is overtenderable and is a cash
        equivalent tender.  Non cash equivalent tender will either not
        be used in calculations or will override this method.
        @return
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getMaxCashChange()
    {
        CurrencyIfc maxCashChange = DomainGateway.getBaseCurrencyInstance();
        if (isCashEquivalentTender())
        {
            TenderADOIfc[] tenders = getTenders();
            for (int i = 0; i < tenders.length; i++)
            {
                CurrencyIfc tenderAmount = tenders[i].getAmount();
                if (tenderAmount.signum() == CurrencyIfc.POSITIVE)
                {
                    maxCashChange = maxCashChange.add(tenderAmount);
                }
            }
        }
        return maxCashChange;
    }

    //----------------------------------------------------------------------
    /**
        This method checks to see if the tender type is overtenderable
        @return
    **/
    //----------------------------------------------------------------------
    protected boolean tenderOvertenderable()
    {
        UtilityIfc util = getUtility();
        
        return util.isOvertenderAllowed(getGroupType().toString());
    }

    //----------------------------------------------------------------------
    /**
        This method determines if you should use the full amount for cash
        change.  Override this method for tenders where you want to use the
        full amount.
        @return
    **/
    //----------------------------------------------------------------------
    public boolean isCashEquivalentTender()
    {
        return false;
    }


    /**
     * Pulls all the tenders from and adds them to a supplied list.
     *
     * @param tenderList
     * @param group
     * @return authTenders
     */
    public List pullAuthPendingTendersFromGroup(List tenderList, TenderGroupADOIfc group)
    {
         TenderADOIfc[] tenders = getTenders();
         List authTenders = new ArrayList();
         for (int i = 0; i < tenders.length; i++)
         {
             if (!((AuthorizableADOIfc) tenders[i]).isAuthorized())
             {
                 authTenders.add(tenders[i]);
             }
         }

         return authTenders;
    }
    
    /**
     * Reset so the limits will be evaluated.
     */
    public void resetEvaluateTenderLimits()
    {
        evaluateTenderLimits = Boolean.TRUE;
    }
}
