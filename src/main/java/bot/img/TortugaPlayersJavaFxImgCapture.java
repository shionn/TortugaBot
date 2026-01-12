package bot.img;

public class TortugaPlayersJavaFxImgCapture extends JavaFxImgCapture {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	String getBaseUrl() {
		return "2026/tortuga-2026.html";
	}

	@Override
	String getElementId() {
		return "tortuga-2026-participants";
	}

	@Override
	String getImgTargetPath() {
		return "players";
	}

}
