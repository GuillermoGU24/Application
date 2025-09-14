package co.com.crediya.model.auth;

public class Rol {
    private Integer idRol;
    private String nameRol;
    private String decriptionRol;

    public Rol() {}

    public Rol(Integer idRol, String nameRol, String decriptionRol) {
        this.idRol = idRol;
        this.nameRol = nameRol;
        this.decriptionRol = decriptionRol;
    }

    public Integer getId() {
        return idRol;
    }

    public void setId(Integer idRol) {
        this.idRol = idRol;
    }

    public String getName() {
        return nameRol;
    }

    public void setName(String nameRol) {
        this.nameRol = nameRol;
    }

    public String getDecription() {
        return decriptionRol;
    }

    public void setDecription(String decriptionRol) {
        this.decriptionRol = decriptionRol;
    }
}
