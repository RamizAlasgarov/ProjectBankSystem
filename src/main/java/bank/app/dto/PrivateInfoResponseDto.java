package bank.app.dto;

import bank.app.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class PrivateInfoResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private DocumentType documentType;
    private String documentNumber;
    private String comment;
    private AddressResponseDto address;

    // Getters and setters (or use Lombok annotations)

    public PrivateInfoResponseDto(Long id, String firstName, String lastName, String email, String phone, LocalDate dateOfBirth,
                                  DocumentType documentType, String documentNumber, String comment, AddressResponseDto address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.comment = comment;
        this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateInfoResponseDto that = (PrivateInfoResponseDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                documentType == that.documentType &&
                Objects.equals(documentNumber, that.documentNumber) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phone, dateOfBirth, documentType, documentNumber, comment, address);
    }
}