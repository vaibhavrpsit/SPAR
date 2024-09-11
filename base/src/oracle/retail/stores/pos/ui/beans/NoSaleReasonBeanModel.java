/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NoSaleReasonBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


//-------------------------------------------------------------------------
/**
This is the bean model that is used by the NoSaleReasonBean.
@see oracle.retail.stores.pos.ui.beans.NoSaleReasonBean
@author  $KW=@(#); $Own=michaelm; $EKW;
@version $KW=@(#); $Ver=rapp.q_2.6.1:2; $EKW;
@deprecated This class has been deprecated in Release 6.0. Use the
{@link ReasonBeanModel} class instead.
**/
//-------------------------------------------------------------------------
public class NoSaleReasonBeanModel extends ReasonBeanModel
{
    protected transient java.beans.PropertyChangeSupport propertyChange;

/**
 * NoSaleReasonBeanModel constructor.
 */
public NoSaleReasonBeanModel() {
    super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
    getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
    if (propertyChange == null) {
        propertyChange = new java.beans.PropertyChangeSupport(this);
    };
    return propertyChange;
}
/**
 * Gets the reasonCodes property (java.util.Vector) value.
 * @return The reasonCodes property value.
 * @see #setReasonCodes
 */
public java.util.Vector getReasonCodes() {
    return super.getReasonCodes();
}
/**
 * Gets the selectedReason property (java.lang.String) value.
 * @return The selectedReason property value.
 * @see #setSelectedReason
 */
public String getSelectedReason() {
    return super.getSelectedReason();
}
/**
 * Gets the selected property (boolean) value.
 * @return The selected property value.
 * @see #setSelected
 */
public boolean isSelected() {
    return super.isSelected();
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
    getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * Sets the reasonCodes property (java.util.Vector) value.
 * @param reasonCodes The new value for the property.
 * @see #getReasonCodes
 */
public void setReasonCodes(java.util.Vector reasonCodes) {
    java.util.Vector oldValue = fieldReasonCodes;
    super.setReasonCodes(reasonCodes);
    firePropertyChange("reasonCodes", oldValue, reasonCodes);
}
/**
 * Sets the selected property (boolean) value.
 * @param selected The new value for the property.
 * @see #getSelected
 */
public void setSelected(boolean selected) {
    boolean oldValue = fieldSelected;
    super.setSelected(selected);
    firePropertyChange("selected", new Boolean(oldValue), new Boolean(selected));
}
/**
 * Sets the selectedReason property (java.lang.String) value.
 * @param selectedReason The new value for the property.
 * @see #getSelectedReason
 */
public void setSelectedReason(String selectedReason) {
    String oldValue = fieldSelectedReason;
    super.setSelectedReasonCode(selectedReason);
    firePropertyChange("selectedReason", oldValue, selectedReason);
}
}
