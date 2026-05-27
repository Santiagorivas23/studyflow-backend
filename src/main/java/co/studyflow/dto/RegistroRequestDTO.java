package co.studyflow.dto;

import lombok.Data;

@Data
public class RegistroRequestDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
}
