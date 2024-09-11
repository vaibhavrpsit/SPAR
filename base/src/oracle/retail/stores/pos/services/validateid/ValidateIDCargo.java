/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/ValidateIDCargo.java /rgbustores_13.4x_generic_branch/2 2011/07/20 04:31:51 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================

     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape
     $

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.validateid;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.factory.FoundationObjectFactoryIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------

public class ValidateIDCargo extends UserAccessCargo implements ValidateIDCargoIfc
{
    // These attributes govern the behavior of the valdate ID service
    /** Reason code name for obtaining the list of valid ID types **/
    protected String codeConstant = null;
    /** Whether the service should collect countries for ID's that
     * don't reflect a state. Note that if you retrieve a state, you
     * have the country by default.
     **/
    protected boolean captureCountry = false;
    /** whether the service should allow swipe of an ID. **/
    protected boolean allowSwipe = true;
    /** whether the service should always capture country/state
     * of ID regardless of swipe allowed.
     ***/
    protected boolean alwaysCaptureIssuer = false;

    protected LocalizedCodeIfc localizedPersonalIDCode = DomainGateway.getFactory().getLocalizedCode();

    protected String idTypeName = null;
    /** The ID Number used for validation **/
    protected String idNumber = null;
    /** The MSR Model containing the ID Swipe used for validation **/
    protected MSRModel msrModel = null;
    /** The issuing country of the ID used for validation **/
    protected String idCountry = null;
    /** The issuing state/province of the ID used for validation **/
    protected String idState = null;
    /** The Enciphered object for ID **/
    protected EncipheredDataIfc idNumberEncipheredData = null;

    protected CodeListIfc personalIDTypes;

    // These methods save and retrieve information for the calling service
    /**
     * Retrieves the reason code name for obtaining the list of valid ID types
     *
     * @return The Reason Code Constant
     */
    public String getIDTypeCodeConstant()
    {
        return codeConstant;
    }

    /**
     * Sets the reason code name for obtaining the list of valid ID types
     *
     * @param codeConstant The Reason Code Constant
     */
    public void setIDTypeCodeConstant(String codeConstant)
    {
        this.codeConstant = codeConstant;
    }

    /**
     * Retrieves whether the service should collect countries for ID's that
     * don't reflect a state. Note that if you retrieve a state, you
     * have the country by default.
     *
     * @return If the ID Issuing country should be captured
     */
    public boolean isCaptureCountry()
    {
        return captureCountry;
    }

    /**
     * Sets whether the service should collect countries for ID's that
     * don't reflect a state. Note that if you retrieve a state, you
     * have the country by default.
     *
     * @param captureCountry If the ID Issuing country should be captured
     */
    public void setCaptureCountry(boolean captureCountry)
    {
        this.captureCountry = captureCountry;
    }

    /**
     * Retrieves whether the service should allow swipe of an ID.
     *
     * @return If we should allow swipe of an ID.
     */
    public boolean isAllowSwipe()
    {
        return allowSwipe;
    }

    /**
     * Sets whether the service should allow swipe of an ID.
     *
     * @param allowSwipe Whether the service should allow swipe of an ID.
     */
    public void setAllowSwipe(boolean allowSwipe)
    {
        this.allowSwipe = allowSwipe;
    }

    /**
     * Retrieves whether the service should always capture country/state
     * of ID regardless of swipe allowed.
     *
     * @return If the service should always capture country/state
     * of ID.
     */
    public boolean isAlwaysCaptureIssuer()
    {
        return alwaysCaptureIssuer;
    }

    /**
     * Sets whether the service should always capture country/state
     * of ID regardless of swipe allowed.
     *
     * @param alwaysCaptureIssuer the service should always capture country/state
     * of ID.
     */
    public void setAlwaysCaptureIssuer(boolean alwaysCaptureIssuer)
    {
        this.alwaysCaptureIssuer = alwaysCaptureIssuer;
    }

    /**
     * Retrieves the ID Number used for validation
     *
     * @return The ID Number as a string
     */
    public String getIDNumber()
    {
        return idNumber;
    }

    /**
     * Sets the ID Number used for validation
     *
     * @param idNumber The ID Number as a string
     */
    public void setIDNumber(String idNumber)
    {
        this.idNumber = idNumber;
    }

    /**
     * Retrieves the MSR Model containing the ID Swipe used for validation
     *
     * @return The MSR Model
     */
    public MSRModel getMSRModel()
    {
        return msrModel;
    }

    /**
     * Sets the MSR Model containing the ID Swipe used for validation
     *
     * @param msrModel The MSR Model
     */
    public void setMSRModel(MSRModel msrModel)
    {
        this.msrModel = msrModel;
    }

    /**
     * Retrieves the issuing country of the ID used for validation
     *
     * @return The country as a string
     */
    public String getIDCountry()
    {
        return idCountry;
    }

    /**
     * Sets the issuing country of the ID used for validation
     *
     * @param idCountry The country as a string
     */
    public void setIDCountry(String idCountry)
    {
        this.idCountry = idCountry;
    }

    /**
     * Retrieves the issuing state/province of the ID used for validation
     *
     * @return The state/province as a string
     */
    public String getIDState()
    {
        return idState;
    }

    /**
     * Sets the issuing state/province of the ID used for validation
     *
     * @param idState The state/province as a string
     */
    public void setIDState(String idState)
    {
        this.idState = idState;
    }

    //--------------------------------------------------------------------------
    /**
        Create a SnapshotIfc which can subsequently be used to restore
            the cargo to its current state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The cargo is able to make a snapshot.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>A snapshot is returned which contains enough data to restore the
            cargo to its current state.
        </UL>
        @return an object which stores the current state of the cargo.
        @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    */
    //--------------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    //--------------------------------------------------------------------------
    /**
        Reset the cargo data using the snapshot passed in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The snapshot represents the state of the cargo, possibly relative
        to the existing state of the cargo.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>The cargo state has been restored with the contents of the snapshot.
        </UL>
        @param snapshot is the SnapshotIfc which contains the desired state
            of the cargo.
        @exception ObjectRestoreException is thrown when the cargo cannot
            be restored with this snapshot
    */
    //--------------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }

    /**
     * @return the personalIDTypes
     */
    public CodeListIfc getPersonalIDTypes()
    {
        return personalIDTypes;
    }

    /**
     * @param personalIDTypes the personalIDTypes to set
     */
    public void setPersonalIDTypes(CodeListIfc personalIDTypes)
    {
        this.personalIDTypes = personalIDTypes;
    }

  /**
   * returns idNumberEncipheredData
   */
  @Override
  public EncipheredDataIfc getIdNumberEncipheredData()
  {
    if (idNumberEncipheredData == null)
    {
      FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
      idNumberEncipheredData = factory.createEncipheredDataInstance();
    }
    return idNumberEncipheredData;
  }

  /**
   * sets idNumberEncipheredData
   */
  @Override
  public void setIdNumberEncipheredData(EncipheredDataIfc idNumberEncipheredData)
  {
    this.idNumberEncipheredData = idNumberEncipheredData;
  }

  public String getIdTypeName()
	{
		return idTypeName;
	}

	public void setIdTypeName(String idTypeName)
	{
		this.idTypeName = idTypeName;
	}

	public LocalizedCodeIfc getLocalizedPersonalIDCode()
	{
		return localizedPersonalIDCode;
	}

	public void setLocalizedPersonalIDCode(LocalizedCodeIfc localizedPersonalIDCode)
	{
		this.localizedPersonalIDCode = localizedPersonalIDCode;
	}
	
	
}
