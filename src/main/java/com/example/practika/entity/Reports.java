package com.example.practika.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reports")
public class Reports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_REPORT_REQUEST"))
    private Request request; // Связь с заявкой

    @Column(name = "totalTime")
    private int totalTime;

    @Column(name = "usedParts")
    private String usedParts;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    // Getters and Setters
    public Integer getRequestId(){
        return request.getRequestId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getUsedParts() {
        return usedParts;
    }

    public void setUsedParts(String usedParts) {
        this.usedParts = usedParts;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}

