package com.example.practika.repository;

import com.example.practika.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request,Integer> {

    public List<Request> findByClient_Id(Long clientId);
}
