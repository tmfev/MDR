/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.envers.entities.mapper.relation.query;

import java.util.Collections;

import org.hibernate.Query;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.configuration.AuditEntitiesConfiguration;
import org.hibernate.envers.configuration.GlobalConfiguration;
import org.hibernate.envers.entities.mapper.id.QueryParameterData;
import org.hibernate.envers.entities.mapper.relation.MiddleComponentData;
import org.hibernate.envers.entities.mapper.relation.MiddleIdData;
import org.hibernate.envers.reader.AuditReaderImplementor;
import org.hibernate.envers.strategy.AuditStrategy;
import org.hibernate.envers.tools.query.Parameters;
import org.hibernate.envers.tools.query.QueryBuilder;

/**
 * Selects data from a relation middle-table and a related versions entity.
 * @author Adam Warski (adam at warski dot org)
 */
public final class TwoEntityQueryGenerator implements RelationQueryGenerator {
    private final String queryString;
    private final MiddleIdData referencingIdData;

    public TwoEntityQueryGenerator(GlobalConfiguration globalCfg,
                                   AuditEntitiesConfiguration verEntCfg,
                                   AuditStrategy auditStrategy,
                                   String versionsMiddleEntityName,
                                   MiddleIdData referencingIdData,
                                   MiddleIdData referencedIdData,
                                   MiddleComponentData... componentDatas) {
        this.referencingIdData = referencingIdData;

        /*
         * The query that we need to create:
         *   SELECT new list(ee, e) FROM versionsReferencedEntity e, middleEntity ee
         *   WHERE
         * (entities referenced by the middle table; id_ref_ed = id of the referenced entity)         
         *     ee.id_ref_ed = e.id_ref_ed AND
         * (only entities referenced by the association; id_ref_ing = id of the referencing entity)
         *     ee.id_ref_ing = :id_ref_ing AND
         *     
         * (selecting e entities at revision :revision)
         *   --> for DefaultAuditStrategy:
         *     e.revision = (SELECT max(e2.revision) FROM versionsReferencedEntity e2
         *       WHERE e2.revision <= :revision AND e2.id = e.id) 
         *     
         *   --> for ValidityAuditStrategy:
         *     e.revision <= :revision and (e.endRevision > :revision or e.endRevision is null)
         *     
         *     AND
         *     
          * (the association at revision :revision)
         *   --> for DefaultAuditStrategy:
         *     ee.revision = (SELECT max(ee2.revision) FROM middleEntity ee2
         *       WHERE ee2.revision <= :revision AND ee2.originalId.* = ee.originalId.*)
         *       
         *   --> for ValidityAuditStrategy:
         *     ee.revision <= :revision and (ee.endRevision > :revision or ee.endRevision is null)
         *     
         * (only non-deleted entities and associations)
         *     ee.revision_type != DEL AND
         *     e.revision_type != DEL
         */
        String revisionPropertyPath = verEntCfg.getRevisionNumberPath();
        String originalIdPropertyName = verEntCfg.getOriginalIdPropName();

        String eeOriginalIdPropertyPath = "ee." + originalIdPropertyName;

        // SELECT new list(ee) FROM middleEntity ee
        QueryBuilder qb = new QueryBuilder(versionsMiddleEntityName, "ee");
        qb.addFrom(referencedIdData.getAuditEntityName(), "e");
        qb.addProjection("new list", "ee, e", false, false);
        // WHERE
        Parameters rootParameters = qb.getRootParameters();
        // ee.id_ref_ed = e.id_ref_ed
        referencedIdData.getPrefixedMapper().addIdsEqualToQuery(rootParameters, eeOriginalIdPropertyPath,
                referencedIdData.getOriginalMapper(), "e." + originalIdPropertyName);
        // ee.originalId.id_ref_ing = :id_ref_ing
        referencingIdData.getPrefixedMapper().addNamedIdEqualsToQuery(rootParameters, originalIdPropertyName, true);

        // (selecting e entities at revision :revision)
        // --> based on auditStrategy (see above)
        auditStrategy.addEntityAtRevisionRestriction(globalCfg, qb, "e." + revisionPropertyPath,
        		"e." + verEntCfg.getRevisionEndFieldName(), false,
        		referencedIdData, revisionPropertyPath, originalIdPropertyName, "e", "e2");

        // (with ee association at revision :revision)
        // --> based on auditStrategy (see above)
        auditStrategy.addAssociationAtRevisionRestriction(qb, revisionPropertyPath,
        		verEntCfg.getRevisionEndFieldName(), true, referencingIdData, versionsMiddleEntityName,
        		eeOriginalIdPropertyPath, revisionPropertyPath, originalIdPropertyName, componentDatas);

        // ee.revision_type != DEL
        rootParameters.addWhereWithNamedParam(verEntCfg.getRevisionTypePropName(), "!=", "delrevisiontype");
        // e.revision_type != DEL
        rootParameters.addWhereWithNamedParam("e." + verEntCfg.getRevisionTypePropName(), false, "!=", "delrevisiontype");

        StringBuilder sb = new StringBuilder();
        qb.build(sb, Collections.<String, Object>emptyMap());
        queryString = sb.toString();
    }

    public Query getQuery(AuditReaderImplementor versionsReader, Object primaryKey, Number revision) {
        Query query = versionsReader.getSession().createQuery(queryString);
        query.setParameter("revision", revision);
        query.setParameter("delrevisiontype", RevisionType.DEL);
        for (QueryParameterData paramData: referencingIdData.getPrefixedMapper().mapToQueryParametersFromId(primaryKey)) {
            paramData.setParameterValue(query);
        }

        return query;
    }
}
