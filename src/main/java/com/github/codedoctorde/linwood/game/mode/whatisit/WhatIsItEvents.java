package com.github.codedoctorde.linwood.game.mode.whatisit;

import com.github.codedoctorde.linwood.Linwood;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.text.MessageFormat;

/**
 * @author CodeDoctorDE
 */
public class WhatIsItEvents {
    private final WhatIsIt whatIsIt;

    public WhatIsItEvents(WhatIsIt whatIsIt){
        this.whatIsIt = whatIsIt;
    }
    @SubscribeEvent
    public void onGuess(MessageReceivedEvent event){
        try {
            if (event.getChannel().getIdLong() != whatIsIt.getTextChannelId() || event.getMember() == null)
                return;
            var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
            var message = event.getMessage();
            if (whatIsIt.getTextChannelId() != message.getTextChannel().getIdLong()) return;
            var round = whatIsIt.getRound();
            if (round != null && round.getWord() != null && message.getContentStripped().toLowerCase().contains(round.getWord().toLowerCase())) {
                message.delete().queue();
                if (!round.isGuesser(event.getMember()) && message.getAuthor().getIdLong() != round.getWriterId())
                    event.getChannel().sendMessage(MessageFormat.format(whatIsIt.getBundle(session).getString("Guess"), event.getAuthor().getName(), round.guessCorrectly(event.getMember()))).queue(message1 -> {
                        var session1 = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
                        round.checkEverybody(session1);
                        session.close();
                    });
            }
            session.close();
        }
        catch(Exception e){
            e.printStackTrace();
            Sentry.capture(e);
        }
    }
    @SubscribeEvent
    public void onWord(MessageReceivedEvent event) {
        try {
            if (event.getChannelType() != ChannelType.PRIVATE || whatIsIt.getRound() == null)
                return;
            if (whatIsIt.getRound().getWriterId() != event.getAuthor().getIdLong())
                return;
            if(whatIsIt.getRound().getWord() != null)
                return;
            whatIsIt.getRound().startRound(event.getMessage().getContentStripped());
        }
        catch(Exception e){
            e.printStackTrace();
            Sentry.capture(e);
        }
    }
    @SubscribeEvent
    public void onJoin(MessageReactionAddEvent event){
        try {
            var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
            var entity = Linwood.getInstance().getDatabase().getGuildById(session, event.getGuild().getIdLong());
            var bundle = whatIsIt.getBundle(session);
            if (event.getChannel().getIdLong() != whatIsIt.getTextChannelId() || event.getMember() == null ||
                    event.getMessageIdLong() != whatIsIt.getWantWriterMessageId())
                return;
            if (event.getMember().getUser().isBot() || !event.getReactionEmote().isEmoji())
                return;
            switch (event.getReactionEmote().getAsCodepoints()) {
                case "U+1f590U+fe0f":
                    if(!whatIsIt.wantWriter(session, event.getMember()))
                        event.getReaction().removeReaction(event.getMember().getUser()).queue();
                    break;
                case "U+26d4":
                    if (entity.isGameMaster(event.getMember())) {
                        whatIsIt.finishGame();
                        break;
                    }
                    else
                        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(bundle.getString("NoPermission")).queue());
                default:
                    event.getReaction().removeReaction(event.getMember().getUser()).queue();
            }
            session.close();
        }
        catch(Exception e){
            e.printStackTrace();
            Sentry.capture(e);
        }
    }
    @SubscribeEvent
    public void onLeave(MessageReactionRemoveEvent event){
        try {
            var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
            if (event.getChannel().getIdLong() != whatIsIt.getTextChannelId() || event.getMember() == null ||
                    event.getMessageIdLong() != whatIsIt.getWantWriterMessageId())
                return;
            if (event.getMember().getUser().isBot())
                return;
            if (event.getReactionEmote().getAsCodepoints().equals("U+1f590U+fe0f"))
                whatIsIt.removeWriter(session, event.getMember());
            session.close();
        }
        catch(Exception e){
            e.printStackTrace();
            Sentry.capture(e);
        }
    }

    public WhatIsIt getWhatIsIt() {
        return whatIsIt;
    }
}
