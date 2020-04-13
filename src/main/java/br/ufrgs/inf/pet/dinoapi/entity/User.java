package br.ufrgs.inf.pet.dinoapi.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Classe de persistencia para a tabela de usuário no banco de dados
 *
 * @author joao.silva
 */
@Entity
@Table(name = "dino_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String SEQUENCE_NAME = "dino_user_seq";

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Long id;

    @Basic(optional = false)
    @NotNull(message = "Nome não pode ser nulo.")
    @Size(min = 1, max = 100, message = "O nome deve conter entre 1 e 100 caracteres.")
    @Column(name = "name", length = 100)
    private String name;

    @Size(min = 1, max = 100, message = "O email deve conter entre 1 e 100 caracteres.")
    @Email(message = "Email inválido.")
    @Column(name = "email", length = 100, unique = false)
    private String email;

    @Size(min = 1, max = 168, message = "O token de acesso deve conter entre 1 e 168 caracteres.")
    @Column(name = "access_token", length = 168, unique = true)
    private String accessToken;

    @Column(name = "token_expires_data_in_millis")
    private Long tokenExpiresDateInMillis;

    @OneToOne
    @JoinColumn(name = "google_auth_id")
    private GoogleAuth googleAuth;

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public boolean hasGoogleAuth() {
        return googleAuth != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GoogleAuth getGoogleAuth() {
        return googleAuth;
    }

    public void setGoogleAuth(GoogleAuth googleAuth) {
        this.googleAuth = googleAuth;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getTokenExpiresDateInMillis() {
        return tokenExpiresDateInMillis;
    }

    public void setTokenExpiresDateInMillis(Long tokenExpiresDateInMillis) {
        this.tokenExpiresDateInMillis = tokenExpiresDateInMillis;
    }

    public Boolean tokenIsValid() {
        return (new Date()).getTime() <= this.tokenExpiresDateInMillis;
    }
}
