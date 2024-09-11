/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderTypeEnum.java /main/13 2013/10/29 14:06:50 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   10/29/13 - Code extensibility changes for JournalActionEnum and
 *                         TenderTypeEnum
 *    abondala  09/04/13 - initialize collections
 *    sgu       10/12/11 - create house account tender correctly from legacy
 *                         tender line item
 *    sgu       09/08/11 - add house account as a refund tender
 *    ohorne    06/08/11 - added HOUSE_ACCOUNT enum
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/20/11 - Changed TenderType from int constants to a real
 *                         enum.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:58 PM  Robert Pearse
 *
 *   Revision 1.5.2.1  2004/11/15 22:27:36  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/05/21 20:27:59  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.3  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 05 2004 13:46:46   rhafernik
 * log4j changes
 *
 *    Rev 1.0   Nov 04 2003 11:13:20   epd
 * Initial revision.
 *
 *    Rev 1.6   Oct 30 2003 20:35:04   epd
 * added Alternate as valid tender type
 *
 *    Rev 1.5   Oct 25 2003 16:07:10   blj
 * added Money Order Tender
 *
 *    Rev 1.1   Oct 20 2003 16:30:18   epd
 * added definition for Mall Certificate
 *
 *    Rev 1.0   Oct 17 2003 12:33:52   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.context.BeanLocator;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.utility.CardTypeCodesIfc;
import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;


/**
 *    This class enumerates all the tender types.
 *    This class can be extended by configuring new TenderTypeEnums in the 
 *    application context file. The enumerated types are loaded using the Spring bean
 *    "application_TenderTypeHelper". Additional customization of the TenderTypeEnum is
 *    accomplished by extend the TenderTypeHelper class and registering the new class in the
 *    application context xml file. The configuration consists of the name of the enumerated
 *    type and the domain object class that maps to the enumeration.
 *    
 *    To create new constants for custom TenderTypeEnums configured via Spring, declare the new
 *    constants in a class as follows:
 *    
 *    public static final TenderTypeEnum MY_CUSTOM_ENUM = TenderTypeEnum.makeEnumFromString("MyCustomEnum");
 *    
 *    The class where the custom TenderTypeEnum is declared does not need to extend TenderTypeEnum.
 */
public class TenderTypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7309360815538797559L;
    
    /** Log4J Logger */
    protected static final Logger logger = Logger.getLogger(TenderTypeEnum.class);

    /** The internal representation of an enumeration instance */
    private String enumer;

    /** Description */
    private String description;

    /** The RDO tender types that map to an ADO tender type */
    protected Class<?> rdoType;

    /** TenderType for mapping */
    protected TenderType tenderType;


    //-------------------------------------------------------------------
    // Code Extensibility Changes
    //-------------------------------------------------------------------
   
    
    /** Spring loaded TransactionTypeMap */
    protected static TenderTypeHelperIfc typeHelper;
  

    /** The map containing the enumeration instances */
    protected static final HashMap<String, TenderTypeEnum> enumMap = new HashMap<String, TenderTypeEnum>();
    
    // Load the tender type helper during class initialization
    // and call the method to register the configured TenderTypeEnums with 
    // this class.
    static
    {
        try
        {
            typeHelper = (TenderTypeHelperIfc)BeanLocator.getApplicationBean(TenderTypeHelperIfc.TENDER_TYPE_HELPER);
        }
        catch (Throwable eth)
        {
            logger.fatal("Unable to load TenderTypeHelper Bean from Spring", eth);
            throw eth;
        }
        List<TenderTypeEnumParameters> typeParms = typeHelper.getTenderTypeEnumParameters();
        TenderTypeEnum tdrEnum = null;
        for (TenderTypeEnumParameters enumParm : typeParms)
        {
            tdrEnum = new TenderTypeEnum(enumParm.getEnumeration(), enumParm.getRDOType(), enumParm.getTenderType(),enumParm.getDescription());
            enumMap.put(enumParm.getEnumeration(), tdrEnum);
        }
    }
    
    /////////////////////////////////////////////////////////////////
    // Base Product TenderTypeEnum constants
    /////////////////////////////////////////////////////////////////

    // Code Ext refactor gets the instances from the enumType map after
    // map is populated by the TenderTypeHelper
    
    /** CASH */
    public static final TenderTypeEnum CASH             = enumMap.get("Cash");
    
    /** CHECK */
    public static final TenderTypeEnum CHECK            = enumMap.get("Check");
    
    /** COUPON */
    public static final TenderTypeEnum COUPON           = enumMap.get("Coupon");
    
    /** CREDIT */
    public static final TenderTypeEnum CREDIT           = enumMap.get("Credit");

    /** DEBIT */
    public static final TenderTypeEnum DEBIT            = enumMap.get("Debit");
    
    /** GIFT_CARD */
    public static final TenderTypeEnum GIFT_CARD        = enumMap.get("GiftCard");

    /** GIFT_CERT */
    public static final TenderTypeEnum GIFT_CERT        = enumMap.get("GiftCert");

    /** MAIL_CHECK */
    public static final TenderTypeEnum MAIL_CHECK       = enumMap.get("MailCheck");

    /** PURCHASE_ORDER */
    public static final TenderTypeEnum PURCHASE_ORDER   = enumMap.get("PurchaseOrder");
    
    /** STORE_CREDIT */
    public static final TenderTypeEnum STORE_CREDIT     = enumMap.get("StoreCredit");
    
    /** TRAVELERS_CHECK */
    public static final TenderTypeEnum TRAVELERS_CHECK  = enumMap.get("TravCheck");

    /** MALL_CERT */
    public static final TenderTypeEnum MALL_CERT        = enumMap.get("MallCert");

    /** MONEY_ORDER */
    public static final TenderTypeEnum MONEY_ORDER         = enumMap.get("MoneyOrder");

    /** HOUSE_ACCOUNT */
    public static final TenderTypeEnum HOUSE_ACCOUNT        = enumMap.get("HouseAccount");

    /**
     * Alternate requires special handling.  There is no RDO type associated with the Alternate button
     */
    public static final TenderTypeEnum ALTERNATE        = enumMap.get("Alternate");
    
    
    /**
     * Factory method for the helper class to create the TenderTypeEnumeration instances
     * Package level visibility for use by final method of the TenderTypeHelper class
     * @param typeName name of the enumerated type
     * @param rdoClass class of the tender line item the enumerated type maps to
     * @param type TenderType enumeration
     * @param desc description of the TenderTypeEnumeration
     */
    public static final TenderTypeEnum createTenderTypeEnum(String typeName, Class<?> rdoClass, TenderType type, String desc)
    {
        return new TenderTypeEnum(typeName, rdoClass, type, desc);
    }

    
    //----------------------------------------------------------------------
    /**
        TenderTypeEnum constructor
        Creates the TenderTypeEnum instance
        @param tenderName String
        @param rdoType Class
        @param tenderType
        @param desc description of the TenderTypeEnum 
    **/
    //----------------------------------------------------------------------
    protected TenderTypeEnum(String tenderName, Class<?> rdoClass, TenderType tenderType, String description)
    {
        this.enumer = tenderName;
        this.rdoType = rdoClass;
        this.tenderType = tenderType;
        this.description = description;
        //enumMap.put(tenderName, this);
    }

    //----------------------------------------------------------------------
    /**
     * Accessor method for the registered TenderTypeEnums
     * @return collection of registered TenderTypeEnum
     */
    //----------------------------------------------------------------------
    public static final Collection<TenderTypeEnum> getTenderTypeEnums()
    {
        return enumMap.values();
    }
    

    //-------------------------------------------------------------------
    // End Code Extensibility Changes
    //-------------------------------------------------------------------
    

    
    //----------------------------------------------------------------------
    /**
        Factory method to return a TenderTypeEnum instance given an RDO.
        @param rdoObject The RDO object.
        @return An enumerated instance of the tender type.
    **/
    //----------------------------------------------------------------------
    public static TenderTypeEnum makeTenderTypeEnumFromRDO(TenderLineItemIfc rdoObject)
    {
        // The Code extensibility refactor delegates this call to the helper class
        return typeHelper.getTenderTypeEnum(enumMap, rdoObject);
    }

    protected static boolean typeMatchesMyType(Class<?> testType, Class<?> rdoType)
    {
        // test for degenerate case
        if (testType == null)
        {
            return false;
        }

        boolean result = false;
        if (testType.equals(rdoType))
        {
            result = true;
        }
        else
        {
            result = typeMatchesMyType(testType.getSuperclass(), rdoType);
        }
        return result;
    }

    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    //----------------------------------------------------------------------
    /**
        Gets the description
        @return String
    **/
    //----------------------------------------------------------------------
    public String getDescription()
    {
        return description;
    }
    
    public Class<?> getRDOClass()
    {
        return rdoType;
    }

    /** factory method.  May return null */
    public static TenderTypeEnum makeEnumFromString(String enumer)
    {
        return (TenderTypeEnum)enumMap.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return enumMap.get(enumer);
    }

    /**
     * Returns the "real" enum value for this TenderTypeEnum (from the java enum perspective).
     * Note that this class was created befor real enums were available.
     * Note that this class was not refactored/converted since its days are numbered.
     * It will be removed as part of the configurable tenders feature.
     *
     * @return the tender type's "real" enum value (from the java enum perspective)
     */
    public TenderType getTenderType()
    {
        return tenderType;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        boolean isEquals = false;
        
        if ((obj != null)&&(obj instanceof TenderTypeEnum))
        {
            TenderTypeEnum comp = (TenderTypeEnum)obj;
            
            if(!this.enumer.equals(comp.enumer))
            {
                isEquals = false;
            }
            else if (!this.tenderType.equals(comp.tenderType))
            {
                isEquals = false;
            }
            else
            {
    
                if ((this.rdoType == null)&&(comp.rdoType != null))
                {
                    isEquals = false;
                }
                else if ((this.rdoType != null)&&(comp.rdoType == null))
                {
                    isEquals = false;
                }
                else if ((this.rdoType == null)&&(comp.rdoType == null))
                {
                    isEquals = true;
                }
                else
                {
                    isEquals = this.rdoType.equals(comp.rdoType);
                }
        
                
                if (!isEquals)
                {
                    return false;
                }
                
                if ((this.description == null)&&(comp.description == null))
                {
                    isEquals = true;
                }
                else if ((this.description != null)&&(comp.description == null))
                {
                    isEquals = false;
                }
                else if ((this.description != null)&&(comp.description == null))
                {
                    isEquals = false;
                }
                else
                {
                    isEquals = this.description.equals(comp.description);
                }
            }
        }
        
        return isEquals;
    }
   
}
