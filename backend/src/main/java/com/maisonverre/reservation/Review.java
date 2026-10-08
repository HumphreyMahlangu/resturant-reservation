package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    @Column(nullable = false)
    private int rating;
    @Column(length = 2000)
    private String comment;
    @Column(nullable = false, updatable = false)
    private Instant reviewDate = Instant.now();
    protected Review() {}
    public Review(UserAccount user, MenuItem menuItem, int rating, String comment) {
        this.user = user; this.menuItem = menuItem; this.rating = rating; this.comment = comment;
    }
    public UUID getId() { return id; }
    public UserAccount getUser() { return user; }
    public MenuItem getMenuItem() { return menuItem; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getReviewDate() { return reviewDate; }
    public void update(UserAccount user, MenuItem menuItem, int rating, String comment) {
        this.user = user; this.menuItem = menuItem; this.rating = rating; this.comment = comment;
    }
}
