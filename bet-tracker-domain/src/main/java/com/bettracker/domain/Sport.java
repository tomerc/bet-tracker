package com.bettracker.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Tomer Cohen
 */
@Entity
@Table(name = "sport")
public class Sport implements Serializable {
    private static final long serialVersionUID = -7547643361603214503L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sports_id")
    private Integer sportId;

    @Column(name = "sport_name")
    private String sportName;

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sport sport = (Sport) o;

        if (!sportId.equals(sport.sportId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sportId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sport{");
        sb.append("sportId=").append(sportId);
        sb.append(", sportName='").append(sportName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
