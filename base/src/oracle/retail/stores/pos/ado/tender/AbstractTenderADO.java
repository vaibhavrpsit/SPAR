/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/AbstractTenderADO.java /rgbustores_13.4x_generic_branch/3 2011/09/26 09:48:08 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/26/11 - fix possible ado context npe
 *    cgreene   09/23/11 - print Balance Not Available for gift cards when in
 *                         trans reentry mode
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         4/25/2007 8:52:55 AM   Anda D. Cadar   I18N
 *       merge
 *       
 *  4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:28 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse   
 * $
 * Revision 1.7  2004/09/23 00:07:13  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.6  2004/07/23 22:17:25  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.5  2004/05/02 01:54:05  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.4  2004/03/26 21:32:18  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.3  2004/03/26 20:48:45  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.2 2004/02/12 16:47:55 mcs Forcing head revision
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.9 Feb 05 2004 13:46:10 rhafernik log4j changes
 * 
 * Rev 1.8 Jan 06 2004 11:09:46 epd refactoring to remove references to TenderHelper, DomainGateway
 * 
 * Rev 1.7 Dec 22 2003 15:13:04 epd Updates to facilitate unit testing
 * 
 * Rev 1.6 Dec 17 2003 14:48:44 epd Refactorings to accommodate Unit testing
 * 
 * Rev 1.5 Dec 07 2003 18:47:04 crain Added foreign amount Resolution for 3421: Tender redesign
 * 
 * Rev 1.4 Nov 19 2003 22:09:38 blj added code for gift card tender using ado design
 * 
 * Rev 1.3 Nov 12 2003 10:07:20 rwh Added getContext() method to ADO base class Resolution for Foundation SCR-265: Add
 * ADOContext reference to ADO base class
 * 
 * Rev 1.2 Nov 12 2003 09:26:38 rwh Added setChildContexts() method Resolution for Foundation SCR-265: Add ADOContext
 * reference to ADO base class
 * 
 * Rev 1.1 Nov 05 2003 13:18:12 epd updates for authorization
 * 
 * Rev 1.0 Nov 04 2003 11:13:08 epd Initial revision.
 * 
 * Rev 1.1 Oct 21 2003 10:00:56 epd Refactoring. Moved RDO tender to abstract class
 * 
 * Rev 1.0 Oct 17 2003 12:33:42 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterMode;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

/**
 *  
 */
public abstract class AbstractTenderADO extends TenderBaseADO implements TenderADOIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -2515590156128670844L;

    /** The RDO tender */
    protected TenderLineItemIfc tenderRDO;
    /* dirty flag */
    protected boolean dirty;

    /**
     * Transaction Reentry status
     * @deprecated as of 13.4. No replacement.
     */
    protected boolean transactionReentryMode = false;

    /**
     * No-arg constructor provided to enforce execution of initializeTenderRDO()
     * method
     */
    protected AbstractTenderADO()
    {
        initializeTenderRDO();
    }

    /**
     * Each concrete tender type must implement this method in order to
     * instantiate the proper concrete RDO tender type that corresponds to the
     * current ADO type.
     */
    protected abstract void initializeTenderRDO();

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getAmount()
     */
    public CurrencyIfc getAmount()
    {
        return tenderRDO.getAmountTender();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object) For now, use equals
     * functionality in the RDO tender
     */
    public boolean equals(Object arg0)
    {
        return tenderRDO.equals(arg0);
    }

    /**
     * This method is used to flag a tender as dirty.
     * 
     * @param dirty Set an attribute to signify that the tender changed(is
     *            dirty).
     */
    public void setDirtyFlag(boolean dirtyFlag)
    {
        dirty = dirtyFlag;
    }

    /**
     * Return the dirty flag
     * 
     * @return
     */
    public boolean isDirtyFlag()
    {
        return dirty;
    }

    /**
     * @return Returns the transactionReentryMode.
     */
    public boolean isTransactionReentryMode()
    {
        ADOContextIfc context = ContextFactory.getInstance().getContext();
        if (context != null)
        {
            return context.getRegisterADO().isInMode(RegisterMode.REENTRY);
        }
        return false;
    }

    /**
     * Returns whether we are in training mode
     * 
     * @return
     */
    public boolean isTrainingMode()
    {
        ADOContextIfc context = ContextFactory.getInstance().getContext();
        if (context != null)
        {
            return context.getRegisterADO().isInMode(RegisterMode.TRAINING);
        }
        return false;
    }

    /**
     * @param transactionReentryMode The transactionReentryMode to set.
     * @deprecated as of 13.4. No replacement.
     */
    public void setTransactionReentryMode(boolean transactionReentryMode)
    {
        this.transactionReentryMode = transactionReentryMode;
    }

}
