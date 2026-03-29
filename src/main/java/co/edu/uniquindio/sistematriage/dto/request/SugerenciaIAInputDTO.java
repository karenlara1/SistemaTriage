package co.edu.uniquindio.sistematriage.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SugerenciaIAInputDTO {
    @NotBlank
    private String descripcion;

}
