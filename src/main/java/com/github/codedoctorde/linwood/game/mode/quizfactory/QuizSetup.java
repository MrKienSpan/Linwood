package com.github.codedoctorde.linwood.game.mode.quizfactory;

import com.github.codedoctorde.linwood.Linwood;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Objects;

/**
 * @author CodeDoctorDE
 */
public class QuizSetup {
    private final QuizFactory factory;

    public QuizSetup(QuizFactory factory){
        this.factory = factory;
        Objects.requireNonNull(Linwood.getInstance().getJda().getUserById(factory.getOwnerId())).openPrivateChannel().queue(channel -> sendMessage(channel, "SetupAddMessage"));
    }

    public void sendMessage(PrivateChannel channel, String content){
        var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
        channel.sendMessage(factory.getBundle(session).getString(content)).queue(message ->
                message.addReaction("white_check_mark").queue(aVoid -> message.addReaction("x").queue()));
        session.close();
    }

    public QuizFactory getFactory() {
        return factory;
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getChannelType() != ChannelType.PRIVATE || event.getAuthor().getIdLong() != factory.getOwnerId())
            return;
        factory.getQuestions().add(event.getMessage().getContentRaw());
        sendMessage(event.getPrivateChannel(), "SetupSuccessAddMessage");
    }
}
