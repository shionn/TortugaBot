package bot.discord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class TortugaDiscordBot implements EventListener {
	@RequiredArgsConstructor
	@Getter
	private enum Model {
		// ancien : 1324709379110469653L
		// 2025 : 1307668938385002538L
		PLAYERS("https://shionn.github.io/TortugaBot/rss/players.rss", 1454423877823234172L),
		QUESTS("https://shionn.github.io/TortugaBot/rss/quests.rss", 1454423877823234172L),
		PLAYERS_GALY("https://shionn.github.io/TortugaBot/rss/players.rss", 1123512494468644984L),
		QUESTS_GALY("https://shionn.github.io/TortugaBot/rss/quests.rss", 1123512494468644984L);

		private final String rss;
		private final long channel;

	}

	// INVITATION :
	// https://discord.com/oauth2/authorize?client_id=1324670615785050184&scope=bot&permissions=67584

	public static void main(String[] args) throws InterruptedException, IOException, FeedException {

		TortugaDiscordBot bot = new TortugaDiscordBot();
		bot.start(Model.PLAYERS);
		bot.start(Model.QUESTS);
	}

	private void start(Model model) throws InterruptedException, IOException, FeedException {
		SyndEntry syndEntry = getLastEntry(model);
		JDA jda = buildBot();

//		jda.getTextChannels().stream().forEach(System.out::println);

		TextChannel channel = jda.getTextChannelById(model.getChannel());
		channel.getHistory().retrievePast(30).queue(history -> {
			boolean notPosted = history
					.stream()
					.filter(m -> isAlreadyPosted(m, syndEntry))
					.filter(m -> syndEntry.getUri().equalsIgnoreCase(m.getContentRaw()))
					.findAny()
					.isEmpty();
			if (notPosted) {
				channel.sendMessage(syndEntry.getUri()).queue(message -> {
					jda.shutdown();
				});
			} else {
				jda.shutdown();
			}
		});
		jda.awaitShutdown();
	}

	private boolean isAlreadyPosted(Message m, SyndEntry syndEntry) {
		return m != null && "PourLaGloireDeTortuga".equals(m.getAuthor().getName())
				&& syndEntry.getUri().equalsIgnoreCase(m.getContentRaw());
	}

	private JDA buildBot() throws IOException, InterruptedException {
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("token.properties"));
		return JDABuilder.create(properties.getProperty("token"), Arrays.asList(GatewayIntent.MESSAGE_CONTENT))
				.addEventListeners(this).build().awaitReady();
	}

	private SyndEntry getLastEntry(Model model) throws FeedException, IOException, MalformedURLException {
		try (InputStream is = new URL(model.getRss()).openStream(); InputStreamReader isr = new InputStreamReader(is)) {
			SyndFeed feed = new SyndFeedInput().build(isr);
			return feed.getEntries().get(0);
		}
	}

	@Override
	public void onEvent(GenericEvent event) {
//		System.out.println(event.getClass());
	}
}
