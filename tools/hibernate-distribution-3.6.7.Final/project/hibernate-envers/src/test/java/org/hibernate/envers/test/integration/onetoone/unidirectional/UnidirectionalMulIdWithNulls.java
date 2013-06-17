package org.hibernate.envers.test.integration.onetoone.unidirectional;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.test.AbstractEntityTest;
import org.hibernate.envers.test.entities.ids.EmbId;
import org.hibernate.envers.test.entities.ids.EmbIdTestEntity;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;

/**
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class UnidirectionalMulIdWithNulls extends AbstractEntityTest {
    private EmbId ei;
    
    public void configure(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(EmbIdTestEntity.class);
        cfg.addAnnotatedClass(UniRefIngMulIdEntity.class);
    }

    @BeforeClass(dependsOnMethods = "init")
    public void initData() {
        ei = new EmbId(1, 2);

        EntityManager em = getEntityManager();
        
        // Revision 1
        EmbIdTestEntity eite = new EmbIdTestEntity(ei, "data");
        UniRefIngMulIdEntity notNullRef = new UniRefIngMulIdEntity(1, "data 1", eite);
        UniRefIngMulIdEntity nullRef = new UniRefIngMulIdEntity(2, "data 2", null);

        em.getTransaction().begin();
        em.persist(eite);
        em.persist(notNullRef);
        em.persist(nullRef);
        em.getTransaction().commit();
    }

    @Test
    public void testNullReference() {
        UniRefIngMulIdEntity nullRef = getAuditReader().find(UniRefIngMulIdEntity.class, 2, 1);
        assert nullRef.getReference() == null;
    }

    @Test
    public void testNotNullReference() {
        EmbIdTestEntity eite = getAuditReader().find(EmbIdTestEntity.class, ei, 1);
        UniRefIngMulIdEntity notNullRef = getAuditReader().find(UniRefIngMulIdEntity.class, 1, 1);
        assert notNullRef.getReference() != null;
        assert notNullRef.getReference().equals(eite);
    }
}
