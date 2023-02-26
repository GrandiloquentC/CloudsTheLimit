package hackathoncode;

//import javax.media.*;
import javax.media.*;
import java.io.*;

public class ImageTaker {

    public static void main(String[] args) throws Exception {

        // Create a JMF DataSource that represents the webcam
        DataSource ds = Manager.createDataSource(new MediaLocator("vfw://0"));

        // Create a JMF Player that will capture the webcam stream
        Player player = Manager.createRealizedPlayer(ds);
        player.start();

        // Get the webcam video output
        Component video = player.getVisualComponent();

        // Create an image buffer from the webcam video
        Buffer buf = new Buffer();
        buf.setFormat(new RGBFormat());
        ((FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl")).grabFrame(buf);
        Image img = (new BufferToImage((VideoFormat)buf.getFormat()).createImage(buf));

        // Save the image to a file
        File file = new File("image.jpg");
        ImageIO.write((RenderedImage)img, "jpg", file);

        // Stop the player and release resources
        player.stop();
        player.close();
    }
}