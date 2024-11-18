package bank.app.service.impl;

import bank.app.dto.*;
import bank.app.exeptions.UserAlreadyDeletedException;
import bank.app.exeptions.UserNotFoundException;
import bank.app.mapper.AddressMapper;
import bank.app.mapper.UserMapper;
import bank.app.model.entity.Account;
import bank.app.model.entity.Address;
import bank.app.model.entity.PrivateInfo;
import bank.app.model.entity.User;
import bank.app.model.enums.Role;
import bank.app.model.enums.Status;
import bank.app.repository.AccountRepository;
import bank.app.repository.AddressRepository;
import bank.app.repository.UserRepository;
import bank.app.service.AddressService;
import bank.app.service.PrivateInfoService;
import bank.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PrivateInfoService privateInfoService;
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;
    private final AddressMapper addressMapper;
    private final UserMapper userMapper;


    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto) // Преобразуем User в UserResponseDto
                .collect(Collectors.toList());
    }
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User manager = userRepository.findById(userRequestDto.manager())
                .orElseThrow(() -> new UserNotFoundException("Manager with ID " + userRequestDto.manager() + " not found"));

        User user = new User(userRequestDto.username(),userRequestDto.password(),
                Status.ACTIVE, userRequestDto.role(),manager);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public User getUserByStatus(Role role) {
        User user = userRepository.findByRole(role)
                .orElseThrow(() -> new UserNotFoundException("User with role " + role + "not found"));
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
                throw new UserNotFoundException("User with ID " + id + " not found");
        }
        User user = userOptional.get();
        if (user.getStatus().equals(Status.DELETED)) {
                throw new UserAlreadyDeletedException("User with ID " + id + " is already deleted");
        }
        user.setStatus(Status.DELETED);

        List<Account> accounts = accountRepository.findAllByUserId(id);
        for (Account account : accounts) {
            account.setStatus(Status.DELETED);
        }

        accountRepository.saveAll(accounts);
        userRepository.save(user);
    }

    @Override
    public UserResponseDto addPrivateInfo(Long id, PrivateInfoRequestDto privateInfoRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        Address address = addressMapper.toAddress(privateInfoRequestDto.getAddress());
        addressRepository.save(address);
        PrivateInfo savedPrivateInfo = privateInfoService.createPrivateInfo(privateInfoRequestDto, address);
        user.setPrivateInfo(savedPrivateInfo);
        userRepository.save(user);
        return userMapper.toDto(user);
    }



    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("FullUserDto cannot be null");
        }
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        try {

            existingUser.setUsername(userDto.username());
            existingUser.setPassword(userDto.password());
            existingUser.setStatus(Status.valueOf(userDto.status()));
            existingUser.setRole(userDto.role());

            if (userDto.manager() != null) {
                User manager = userRepository.findById(userDto.manager())
                        .orElseThrow(() -> new EntityNotFoundException("Manager not found with id: " + userDto.manager()));
                existingUser.setManager(manager);
            } else {
                existingUser.setManager(null);
            }
            userRepository.save(existingUser);
            return userMapper.toDto(existingUser);

        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserResponseDto updatePrivateInfo(Long id, PrivateInfoDto privateInfoDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        PrivateInfo privateInfo = user.getPrivateInfo();
        if (privateInfo == null) {
            privateInfo = new PrivateInfo();
            user.setPrivateInfo(privateInfo);
        }

        privateInfo.setFirstName(privateInfoDto.firstName());
        privateInfo.setLastName(privateInfoDto.lastName());
        privateInfo.setEmail(privateInfoDto.email());
        privateInfo.setPhone(privateInfoDto.phone());
        privateInfo.setDateOfBirth(privateInfoDto.dateOfBirth());
        privateInfo.setDocumentType(privateInfoDto.documentType());
        privateInfo.setDocumentNumber(privateInfoDto.documentNumber());
        privateInfo.setComment(privateInfoDto.comment());

        privateInfoService.savePrivateInfo(privateInfo);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateAddress(Long id, AddressRequestDto AddressRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        PrivateInfo privateInfo = user.getPrivateInfo();
        if (privateInfo == null) {
            privateInfo = new PrivateInfo();
            user.setPrivateInfo(privateInfo);
        }

        Address address = privateInfo.getAddress();
        if (address == null) {
            address = new Address();
            privateInfo.setAddress(address);
        }

        address.setCountry(AddressRequestDto.country());
        address.setCity(AddressRequestDto.city());
        address.setPostcode(AddressRequestDto.postcode());
        address.setStreet(AddressRequestDto.street());
        address.setHouseNumber(AddressRequestDto.houseNumber());
        address.setInfo(AddressRequestDto.info());

        userRepository.save(user);
        return userMapper.toDto(user);
    }

}
