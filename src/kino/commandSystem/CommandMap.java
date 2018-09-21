package kino.commandSystem;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import kino.commandSystem.Command.ExecutorType;
import kino.commands.CommandesNSFW;
import kino.commands.CommandesSimples;
import kino.commands.CommandesSpeciales;
import main.MainClass;
import kino.commandSystem.Command.ExecutorType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public final class CommandMap {
	private final MainClass bot;
	private final Map<String,SimpleCommand> commands = new HashMap<>();
	private final String tag ="r!";
	/**/
	public CommandMap(MainClass bot){
		this.bot = bot;
		registerCommands(new CommandesSimples(bot, this),new CommandesNSFW(bot, this),new CommandesSpeciales(bot, this));
	}
	/**/
	public Collection<SimpleCommand> getCommands() {
		return commands.values();
	}
	public String getTag() {
		return tag;
	}
	
	public void registerCommand(Object o) {
		for (Method methode : o.getClass().getDeclaredMethods()) {
			if (methode.isAnnotationPresent(Command.class)){
				Command command = methode.getAnnotation(Command.class);
				methode.setAccessible(true);
				SimpleCommand simpleCommand = new SimpleCommand(command.name(),command.description(),command.type(),o,methode);
				commands.put(command.name(), simpleCommand);
			}
		}
	}
	public void registerCommands(Object...objects) {
		for (Object o : objects) registerCommand(o);
	}
	private Object[] getCommand(String command) {
		String[] splitCommand = command.split(" ");
		String[] args = new String[splitCommand.length - 1];
		for (int i=1;i<splitCommand.length;i++)
			args[i-1] = splitCommand[i];
		SimpleCommand simpleCommand = commands.get(splitCommand[0]);
		return new Object[] {simpleCommand,args};
	}
	
	public void commandConsole(String command) {
		Object[] object = getCommand(command);
		if (object[0] == null || ((SimpleCommand)object[0]).getExecutorType().equals(ExecutorType.USER)) {
			System.out.println("Commande null ou incompatible");return;}
		//execute apres
		try{
	        execute(((SimpleCommand)object[0]), command,(String[])object[1], null);
	    }catch(Exception exception){
	        System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
	    }
	}
	public boolean commandUser(User user,String command,Message message) {
		Object[] object = getCommand(command);
		System.out.println(command);
		if (object[0] == null || ((SimpleCommand)object[0]).getExecutorType().equals(ExecutorType.CONSOLE)) {
			System.out.println("Commande null ou incompatible");return false;}
		//code execute
		try{
	       execute(((SimpleCommand)object[0]), command,(String[])object[1], message);
	    }catch(Exception exception){
	       System.out.println("La methode "+((SimpleCommand)object[0]).getMethod().getName()+" n'est pas correctement initialisé.");
	    }
		return true;
	}
	
	private void execute(SimpleCommand simpleCommand, String command, String[] args, Message message) throws Exception{
        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            if(parameters[i].getType() == String[].class) objects[i] = args;
            else if(parameters[i].getType() == User.class) objects[i] = message == null ? null : message.getAuthor();
            else if(parameters[i].getType() == TextChannel.class) objects[i] = message == null ? null : message.getTextChannel();
            else if(parameters[i].getType() == PrivateChannel.class) objects[i] = message == null ? null : message.getPrivateChannel();
            else if(parameters[i].getType() == Guild.class) objects[i] = message == null ? null : message.getGuild();
            else if(parameters[i].getType() == String.class) objects[i] = command;
            else if(parameters[i].getType() == Message.class) objects[i] = message;
            else if(parameters[i].getType() == JDA.class) objects[i] = MainClass.getJda();
            else if(parameters[i].getType() == MessageChannel.class) objects[i] = message == null ? null : message.getChannel();
        }
        simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
    }
}
