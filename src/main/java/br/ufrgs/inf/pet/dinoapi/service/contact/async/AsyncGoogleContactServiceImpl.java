package br.ufrgs.inf.pet.dinoapi.service.contact.async;

import br.ufrgs.inf.pet.dinoapi.communication.google.people.GooglePeopleCommunicationImpl;
import br.ufrgs.inf.pet.dinoapi.entity.auth.Auth;
import br.ufrgs.inf.pet.dinoapi.entity.contacts.Contact;
import br.ufrgs.inf.pet.dinoapi.entity.contacts.GoogleContact;
import br.ufrgs.inf.pet.dinoapi.entity.user.User;
import br.ufrgs.inf.pet.dinoapi.model.contacts.GoogleContactDataModel;
import br.ufrgs.inf.pet.dinoapi.model.google.people.GooglePeopleModel;
import br.ufrgs.inf.pet.dinoapi.repository.contact.PhoneRepository;
import br.ufrgs.inf.pet.dinoapi.service.auth.google.GoogleScopeServiceImpl;
import br.ufrgs.inf.pet.dinoapi.service.contact.ContactServiceImpl;
import br.ufrgs.inf.pet.dinoapi.service.user.UserSettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
public class AsyncGoogleContactServiceImpl implements AsyncGoogleContactService {
    private final GooglePeopleCommunicationImpl googlePeopleCommunication;
    private final ContactServiceImpl contactService;
    private final PhoneRepository phoneRepository;
    private final GoogleScopeServiceImpl googleScopeService;
    private final UserSettingsServiceImpl userSettingsService;
    private final AsyncSaveGoogleContact asyncSaveGoogleContact;

    @Autowired
    public AsyncGoogleContactServiceImpl(GooglePeopleCommunicationImpl googlePeopleCommunication,
                                         ContactServiceImpl contactService,
                                         PhoneRepository phoneRepository,
                                         GoogleScopeServiceImpl googleScopeService,
                                         AsyncSaveGoogleContactImpl asyncSaveGoogleContact,
                                         UserSettingsServiceImpl userSettingsService) {
        this.googlePeopleCommunication = googlePeopleCommunication;
        this.contactService = contactService;
        this.phoneRepository = phoneRepository;
        this.googleScopeService = googleScopeService;
        this.userSettingsService = userSettingsService;
        this.asyncSaveGoogleContact = asyncSaveGoogleContact;
    }

    @Override
    @Async("defaultThreadPoolTaskExecutor")
    public void createContactOnGoogleAPI(GoogleContactDataModel model, BiFunction<GoogleContactDataModel, Auth, Void> save) {
        final Optional<Contact> contactSearch = contactService.findById(model.getContactId());

        if (contactSearch.isPresent()) {
            final Contact contact = contactSearch.get();
            final User user = contact.getUser();

            final boolean syncWithGoogleAPI = googleScopeService.hasGoogleContactScope(user)
                    && userSettingsService.saveContactsOnGoogleAPI(user);

            if (syncWithGoogleAPI) {
                final List<String> phoneNumbers = phoneRepository.findAllPhoneNumbersByContactId(contact.getId());

                final GooglePeopleModel googlePeopleModel = googlePeopleCommunication.createContact(user, contact.getName(), contact.getDescription(), phoneNumbers);

                this.saveGoogleContact(user, model.getId(), googlePeopleModel, contact.getId(), save);
            }
        }
    }

    @Override
    @Async("defaultThreadPoolTaskExecutor")
    public void updateContactOnGoogleAPI(GoogleContact entity, BiFunction<GoogleContactDataModel, Auth, Void> save) {
        final Contact contact = entity.getContact();

        final User user = contact.getUser();

        final boolean syncWithGoogleAPI = googleScopeService.hasGoogleContactScope(user)
                && userSettingsService.saveContactsOnGoogleAPI(user);

        if (syncWithGoogleAPI) {
            final List<String> phoneNumbers = phoneRepository.findAllPhoneNumbersByContactId(contact.getId());

            final GooglePeopleModel googlePeopleModel;

            if (entity.getResourceName() != null) {
                googlePeopleModel = googlePeopleCommunication.updateContact(
                        user, contact.getName(), contact.getDescription(), phoneNumbers, entity.getResourceName());
            } else {
                googlePeopleModel = googlePeopleCommunication.createContact(
                        user, contact.getName(), contact.getDescription(), phoneNumbers);
            }

            this.saveGoogleContact(user, entity.getId(), googlePeopleModel, contact.getId(), save);
        }
    }

    @Override
    @Async("defaultThreadPoolTaskExecutor")
    public void deleteContactOnGoogleAPI(GoogleContact entity) {
        if (entity.getResourceName() != null) {
            final Contact contact = entity.getContact();
            final User user = contact.getUser();

            final boolean syncWithGoogleAPI = googleScopeService.hasGoogleContactScope(user)
                    && userSettingsService.saveContactsOnGoogleAPI(user);

            if (syncWithGoogleAPI) {
                googlePeopleCommunication.deleteContact(user, entity);
            }
        }
    }

    private void saveGoogleContact(User user, Long id, GooglePeopleModel googlePeopleModel, Long contactId,
                                   BiFunction<GoogleContactDataModel, Auth, Void> save) {
        this.asyncSaveGoogleContact.saveGoogleContact(user, id, contactId, googlePeopleModel, save);
    }
}