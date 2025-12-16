package com.bsu.lab3;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private boolean isDone;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Difficulty {
        EASY, MEDIUM, HARD, NIGHTMARE
    }

    public void setDone(boolean val){
        this.isDone = val;
    }
}
