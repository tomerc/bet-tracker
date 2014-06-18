package com.bettracker.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Tomer Cohen
 */
@Entity
@Table(name = "team")
public class Team implements Serializable {
    private static final long serialVersionUID = -8508785356619277654L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "team_name")
    private String teamName;

    @ManyToOne(targetEntity = Sport.class)
    @Column(name = "sport_id")
    private Sport sport;


    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Team team = (Team) o;

        if (!teamId.equals(team.teamId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return teamId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Team{");
        sb.append("teamId=").append(teamId);
        sb.append(", teamName='").append(teamName).append('\'');
        sb.append(", sport=").append(sport);
        sb.append('}');
        return sb.toString();
    }
}
