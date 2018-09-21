package kino.listener;

import java.awt.Color;

import kino.commandSystem.CommandMap;
import main.MainClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener{
	private static boolean welcome = false;
	private final CommandMap commandMap;
	public BotListener(CommandMap commandeMap) {
		this.commandMap = commandeMap;
	}
	/**/
	public static boolean isWelcome() {
		return welcome;
	}
	public static void setWelcome(boolean welcome) {
		BotListener.welcome = welcome;
	}
	/**/
	public void onEvent(Event event) {
		if (event instanceof MessageReceivedEvent && !(((MessageReceivedEvent) event).getChannelType().equals(ChannelType.PRIVATE))) 
			onMessageGuild((MessageReceivedEvent)event);
		if (event instanceof GuildMemberJoinEvent)
			onGuildMemberJoin((GuildMemberJoinEvent)event);
		if (event instanceof GuildMemberLeaveEvent)
			onGuildMemberleave((GuildMemberLeaveEvent)event);
		if (event instanceof ReadyEvent) {
			int count = MainClass.getJda().getGuilds().size();
			MainClass.getJda().getPresence().setGame(Game.playing("Ready to use | "+count+" Guilds"));
		}
	}
	//Listener :
	//Guild
	private void onMessageGuild(MessageReceivedEvent event) {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		String msg = event.getMessage().getContentDisplay();
		if (msg.startsWith(commandMap.getTag())) {
			msg = msg.replaceFirst(commandMap.getTag(), "");
			commandMap.commandUser(event.getAuthor(), msg, event.getMessage());
		}	
	}
	/**/
	/*Events leave and join*/
	private void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (!welcome) return;
		TextChannel channel = event.getGuild().getSystemChannel();
		if (channel != null) {
			channel.sendMessage(new EmbedBuilder().setDescription("Hey ! A new member has arrived " + 
					 ", Welcome **"+event.getUser().getAsMention()+"** sensei!").setColor(Color.green).build()).queue();
		}else {
			event.getGuild().getTextChannels().get(0).sendMessage(new EmbedBuilder().setDescription("Hey ! A new member has arrived " + 
					 ", Welcome **"+event.getUser().getAsMention()+"** sensei!").setColor(Color.green).build()).queue();
		}
		
	}
	private void onGuildMemberleave(GuildMemberLeaveEvent event) {
		event.getGuild().getTextChannels().get(0).sendMessage(new EmbedBuilder().setDescription("oh ! **"+event.getUser().getAsMention()+"** "
				+ "leaved us ! We are now "+event.getGuild().getMembers().size()+" Students!").setColor(Color.red).build()).queue();
	}
}
