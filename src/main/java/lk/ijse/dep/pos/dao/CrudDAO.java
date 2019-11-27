package lk.ijse.dep.pos.dao;

import lk.ijse.dep.pos.entity.SuperEntity;

import java.util.List;

public interface CrudDAO<T extends SuperEntity,ID> extends SuperDAO {

    public abstract List<T> findAll() throws Exception;

    public abstract T find(ID id)throws Exception;

    public abstract void save(T entity)throws Exception;

    public abstract void update(T entity)throws Exception;

    public abstract void delete(ID id)throws Exception;

}
