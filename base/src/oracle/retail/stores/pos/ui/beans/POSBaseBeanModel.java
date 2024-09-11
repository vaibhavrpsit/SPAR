/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/POSBaseBeanModel.java /main/20 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/09/14 - Added field selectedTabIndex for use with beans
 *                         that use the TabbedUIBean.
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    cgreene   11/18/13 - moved unlock container methods up to foundation
 *                         superclass so that the UISubsystem can access this
 *                         flag
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    rsnayak   07/29/11 - Timeout error fix
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    blarsen   03/09/10 - Added unlockContainer. This flag prevents specific
 *                         requests from unlocking user input.
 *    abondala  01/03/10 - update header date
 *    mchellap  11/21/08 - Merge
 *    mchellap  11/21/08 - Renamed TransactionStatus to TransactionStatusBean
 *    mchellap  11/20/08 - Changes for code review comments
 *    mchellap  11/20/08 - Modified the default constructor to use timeout
 *                         parameter based on transaction status
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         2/25/2008 12:56:25 AM  Manikandan Chellapan
 *        CR#30505 Service Alert Screens are not timing out
 *   4    360Commerce 1.3         12/19/2007 8:46:38 AM  Manikandan Chellapan
 *        PAPB FR27 Bulk Checkin-4
 *   3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:10 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:13:05 PM  Robert Pearse
 *
 *  Revision 1.4  2004/09/23 00:07:11  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   29 Jul 2003 03:38:54   baa
 * training mode
 *
 *    Rev 1.1   Aug 14 2002 18:18:22   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;
import oracle.retail.stores.pos.ui.timer.ScreenTimeoutIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * This class is the base class for all POS UI models; it provides access to the
 * information required by all the beans that do not occupy the task panel.
 * 
 * @version $Revision: /main/20 $
 */
public class POSBaseBeanModel extends UIModel implements ScreenTimeoutIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3725771572820768758L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /** Contains the model for the Local Button Bean */
    protected NavigationButtonBeanModel localButtonBeanModel = null;

    /** Contains the model for the Global Button Bean */
    protected NavigationButtonBeanModel globalButtonBeanModel = null;

    /** Contains the model for the Status Bean */
    protected StatusBeanModel statusBeanModel = null;

    /** Contains the model for the Prompt Area */
    protected PromptAndResponseModel promptAndResponseModel = null;

    /**
     * When a form finds a field in error, it can the field name on this
     * attribute; the system will attempt to place the focus on this field.
     */
    protected String fieldInErrorName = null;

    /**
     * When true this member indicates that this model should update StatusBean
     * ONLY.
     */
    protected boolean updateStatusBean = false;

    /** true if training mode is on */
    protected boolean inTraining = false;

    /** TimerModel used for automatic logoff after user inactivity timeout */
    protected TimerModelIfc timerModel = null;

    /** the selected tab index for the tabbed UI */
    private int selectedTabIndex = 0;

    /**
     * Default constructor.
     */
    public POSBaseBeanModel()
    {
        timerModel = new DefaultTimerModel();
    }

    /**
     * Gets the Local Button Bean Model.
     * 
     * @return the localButtonBeanModel
     */
    public NavigationButtonBeanModel getLocalButtonBeanModel()
    {
        return localButtonBeanModel;
    }

    /**
     * Sets the Local Button Bean Model.
     * 
     * @param value the localButtonBeanModel
     */
    public void setLocalButtonBeanModel(NavigationButtonBeanModel value)
    {
        localButtonBeanModel = value;
    }

    /**
     * Gets the Global Button Bean Model.
     * 
     * @return the globalButtonBeanModel
     */
    public NavigationButtonBeanModel getGlobalButtonBeanModel()
    {
        return globalButtonBeanModel;
    }

    /**
     * Sets the Global Button Bean Model.
     * 
     * @param value the globalButtonBeanModel
     */
    public void setGlobalButtonBeanModel(NavigationButtonBeanModel value)
    {
        globalButtonBeanModel = value;
    }

    /**
     * Gets the Status Bean Model
     * 
     * @return the statusBeanModel
     */
    public StatusBeanModel getStatusBeanModel()
    {
        return statusBeanModel;
    }

    /**
     * Sets the Status Bean Model
     * 
     * @param value the statusBeanModel
     */
    public void setStatusBeanModel(StatusBeanModel value)
    {
        statusBeanModel = value;
    }

    /**
     * Gets the Text Areas Model
     * 
     * @return the promptAndResponseModel
     */
    public PromptAndResponseModel getPromptAndResponseModel()
    {
        return promptAndResponseModel;
    }

    /**
     * Sets the Text Areas Model
     * 
     * @param value the promptAndResponseModel
     */
    public void setPromptAndResponseModel(PromptAndResponseModel value)
    {
        promptAndResponseModel = value;
    }

    /**
     * Gets the fieldInErrorName
     * 
     * @return the name of the form field in error.
     */
    public String getFieldInErrorName()
    {
        return fieldInErrorName;
    }

    /**
     * Sets the fieldInErrorName
     * 
     * @param fieldInErrorName the name of the field in error.
     */
    public void setFieldInErrorName(String fieldInErrorName)
    {
        this.fieldInErrorName = fieldInErrorName;
    }

    /**
     * Gets the updateStatusBean
     * 
     * @return if true update the StatusBean ONLY.
     */
    public boolean getUpdateStatusBean()
    {
        return updateStatusBean;
    }

    /**
     * Sets the updateStatusBean
     * 
     * @param update if true update the StatusBean ONLY.
     */
    public void setUpdateStatusBean(boolean update)
    {
        updateStatusBean = update;
    }

    /**
     * Gets user locale
     * 
     * @Locale.
     */
    public Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
    }

    /**
     * Gets default locale
     * 
     * @return Locale
     */
    public Locale getDefaultLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }

    /**
     * Retrieves flag that indicates if in training mode
     * 
     * @return training mode flag
     */
    public boolean isInTraining()
    {
        return inTraining;
    }

    /**
     * Set training mode flag
     * 
     * @param b training mode flag
     */
    public void setInTraining(boolean b)
    {
        inTraining = b;
    }

    /**
     * Sets the timerModel to be used in this class, the default value is the
     * DefaultTimerModel class.
     * 
     * @param timerModel TimerModel to use
     */

    public void setTimerModel(TimerModelIfc timerModel)
    {
        this.timerModel = timerModel;
    }

    /**
     * Return the timerModel this LineItemsModel is using
     * 
     * @return timerModel
     */
    public TimerModelIfc getTimerModel()
    {
        if (this.timerModel == null)
        {
            this.timerModel = new DefaultTimerModel(false, TimerModelIfc.TRANS_WITHOUT);
        }

        return timerModel;
    }

    /**
     * Returns the <code>selectedTabIndex</code> value.  This field is useful for Beans
     * that use the {@link TabbedUIBean}.
     * @return the selectedTabIndex
     * @since 14.1
     */
    public int getSelectedTabIndex()
    {
        return selectedTabIndex;
    }

    /**
     * Sets the <code>selectedTabIndex</code> value.  This field is useful for Beans that
     * use the {@link TabbedUIBean}.
     * @param selectedTabIndex the selectedTabIndex to set
     * @since 14.1
     */
    public void setSelectedTabIndex(int selectedTabIndex)
    {
        this.selectedTabIndex = selectedTabIndex;
    }
}