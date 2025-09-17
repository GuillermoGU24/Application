package co.com.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer idUser;
    private String name;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private Double baseSalary;
    private String rolName;
}
