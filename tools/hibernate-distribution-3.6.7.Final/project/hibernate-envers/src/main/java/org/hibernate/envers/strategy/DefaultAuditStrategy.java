package org.hibernate.envers.strategy;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
import org.hibernate.envers.entities.mapper.relation.MiddleComponentData;
import org.hibernate.envers.entities.mapper.relation.MiddleIdData;
import org.hibernate.envers.tools.query.Parameters;
import org.hibernate.envers.tools.query.QueryBuilder;

/**
 * Default strategy is to simply persist the audit data.
 *
 * @author Adam Warski
 * @author Stephanie Pau
 */
public class DefaultAuditStrategy implements AuditStrategy {
    public void perform(Session session, String entityName, AuditConfiguration auditCfg, Serializable id, Object data,
                        Object revision) {
        session.save(auditCfg.getAuditEntCfg().getAuditEntityName(entityName), data);
    }

    public void performCollectionChange(Session session, AuditConfiguration auditCfg,
                                        PersistentCollectionChangeData persistentCollectionChangeData, Object revision) {
        session.save(persistentCollectionChangeData.getEntityName(), persistentCollectionChangeData.getData());
    }

    
	public void addEntityAtRevisionRestriction(GlobalConfiguration globalCfg, QueryBuilder rootQueryBuilder, String revisionProperty,
			String revisionEndProperty, boolean addAlias, MiddleIdData idData, String revisionPropertyPath, 
			String originalIdPropertyName, String alias1, String alias2) {
		Parameters rootParameters = rootQueryBuilder.getRootParameters();
		
		// create a subquery builder
        // SELECT max(e.revision) FROM versionsReferencedEntity e2
        QueryBuilder maxERevQb = rootQueryBuilder.newSubQueryBuilder(idData.getAuditEntityName(), alias2);
        maxERevQb.addProjection("max", revisionPropertyPath, false);
        // WHERE
        Parameters maxERevQbParameters = maxERevQb.getRootParameters();
        // e2.revision <= :revision
        maxERevQbParameters.addWhereWithNamedParam(revisionPropertyPath, "<=", "revision");
        // e2.id_ref_ed = e.id_ref_ed
        idData.getOriginalMapper().addIdsEqualToQuery(maxERevQbParameters,
                alias1 + "." + originalIdPropertyName, alias2 +"." + originalIdPropertyName);
		
		// add subquery to rootParameters
        String subqueryOperator = globalCfg.getCorrelatedSubqueryOperator();
		rootParameters.addWhere(revisionProperty, addAlias, subqueryOperator, maxERevQb);
	}

	public void addAssociationAtRevisionRestriction(QueryBuilder rootQueryBuilder,  String revisionProperty, 
	          String revisionEndProperty, boolean addAlias, MiddleIdData referencingIdData, String versionsMiddleEntityName,
	          String eeOriginalIdPropertyPath, String revisionPropertyPath,
	          String originalIdPropertyName, MiddleComponentData... componentDatas) {
		Parameters rootParameters = rootQueryBuilder.getRootParameters();

    	// SELECT max(ee2.revision) FROM middleEntity ee2
        QueryBuilder maxEeRevQb = rootQueryBuilder.newSubQueryBuilder(versionsMiddleEntityName, "ee2");
        maxEeRevQb.addProjection("max", revisionPropertyPath, false);
        // WHERE
        Parameters maxEeRevQbParameters = maxEeRevQb.getRootParameters();
        // ee2.revision <= :revision
        maxEeRevQbParameters.addWhereWithNamedParam(revisionPropertyPath, "<=", "revision");
        // ee2.originalId.* = ee.originalId.*
        String ee2OriginalIdPropertyPath = "ee2." + originalIdPropertyName;
        referencingIdData.getPrefixedMapper().addIdsEqualToQuery(maxEeRevQbParameters, eeOriginalIdPropertyPath, ee2OriginalIdPropertyPath);
        for (MiddleComponentData componentData : componentDatas) {
            componentData.getComponentMapper().addMiddleEqualToQuery(maxEeRevQbParameters, eeOriginalIdPropertyPath, ee2OriginalIdPropertyPath);
        }

		// add subquery to rootParameters
        rootParameters.addWhere(revisionProperty, addAlias, "=", maxEeRevQb);
	}

}
