package com.example.practika.dto;

import lombok.Data;


@Data
public class RepairRequestDTO {

    private String fio;

    private String phone;

    private String deviceType;

    private String deviceModel;

    private String problemDescription;
}
