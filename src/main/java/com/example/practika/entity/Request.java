package com.example.practika.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Check;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "requests")  // Переименуем таблицу "Заявки3" в "requests"
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    // Связь с клиентом
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_client_request"))
    private User client;

    @Column(name = "description", nullable = false)
    private String description;

    // Статус с проверкой допустимых значений
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Check(constraints = "status IN ('New', 'InProgress', 'WaitingForParts', 'Completed')")
    private Status status;

    @Column(name = "creation_date")
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Column(name = "priority")
    @Check(constraints = "priority BETWEEN 1 AND 5")
    private Integer priority;

    // Связь с мастером
    @ManyToOne
    @JoinColumn(name = "master_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_master_request"))
    private User master;

    // Связь с оператором
    @ManyToOne
    @JoinColumn(name = "operator_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_operator_request"))
    private User operator;

    @Column(name = "completion_date")
    @Temporal(TemporalType.DATE)
    private Date completionDate;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parts> parts;

    // Перечисление статусов заявки
    public enum Status {
        New, InProgress, WaitingForParts, Completed;

        @JsonCreator
        public static Status fromValue(String value) {
            switch (value) {
                case "Новая":
                    return New;
                case "В работе":
                    return InProgress;
                case "Ожидание запчастей":
                    return WaitingForParts;
                case "Завершена":
                    return Completed;
                default:
                    throw new IllegalArgumentException("Неверный статус: " + value);
            }
        }
    }
}
