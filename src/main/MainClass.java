package main;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import kino.listener.BotListener;
import kino.commandSystem.CommandMap;
import kino.listener.BotListener;
import kino.commandSystem.CommandMap;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class MainClass implements Runnable{
	/**/
	private static JDA jda;
	private final CommandMap commandMap = new CommandMap(this);
	private boolean running;
	private Scanner sc = new Scanner(System.in);
	/**/
	/*
	 * 
	 */
	public static void main(String[] args) throws LoginException {
		MainClass bot = new MainClass();
		try {
			new Thread(bot, "bot").start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public MainClass() throws LoginException {
		jda = new JDABuilder(AccountType.BOT).setToken("Token Here").setStatus(OnlineStatus.DO_NOT_DISTURB)
				.setGame(Game.playing("Ready to use")).addEventListener(new BotListener(commandMap)).buildAsync();
		System.out.println("connecté");
	}
	
	/*
	 * 
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isRunning() {
		return running;
	}
	public static JDA getJda() {
		return jda;
	}
	@Override
	public void run() {
		running = true;
		while(running) {
			if (sc.hasNextLine()) commandMap.commandConsole(sc.nextLine());
		}
		sc.close();
		System.out.println("Fin.");
		
	}
}