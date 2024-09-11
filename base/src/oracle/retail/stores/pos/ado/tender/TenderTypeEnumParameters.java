package oracle.retail.stores.pos.ado.tender;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;

/**
 * TenderTypeEnumParameters is a utility data object to transfer TenderTYpeEnum  
 * between the TenderTypeHelper implementation and the TenderTypeEnum class to provide
 * an extensibility mechanism
 *
 * @author rhaight
 *
 */
public class TenderTypeEnumParameters {

	 private String enumer;

	    /** Description */
	    private String description;

	    /** The RDO tender types that map to an ADO tender type */
	    protected Class<?> rdoType;

	    /** TenderType for mapping */
	    protected TenderType tenderType;

	    
        /**
         * Constructor for the TenderTYpeEnumParameters. This constructor 
         * creates an immutable object
         * @param enumer
         * @param rdoType
         * @param tenderType
         * @param description
         */
	    public TenderTypeEnumParameters(String enumer, Class<?> rdoType, TenderType tenderType, String description)
	    {
	    	this.enumer = enumer;
	    	this.description = description;
	    	this.rdoType = rdoType;
	    	this.tenderType = tenderType;
	    }
	    
	    /**
	     * 
	     * @return enumeration name
	     */
	    public String getEnumeration()
	    {
	    	return enumer;
	    }
	    
	    /**
	     * 
	     * @return description of tender type enumeration
	     */
	    public String getDescription()
	    {
	    	return description;
	    }
	    
	    /**
	     * 
	     * @return class of the retail domain object
	     */
	    public Class<?> getRDOType()
	    {
	    	return rdoType;
	    }
	    
	    /**
	     * 
	     * @return TenderType of the enumeration
	     */
	    public TenderType getTenderType()
	    {
	    	return tenderType;
	    }
}
