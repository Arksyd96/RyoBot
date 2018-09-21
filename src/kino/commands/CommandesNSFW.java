package kino.commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import kino.commandSystem.Command;
import kino.commandSystem.CommandMap;
import kino.commandSystem.Command.ExecutorType;
import main.MainClass;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandesNSFW {
	private final String googleKey ="AIzaSyDxuu89IHroU0lVgpIrHNCHyhlKFuK2frA";
	private final String engineKey = "004389539208966928558:jt-ssjoe0cm";
	private final String admin = "220923868292972547";
	private final MainClass bot;
	private CommandMap commandMap;
	private static int indx = 0;
	
	/**/
	public CommandesNSFW(MainClass bot,CommandMap commandMap){
		this.bot = bot;
		this.commandMap = commandMap;
	}
	/**/
	@Command(name="rule34",type=ExecutorType.USER)
	private void rule34(Guild guild,User user,String[] args,TextChannel channel) throws IOException {
		if (args.length == 0) return;
		if (!channel.isNSFW()) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Hmm "+user.getAsMention()+" Senpai! This kind "
					+ "of stuff belongs to the NSFW channels ").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		}
		//prep du lien :
		String url = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&tags=";
		String urlRescue = "https://rule34.xxx/index.php?page=dapi&s=post&q=index&tags=";
		String tags = new String(args[0]);
		String tagsRescue = new String(args[0]);
		for (int i=1;i<args.length;i++) {
			tags += "+"+args[i];
			tagsRescue += "_"+args[i];
		}
		/*String page = String.valueOf((int)(Math.random()*5.0));
		System.out.println(Character.getNumericValue(page.charAt(0))-1);*/
		url += tags+"&pid=0";
		urlRescue += tagsRescue+"&pid=0";
		String pic = null;
		try {
			URL lien = new URL(url);
			URL lienRescue = new URL(urlRescue);
			InputStream is = lien.openStream();
			InputStream isRescue = lienRescue.openStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);
			Document docRescue = builder.parse(isRescue);
			Element root = doc.getDocumentElement();
			Element rootRescue = docRescue.getDocumentElement();
			NodeList listNoeud = root.getElementsByTagName("post");
			NodeList listNoeudRescue = rootRescue.getElementsByTagName("post");
			int rand = (int)(Math.random()*listNoeud.getLength());
			int randRescue = (int)(Math.random()*listNoeudRescue.getLength());
			Element post = (Element)listNoeud.item(rand);
			try {
				pic = post.getAttribute("file_url");
			}catch(Exception e) {}
			if (pic == null);{
				System.out.println("ici");
				post = (Element)listNoeudRescue.item(randRescue);
				try {
					pic = post.getAttribute("file_url");
				}catch(Exception e) {
					channel.sendMessage("Aucun resultat pour ces tags ! essayez d'autre tags ..").queue();
					e.printStackTrace();
					return;
				}
			}
			channel.sendMessage(pic).queue();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * */
	@Command(name="hentaihaven",type=ExecutorType.USER,description="r!hentaihaven {tags} : renvoi les 5 premiers resultat de la recherche dans le site hentaihaven :3")
	private void hentaihaven(Guild guild,TextChannel channel,User user,String[] args) {
		if (args.length == 0) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("```g!hentaihaven {tags}```")
					.setTitle("Commande 'hentaiHaven' :").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		};
		if (!channel.isNSFW()) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Hmm "+user.getAsMention()+" Senpai! This kind "
					+ "of stuff belongs to the NSFW channels ").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		}
		String tags = args[0];
		for (int i = 1;i<args.length;i++)
			tags += "+"+args[i];
		String url = "https://www.googleapis.com/customsearch/v1?key="+googleKey+"&cx="+engineKey+"&q="+tags;
		System.out.println(url);
		Unirest.get(url).asJsonAsync(new Callback<JsonNode>() {
			public void completed(HttpResponse<JsonNode> arg0) {
				JSONObject obj = arg0.getBody().getObject().getJSONObject("queries");
				String count = obj.getJSONArray("request").getJSONObject(0).getString("totalResults");
				if (count.equals("0")) {
					Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Aucun résultat pour cette recherche .."
							+ "essayez avec d'autres ***tags***.").setColor(Color.red).build()).complete();
					new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
					return;
				}
				EmbedBuilder embed = new EmbedBuilder().setTitle("__Resultat de la recherche :__").setColor(Color.black);
				JSONArray items = arg0.getBody().getObject().getJSONArray("items");
				String desc = new String("");
				for (int i = 0;i < (Integer.valueOf(count) > 5 ? 5 : Integer.valueOf(count));i++){
					JSONObject item = items.getJSONObject(i);
					desc += "["+(i+1)+"] - [>]("+(i+1)+") ["+item.getString("title")+"]("+item.getString("link")+")\n";
				}
				embed.setDescription(desc);
				channel.sendMessage(embed.build()).queue();
			}
			public void failed(UnirestException arg0) {
				
			}
			public void cancelled() {
			}
		});
	}
	/*
	 * */
	@Command(name="hh",type=ExecutorType.USER)
	private void hh(TextChannel channel,User user ,Guild guild,String[] args) {
		if (!channel.isNSFW()) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Hmm "+user.getAsMention()+" Senpai! This kind "
					+ "of stuff belongs to the NSFW channels ").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		}
		String tags = args[0];
		for (int i=1;i<args.length;i++)
			tags += "+"+args[i];
		Unirest.get("http://motyar.info/webscrapemaster/api/?xpath=a&url=http://hentaihaven.org/search/"+tags).asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> arg0) {
				try {
					JSONArray array = arg0.getBody().getArray();
					String result = new String("");
					for (int i=0;i<array.length();i++) {
						JSONObject obj = array.getJSONObject(i);
						try {
							if (obj.getString("class").equals("brick-title")) {
								result = obj.getString("href");
								break;
							}
						}catch(Exception e) {}
					}
					if (result.equals("")) return;
					final String lien = result;
					Unirest.get("http://motyar.info/webscrapemaster/api/?xpath=a&url="+lien+"&xpath=meta").asJsonAsync(new Callback<JsonNode>() {
						public void completed(com.mashape.unirest.http.HttpResponse<JsonNode> hr) {
							JSONArray array = hr.getBody().getArray();
							String tags = new String("");String name = new String("");
							String image = new String("");String title = new String("");
							for (int i=0;i<array.length();i++) {
								JSONObject obj = array.getJSONObject(i);
								try {
									if (obj.getString("property").equals("article:tag"))
										tags += obj.getString("content")+" ";
									if (obj.getString("property").equals("og:image"))
										image = obj.getString("content");
									if (obj.getString("property").equals("og:title"))
										title = obj.getString("content");
									if (obj.getString("property").equals("article:section"))
										name = obj.getString("content");
								}catch(Exception e) {}
							}
							final String titre = title;
							final String nom = name;
							final String lesTags = tags;
							final String img = image;
							Unirest.get("http://motyar.info/webscrapemaster/api/?xpath=a&url="+lien+"&xpath=tr").asJsonAsync(new Callback<JsonNode>() {
								public void completed(com.mashape.unirest.http.HttpResponse<JsonNode> arg0) {
									JSONArray array = arg0.getBody().getArray();
									final String alterName = array.getJSONObject(0).getString("text");
									final String dateSortie = array.getJSONObject(1).getString("text");
									final String nombreEp = array.getJSONObject(2).getString("text");
									Unirest.get("http://motyar.info/webscrapemaster/api/?xpath=a&url="+lien+"&xpath=source").asJsonAsync(new Callback<JsonNode>() {
										public void completed(com.mashape.unirest.http.HttpResponse<JsonNode> arg) {
											HashMap<String,String> liens = new HashMap<>();
											JSONArray array = arg.getBody().getArray();
											String dl = new String("");
											for (int i=0;i<array.length();i++) {
												JSONObject obj = array.getJSONObject(i);
												if (!obj.getString("data-res").equals("0")) {
													liens.put(obj.getString("res"), obj.getString("src").replaceAll(" ", "_"));
												}
											}
											for (String c : liens.keySet()) {
												dl += "["+c+"]("+liens.get(c)+") ";
											}
											EmbedBuilder embed = new EmbedBuilder().setTitle("__**"+titre+"**__:", lien).setColor(Color.BLACK)
													.setDescription("__**Info:**__\n"
													+ "title : "+nom+"\n"
													+dateSortie+"\n"
													+nombreEp+"\n"
													+alterName+"\n\n"
													+ "__**Tags:**__\n"+lesTags+"\n\n"
													+ "__**Download/streaming:**__\n"+dl).setImage(img+"?size=1024");
											channel.sendMessage(embed.build()).queue();
										}
										public void cancelled() {}; {};
										public void failed(UnirestException arg0) {}; {};
									});
									
								}
								public void failed(UnirestException arg0) {}; {};
								public void cancelled() {}; {};
							});
							
						}
						public void cancelled() {}; {};
						public void failed(UnirestException arg0) {}; {};
					});
				}catch(Exception e) {e.printStackTrace();}
			}
			@Override
			public void cancelled() {System.out.println("cancelled");}
			@Override
			public void failed(UnirestException arg0) {System.out.println("failed");}
		});
	}
	/*
	 * */
	@Command(name="lewd",type=ExecutorType.USER,description="Voici la commande 'r!lewd' , je vous laisse le plaisir d'essayer..")
	private void lewd(Guild guild,User user,TextChannel channel) throws IOException {
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		if (!channel.isNSFW()) {
			Message msg = channel.sendMessage(new EmbedBuilder().setDescription("Hmm "+user.getAsMention()+" Senpai! This kind "
					+ "of stuff belongs to the NSFW channels ").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		}
		Unirest.get("https://nekos.life/api/lewd/neko").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.green).
						setImage(hr.getBody().getObject().getString("neko")).build())
				.queue();
			}
			@Override
			public void failed(UnirestException arg0) {
				channel.sendMessage("erreur Failed").queue();
			}
			@Override
			public void cancelled() {
				channel.sendMessage("erreur Cancelled").queue();
			}
		});
	}
}
