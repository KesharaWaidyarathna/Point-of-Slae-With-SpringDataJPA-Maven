package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.SuperEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class CrudDAOImpl<T extends SuperEntity, ID extends Serializable> implements CrudDAO<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;
    private Class<T> entity;

    public CrudDAOImpl() {

        entity=(Class<T>)(((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
    }


    @Override
    public List<T> findAll() throws Exception {
        return entityManager.createQuery("SELECT c FROM "+entity.getName()+ " c").getResultList();
    }

    @Override
    public T find(ID id) throws Exception {
        return entityManager.find(entity,id);
    }

    @Override
    public void save(T entity) throws Exception {
         entityManager.persist(entity);
    }

    @Override
    public void update(T entity) throws Exception {
        entityManager.merge(entity);
    }

    @Override
    public void delete(ID id) throws Exception {
        entityManager.remove(entityManager.getReference(entity,id));
    }

}
