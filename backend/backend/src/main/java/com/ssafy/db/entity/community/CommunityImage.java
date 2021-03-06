package com.ssafy.db.entity.community;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 커뮤니티 이미지 관련 Entity
 */
@Entity
@Table(name="community_image", schema = "community")
@Getter
@Setter
public class CommunityImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="community_id")
    private Community communityId;       // 커뮤니티 번호

    @Column(name="file_path")
    private String filePath;        // 이미지 파일 경로

}

