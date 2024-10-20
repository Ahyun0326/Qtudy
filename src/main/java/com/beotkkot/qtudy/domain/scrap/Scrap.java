package com.beotkkot.qtudy.domain.scrap;

import com.beotkkot.qtudy.domain.posts.Posts;
import com.beotkkot.qtudy.domain.primaryKey.ScrapPk;
import com.beotkkot.qtudy.domain.user.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Scrap {

    @EmbeddedId
    private ScrapPk id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Posts post;

    private String scrapAt;

    public Scrap(Users user, Posts post, String scrapAt) {
        this.id = new ScrapPk(user.getUserId(), post.getPostId());
        this.user = user;
        this.post = post;
        this.scrapAt = scrapAt;
    }
}
