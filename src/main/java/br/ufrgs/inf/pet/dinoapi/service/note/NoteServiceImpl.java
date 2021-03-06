package br.ufrgs.inf.pet.dinoapi.service.note;

import br.ufrgs.inf.pet.dinoapi.constants.NoteConstants;
import br.ufrgs.inf.pet.dinoapi.entity.auth.Auth;
import br.ufrgs.inf.pet.dinoapi.entity.note.Note;
import br.ufrgs.inf.pet.dinoapi.entity.note.NoteColumn;
import br.ufrgs.inf.pet.dinoapi.exception.synchronizable.AuthNullException;
import br.ufrgs.inf.pet.dinoapi.exception.synchronizable.ConvertModelToEntityException;
import br.ufrgs.inf.pet.dinoapi.model.note.NoteDataModel;
import br.ufrgs.inf.pet.dinoapi.repository.note.NoteRepository;
import br.ufrgs.inf.pet.dinoapi.service.auth.AuthServiceImpl;
import br.ufrgs.inf.pet.dinoapi.service.clock.ClockServiceImpl;
import br.ufrgs.inf.pet.dinoapi.service.log_error.LogAPIErrorServiceImpl;
import br.ufrgs.inf.pet.dinoapi.service.synchronizable.SynchronizableServiceImpl;
import br.ufrgs.inf.pet.dinoapi.websocket.enumerable.WebSocketDestinationsEnum;
import br.ufrgs.inf.pet.dinoapi.websocket.service.SynchronizableQueueMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl extends SynchronizableServiceImpl<Note, Long, NoteDataModel, NoteRepository> {

    private final NoteColumnServiceImpl noteColumnService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, AuthServiceImpl authService, NoteColumnServiceImpl noteColumnService,
                           SynchronizableQueueMessageService<Long, NoteDataModel> synchronizableQueueMessageService,
                           ClockServiceImpl clockService, LogAPIErrorServiceImpl logAPIErrorService) {
        super(noteRepository, authService, clockService, synchronizableQueueMessageService, logAPIErrorService);
        this.noteColumnService = noteColumnService;
    }

    @Override
    public NoteDataModel convertEntityToModel(Note entity) {
        final NoteDataModel noteDataModel = new NoteDataModel();
        noteDataModel.setOrder(entity.getOrder());
        noteDataModel.setQuestion(entity.getQuestion());
        noteDataModel.setAnswer(entity.getAnswer());
        noteDataModel.setColumnId(entity.getNoteColumn().getId());
        noteDataModel.setTags(entity.getTags());

        return noteDataModel;
    }

    @Override
    public Note convertModelToEntity(NoteDataModel model, Auth auth) throws ConvertModelToEntityException, AuthNullException {
        if (auth != null) {
            final Optional<NoteColumn> noteColumn = noteColumnService.findEntityByIdThatUserCanRead(model.getColumnId(), auth);
            if (noteColumn.isPresent()) {
                final Note note = new Note();
                note.setNoteColumn(noteColumn.get());
                note.setQuestion(model.getQuestion());
                note.setOrder(model.getOrder());
                note.setTags(model.getTags());
                note.setAnswer(model.getAnswer());

                return note;
            }

            throw new ConvertModelToEntityException(NoteConstants.INVALID_COLUMN);
        }

        throw new AuthNullException();
    }

    @Override
    public void updateEntity(Note entity, NoteDataModel model, Auth auth) throws ConvertModelToEntityException, AuthNullException {
        if (auth != null) {
            if (!entity.getNoteColumn().getId().equals(model.getColumnId())) {
                final Optional<NoteColumn> noteColumn =
                        noteColumnService.findEntityByIdThatUserCanRead(model.getColumnId(), auth);

                if (noteColumn.isPresent()) {
                    entity.setNoteColumn(noteColumn.get());
                } else {
                    throw new ConvertModelToEntityException(NoteConstants.INVALID_COLUMN);
                }
            }

            entity.setAnswer(model.getAnswer());
            entity.setTags(model.getTags());
            entity.setOrder(model.getOrder());
            entity.setQuestion(model.getQuestion());
        } else {
            throw new AuthNullException();
        }
    }

    @Override
    public Optional<Note> findEntityByIdThatUserCanRead(Long id, Auth auth) throws AuthNullException {
        return this.findByIdAnUser(id, auth);
    }

    @Override
    public Optional<Note> findEntityByIdThatUserCanEdit(Long id, Auth auth) throws AuthNullException {
        return this.findByIdAnUser(id, auth);
    }

    private Optional<Note> findByIdAnUser(Long id, Auth auth) throws AuthNullException {
        if (auth == null) {
            throw new AuthNullException();
        }
        return this.repository.findByIdAndUserId(id, auth.getUser().getId());
    }

    @Override
    public List<Note> findEntitiesThatUserCanRead(Auth auth) throws AuthNullException {
        if (auth == null) {
            throw new AuthNullException();
        }
        return this.repository.findAllByUserId(auth.getUser().getId());
    }

    @Override
    public List<Note> findEntitiesByIdThatUserCanEdit(List<Long> ids, Auth auth) throws AuthNullException {
        if (auth == null) {
            throw new AuthNullException();
        }
        return this.repository.findAllByIdsAndUserId(ids, auth.getUser().getId());
    }

    @Override
    public List<Note> findEntitiesThatUserCanReadExcludingIds(Auth auth, List<Long> ids) throws AuthNullException {
        if (auth == null) {
            throw new AuthNullException();
        }
        return this.repository.findAllByUserIdExcludingIds(auth.getUser().getId(), ids);
    }

    @Override
    public WebSocketDestinationsEnum getWebSocketDestination() {
        return WebSocketDestinationsEnum.NOTE;
    }
}
