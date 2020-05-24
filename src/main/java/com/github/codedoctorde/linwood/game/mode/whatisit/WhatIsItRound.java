package com.github.codedoctorde.linwood.game.mode.whatisit;

import com.github.codedoctorde.linwood.Main;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

/**
 * @author CodeDoctorDE
 */
public class WhatIsItRound {
    private long writerId;
    private String word;
    private WhatIsIt whatIsIt;
    private List<Long> guesser;

    public long getWriterId() {
        return writerId;
    }
    public Member getWriter(){
        return whatIsIt.getGame().getGuild().getMemberById(writerId);
    }

    public WhatIsIt getWhatIsIt() {
        return whatIsIt;
    }

    public String getWord() {
        return word;
    }

    public List<Long> getGuesser() {
        return guesser;
    }
}