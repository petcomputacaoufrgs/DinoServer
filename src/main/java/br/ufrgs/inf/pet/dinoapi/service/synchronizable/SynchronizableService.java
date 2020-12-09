package br.ufrgs.inf.pet.dinoapi.service.synchronizable;

import br.ufrgs.inf.pet.dinoapi.entity.synchronizable.SynchronizableEntity;
import br.ufrgs.inf.pet.dinoapi.entity.user.User;
import br.ufrgs.inf.pet.dinoapi.exception.ConvertModelToEntityException;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.*;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.request.SynchronizableDeleteAllListModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.request.SynchronizableDeleteModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.request.SynchronizableGetModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.request.SynchronizableSaveAllListModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.response.SynchronizableGenericResponseModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.response.SynchronizableListDataResponseModel;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.response.SynchronizableDataResponseModel;
import br.ufrgs.inf.pet.dinoapi.websocket.enumerable.WebSocketDestinationsEnum;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Base service with get, getAll, save/update and delete for synchronizable entity
 *
 * @param <ENTITY> Synchronizable entity
 * @param <ID> Id type of synchronizable entity
 * @param <DATA_MODEL> Data model of synchronizable entity
 */
public interface SynchronizableService<ENTITY extends SynchronizableEntity<ID>,
        ID extends Comparable<ID> & Serializable, DATA_MODEL extends SynchronizableDataModel<ID>> {

    /**
     * Create a complete data model ({@link DATA_MODEL}) based in an entity ({@link ENTITY})
     * @exception NullPointerException service will throws this exception if this method returns null
     * @param entity base entity
     * @return data model
     */
    DATA_MODEL convertEntityToModel(ENTITY entity);

    /**
     * Create a new entity ({@link ENTITY}) based in a data model ({@link DATA_MODEL})
     * @exception NullPointerException service will throws this exception if this method returns null
     * @param model data model
     * @return entity
     */
    ENTITY convertModelToEntity(DATA_MODEL model, User user) throws ConvertModelToEntityException;

    /**
     * Update entity's ({@link ENTITY}) attributes based in a data model ({@link DATA_MODEL})
     * @param entity entity
     * @param model data model
     */
    void updateEntity(ENTITY entity, DATA_MODEL model) throws ConvertModelToEntityException;

    /**
     * Get entity from database using userId for security validation (only takes data that the user has access)
     * @param id entity's id
     * @param user current user
     * @return database entity if valid params or null
     */
    Optional<ENTITY> getEntityByIdAndUser(ID id, User user);

    /**
     * Get entities from database using userId for security validation (only takes data that the user has access)
     * @param user current user
     * @return list of database entities (can be an empty list)
     */
    List<ENTITY> getEntitiesByUserId(User user);

    /**
     * Get entities from database using a list of ids and a userId for security validation (only takes data that the user has access)
     * @param ids list of entity ids
     * @param user current user
     * @return list of database entities (can be an empty list)
     */
    List<ENTITY> getEntitiesByIdsAndUserId(List<ID> ids, User user);

    /**
     * Get WebSocket destination for update entity
     * @return WebSocketDestinationEnum wuth update
     */
    WebSocketDestinationsEnum getUpdateWebsocketDestination();

    /**
     * Get WebSocket destination for delete entity
     * @return WebSocketDestinationEnum wuth update
     */
    WebSocketDestinationsEnum getDeleteWebsocketDestination();

    /**
     * Define if entity should be deleted
     * @param entity base entity
     */
    boolean shouldDelete(ENTITY entity, SynchronizableDeleteModel<ID> model);

    /**
     * Implements get method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableDataResponseModel<ID, DATA_MODEL>> get(SynchronizableGetModel<ID> model);

    /**
     * Implements save method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableDataResponseModel<ID, DATA_MODEL>> save(DATA_MODEL model);

    /**
     * Implements delete method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableDataResponseModel<ID, DATA_MODEL>> delete(SynchronizableDeleteModel<ID> model);

    /**
     * Implements getAll method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableListDataResponseModel<ID, DATA_MODEL>> getAll();

    /**
     * Implements saveAll method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableGenericResponseModel>
    saveAll(SynchronizableSaveAllListModel<ID, DATA_MODEL> model);

    /**
     * Implements deleteAll method of {@link br.ufrgs.inf.pet.dinoapi.controller.synchronizable.SynchronizableController}
     */
    ResponseEntity<SynchronizableGenericResponseModel>
    deleteAll(SynchronizableDeleteAllListModel<ID> model);

}