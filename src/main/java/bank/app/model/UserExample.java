package bank.app.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserExample {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String telephone;

}
