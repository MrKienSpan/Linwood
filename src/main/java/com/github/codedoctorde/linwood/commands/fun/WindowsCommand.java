package com.github.codedoctorde.linwood.commands.fun;

import com.github.codedoctorde.linwood.commands.Command;
import com.github.codedoctorde.linwood.entity.GuildEntity;
import net.dv8tion.jda.api.entities.Message;
import org.hibernate.Session;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * @author CodeDoctorDE
 */
public class WindowsCommand implements Command {
    private final Random random = new Random();

    @Override
    public boolean onCommand(Session session, Message message, GuildEntity entity, String label, String[] args) {
        if(args.length > 0)
            return false;
        String response;
        InputStream file = null;
        var bundle = getBundle(entity);
        if(bundle == null)
            return false;
        switch (random.nextInt(3)){
            case 0:
                response = bundle.getString("Crash");

                file = getClass().getClassLoader().getResourceAsStream("assets/crash.png");
                break;
            case 1:
                response = bundle.getString("Update");

                file = getClass().getClassLoader().getResourceAsStream(("assets/update.png"));
                break;
            default:
                response = bundle.getString("Loading");
                break;
        }
        var action = message.getChannel().sendMessage(response);
        if(file != null)
            action = action.addFile(file, "windows.png");
        action.queue();
        return true;
    }

    @Override
    public String[] aliases(GuildEntity entity) {
        return new String[]{
                "windows",
                "win",
                "window"
        };
    }

    @Override
    public ResourceBundle getBundle(GuildEntity entity) {
        return ResourceBundle.getBundle("locale.commands.fun.Windows", entity.getLocalization());
    }
}
