package br.ufrgs.inf.pet.dinoapi.model.synchronizable;

import br.ufrgs.inf.pet.dinoapi.entity.synchronizable.SynchronizableEntity;

/**
 * Response model for synchronizable entity
 */

public final class SynchronizableResponseModel<ENTITY extends SynchronizableEntity<ID>, ID, DATA_MODEL extends SynchronizableDataModel<ENTITY, ID>> {
    private boolean success;
    private String error;
    private DATA_MODEL data;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DATA_MODEL getData() {
        return data;
    }

    public void setData(DATA_MODEL data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }
}
