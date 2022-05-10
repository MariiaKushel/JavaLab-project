package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.entity.User;
import com.epam.esm.dao.entity.User_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User entity) {
        throw new UnsupportedOperationException("Invalid operation \"save\" for User");
    }

    @Override
    public User update(User entity) {
        throw new UnsupportedOperationException("Invalid operation \"update\" for User");
    }

    @Override
    public void delete(User entity) {
        throw new UnsupportedOperationException("Invalid operation \"delete\" for User");
    }

    @Override
    public List<User> findAll(int page, int size) {
        throw new UnsupportedOperationException("Invalid operation \"findAll\" for User");
    }

    @Override
    public Optional<User> findById(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root)
                .where(builder.equal(root.get(User_.id), id));
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Invalid operation \"findAll\" for User");
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root)
                .where(
                        builder.equal(root.get(User_.login), login),
                        builder.equal(root.get(User_.password), password)
                );
        return entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .findFirst();
    }

}
