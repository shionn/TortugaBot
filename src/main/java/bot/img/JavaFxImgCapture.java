package bot.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public abstract class JavaFxImgCapture extends Application implements ChangeListener<Worker.State> {

	private static final int SLEEP_TIME = 2;

	private static final String BASE_URL = "https://shionn.github.io";

	WebView webView;

	@Override
	public void start(Stage primaryStage) throws Exception {
		webView = new WebView();

		String url = BASE_URL;
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += getBaseUrl();
		System.out.println("open url " + url);
		webView.getEngine().load(url);
		webView.getEngine().getLoadWorker().stateProperty().addListener(this);

		Scene scene = new Scene(webView, 1500, 1400);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	abstract String getBaseUrl();

	abstract String getElementId();

	abstract String getImgTargetPath();

	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newState) {
		if (newState == Worker.State.SUCCEEDED) {
			sleepAndExecute(new Runnable() {
				@Override
				public void run() {
					requestScrollToElement(getElementId());
//					requestCapture(getElementId(), getImgTargetPath());
				}

			});
		}
	}

	private void sleepAndExecute(Runnable runnable) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(SLEEP_TIME);
					Platform.runLater(runnable);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void requestScrollToElement(String id) {
		sleepAndExecute(new Runnable() {
			@Override
			public void run() {
				webView
						.getEngine()
						.executeScript("document.getElementById(\"" + id + "\").scrollIntoView({block : 'center'})");
				requestCapture(id, getImgTargetPath());
			}
		});
	}

	private void requestCapture(String id, String path) {
		sleepAndExecute(new Runnable() {
			@Override
			public void run() {
				doCapture(id, path);
			}
		});
	}

	private void doCapture(String id, String path) {
		SnapshotParameters params = new SnapshotParameters();
		params.setViewport(new Rectangle2D(reteiveDouble(id, "left"), reteiveDouble(id, "top"),
				reteiveDouble(id, "width"), reteiveDouble(id, "height")));
		WritableImage snapshot = webView.snapshot(params, null);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
		System.out.println("capture done for " + id);
		try {
			File output = new File("docs/img/tortuga-2026/" + path + "/temp.png");
			output.mkdirs();
			ImageIO.write(bufferedImage, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Platform.exit();
	}

	private double reteiveDouble(String id, String prop) {
		return ((Number) webView.getEngine()
				.executeScript("q(\"#" + id + " table\").obj[0].getBoundingClientRect()." + prop)).doubleValue();
	}

}
