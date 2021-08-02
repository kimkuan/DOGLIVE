package com.ssafy.db.entity.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 사용자 정보 모델을 정의하는 Entity
 */
@Entity
@Data
@Table(name="user_profile", schema = "auth")
@Getter
@Setter
public class UserProfile implements Serializable {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id = null;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name="user_id")
  private User userId;


  @Column(length = 30)
  private String email;

  @Column(name="profile_image_url")
  private String profileImageUrl;

  @Column(length = 10)
  private String name;

  @Column(length = 13, name="phone_number")
  private String phoneNumber;


  @OneToMany(mappedBy = "applicantId", cascade = {CascadeType.ALL}, orphanRemoval=true)
  private List<CounselingHistory> counselingHistories;

  @OneToMany(mappedBy = "userId", cascade = {CascadeType.ALL}, orphanRemoval=true)
  private List<Bookmark> bookmarks;


}
