package co.com.crediya.model.auth;




public class AuthUser {
    private Integer idUser;
    private String email;
    private String passwordHash;
    private Rol rol;
    private String document;

    public AuthUser() {

    }

    public AuthUser(Integer idUser, String email, String passwordHash, Rol rol, String document) {
        this.idUser = idUser;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.document = document;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
