package br.ufrgs.inf.pet.dinoapi.websocket.service;
import br.ufrgs.inf.pet.dinoapi.entity.auth.Auth;
import br.ufrgs.inf.pet.dinoapi.entity.user.User;
import br.ufrgs.inf.pet.dinoapi.model.synchronizable.SynchronizableDataLocalIdModel;
import br.ufrgs.inf.pet.dinoapi.service.auth.AuthServiceImpl;
import br.ufrgs.inf.pet.dinoapi.websocket.enumerable.WebSocketDestinationsEnum;
import br.ufrgs.inf.pet.dinoapi.websocket.model.SynchronizableWSDeleteModel;
import br.ufrgs.inf.pet.dinoapi.websocket.model.SynchronizableWSGenericModel;
import br.ufrgs.inf.pet.dinoapi.websocket.model.SynchronizableWSUpdateModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.io.Serializable;
import java.util.List;

public abstract class SynchronizableMessageService<
        ID extends Comparable<ID> & Serializable,
        DATA_MODEL extends SynchronizableDataLocalIdModel<ID>> {

    protected static final String webSocketUpdateURL = "/update/";
    protected static final String webSocketDeleteURL = "/delete/";

    protected final SimpMessagingTemplate simpMessagingTemplate;

    protected final AuthServiceImpl authService;


    public SynchronizableMessageService(SimpMessagingTemplate simpMessagingTemplate,
                                        AuthServiceImpl authService) {
        this.authService = authService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendUpdateMessage(List<DATA_MODEL> data, WebSocketDestinationsEnum pathEnum, Auth auth) {
        if (!data.isEmpty()) {
            this.sendModel(this.generateUpdateModel(data), pathEnum.getValue() + webSocketUpdateURL, auth);
        }
    }

    public void sendDeleteMessage(List<ID> data, WebSocketDestinationsEnum pathEnum, Auth auth) {
        if(!data.isEmpty()) {
            this.sendModel(this.generateDeleteModel(data), pathEnum.getValue() + webSocketDeleteURL, auth);
        }
    }

    private SynchronizableWSGenericModel<DATA_MODEL> generateUpdateModel(List<DATA_MODEL> data) {
        final SynchronizableWSUpdateModel<ID, DATA_MODEL> updateModel = new SynchronizableWSUpdateModel<>();
        updateModel.setData(data);

        return updateModel;
    }

    private SynchronizableWSGenericModel<ID> generateDeleteModel(List<ID> data) {
        final SynchronizableWSDeleteModel<ID> deleteModel = new SynchronizableWSDeleteModel<>();
        deleteModel.setData(data);

        return deleteModel;
    }

    protected abstract <TYPE> void sendModel(SynchronizableWSGenericModel<TYPE> data, String url, Auth auth);

    protected <TYPE> void send(SynchronizableWSGenericModel<TYPE> data, String url, List<String> webSocketTokens) {
        final String dest = this.generateQueueDest(url);
        webSocketTokens.forEach(token ->
                this.simpMessagingTemplate.convertAndSendToUser(token, dest, data)
        );
    }

    protected String generateQueueDest(String url) {
        return "/queue/" + url;
    }
}
