package com.example.practika.repository;

import com.example.practika.entity.Parts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Parts,Integer> {
}
