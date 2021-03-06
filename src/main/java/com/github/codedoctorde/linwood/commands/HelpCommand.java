package com.github.codedoctorde.linwood.commands;

import com.github.codedoctorde.linwood.Linwood;
import com.github.codedoctorde.linwood.entity.GuildEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.hibernate.Session;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * @author CodeDoctorDE
 */
public class HelpCommand implements Command {

    @Override
    public boolean onCommand(Session session, Message message, GuildEntity entity, String label, String[] args) {
        Command command = Linwood.getInstance().getBaseCommand().getCommand(entity, args);
        if(command == null)
            return false;

        var bundle = getBundle(entity);
        assert bundle != null;
        var commandBundle = command.getBundle(entity);
        if(commandBundle == null) {
            message.getChannel().sendMessage(bundle.getString("NotExist")).queue();
            return true;
        }
        var output = new MessageBuilder()
                .append(" ")
                .setEmbed(new EmbedBuilder()
                        .setTitle("Help")
                        .setDescription(commandBundle.containsKey("Description")?commandBundle.getString("Description"):"")
                        .setColor(new Color(0x3B863B))
                        .setTimestamp(LocalDateTime.now())
                        .setFooter(null, null)
                        .addField("Aliases", String.join(", ", command.aliases(entity)), true)
                        .addField("Permissions", commandBundle.containsKey("Permission")?commandBundle.getString("Permission"):"", true)
                        .addField("Syntax", commandBundle.containsKey("Syntax")?commandBundle.getString("Syntax"):"", false)
                        .build())
                .build();
        message.getChannel().sendMessage(output).queue();
        return true;
    }

    @Override
    public String[] aliases(GuildEntity entity) {
        return new String[]{
                "help",
                "h"
        };
    }

    @Override
    public ResourceBundle getBundle(GuildEntity entity) {
        return ResourceBundle.getBundle("locale.commands.Help", entity.getLocalization());
    }
}
