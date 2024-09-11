/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/TillPickupCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:28 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 04 2004 18:59:58   DCobb
 * Removed unused imports.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:58:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 23 2003 13:44:18   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.0   Apr 29 2002 15:26:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:44   msg
 * Initial revision.
 * 
 *    Rev 1.2   02 Mar 2002 12:48:04   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 * 
 *    Rev 1.1   21 Nov 2001 14:27:46   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:19:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

// Foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.TillCargo;

//------------------------------------------------------------------------------
/**
    Cargo for the pickup service.     
     
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillPickupCargo extends TillCargo
{
    /**
       Pickup count type.
    **/
    protected int pickupCountType = FinancialCountIfc.COUNT_TYPE_NONE;
    /**
           Pickup count received from counting service
    **/
    protected FinancialTotalsIfc pickupTotals = null;
    /**
           Currency type to pickup
    **/
    protected CurrencyIfc pickupCurrency = null;
    /**
           Currency type to pickup
    **/
    protected String tenderName = DomainGateway.getFactory()
                                               .getTenderTypeMapInstance()
                                               .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH);
    /**
           Currency Country Code
    **/
    protected String tenderNationality = null;

    /**
     *     Nationality descriptor
     */
    protected String nationalityDescriptor = null;

    /**
     *     Nationality Tag Suffix
     */
    public static final String NATIONALITY_SUFFIX = "_Nationality";

    //----------------------------------------------------------------------
    /**
        Returns the pickup count type. <P>
        @return The pickup count type.
    **/
    //----------------------------------------------------------------------
    public int getPickupCountType()
    {                                   // begin getPickupCountType()
        return pickupCountType;
    }                                   // end getPickupCountType()

    //----------------------------------------------------------------------
    /**
        Sets the pickup count type. <P>
        @param  value  The pickup count type.
    **/
    //----------------------------------------------------------------------
    public void setPickupCountType(int value)
    {                                   // begin setPickupCountType()
        pickupCountType = value;
    }                                   // end setPickupCountType()

    //----------------------------------------------------------------------
    /**
        Returns the pickup financial totals. <P>
        @return The pickup financial totals.
    **/

    //----------------------------------------------------------------------
    public FinancialTotalsIfc getPickupTotals()
    {                                   
        return pickupTotals;
    }                                  

    //----------------------------------------------------------------------
    /**
        Sets the pickup financial totals. <P>
        @param pickup financial totals
    **/
    //----------------------------------------------------------------------
    public void setPickupTotals(FinancialTotalsIfc value)
    {             
        pickupTotals = value;
    }            

    //----------------------------------------------------------------------
    /**
        Returns the pickup Currency. <P>
        @return The pickup Currency.
    **/

    //----------------------------------------------------------------------
    public CurrencyIfc getPickupCurrency()
    {
        if (pickupCurrency == null)
        {
            if ((tenderNationality == null) ||
                tenderNationality.equals(DomainGateway.getBaseCurrencyInstance().getCountryCode()))
            {
                pickupCurrency = DomainGateway.getBaseCurrencyInstance();
            }
        }

        return pickupCurrency;
    }

    //----------------------------------------------------------------------
    /**
        Sets the pickup Currency. <P>
        @param value pickup Currency
    **/
    //----------------------------------------------------------------------
    public void setPickupCurrency(CurrencyIfc value)
    {             
        pickupCurrency = value;
    }            

    //----------------------------------------------------------------------
    /**
        Returns the pickup tender name. <P>
        @return the pickup tender name.
    **/

    //----------------------------------------------------------------------
    public String getTenderName()
    {
        return tenderName;
    }                                  

    //----------------------------------------------------------------------
    /**
        Sets the name of pickup tender. <P>
        @param String the name of the pickup tender.
    **/
    //----------------------------------------------------------------------
    public void setTenderName(String value)
    {             
        tenderName = value;
    }            

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int function ID 
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TILL_PICKUP_LOAN;
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
    //----------------------------------------------------------------------
    /**
        Sets the tender nationality. <P>
        @param String the country code.
    **/
    //----------------------------------------------------------------------
    public void setTenderNationality(String value)
    {
        tenderNationality = value;
    }
    //----------------------------------------------------------------------
    /**
        Returns the tender nationality. <P>
        @return the tender nationality.
    **/

    //----------------------------------------------------------------------
    public String getTenderNationality()
    {
        if (tenderNationality == null)
        {
            return DomainGateway.getBaseCurrencyInstance().getCountryCode();
        }
        else
        {
            return tenderNationality;
        }
    }
    //----------------------------------------------------------------------
     /**
         Sets the  nationality descriptor. <P>
         @param String the country code.
     **/
     //----------------------------------------------------------------------
     public void setNationalityDescriptor(String value)
     {
         nationalityDescriptor = value;
     }
     //----------------------------------------------------------------------
     /**
         Returns the nationality descriptor. <P>
         @return the nationality descriptor.
     **/

     //----------------------------------------------------------------------
     public String getNationalityDescriptor()
     {
         return nationalityDescriptor;
     }
}
