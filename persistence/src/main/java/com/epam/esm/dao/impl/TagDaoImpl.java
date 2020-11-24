package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TagDaoImpl implements TagDao {
    @PersistenceContext
    private EntityManager em;

    private static final String ID = "id";
    private static final String NAME = "name";

    @Override
    public Set<Tag> findAll(int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> root = cq.from(Tag.class);
        cq.select(root);
        TypedQuery<Tag> query = em.createQuery(cq);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        List<Tag> tags = query.getResultList();
        return new HashSet<>(tags);
    }

    @Override
    public Optional<Tag> findById(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> root = cq.from(Tag.class);
        cq.select(root)
                .where(
                        cb.equal(root.get(ID), id)
                );
        TypedQuery<Tag> query = em.createQuery(cq);
        return query.getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Tag> findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> root = cq.from(Tag.class);
        cq.select(root)
                .where(
                        cb.equal(root.get(NAME), name)
                );
        TypedQuery<Tag> query = em.createQuery(cq);
        return query.getResultStream()
                .findFirst();
    }

    @Override
    public Tag save(Tag tag) {
        em.persist(tag);
        em.flush();
        return tag;
    }

    @Override
    public void deleteById(long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Tag> delete = cb.createCriteriaDelete(Tag.class);
        Root<Tag> certificateRoot = delete.from(Tag.class);
        delete.where(
                cb.equal(certificateRoot.get(ID), id)
        );
        em.createQuery(delete)
                .executeUpdate();
    }
}
