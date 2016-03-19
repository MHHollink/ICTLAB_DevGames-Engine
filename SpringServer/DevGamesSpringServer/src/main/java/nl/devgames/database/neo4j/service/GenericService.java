package nl.devgames.database.neo4j.service;

import nl.devgames.database.neo4j.factory.Neo4JSessionFactory;
import nl.devgames.model.AbsModel;
import org.neo4j.ogm.session.Session;

public abstract class GenericService<T> implements Service<T> {

    private static final int DEPTH_LIST = 0;
    private static final int DEPTH_ENTITY = 1;
    private Session session = Neo4JSessionFactory.getInstance().getNeo4jSession();

    @Override
    public Iterable<T> findAll() {
        return session.loadAll(getModelType(), DEPTH_LIST);
    }

    @Override
    public T find(Long id) {
        return session.load(getModelType(), id, DEPTH_ENTITY);
    }

    @Override
    public void delete(Long id) {
        session.delete(session.load(getModelType(), id));
    }

    @Override
    public T createOrUpdate(T model) {
        session.save(model, DEPTH_ENTITY);
        return find(((AbsModel) model).getId());
    }

    public abstract Class<T> getModelType();
}