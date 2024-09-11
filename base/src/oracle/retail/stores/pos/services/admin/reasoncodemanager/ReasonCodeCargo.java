/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/ReasonCodeCargo.java /main/15 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   11/03/08 - Updates to the persistence of CodeLists.
 * ===========================================================================
     $Log:
      4    .v8x      1.2.2.0     3/8/2007 4:21:32 PM    Brett J. Larsen CR 4530
            - default reason code not being displayed for code list when no
           reason code is defined for the list - in this case the 1st reason
           code was being displayed by default (wrong)

           changed code to display blank string when no default reason code is
            defined for the code list
      3    360Commerce1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:32 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:13:33 PM  Robert Pearse
     $
     Revision 1.5  2004/04/19 18:48:56  awilliam
     @scr 4374 Reason Code featrure work

     Revision 1.4  2004/04/09 16:56:02  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.3  2004/02/12 16:48:53  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:36:39  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:53:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 19 2003 15:45:24   crain
 * Deprecated code list map methods
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:38:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:32   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Mar 2002 16:52:34   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   21 Jan 2002 17:49:52   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:12:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;

/**
 * The cargo for the reason code manager service.
 * 
 * @version $Revision: /main/15 $
 */
public class ReasonCodeCargo extends UserAccessCargo implements CargoIfc, TourCamIfc, DBErrorCargoIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReasonCodeCargo.class);

    /**
     * The store (e.g., 2006) currently selected for reason code setting.
     */
    protected String store = null;

    /** The store (e.g., 2006) or corperation (CORP). **/
    protected String locationToModify = null;

    /**
     * The reason code group (e.g., CheckIDTypes) currently selected for
     * setting.
     */
    protected ReasonCodeGroupBeanModel reasonCodeGroup = null;

    /**
     * The reason code screen to display (the default is REASON_CODE_LIST)
     */
    protected String reasonCodeScreenToDisplay = POSUIManagerIfc.REASON_CODE_LIST_VIEW_ONLY;

    /** The result of the an interaction with the data manager. **/
    protected int dataExceptionErrorCode;

    /** List IDs **/
    protected List<String> listIDs;

    protected CodeListIfc codeList = null;

    /**
     * Class constructor.
     */
    public ReasonCodeCargo()
    {
    }

    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.REASON_CODES_ADD_MDF;
    }

    /**
     * Returns the store ID.
     * 
     * @return the store ID
     */
    public String getStore()
    {
        return store;
    }

    /**
     * Returns the Location to modify.
     * 
     * @return the location to modify
     */
    public String getLocationToModify()
    {
        return locationToModify;
    }

    /**
     * Returns the reason code group bean model.
     * 
     * @return ReasonCodeGroupBeanModel
     */
    public ReasonCodeGroupBeanModel getReasonCodeGroup()
    {
        return reasonCodeGroup;
    }

    /**
     * Returns the reason code screen to display.
     * 
     * @return the reason code screen to diplsay
     */
    public String getReasonCodeScreenToDisplay()
    {
        return reasonCodeScreenToDisplay;
    }

    /**
     * Sets the store id.
     * 
     * @param value the new value for store ID
     */
    public void setStore(String value)
    {
        store = value;
    }

    /**
     * Sets the location to modify.
     * 
     * @param value the new value
     */
    public void setLocationToModify(String value)
    {
        locationToModify = value;
    }

    /**
     * Sets the reason code group bean model.
     * 
     * @param value the bean model
     */
    public void setReasonCodeGroup(ReasonCodeGroupBeanModel value)
    {
        reasonCodeGroup = value;
    }

    /**
     * Sets the reason code screen to display.
     * 
     * @param value the new value
     */
    public void setReasonCodeScreenToDisplay(String value)
    {
        reasonCodeScreenToDisplay = value;
    }

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return the integer value
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param value the integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Creates a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     * 
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Resets the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     * 
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    /**
     * Determines whether the user is allowed to modify the chosen reasonCode.
     * 
     * @return true when modifiable; false otherwise
     */
    public boolean reasonCodeGroupModificationPermitted()
    {
        return (reasonCodeGroup.getModifiable());
    }

    /**
     * Converts a ReasonCodeGroupBeanModel bean (as used by the UI) into a
     * CodeListIfc (as used by the business logic).
     * 
     * @param rcGroup the object to convert
     * @return CodeListIfc corresponding to the ReasonCodeGroupBeanModel
     */
    public CodeListIfc convertBeanToCodeList(ReasonCodeGroupBeanModel rcGroup)
    {
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        CodeListIfc list = DomainGateway.getFactory().getCodeListInstance();
        list.setListDescription(rcGroup.getGroupName());
        list.setGroupName(rcGroup.getParameterGroup());
        list.setNumericCodes(rcGroup.getIdIsNumeric());
        list.setSource(rcGroup.getSource());

        // Note: We will update the location chosen by the user.
        // Even though most codes emanate from corporate, if a change
        // is made at the store level, it goes in the can as a
        // store-level change.
        list.setStoreID(getLocationToModify());

        // Convert the ReasonCode beans to CodeEntries
        Vector<ReasonCode> beanValues = rcGroup.getReasonCodes();
        ReasonCode bean = null;
        CodeEntryIfc entry = null;

        for (int i = 0; i < beanValues.size(); i++)
        {
            bean = beanValues.get(i);
            entry = DomainGateway.getFactory().getCodeEntryInstance();
            entry.setCode(bean.getDatabaseId());
            entry.setSortIndex(i);
            entry.setEnabled(bean.getEnabled());
            entry.setReferenceKey(bean.getReferenceKey());

            // Restore original localizedText and override the modified one
            restoreLocalizedText(entry);

            entry.setText(lcl, bean.getReasonCodeName());

            list.addEntry(entry);
            // Set the default reason code
            if ((bean.getReasonCodeName() != null) && bean.getReasonCodeName().equals(rcGroup.getDefaultReasonCode()))
            {
                list.setDefaultCodeString(bean.getDatabaseId());
            }

            if (Util.isEmpty(bean.getCodeName()))
            {
                entry.setCodeName(entry.getText(lcl));
            }
            else
            {
                entry.setCodeName(bean.getCodeName());
            }
        }
        return list;
    }

    /**
     * Restore the Localized Code
     * 
     * @param codeEntry
     */
    protected void restoreLocalizedText(CodeEntryIfc codeEntry)
    {
        CodeListIfc originalCodeList = getCodeList();
        CodeEntryIfc originalCodeEntry = originalCodeList.findListEntryByCode(codeEntry.getCode(), false);
        if (originalCodeEntry != null)
        {
            codeEntry.setLocalizedText(originalCodeEntry.getLocalizedText());
        }
    }

    /**
     * Returns all fields to their initial values. All fields are returned to
     * their initial values (existing values are erased).
     */
    public void reset()
    {
        if (logger.isInfoEnabled())
            logger.info("ListEditorCargo.reset");
        reasonCodeGroup = null;
    }

    /**
     * @return the listIDs
     */
    public List<String> getListIDs()
    {
        return listIDs;
    }

    /**
     * @param listIDs the listIDs to set
     */
    public void setListIDs(List<String> listIDs)
    {
        this.listIDs = listIDs;
    }

    /**
     * @return the codeList
     */
    public CodeListIfc getCodeList()
    {
        return codeList;
    }

    /**
     * @param codeList the codeList to set
     */
    public void setCodeList(CodeListIfc codeList)
    {
        this.codeList = codeList;
    }

}
