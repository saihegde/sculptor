/*************************************************************************************
 *
 * Generated on Mon Aug 29 17:53:16 CEST 2011 by XSpray CreateReferenceAsListFeature.xtend
 *
 * This file contains generated and should not be changed.
 * Use the extension point class (the direct subclass of this class) to add manual code
 *
 *************************************************************************************/
package org.xspray.examples.mod4j.features;

import BusinessDomainDsl.BusinessClass;
import BusinessDomainDsl.EnumerationProperty;
import BusinessDomainDsl.BusinessDomainDslFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.xspray.runtime.containers.SampleUtil;

public class mod4jCreateBusinessClasspropertiesEnumerationPropertyFeatureBase extends AbstractCreateFeature {

    private static final String TITLE = "Create EnumerationProperty";

    private static final String USER_QUESTION = "Enter new EnumerationProperty dataType";

    public mod4jCreateBusinessClasspropertiesEnumerationPropertyFeatureBase(IFeatureProvider fp) {
        // set name and description of the creation feature
        super(fp, "EnumerationProperty", "Create EnumerationProperty");
    }

    public boolean canCreate(ICreateContext context) {
        Shape target = context.getTargetContainer();
        Object domainObject = getBusinessObjectForPictogramElement(target);
        return domainObject instanceof BusinessClass;
    }

    public Object[] create(ICreateContext context) {
        // ask user for EnumerationProperty name
        String newName = SampleUtil.askString(TITLE, USER_QUESTION, "");
        if (newName == null || newName.trim().length() == 0) {
            return EMPTY;
        }

        Shape target = context.getTargetContainer();
        Object domainObject = getBusinessObjectForPictogramElement(target);
        BusinessClass owner = (BusinessClass) domainObject;

        // create EnumerationProperty
        EnumerationProperty newDomainObject = BusinessDomainDslFactory.eINSTANCE.createEnumerationProperty();
        newDomainObject.setDataType(newName);
        owner.getProperties().add(newDomainObject);

        // do the add
        addGraphicalRepresentation(context, newDomainObject);
        // return newly created business object(s)
        return new Object[] { newDomainObject };
    }

    @Override
    public boolean hasDoneChanges() {
        return false;
    }

    @Override
    public boolean canUndo(IContext context) {
        return false;
    }

}
