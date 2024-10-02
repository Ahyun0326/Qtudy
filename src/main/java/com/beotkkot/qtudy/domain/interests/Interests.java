package com.beotkkot.qtudy.domain.interests;

import com.beotkkot.qtudy.domain.category.Category;
import com.beotkkot.qtudy.domain.primaryKey.InterestsPK;
import com.beotkkot.qtudy.domain.user.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Interests {

    @EmbeddedId
    private InterestsPK id; // 복합 키

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")   // 복합 키의 userId와 매핑
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")   // 복합 키의 categoryId와 매핑
    @JoinColumn(name = "categoryId")
    private Category category;

    public Interests(Users user, Category category) {
        this.id = new InterestsPK(user.getKakaoId(), category.getCategoryId());
        this.user = user;
        this.category = category;
    }
}
