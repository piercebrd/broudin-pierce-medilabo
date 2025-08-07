package com.p9.frontendservice.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Patient {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phoneNumber;


}
