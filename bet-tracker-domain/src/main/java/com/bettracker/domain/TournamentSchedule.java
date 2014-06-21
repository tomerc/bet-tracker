package com.bettracker.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * @author Tomer Cohen
 */
@Entity
@Table(name = "tournament_schedule")
public class TournamentSchedule implements Serializable {
    private static final long serialVersionUID = -4574889118420362792L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;


    @ManyToOne
    @Column(name = "bet_id")
    private Bet bet;

    @ManyToOne
    @Column(name = "team_id")
    private Team team;

    @Column(name = "game_date")
    private Date gameDate;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TournamentSchedule that = (TournamentSchedule) o;

        if (!scheduleId.equals(that.scheduleId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return scheduleId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TournamentSchedule{");
        sb.append("scheduleId=").append(scheduleId);
        sb.append(", bet=").append(bet);
        sb.append(", team=").append(team);
        sb.append(", gameDate=").append(gameDate);
        sb.append('}');
        return sb.toString();
    }
}
