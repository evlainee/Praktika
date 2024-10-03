package com.example.practika.repository;

import com.example.practika.entity.Reports;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Reports,Integer> {

}