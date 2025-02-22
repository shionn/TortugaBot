package bot.img;

public class TortugaPlayersJavaFxImgCapture extends JavaFxImgCapture {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	String getBaseUrl() {
		return "2025/pour-la-gloire-de-tortuga.html";
	}

	@Override
	String getElementId() {
		return "participants";
	}

	@Override
	String getImgTargetPath() {
		return "players";
	}

}
