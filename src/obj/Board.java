package obj;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import Utils.TextUtil;

public class Board {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private Scoreboard board;
    private Objective objective;
    private PlayerInfo pi;

    public Board(String displayName, PlayerInfo pi) {
    	this.pi=pi;
        this.board = manager.getNewScoreboard();
        this.objective = board.registerNewObjective("test", "dummy", TextUtil.str(displayName));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Team owners = board.registerNewTeam("Owners");
        owners.setCanSeeFriendlyInvisibles(true);
        owners.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
        owners.setColor(ChatColor.BLUE);
		
        Team robbers = board.registerNewTeam("Robbers");
        robbers.setCanSeeFriendlyInvisibles(true);
        robbers.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
        robbers.setColor(ChatColor.RED);
        
        board.registerNewObjective("jump", "jump", "jump");
		
    }
    
    public int getObjective(String name){
    	return board.getObjective(name).getScore(pi.pname).getScore();
    }
    
    public void setObjective(String name, int set){
    	board.getObjective(name).getScore(pi.pname).setScore(set);
    }

    public Scoreboard getScoreboard() {
        return this.board;
    }

    public void setDisplayName(String name) {
        this.objective.setDisplayName(TextUtil.str(name));
    }

    public void setScore(String name, int index) {
        String string = build(index);
        Team team = this.board.getTeam(string);
        if (team == null) {
            team = this.board.registerNewTeam(string);
            team.addEntry(string);
            Score score = objective.getScore(string);
            score.setScore(index);
        }
        List<String> splitted = TextUtil.smartColoredSplit(TextUtil.str(name), 16);
        team.setPrefix(splitted.get(0));
        if(splitted.size()>1){
        	team.setSuffix(splitted.get(1));
        }else team.setSuffix("");
    }

    public void resetScores(int index) {
        String string = build(index);
        this.board.getTeam(string).unregister();
        this.board.resetScores(string);
    }
    
    public void resetScores() {
    	for(Team t:new ArrayList<>(board.getTeams())){
    		for(String st:new ArrayList<>(t.getEntries())){
    			int index = objective.getScore(st).getScore();
    			resetScores(index);
    		}
    	}
    }
 
    public String build(int index) {
        String hex = Integer.toHexString(index);
        StringBuilder sb = new StringBuilder();
        for (char c : hex.toCharArray()) {
            sb.append("§" + c);
        }
        return sb.toString();
    }
}