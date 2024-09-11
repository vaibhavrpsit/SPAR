/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/DataReplicationSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:57 mszekely Exp $
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
 *    1    360Commerce 1.0         11/9/2006 6:42:34 PM   Jack G. Swan    
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;

import oracle.retail.stores.xmlreplication.extractor.EntityReaderCatalogIfc;
import oracle.retail.stores.xmlreplication.extractor.EntitySearchIfc;

//---------------------------------------------------------------------
/**
    This class holds the objects required to lookup a Data Replication 
    entity.
    @version@ $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//---------------------------------------------------------------------
public class DataReplicationSearchCriteria implements Serializable
{
	/** Contains the instructions on which tables to read. */ 
	protected EntityReaderCatalogIfc catalog = null; 
	/** Contains the transaction primary key information. */ 
	protected EntitySearchIfc entitySearch = null;
	
	//---------------------------------------------------------------------
	/**
	 * The default constructor.
	**/
	//---------------------------------------------------------------------
	public DataReplicationSearchCriteria()
	{
	}
	
	//---------------------------------------------------------------------
	/**
	 * Gets the catalog
	 * @return EntityReaderCatalogIfc
	**/
	//---------------------------------------------------------------------
	public EntityReaderCatalogIfc getCatalog() 
	{
		return catalog;
	}
	//---------------------------------------------------------------------
	/**
	 * Sets the catalog
	 * @parm EntityReaderCatalogIfc
	**/
	//---------------------------------------------------------------------
	public void setCatalog(EntityReaderCatalogIfc catalog) 
	{
		this.catalog = catalog;
	}

	//---------------------------------------------------------------------
	/**
	 * Gets the EntitySearchIfc
	 * @return EntitySearchIfc
	**/
	//---------------------------------------------------------------------
	public EntitySearchIfc getEntitySearch() 
	{
		return entitySearch;
	}
	//---------------------------------------------------------------------
	/**
	 * Sets the EntitySearchIfc
	 * @parm EntitySearchIfc
	**/
	//---------------------------------------------------------------------
	public void setEntitySearch(EntitySearchIfc entitySearch) 
	{
		this.entitySearch = entitySearch;
	}
}
