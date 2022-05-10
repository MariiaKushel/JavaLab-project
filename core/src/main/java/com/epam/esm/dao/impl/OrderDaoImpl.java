package com.epam.esm.dao.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.entity.Order;
import com.epam.esm.dao.entity.Order_;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDaoImpl implements OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Order save(Order entity) {
        return entityManager.merge(entity);
    }

    @Override
    public Order update(Order entity) {
        throw new UnsupportedOperationException("Invalid operation \"update\" for Order");
    }

    @Override
    public void delete(Order entity) {
        throw new UnsupportedOperationException("Invalid operation \"delete\" for Order");
    }

    @Override
    public List<Order> findAll(int page, int size) {
        throw new UnsupportedOperationException("Invalid operation \"findAll\" for Order");
    }

    @Override
    public Optional<Order> findById(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.select(root)
                .where(builder.equal(root.get(Order_.id), id));
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Invalid operation \"findAll\" for Order");
    }

    @Override
    public List<Order> findByUser(Long userId, int page, int size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.select(root)
                .where(builder.equal(root.get(Order_.user), userId));
        return entityManager.createQuery(criteria)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public Optional<Order> findByIdAndByUser(Long orderId, Long userId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.select(root)
                .where(
                        builder.equal(root.get(Order_.user), userId),
                        builder.equal(root.get(Order_.id), orderId)
                );
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public long countByUser(Long userId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Order> root = criteria.from(Order.class);
        criteria.select(builder.count(root.get(Order_.id)))
                .where(builder.equal(root.get(Order_.user), userId));
        return entityManager.createQuery(criteria)
                .getSingleResult();
    }
}
