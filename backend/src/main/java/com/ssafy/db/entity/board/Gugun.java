package com.ssafy.db.entity.board;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="gugun", schema = "board")
@Getter
@Setter
public class Gugun extends BaseEntity {

    String name;

    @Column(name="sido_code")
    Long sidoCode;
}
