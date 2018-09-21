package kino.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

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
import net.dv8tion.jda.core.requests.restaction.order.CategoryOrderAction;

public class CommandesSpeciales {
	private final String unisplash = "https://api.unsplash.com/search/photos/?client_id=54813c89d617f73bafd770d7fbd56ed5a123f999d10cfec08cccd50ab368bd9a&query=";
	private final String admin = "220923868292972547";
	private final MainClass bot;
	private CommandMap commandMap;
	/**/
	public CommandesSpeciales(MainClass bot,CommandMap commandMap) {
		this.bot = bot;
		this.commandMap = commandMap;
	}
	/**/
	@Command(name="cat",type=ExecutorType.USER,description="Affiche l'image un chat aleatoire")
	private void cat(Guild guild,User user,TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		Unirest.get("http://aws.random.cat/meow").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.green).
						setImage(hr.getBody().getObject().getString("file")).build())
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
	/*
	 * */
	@Command(name="choose",type=ExecutorType.USER,description="Asks the bot to make a choice between the ones given")
	private void choose(Guild guild,TextChannel channel,String[] args) {
	}
	/*
	 * */
	@Command(name="anime",type=ExecutorType.USER,description="Search for an anime")
	private void anime(Guild guild,User user,String[] args,TextChannel channel) {
		if (args.length < 1) {
			channel.sendMessage(new EmbedBuilder().setTitle("Command anime : search for an anime").setDescription(
					"```r!anime {title}```").setColor(Color.black).build()).queue();
			return;
		}
		String tags = args[0];
		for (int i=1;i<args.length;i++)
			tags += "+"+args[i];
		Unirest.get("https://kitsu.io/api/edge/anime?filter[text]="+tags).asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> response) {
				JSONObject obj = response.getBody().getObject().getJSONArray("data").getJSONObject(0);
				String airedD = "N/A to ";
				String airedF = "N/A";
				if (!obj.getJSONObject("attributes").isNull("endDate")) {
					airedF = obj.getJSONObject("attributes").getString("endDate");}
				if (!obj.getJSONObject("attributes").isNull("startDate")) {
					airedD = obj.getJSONObject("attributes").getString("startDate") + " to ";}
				String synopsis = obj.getJSONObject("attributes").getString("synopsis");
				String title = obj.getJSONObject("attributes").getString("canonicalTitle");
				String title_jp = "";
				String title_ja = "";
				if (obj.getJSONObject("attributes").getJSONObject("titles").has("en_jp") && !obj.getJSONObject("attributes").getJSONObject("titles").isNull("en_jp")) 
					title_jp = obj.getJSONObject("attributes").getJSONObject("titles").getString("en_jp");
				if (obj.getJSONObject("attributes").getJSONObject("titles").has("ja_jp") && !obj.getJSONObject("attributes").getJSONObject("titles").isNull("ja_jp")) 
					title_ja = obj.getJSONObject("attributes").getJSONObject("titles").getString("ja_jp");
				String rating = obj.getJSONObject("attributes").getString("averageRating"); 
				String statue = obj.getJSONObject("attributes").getString("status");
				String ageRating = obj.getJSONObject("attributes").getString("ageRatingGuide");
				String imageUrl = obj.getJSONObject("attributes").getJSONObject("posterImage").getString("original");
				String coverUrl = obj.getJSONObject("attributes").getJSONObject("coverImage").getString("original"); 
				int epCount = obj.getJSONObject("attributes").getInt("episodeCount");
				String ytTrailer = "https://www.youtube.com/watch?v=" + obj.getJSONObject("attributes").getString("youtubeVideoId");
				String epLenght = "N/A";
				if (!obj.getJSONObject("attributes").isNull("episodeLength")) {
					epLenght = String.valueOf(obj.getJSONObject("attributes").getInt("episodeLength"));
				}
				/**/
				String desc = "**Synopsis :** \n```"+synopsis+"```\n"
						+"**Japaness title :** "+title_ja+" , "+title_jp+"\n"
						+"**Rating :** "+rating+"\n"
						+"**Aired :** "+airedD+airedF+"\n"
						+"**Status :** "+statue+"\n"
						+"**Age Rating :** "+ageRating+"\n"
						+"**Episodes count :** "+epCount+"\n"
						+"**Episode length :** "+epLenght+"\n"
						+"**Trailer :**\n"+ytTrailer+"\n";
				/**/		
				channel.sendMessage(new EmbedBuilder().setTitle("**"+title+"**").setImage(coverUrl)
					.setFooter("Results from Kitsu",null).setThumbnail(imageUrl+"?size=512")
					.setColor(Color.BLACK).setDescription(desc).build()).queue();
				
			}
			@Override
			public void cancelled() {System.out.println("cancelled");}
			@Override
			public void failed(UnirestException e) {e.printStackTrace();}
		});
	}
	/*
	 * */
	@Command(name="8ball",type=ExecutorType.USER,description="Absolutely ...")
	private void c8ball(Guild guild,TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		Unirest.get("https://nekos.life/api/v2/img/8ball").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.BLACK).
						setImage(hr.getBody().getObject().getString("url")).build())
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
	/*
	 * */
	@Command(name="lizard",type=ExecutorType.USER,description="vous renvoie un ..un .. un lézard , oui !")
	private void lizard(Guild guild,TextChannel channel) {
		if (!guild.getSelfMember().hasPermission(Permission.MESSAGE_EMBED_LINKS)) return;
		Unirest.get("https://nekos.life/api/lizard").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.BLACK).setDescription("Ohh -oh -Un lézard").
						setImage(hr.getBody().getObject().getString("url")).build())
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
	/*
	 * */
	@Command(name="pat",type=ExecutorType.USER,description="g!tapoter [@user] : cette commande est tellement dur a comprendre...")
	private void pat(Guild guild,TextChannel channel,User user,Message message,String[] args) {
		if (args.length == 0 || args[0].charAt(0) != '@') {
			Message msg = channel.sendMessage(new EmbedBuilder().setColor(Color.red).setTitle("Commande 'tapoter' :")
					.setDescription("```g!tapoter {@user}```").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		Unirest.get("https://nekos.life/api/pat").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(message.getMentionedMembers().get(0).getAsMention()+" Senpai! "+user.getAsMention()+" est entrain de vous "
								+ "tapoter :3 si c'est pas mignon ...(◍•ᴗ•◍)❤\n"+hr.getBody().getObject().getString("url")).queue();
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
	/*
	 * */
	@Command(name="hug",type=ExecutorType.USER,description="g!hug {@user} : faut apprendre anglais aussi ..")
	private void hug(Guild guild,TextChannel channel,User user,Message message,String[] args) {
		if (args.length == 0 || args[0].charAt(0) != '@') {
			Message msg = channel.sendMessage(new EmbedBuilder().setColor(Color.red).setTitle("Commande 'tapoter' :")
					.setDescription("```g!tapoter {@user}```").build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 10000);
			return;
		}
		Unirest.get("https://nekos.life/api/hug").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				channel.sendMessage(message.getMentionedMembers().get(0).getAsMention()+", ça se "
						+ "voit que vous vous aimez ..ʕ•͡-•ʔ\n"+hr.getBody().getObject().getString("url")).queue();
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
	/*
	 * */
	@Command(name="meme",type=ExecutorType.USER,description="Renvoi un meme aleatoire depuis imgflip.com")
	private void meme(Guild guild,TextChannel channel,User user,Message message,String[] args) {
		Unirest.get("https://api.imgflip.com/get_memes").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {
				JSONObject obj = hr.getBody().getObject();
				JSONObject data = obj.getJSONObject("data");
				JSONArray memes = data.getJSONArray("memes");
				String[] names = new String[memes.length()];    
				String[] url = new String[memes.length()];  
				for(int i=0;i<memes.length();i++)
				{
				    JSONObject jsonObject = memes.getJSONObject(i);

				    names [i] = jsonObject.getString("name");
				    url [i] = jsonObject.getString("url");
				}
				int rand = (int)(Math.random()*100);
				channel.sendMessage(names[rand]+"\n"+url[rand]).queue();
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
	@Command(name="unisplash",type=ExecutorType.USER,description="g!unisplash {tags} ; renvoi une image de qualité depuis l'api d'unisplash")
	private void unisplash(Guild guild,TextChannel channel,User user,Message message,String[] args) {
		if (args.length == 0) {
			Message msg = channel.sendMessage(new EmbedBuilder().setTitle("Commande 'Unisplash'").setColor(Color.red)
					.setDescription("```g!unisplash {tags}```").build()).complete();
			return;
		}
		
		String tags = args[0];
		for (int i=1;i<args.length;i++)
			tags+="+"+args[i];
		final String lien = unisplash+tags;
		Unirest.get(lien).asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> hr) {	int count,pages;
				JSONObject obj = hr.getBody().getObject();
				count = obj.getInt("total"); pages = obj.getInt("total_pages");
				if (count == 0) {
					channel.sendMessage(new EmbedBuilder().setDescription("Il n'existe aucun résultat a votre recherche ,"
							+ "réesseyez avec d'autre tags ..").setColor(Color.red).build()).complete();
					return;
				}
				int rand = (int)(Math.random()*pages);
				System.out.println(String.valueOf(rand));
				Unirest.get(lien+"&page="+String.valueOf(rand)).asJsonAsync(new Callback<JsonNode>() {
					public void completed(HttpResponse<JsonNode> arg0) {
						JSONObject obj = arg0.getBody().getObject();
						JSONArray results = obj.getJSONArray("results");
						JSONObject instance = results.getJSONObject((int)(Math.random()*results.length()));
						JSONObject urls = instance.getJSONObject("urls");
						String lien = urls.getString("regular");
						channel.sendMessage(lien).queue();
					}
					public void cancelled() {}
					public void failed(UnirestException arg0) {}
				});
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
	/*
	 * */
	@Command(name="image",type=ExecutorType.USER,description="Sends an aleatory image from google")
	private void image(User user,TextChannel channel,Guild guild,String[] args) {
		if (args.length == 0) {
			Message msg = channel.sendMessage(new EmbedBuilder().setTitle("Commande 'image' :").setDescription(""
					+ "```g!image {tags}```").setColor(Color.red).build()).complete();
			new Timer().schedule(new TimerTask() {public void run() {msg.delete().complete();}}, 20000);
			return;
		}
		String search = args[0];
		for (int i=1;i<args.length;i++)
			search += "+"+args[i];
		int num = (int)(((Math.random()*9)*10)+1);
		System.out.print(search +" "+num);
		Unirest.get("https://www.googleapis.com/customsearch/v1?key=AIzaSyDxuu89IHroU0lVgpIrHNCHyhlKFuK2frA&cx=004389539208966928558:jxfqv3w5qbs&q="+search+"&searchType=image&start="+num).asJsonAsync(new Callback<JsonNode>() {
			public void completed(HttpResponse<JsonNode> arg0) {
				JSONObject obj = arg0.getBody().getObject();
				JSONArray items = obj.getJSONArray("items");
				int index = (int)(Math.random()*items.length());
				System.out.print(" "+index+"\n");
				String img = items.getJSONObject(index).getString("link");
				channel.sendMessage(img).queue();
			}
			public void cancelled() {System.out.println("cancelled");}
			public void failed(UnirestException arg0) {System.out.println("failed");}
		});
	}
	/**/
	@Command(name="kiss",type=ExecutorType.USER,description="Kiss someone .. or yourself :3")
	private void kiss(User user,TextChannel channel,Message message,String[] args) {
		
		Unirest.get("https://nekos.life/api/v2/img/kiss").asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> response) {
				String lien = response.getBody().getObject().getString("url");
				String msg;
				if (args.length < 1) {
					msg = user.getAsMention() + " kisses himself :3..";
				}
				else
					msg = user.getAsMention() + " kisses "+ message.getMentionedMembers().get(0).getAsMention();
				channel.sendMessage(msg+" <3 \n"+lien).queue();
			}
			@Override
			public void failed(UnirestException e) {e.printStackTrace();}
			@Override
			public void cancelled() {System.out.println("Cancelled");}
		});
	}
	@Command(name="manga",type=ExecutorType.USER,description="Search for a manga")
	private void manga(TextChannel channel,User user,Guild guild,Message message,String[] args){
		if (args.length < 1) {
			channel.sendMessage(new EmbedBuilder().setTitle("Command anime : search for a manga").setDescription(
					"```r!manga {title}```").setColor(Color.black).build()).queue();
			return;
		}
		String tags = args[0];
		for (int i=1;i<args.length;i++)
			tags += "+"+args[i];
		Unirest.get("https://kitsu.io/api/edge/manga?filter[text]="+tags).asJsonAsync(new Callback<JsonNode>() {
			@Override
			public void completed(HttpResponse<JsonNode> response) {
				JSONObject obj = response.getBody().getObject().getJSONArray("data").getJSONObject(0);
				/**/
				String synopsis = obj.getJSONObject("attributes").getString("synopsis");
				String airedF = obj.getJSONObject("attributes").isNull("endDate") ? " to N/A" : obj.getJSONObject("attributes").getString("endDate");
				String airedD = obj.getJSONObject("attributes").isNull("startDate") ? "N/A" : obj.getJSONObject("attributes").getString("startDate") + " to ";
				String otherTitles = "";
				JSONObject titles = obj.getJSONObject("attributes").getJSONObject("titles");
				for (String key : JSONObject.getNames(titles)) {
					if (!obj.getJSONObject("attributes").getJSONObject("titles").isNull(key))
						otherTitles += obj.getJSONObject("attributes").getJSONObject("titles").getString(key) + ", ";
				}
				String title = obj.getJSONObject("attributes").getString("canonicalTitle");
				String rating = obj.getJSONObject("attributes").isNull("averageRating") ? "N/A" : obj.getJSONObject("attributes").getString("averageRating"); 
				String statue = obj.getJSONObject("attributes").getString("status");
				String ageRating = obj.getJSONObject("attributes").isNull("ageRatingGuide") ? "N/A" : obj.getJSONObject("attributes").getString("ageRatingGuide");
				String imageUrl = obj.getJSONObject("attributes").getJSONObject("posterImage").getString("original");
				String coverUrl = obj.getJSONObject("attributes").isNull("coverImage") ?  null : obj.getJSONObject("attributes").getJSONObject("coverImage").getString("original");
				String chapterCount = obj.getJSONObject("attributes").isNull("chapterCount") ? "N/A" : String.valueOf(obj.getJSONObject("attributes").getInt("chapterCount"));
				String volumeCount = obj.getJSONObject("attributes").isNull("volumeCount") ? "N/A" : String.valueOf(obj.getJSONObject("attributes").getInt("volumeCount"));
				String serial = obj.getJSONObject("attributes").getString("serialization");
				/**/
				String desc = "**Synopsis :** \n```"+synopsis+"```\n"
						+"**Other titles :** "+otherTitles+"\n"
						+"**Rating :** "+rating+"\n"
						+"**Aired :**"+airedD+airedF+"\n"
						+"**Status :** "+statue+"\n"
						+"**Age Rating :** "+ageRating+"\n"
						+"**Chapters Count :** "+chapterCount+"\n"
						+"**Volume Count :** "+volumeCount+"\n"
						+"**Serialization :** "+serial+"\n";
						
				/**/		
				channel.sendMessage(new EmbedBuilder().setTitle("**"+title+"**").setImage(coverUrl)
					.setFooter("Results from Kitsu",null).setThumbnail(imageUrl+"?size=512")
					.setColor(Color.BLACK).setDescription(desc).build()).queue();
				
			}
			@Override
			public void cancelled() {System.out.println("cancelled");}
			@Override
			public void failed(UnirestException e) {e.printStackTrace();}
		});
	}
	/*
	 * */
	@Command(name="game",type=ExecutorType.USER,description="Search for a game on IGDB")
	private void game(TextChannel channel,User user,Guild guild,String[] args) throws UnirestException {
		if (args.length < 1) {
			channel.sendMessage(new EmbedBuilder().setTitle("Command game : search for a game from IGDB").setDescription(
					"```r!game {title}```").setColor(Color.black).build()).queue();
			return;
		}
		String tags = args[0];
		for (int i=1;i<args.length;i++)
			tags += "+"+args[i];
		JSONObject game = Unirest.get("https://api-endpoint.igdb.com/games/?fields=*&search="+tags+"&limit=1")
		.header("user-key", "e77c1bb12472a1c295eec73ea5bd5c1a")
		.header("Accept", "application/json")
		.asJson().getBody().getArray().getJSONObject(0);
		/**/
		System.out.println(game);
		String url = game.getString("url");
		String name = game.getString("name");
		String summary = game.has("summary") ? game.getString("summary") : "N/A";
		String storyline = game.has("storyline") ? game.getString("storyline") : "";
		String popularity = String.valueOf((int)game.getDouble("popularity")) + "/100";
		String rating = game.has("rating") ? String.valueOf((double)Math.round(game.getDouble("rating"))/5) : "N/A";
		String cover = "http:"+game.getJSONObject("cover").getString("url");
		/**/
		String platforms = new String("");
		//for (int i = 0 ; i < game.getJSONArray("platforms").length();i++) {
			String platform = Unirest.get("https://api-endpoint.igdb.com/platforms/6?fields=*")
					.header("user-key", "e77c1bb12472a1c295eec73ea5bd5c1a")
					.header("Accept", "application/json")
					.asJson().getBody().getObject().getString("name");
			System.out.println(platform);
			//platforms += platform.getString("name")+" ,";
		//}
		

		
		
		
		
		String str = "**Summary :**\n"+summary+"\n\n"+storyline+"\n"
				+"```"
				+"Platforms : "+platforms+"\n"
				+"Popularity : "+popularity+"\n"
				+"Rating : "+rating+"\n"
				+"Link : "+url+"\n"
				+"```";
		
		channel.sendMessage(new EmbedBuilder().setTitle("**"+name+"**").setDescription(str).setColor(Color.black)
				.setThumbnail(cover).build()).queue();
	}
	

}
