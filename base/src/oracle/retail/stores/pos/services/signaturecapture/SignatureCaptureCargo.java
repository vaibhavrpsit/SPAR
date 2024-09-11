/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/signaturecapture/SignatureCaptureCargo.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   04/25/12 - implement locking mechanism for concurrent users
 *                         using a single CPOI device.
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  03/19/09 - Fixed signature capture dialog issue for PDO
 *                         transaction
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:20 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:15 PM  Robert Pearse
 *
 *   Revision 1.5  2004/05/07 20:52:44  rzurga
 *   @scr 4720 Add amount tendered to CPOI sigcap screen [code review]
 *
 *   Revision 1.4  2004/05/04 01:59:15  rzurga
 *   @scr 4672 Add amount tendered to CPOI sigcap screen
 *
 *   Revision 1.3  2004/02/12 16:51:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:02:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:25:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.signaturecapture;

// Java imports
import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;

//------------------------------------------------------------------------------
/**
 * Data and methods common to the sites in SignatureCapture Service.
 *
 **/
//------------------------------------------------------------------------------
public class SignatureCaptureCargo implements SignatureCaptureCargoIfc
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4949448573683855118L;



    /**
     * This flag is used to determine whether to display the signature
     * verification screen.
     **/
    protected boolean verifySignature = true;

    /**
     * Contains the signature data. No assumptions are made about the format of
     * the data, it is up to the calling service to cast it appropriately.
     **/
    protected Serializable signature = null;

    /**
     * Contains a POSDeviceActionGroup object. The same one is used throughout
     * the signature capture service to avoid clearing the signature input
     * before the data has been retrieved from the device by the service.
     **/
    protected POSDeviceActions deviceActions = null;

    /**
     * Amount authorized
     */
    protected CurrencyIfc authAmount = null;

    // special Order/PDO transaction flag
    protected boolean isOrderTransaction;

    /**
     * Legal document that requires signature capture
     */
    protected LegalDocumentIfc legalDocument =  null;

    /**
     * Register handle
     */
    protected RegisterIfc register = null;
    // ---------------------------------------------------------------------
    /**
     * Sets the signature verification flag.
     *
     * @param boolean indicating whether to display the signature verification
     *        screen.
     **/
    // ---------------------------------------------------------------------
    public void setVerifySignature(boolean verify)
    {
        verifySignature = verify;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the signature verification flag.
     *
     * @return boolean signature verification flag
     **/
    // ---------------------------------------------------------------------
    public boolean verifySignature()
    {
        return verifySignature;
    }

    // ---------------------------------------------------------------------
    /**
     * Sets the signature data field.
     *
     * @param Point[] containing the signature data.
     **/
    // ---------------------------------------------------------------------
    public void setSignature(Serializable sig)
    {
        signature = sig;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the signature data.
     *
     * @return the array containing the signature
     **/
    // ---------------------------------------------------------------------
    public Serializable getSignature()
    {
        return signature;
    }

    // ---------------------------------------------------------------------
    /**
     * Sets the POSDeviceActions field.
     *
     * @param POSDeviceActions data.
     **/
    // ---------------------------------------------------------------------
    public void setDeviceActions(POSDeviceActions actions)
    {
        deviceActions = actions;
    }

    // ---------------------------------------------------------------------
    /**
     * Returns the POSDeviceActions field.
     *
     * @return POSDeviceActions for this session
     **/
    // ---------------------------------------------------------------------
    public POSDeviceActions getDeviceActions()
    {
        return deviceActions;
    }

    //--------------------------------------------------------------------------
    /**
     * General toString function
     *
     * @return the String representation of this class
     **/
    //--------------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SignatureCaptureCargo (Revision " + getRevisionNumber() + ") @"
                + hashCode());

        strResult += "VerifySignature = " + "[" + verifySignature + "]\n" + "Signature = " + "[" + signature + "]";

        return strResult;

    }

    // ---------------------------------------------------------------------
    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     *
     * @return String representation of revision number
     **/
    // ---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * @return Returns the amount to be authorized.
     */
    public CurrencyIfc getAuthAmount()
    {
        return authAmount;
    }

    /**
     * @param authAmount Sets the amount to be authorized.
     */
    public void setAuthAmount(CurrencyIfc authAmount)
    {
        this.authAmount = authAmount;
    }

    /**
     * Set special Order/PDO transaction flag
     *
     * @param value the special Order/PDO transaction flag
     */
    public void setOrderTransactionFlag(boolean value)
    {
        isOrderTransaction = value;
    }

    /**
     * Returns special Order/PDO transaction flag
     */
    public boolean isOrderTransaction()
    {
        return (isOrderTransaction);
    }

    /**
     * @return the legalDocument
     */
    public LegalDocumentIfc getLegalDocument()
    {
        return legalDocument;
    }

    /**
     * @param legalDocument the legalDocument to set
     */
    public void setLegalDocument(LegalDocumentIfc legalDocument)
    {
        this.legalDocument = legalDocument;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargoIfc#setRegister(oracle.retail.stores.domain.financial.RegisterIfc)
     */
    @Override
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargoIfc#getRegister()
     */
    @Override
    public RegisterIfc getRegister()
    {
        return this.register;
    }
}
