package com.github.codedoctorde.linwood.listener;

import com.github.codedoctorde.linwood.Linwood;
import com.github.codedoctorde.linwood.entity.GuildEntity;
import io.sentry.Sentry;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author CodeDoctorDE
 */
public class CommandListener {
    @SubscribeEvent
    public void onCommand(@Nonnull MessageReceivedEvent event) {
        var session = Linwood.getInstance().getDatabase().getSessionFactory().openSession();
        if(event.getChannelType() != ChannelType.TEXT)
            return;
        var guild = GuildEntity.get(session, event.getGuild().getIdLong());
        if(event.getAuthor().isBot())
            return;
        var content = event.getMessage().getContentRaw();
        var prefix = guild.getPrefix();
        var id = event.getJDA().getSelfUser().getId();
        var nicknameMention = "<@!" + id + ">";
        var normalMention = "<@" + id + ">";
        if (content.startsWith(prefix) || content.startsWith(nicknameMention) || content.startsWith(normalMention)) {
            String split;
            if (content.startsWith(prefix))
                split = prefix;
            else if (content.startsWith(nicknameMention))
                split = nicknameMention;
            else
                split = normalMention;
            var command = content.substring(split.length()).trim().split(" ");
            var bundle = getBundle(guild);
            var commandBundle = Linwood.getInstance().getBaseCommand().getBundle(guild);
            assert bundle != null && commandBundle != null;
            try {
                if (!Linwood.getInstance().getBaseCommand().onCommand(session, event.getMessage(), guild, guild.getPrefix(), command))
                    event.getChannel().sendMessage(MessageFormat.format(bundle.getString("Syntax"), commandBundle.getString("Syntax"))).queue();
            }catch(Exception e){
                event.getChannel().sendMessage(bundle.getString("Error")).queue();
                e.printStackTrace();
                Sentry.capture(e);
            }
        }
        session.close();
    }

    public ResourceBundle getBundle(GuildEntity entity) {
        return ResourceBundle.getBundle("locale.Command", entity.getLocalization());
    }
}
