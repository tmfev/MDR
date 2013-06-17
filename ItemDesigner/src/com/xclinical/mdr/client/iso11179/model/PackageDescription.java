package com.xclinical.mdr.client.iso11179.model;



/**
 * The entities and their relations:
 * 
 * <pre>
 * {@link Assertion}
 * 		{@link Assertion#getTerms()}
 * {@link AssertionEnd}
 * {@link Characteristic}
 * 		{@link Characteristic#getConcepts()}
 * {@link Concept}
 * 		{@link Concept#getAssertions()}
 * {@link ConceptSystem}
 * 		{@link ConceptSystem#getIncludedAssertions()}
 * 		{@link ConceptSystem#getMembers()}
 * {@link ConceptualDomain}
 * 		{@link ConceptualDomain#getUsages()}
 * 		{@link ConceptualDomain#getRepresentations()}
 * 		{@link ConceptualDomain#getMembers()}
 * {@link Context}
 * 		{@link Context#getRelevantItems()}
 * {@link DataElement}
 * {@link DataElementConcept}
 * 		{@link DataElementConcept#getConceptualDomains()}
 * 		{@link DataElementConcept#getRepresentations()}
 * {@link DataType}
 * {@link Definition}
 * 		{@link Definition#getScopes()}
 * {@link DesignatableItem}
 * 		{@link DesignatableItem#getDesignations()}
 * 		{@link DesignatableItem#getDefinitions()}
 * {@link Designation}
 * 		{@link Designation#getScopes()}
 * {@link Dimensionality}
 * 		{@link Dimensionality#getApplicableUnits()}
 * {@link Document}
 * {@link IdentifiedItem}
 * {@link LanguageIdentification}
 * {@link Link}
 * {@link LinkEnd}
 * 		{@link LinkEnd#getEndRoles()}
 * {@link Namespace}
 * {@link ObjectClass}
 * 		{@link ObjectClass#getConcepts()}
 * {@link PermissibleValue}
 * {@link ReferenceDocument}
 * {@link Relation}
 * 		{@link Relation#getRoles()}
 * 		{@link Relation#getLinks()}
 * {@link RelationRole}
 * {@link ScopedIdentifier}
 * {@link Slot}
 * {@link UnitOfMeasure}
 * 		{@link UnitOfMeasure#getDimensionalities()}
 * {@link ValueDomain}
 * 		{@link ValueDomain#getMembers()}
 * {@link ValueMeaning}
 * 		{@link ValueMeaning#getRepresentations()}
 * 
 * {@link SimpleDataElementCommand}
 * {@link SimpleDataTypeCommand}
 * {@link SaveEditableItemCommand}
 * {@link EditableUnitOfMeasure}
 * {@link SaveValueDomainCommand}
 * 		{@link SaveValueDomainCommand#getCodeList()}
 * 
 * 58 Elements
 * </pre>  
 */
interface PackageDescription {

}
