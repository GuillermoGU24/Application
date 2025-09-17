package co.com.crediya.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Integer idUser;
    private String name;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private Double baseSalary;
    private Rol rol;
}
