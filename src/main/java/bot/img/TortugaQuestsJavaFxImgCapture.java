package bot.img;

import org.w3c.dom.Element;

public class TortugaQuestsJavaFxImgCapture extends JavaFxImgCapture {

	public static void main(String[] args) {
		launch(args);
	}

	private static final String QUEST = "tortuga-2026-quest-";

	@Override
	String getBaseUrl() {
		return "2026/tortuga-2026.html";
	}

	@Override
	String getElementId() {
		int i = 50;
		Element element = webView.getEngine().getDocument().getElementById(QUEST + i);
		while (element == null && i > 0) {
			i--;
			element = webView.getEngine().getDocument().getElementById(QUEST + i);
		}
		if (element != null) {
			return QUEST + i;
		}
		throw new IllegalStateException("No Quest found");
	}

	@Override
	String getImgTargetPath() {
		return "quests";
	}

}
