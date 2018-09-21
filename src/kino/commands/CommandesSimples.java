package kino.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import kino.listener.BotListener;
import kino.commandSystem.Command;
import kino.commandSystem.CommandMap;
import kino.commandSystem.SimpleCommand;
import kino.commandSystem.Command.ExecutorType;
import main.MainClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.entities.impl.UserImpl;

public class CommandesSimples {
	private final String admin = "220923868292972547";
	private static Message msg;
	private final MainClass bot;
	private CommandMap commandMap;
	public CommandesSimples(MainClass bot,CommandMap commandMap) {
		this.bot = bot;
		this.commandMap = commandMap;
	}
	/**/
	@Command(name="st",type=ExecutorType.CONSOLE)
	private void st() {
		bot.setRunning(false);
		MainClass.getJda().shutdown();
		System.out.println("Bot déconnecté");
		System.exit(0);
	}
	/*
	 * 
	 * */
	@Command(name="shutdown",type=ExecutorType.USER,description="Technique interdite! que seul le dev peut utiliser")
	private void shutdown(User user,TextChannel channel) {
		Message msg;
		if (user.getId().equals(admin)) {
			msg = channel.sendMessage(
					new EmbedBuilder().setColor(Color.green).setDescription("Sayonara "+user.getAsMention()+" Sensei!").build()
					).complete();
			new Timer().schedule(new TimerTask() {
				public void run() {
					msg.delete().complete();
					bot.setRunning(false);
					MainClass.getJda().shutdown();
					System.out.println("Bot déconnecté");
					System.exit(0);
				}
			}, 5000);
		}
		else{
			msg = channel.sendMessage(
					new EmbedBuilder().setColor(Color.red).setDescription("Seul mon maitre Peut utiliser ce Jutsu!").build()
					).complete();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					msg.delete().queue();
				}
			}, 10000);
		}
	}
	/*
	 * 
	 * */
	@Command(name="help",type=ExecutorType.USER)
	private void help(User user,MessageChannel channel) {
		if (channel instanceof TextChannel) {
			if (!((TextChannel) channel).getGuild().getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		}
		EmbedBuilder msg = new EmbedBuilder();
		msg.setTitle("```Liste des Commandes :```");
		msg.setColor(Color.GREEN);
		for (SimpleCommand command : commandMap.getCommands()) {
			if (command.getExecutorType().equals(ExecutorType.CONSOLE)) continue;
			msg.addField("[>] ``"+command.getName()+"``:",command.getDescription(),false);
		}
		if (!user.hasPrivateChannel()) user.openPrivateChannel().complete();
		((UserImpl)user).getPrivateChannel().sendMessage(msg.build()).queue();
		channel.sendMessage(user.getAsMention()+" Sensei! Check your private messages.").queue();
	}
	/**/
	@Command(name="bonjour",type=ExecutorType.USER)
	private void bonjour(User user,MessageChannel ch) {
		ch.sendMessage("Bonjour "+user.getAsMention()+" Sensei!").complete();
	}
	/**/
	@Command(name="game")
	private void game(User user,String[] args,TextChannel channel){
		if (args.length == 0) {
			channel.sendMessage(
					new EmbedBuilder().setTitle("Commande game :").
					setDescription("```r!game {game title}```").setColor(Color.red).build()).queue();
			return;
		}
		StringBuilder str = new StringBuilder();
		for(String ch : args) {
			if (str.length() > 0) str.append(" ");
			str.append(ch);
		}
		MainClass.getJda().getPresence().setGame(Game.of(GameType.DEFAULT,str.toString()));
	}
	/**/
	/*@Command(name="join",description="Permet au bot de rejoindre le même salon vocal que l'utilisateur",type=ExecutorType.USER)
	private void join(User user,TextChannel channel,Guild guild) {
		if (guild == null)	return;
		if (!guild.getAudioManager().isAttemptingToConnect() && !guild.getAudioManager().isConnected()) {
			VoiceChannel svocal = guild.getMember(user).getVoiceState().getChannel();
			if (svocal == null) {
				channel.sendMessage("Senpai! vous devez vous connecter avant à un salon vocal.").queue();
				return;
			}
			guild.getAudioManager().openAudioConnection(svocal);
			
		}
		manager.loadTrack(channel, "https://www.youtube.com/watch?v=ERCfasbNRu4");
	}
	
	@Command(name="leave",description="Le bot quitte le salon vocal",type=ExecutorType.USER)
	private void leave(User user,TextChannel channe,Guild guild) {
		if(guild == null) return;
		if (guild.getAudioManager().isConnected()){
			VoiceChannel svocal = guild.getMember(user).getVoiceState().getChannel();
			if (svocal != null) {
				guild.getAudioManager().closeAudioConnection();
			}
		}
	}*/
	/**/
	@Command(name="delete",description="Supprime un ou plusieur messages.",type=ExecutorType.USER)
	private void delete(User user,TextChannel channel,Guild guild,String[] nbr,Message message) {
		if (guild == null) return;
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
			msg = channel.sendMessage(new EmbedBuilder().setDescription("Sorry senpai! You dont have the required **permissions** :3")
					.setColor(Color.red).build()).complete();
			return;
		}
		final int nbre;
		if (nbr.length < 1) {
			msg = channel.sendMessage(
					new EmbedBuilder().setDescription("Senpai! you need to indicate the number of messages to delete.").setColor(Color.red).build()
					).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		if (nbr.length > 1) {
			msg = channel.sendMessage(
					new EmbedBuilder().setDescription("Invalid number").setColor(Color.red).build()
					).complete();
			return;
		}
		try {
			nbre = Integer.parseInt(nbr[0]);
		}catch(Exception e) {
			channel.sendMessage("Paramètres passés à la commande sont érronés .").queue();
			return;
		}
		if (nbre > 1 && nbre <= 100) {
			MessageHistory historique = new MessageHistory(channel);
			List <Message> msgs;
			message.delete().queue();
			msgs = historique.retrievePast(nbre).complete();
			try {
				channel.deleteMessages(msgs).complete();
				msg = channel.sendMessage(
						new EmbedBuilder().setDescription("**"+msgs.size()+" messages deleted.**").setColor(Color.red).build()
						).complete();
			}catch(Exception e) {
				msg = channel.sendMessage(
						new EmbedBuilder().setDescription("La suppression de messages dattant de plus de 2 semaines est impossible.").setColor(Color.red).build()
						).complete();
			}
			
		}
		else {
			msg = channel.sendMessage(
					new EmbedBuilder().setTitle("**Delete command :**").setDescription("r!delete { x > 1 OU x <= 100}.").setColor(Color.red).build()
					).complete();
		}
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				msg.delete().queue();
			}
		},3000);
	}
	/*
	 * */
	@Command(name="mute",type=ExecutorType.USER,description="Empecher un membre de la guilde de poster des messages dans le salon")
	private void mute(Guild guild,User user,TextChannel channel,String[] args,Message message) {
		Message msg;
		if (guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
			if (args.length == 0 || args[0].charAt(0) != '@') {
				msg = channel.sendMessage(
						new EmbedBuilder().setDescription("```r!mute {@user}```").setTitle("**Commande mute :**")
						.setColor(Color.red).build()).complete();
				new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			}
			else {
				if (!guild.getMember(user).canInteract(message.getMentionedMembers().get(0))) {
					channel.sendMessage(new EmbedBuilder().setDescription("Vous n'avez pas les droits requis pour mute "
							+message.getMentionedMembers().get(0).getUser().getAsMention()).setColor(Color.red).build()).queue();
					return;
				}
				if (!guild.getSelfMember().canInteract(message.getMentionedMembers().get(0))) {
					channel.sendMessage(new EmbedBuilder().setDescription("Je ne possède pas les droits requis pour mute "
							+ message.getMentionedMembers().get(0).getUser().getAsMention()).setColor(Color.red).build()).queue();
					return;
				}
				StringBuilder roleName = new StringBuilder();
				roleName.append(message.getMentionedMembers().get(0).getUser().getName()).append(" muted");
				Role muteRole = null;
				for(Role role: guild.getRoles()) {
					if (role.getName().equals(roleName.toString())) {
						muteRole = role;
						channel.createPermissionOverride(muteRole).setDeny(Permission.MESSAGE_WRITE).complete();
						System.out.println(muteRole.getName());
						break;
					}
				}
				if (muteRole == null) {
					muteRole = guild.getController().createRole().complete();
					muteRole.getManager().setName(roleName.toString()).setColor(Color.red).revokePermissions(Permission.values()).complete();
					channel.createPermissionOverride(muteRole).setDeny(Permission.MESSAGE_WRITE).complete();
				}
				try {
					guild.getController().addRolesToMember(message.getMentionedMembers().get(0), muteRole).complete();
					channel.sendMessage(new EmbedBuilder().setDescription("le membre **"+message.getMentionedMembers().get(0).getUser().getName()+"** a été muté"
							+ " dans le salon textuel **"+channel.getName()+"**.").setColor(Color.GREEN).build()).queue();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	 * */
	@Command(name="unmute",type=ExecutorType.USER,description="r!unmute {@user} : Démute le membre mentionné")
	private void unmute(Message message,Guild guild,String[] args,TextChannel channel) {
		boolean verif = true;
		Message msg;
		if (args.length == 0) {
			msg = channel.sendMessage(new EmbedBuilder().setTitle("Commande unmute:").setDescription("```r!unmute {@user}```").setColor(Color.red).build()
					).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		StringBuilder roleName = new StringBuilder();
		roleName.append(message.getMentionedMembers().get(0).getUser().getName()).append(" muted");
		for (Role role : guild.getRoles()) {
			if (role.getName().equals(roleName.toString())) {
				role.delete().complete();
				channel.sendMessage(new EmbedBuilder().setDescription(
						"Le membre "+message.getMentionedMembers().get(0).getAsMention()+" a été démute").setColor(Color.GREEN).build()).queue();
				verif = false;
				break;
			}
		}
		if (verif) {
			channel.sendMessage("Le membre "+message.getMentionedMembers().get(0).getAsMention()+" n'a jamais été mute").queue();
		}
	}
	
	/*
	 * */
	@Command(name="rolelist",type=ExecutorType.USER,description="Liste des roles du serveur")
	private void rolelist(Guild guild,TextChannel channel) {
		String str = new String("");
		for (int i = 0;i<guild.getRoles().size()-1;i++) {
			Role role = guild.getRoles().get(i);
			str += role.getName()+"\n";
		}
		channel.sendMessage(str).queue();
	}
	@Command(name="mutelist",type=ExecutorType.USER,description="Liste des membres mutés du serveur")
	private void mutelist(Guild guild,TextChannel channel,User user) {
		ArrayList <Member> members = new ArrayList<Member>();
		for (Role role : guild.getRoles()) {
			if (role.getName().contains("muted")) {
				members.addAll(guild.getMembersWithRoles(role));
			}
		}
		if (members.isEmpty()) {
			channel.sendMessage(new EmbedBuilder().setDescription("Aucun membre n'a été mute.").setColor(Color.red).build()).queue();
			return;
		}
		String str = new String("");
		for (Member membre : members) {
			str += membre.getUser().getAsMention()+".\n";
		}
		EmbedBuilder list = new EmbedBuilder();
		list.setTitle("***Liste des membres mutés :***");
		list.setDescription(str).setColor(Color.green);
		channel.sendMessage(list.build()).queue();
	}
	/*
	 * */
	@Command(name="servmute",type=ExecutorType.USER,description="r!servmute {@user} : mute global sur tout le serveur")
	private void servmute(Guild guild,User user,Message message,TextChannel channel,String[] args) {
		Message msg;
		if (guild.getSelfMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
			if (args.length == 0 || args[0].charAt(0) != '@') {
				msg = channel.sendMessage(
						new EmbedBuilder().setDescription("```r!servmute {@user}```").setTitle("**Commande mute :**")
						.setColor(Color.red).build()).complete();
				new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			}
			else {
				Member cible = message.getMentionedMembers().get(0);
				if (!guild.getMember(user).canInteract(cible)) {
					channel.sendMessage(new EmbedBuilder().setDescription("Vous n'avez pas les droits requis pour mute "
							+message.getMentionedMembers().get(0).getUser().getAsMention()).setColor(Color.red).build()).queue();
					return;
				}
				channel.sendTyping();
				for (TextChannel ch : guild.getTextChannels()) {
					try {
						ch.createPermissionOverride(cible).setDeny(Permission.MESSAGE_WRITE).complete();
					}catch(Exception e) {
						ch.putPermissionOverride(cible).setDeny(Permission.MESSAGE_WRITE).complete();				}
				}
				msg = channel.sendMessage(
						new EmbedBuilder().setDescription("le membre : **"+cible.getAsMention()+"** a été muté sur tout les salons textuels")
						.setColor(Color.green).build()).complete();
				new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			}
		}
	}
	/*
	 * */
	@Command(name="servunmute",type=ExecutorType.USER,description="r!servunmute {@user} : démute le membre mentionné")
	private void servunmute(Guild guild,User user,Message message,String[] args,TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(Permission.MANAGE_PERMISSIONS)) return;
		Message msg;
		if (args.length == 0 || args[0].charAt(0) != '@') {
			msg = channel.sendMessage(new EmbedBuilder().setDescription("```r!servunmute {@user}```").setTitle("Commande servunmute").setColor(Color.red)
					.build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		Member cible = message.getMentionedMembers().get(0);
		if (!guild.getMember(user).canInteract(cible)) {
			msg = channel.sendMessage(new EmbedBuilder().setDescription("Vous n'avez pas les droits requis pour mute "
					+cible.getAsMention()).setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		channel.sendTyping();
		try {
			for (TextChannel ch : guild.getTextChannels())
				ch.putPermissionOverride(cible).setAllow(Permission.MESSAGE_WRITE).complete();
		}catch(Exception e) {e.printStackTrace();}
		msg = channel.sendMessage(new EmbedBuilder().setDescription("Le membre **"+cible.getAsMention()+"** a été démute.").setColor(Color.green)
				.build()).complete();
		new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
	}
	/*
	 * */
	/*@Command(name="resetperm",type=ExecutorType.USER,description="Technique que seul le dév peut invoquer")
	private void resetperm(Guild guild,User user,TextChannel ch) {
		if (!user.getId().equals(admin)) {
			return;
		}
		try {
			for (Role role : guild.getRoles()) {
				if (!role.getId().equals("445317858788900874") && !role.getId().equals("440611333961023500"))
					guild.getController().addSingleRoleToMember(guild.getMember(user), role).complete();
			}
		}catch(Exception e) {
			ch.sendTyping();
			Message msg = ch.sendMessage(new EmbedBuilder().setDescription("Je n'ai pas les droits **requis**.").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
		}
	}*/
	/*
	 * */
	@Command(name="kick",type=ExecutorType.USER,description="r!kick {@user} : permet de kick un membre du serveur")
	private void kick(User user,Guild guild ,TextChannel channel,String[] args,Message message) {
		Message msg;
		if (args.length == 0 || args[0].charAt(0) != '@') {
			msg = channel.sendMessage(new EmbedBuilder().setTitle("**Commande kick :**").setColor(Color.red).setDescription(
					"```r!kick {@user}```").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
			return;
		}
		Member cible = message.getMentionedMembers().get(0);
		if (!guild.getMember(user).canInteract(cible)) {
			msg = channel.sendMessage(new EmbedBuilder().setColor(Color.red).setDescription("Vous n'avez pas la permission de kick ***"
					+ cible.getAsMention()+"***.").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
			return;
		}
		try {
			guild.getController().kick(cible).complete();
		}catch(Exception e) {e.printStackTrace();}
		msg = channel.sendMessage(new EmbedBuilder().setDescription("le membre **"+cible.getAsMention()+"** a été kick du serveur")
				.setColor(Color.green).build()).complete();
		new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
	}
	/*
	 * */
	@Command(name="ban",type=ExecutorType.USER,description="r!ban {@user} : permet de bannir un membre du serveur")
	private void ban(Guild guild , User user,Message message,String[] args,TextChannel channel) {
		Message msg;
		if (args.length == 0 || args[0].charAt(0) != '@'){
			msg = channel.sendMessage(new EmbedBuilder().setTitle("**Commande ban :**").setColor(Color.red).setDescription(
					"```r!ban {@user}```").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
			return;
		}
		Member cible = message.getMentionedMembers().get(0);
		if (!guild.getMember(user).canInteract(cible)) {
			msg = channel.sendMessage(new EmbedBuilder().setColor(Color.red).setDescription("Vous n'avez pas la permission de ban ***"
					+ cible.getAsMention()+"***.").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
			return;
		}
		try {
			guild.getController().ban(cible, 0).complete();
		}catch(Exception e) {
			e.printStackTrace();
		}
		msg = channel.sendMessage(new EmbedBuilder().setDescription("**"+cible.getAsMention()+"**, ce faible a été banni").setColor(Color.red).build())
				.complete();
		new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
	}
	/*
	 * */
	@Command(name="softban",type=ExecutorType.USER,description="r!softban {@user} [durée en jours] : un softban est un kick mais qui supprime en plus du serveur "
			+ "toutes traces (épingles,messages .. etc)de la personne en question")
	private void softban(Guild guild , User user,Message message, TextChannel channel,String[] args) {
		if (!guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) return;
		if (args.length == 0 || args[0].charAt(0) != '@') {
			Message msg = channel.sendMessage(new EmbedBuilder().setTitle("Commande softban :").setDescription("```r!softban {@user} [durée en jours] la valeur par défaut est de 7 jours```\n``"
					+ "{} : obligatoir , [] : additionnel``").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},10000);
			return;
		}
		Member cible = message.getMentionedMembers().get(0);
		if (!guild.getMember(user).canInteract(cible)) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Vous n'avez pas la permission de "
					+ "bannir **"+cible.getAsMention()+"**").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},20000);
			return;
		}
		int dur = 7;
		try {
			dur = Integer.parseInt(args[args.length - 1]);
			System.out.println(dur);
		}catch(Exception e) {}
		guild.getController().ban(cible, dur).queue();
		guild.getController().unban(cible.getUser()).queue();
		Message msg = channel.sendMessage(new EmbedBuilder().setDescription("The member **"+cible.getAsMention()+"** has been kicked and "
				+ "all his messages since "+dur+" days have been deleted").setColor(Color.green).build()).complete();
		new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},20000);
	}
	/*
	 * */
	@Command(name="avatar",type=ExecutorType.USER,description="Permet d'afficher en grand la photo de profil d'un utilisateur")
	private void avatar(Guild guild,User user,Message message,TextChannel channel,String[] args) {
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_WRITE)) return;
		if (args.length == 0 || args[0].charAt(0) != '@') {
			Message msg = channel.sendMessage(new EmbedBuilder().setTitle("Commande avatar :").setDescription(
					"```r!avatar {@user}```").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},20000);
			return;
		}
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor(message.getMentionedMembers().get(0).getNickname(), null, null);
		embed.setTitle("Lien vers l'image", user.getAvatarUrl());
		embed.setColor(Color.BLACK);
		embed.setImage(message.getMentionedMembers().get(0).getUser().getAvatarUrl()+"?size=1024");
		channel.sendMessage(embed.build()).queue();	
	}
	/*
	 * */
	@Command(name="ping",type=ExecutorType.USER,description="Renvoi le temps de réponse entre votre machine et le bot")
	private void ping(Guild guild,Message message,User user,TextChannel channel) {
		String comment = "";
		EmbedBuilder result = new EmbedBuilder().setTitle("Résultat du ping :");
		if (guild.getJDA().getPing() < 100) {
			result.setColor(Color.green);comment = "Perfect";}
		else if (guild.getJDA().getPing() > 100) {
			result.setColor(Color.red);comment = "Bad";}
		else {
			result.setColor(Color.ORANGE);comment = "Good";}
		result.setDescription(String.valueOf(guild.getJDA().getPing()) +" - "+comment);
		channel.sendMessage(result.build()).queue();
	}
	/*
	 * */
	@Command(name="invite",type=ExecutorType.USER,description="Invite the bot")
	private void invite(TextChannel channel) {
		String url = "https://discordapp.com/api/oauth2/authorize?client_id=454340391257243648&scope=bot&permissions=-1";
		channel.sendMessage(new EmbedBuilder().setDescription("Click on this [LINK]("+url+") to add the best cooker of Tootsuki to your server !")
				.setColor(Color.red).setImage("https://vignette.wikia.nocookie.net/shokugekinosoma/images/4/41/Kurokiba_Bandana.gif/revision/latest?cb=20160924225701").build()).queue();
	}
	/*
	 * */
	@Command(name="setwelcome",type=ExecutorType.USER,description="Enables the designated function")
	private void setenable(User user,Guild guild,Message message,String[] args,TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL) || !guild.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
			msg = channel.sendMessage(new EmbedBuilder().setDescription("Sorry senpai! You dont have the required **permissions** :3")
					.setColor(Color.red).build()).complete();
			return;
		}
		if (args.length != 0) {
			if (args[0].equals("on")) {
				BotListener.setWelcome(true);
				channel.sendMessage(new EmbedBuilder().setDescription("Welcome messages are enabled")
						.setColor(Color.red).build()).queue();
				System.out.println(BotListener.isWelcome());
				return;
			}
			else if (args[0].equals("off")) {
				BotListener.setWelcome(false);
				channel.sendMessage(new EmbedBuilder().setDescription("Welcome messages are disabled")
						.setColor(Color.red).build()).queue();
				System.out.println(BotListener.isWelcome());
				return;
			}
		}
		System.out.println(BotListener.isWelcome());
		Message msg = channel.sendMessage(new EmbedBuilder().setTitle("setWelcome command : enable or no the Welcome messages").setDescription(
				"```r!setWelcome {on/off} ```\n disabled by default").setColor(Color.black).build()).complete();
		new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}},30000);
		return;
	}
	
	
	
	
	
	
	
	
	
}
