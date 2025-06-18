package com.example.EngLang.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true) // Gmail benzersiz olmalı
    private String gmail;

    // Bir kullanıcının birden fazla kelime listesi olabilir (OneToMany)
    // "user" alanı WordList entity'sindeki User referansını işaret eder
    // CascadeType.ALL: User silindiğinde ilişkili WordList'ler de silinsin
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WordList> wordLists = new ArrayList<>();
}