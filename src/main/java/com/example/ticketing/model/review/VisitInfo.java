package com.example.ticketing.model.review;

import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    private LocalDateTime visitDateTime;

    @Enumerated(EnumType.STRING)
    private Crowdedness crowdedness;

    @OneToOne(mappedBy = "visitInfo", cascade = CascadeType.ALL)
    private Review review;

    @Builder
    public VisitInfo(User user, Store store, LocalDateTime visitDateTime, Crowdedness crowdedness) {
        this.user = user;
        this.store = store;
        this.visitDateTime = visitDateTime;
        this.crowdedness = crowdedness;
    }

    public void updateVisitInfo(LocalDateTime visitDateTime, Crowdedness crowdedness) {
        this.visitDateTime = visitDateTime;
        this.crowdedness = crowdedness;
    }
}
