package com.bettracker.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Tomer Cohen
 */
@Entity
@Table(name = "bet")
public class Bet implements Serializable {
    private static final long serialVersionUID = 5949078041729904250L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bet_id")
    private Integer betId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private TournamentSchedule tournamentSchedule;

    public Integer getBetId() {
        return betId;
    }

    public void setBetId(Integer betId) {
        this.betId = betId;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TournamentSchedule getTournamentSchedule() {
        return tournamentSchedule;
    }

    public void setTournamentSchedule(TournamentSchedule tournamentSchedule) {
        this.tournamentSchedule = tournamentSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bet bet = (Bet) o;

        if (!betId.equals(bet.betId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return betId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Bet{");
        sb.append("betId=").append(betId);
        sb.append(", team=").append(team);
        sb.append(", player=").append(player);
        sb.append(", tournamentSchedule=").append(tournamentSchedule);
        sb.append('}');
        return sb.toString();
    }
}
