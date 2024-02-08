package com.github.backproject.respository.like;

import com.github.backproject.respository.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "like")
public class LikeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId;

    @Column(name = "post_id")
    private Integer postId;

    @JoinColumn(name = "user_id")
    @ManyToOne()
    private UserEntity userEntity;

}
