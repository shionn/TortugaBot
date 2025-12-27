package bot.rss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

public class TortugaRssBuild {

	private static final String BASE_URL = "https://shionn.github.io/TortugaBot/";

	static enum Model {
		QUEST("img/tortuga-2026/quests/", "docs/rss/quests.rss", "Mise à jour des quetes "),
		PLAYERS("img/tortuga-2026/players/", "docs/rss/players.rss", "Mise à jour des joueurs "),;

		private String imgFolder;
		private String rssFile;
		private String rssTitle;

		Model(String imgFolder, String rssFile, String rssTitle) {
			this.imgFolder = imgFolder;
			this.rssFile = rssFile;
			this.rssTitle = rssTitle;
		}

		public String getImgFolder() {
			return imgFolder;
		}

		public String getRssFile() {
			return rssFile;
		}

		public String getRssTitle() {
			return rssTitle;
		}
	}

	private static final String DOCS = "docs/";

	public static void main(String[] args) throws IOException, FeedException {
		TortugaRssBuild builder = new TortugaRssBuild();
		builder.checkImgs(Model.QUEST);
		builder.checkImgs(Model.PLAYERS);
		builder.buildRss(Model.QUEST);
		builder.buildRss(Model.PLAYERS);
	}

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm");
	private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
	private String formatedNow = format.format(new Date());

	private void checkImgs(Model model) throws IOException {
		File temp = new File(DOCS + model.getImgFolder() + "temp.png");
		if (temp.exists()) {
			File last = new File(DOCS + model.getImgFolder() + "last.png");
			if (last.exists()) {
				BufferedImage tempImg = ImageIO.read(temp);
				BufferedImage lastImg = ImageIO.read(last);
				if (!bufferedImagesEqual(tempImg, lastImg)) {
					System.out.println("New image");
					FileUtils.copyFile(temp, new File(DOCS + model.getImgFolder() + formatedNow + ".png"));
					last.delete();
					FileUtils.copyFile(temp, last);
				} else {
					System.out.println("Same image");
				}
			} else {
				System.out.println("First image");
				FileUtils.copyFile(temp, new File(DOCS + model.getImgFolder() + formatedNow + ".png"));
				FileUtils.copyFile(temp, last);
			}
			System.out.println("delete temp file");
			temp.delete();
		}
	}

	private void buildRss(Model model) throws IOException, FeedException {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");
		feed.setTitle("Pour la Gloire de Tortuga");
		feed.setLink(BASE_URL);
		feed.setDescription("Pour la Gloire de Tortuga ");

		List<SyndEntry> entries = Arrays.stream(new File(DOCS + model.imgFolder).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png") && !name.startsWith("last");
			}
		})).sorted((a, b) -> -a.compareTo(b)).map(file -> buildRssEntry(model, file)).toList();
		feed.setEntries(entries);
		feed.setPublishedDate(entries.get(0).getPublishedDate());

		File output = new File(model.getRssFile());
		new SyndFeedOutput().output(feed, output);
	}

	private SyndEntry buildRssEntry(Model model, File file) {
		SyndEntryImpl entry = new SyndEntryImpl();
		entry.setAuthor("shionn");
		entry.setLink(toUrl(model, file).replace(" ", "%20"));
		SyndContentImpl desc = new SyndContentImpl();
		desc.setValue(file.getName());
		entry.setDescription(buildItemDescription(model, file));
		try {
			Date date = format.parse(file.getName());
			entry.setPublishedDate(date);
			entry.setTitle(model.getRssTitle() + displayFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return entry;
	}

	private SyndContent buildItemDescription(Model model, File file) {
		SyndContentImpl content = new SyndContentImpl();
		content.setType("text/html");
		content.setValue("<img src=\"" + toUrl(model, file) + "\">");
		return content;
	}

	private String toUrl(Model model, File file) {
		String url = BASE_URL;
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += model.getImgFolder();
		return url + file.getName();
	}

	boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
			for (int x = 0; x < img1.getWidth(); x++) {
				for (int y = 0; y < img1.getHeight(); y++) {
					if (img1.getRGB(x, y) != img2.getRGB(x, y))
						return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
