package bot.img;

public class TortugaPlayersJavaFxImgCapture extends JavaFxImgCapture {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	String getBaseUrl() {
		return "draft/pour-la-gloire-de-tortuga-players.html";
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
