package Data;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/***
 * 获取视频帧
 */
public class Data {
    String ascii = "@#&$%*o!;.";
    public void get() {
        VideoCapture capture = new VideoCapture("/home/xiaohui/桌面/video.mp4");
        if (!capture.isOpened()) {
            System.out.println("未能打开文件");
            return;
        }
        int frame_width = (int) capture.get(3);//获得视屏流帧的宽度
        int frame_height = (int) capture.get(4);//获得视屏流帧的高度
        //将视频显示类进行实例化
        ImageGui gui = new ImageGui();
        gui.createWin("视屏播放", new Dimension(frame_width, frame_height));

        Mat mat = new Mat();
        Mat img_mat = new Mat();
        while (true) {
            boolean have = capture.read(mat);
            //将图片灰度化
            Imgproc.cvtColor(mat, img_mat, Imgproc.COLOR_RGB2GRAY);
            if (!have) break;
            //将Mat转换为BufferedImage
            BufferedImage image = conver2Image(mat);
            //显示视频
            gui.imshow(image);
            gui.repaint();

            if (!mat.empty()) {System.out.print("\033c");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < img_mat.rows(); i+=7) {
                    for (int j = 0; j < img_mat.cols(); j+=7) {
                        int gray = (int) img_mat.get(i, j)[0];
                        int index = Math.round(gray * (ascii.length() + 1) / 255);
                        result.append(index >= ascii.length() ? " " : String.valueOf(ascii.charAt(index)));
                    }
                    result = result.append("\n");
                }
                System.out.print("\033c");
                System.out.println(result);
            }

        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //将mat图片进行处理转化为BufferedImage
    public BufferedImage conver2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int dims = mat.channels();
        int[] pixels = new int[width * height];
        byte[] rgbdata = new byte[width * height * dims];
        mat.get(0, 0, rgbdata);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int index = 0;
        int r = 0, g = 0, b = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (dims == 3) {
                    index = row * width * dims + col * dims;
                    b = rgbdata[index] & 0xff;
                    g = rgbdata[index + 1] & 0xff;
                    r = rgbdata[index + 2] & 0xff;
                    pixels[row * width + col] = ((255 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
                }
                if (dims == 1) {
                    index = row * width + col;
                    b = rgbdata[index] & 0xff;
                    pixels[row * width + col] = ((255 & 0xff) << 24) | ((b & 0xff) << 16) | ((b & 0xff) << 8) | b & 0xff;
                }
            }
        }
        this.setRGB(image, 0, 0, width, height, pixels);
        return image;
    }
    private void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
            image.getRaster().setDataElements(x, y, width, height, pixels);
        else
            image.setRGB(x, y, width, height, pixels, 0, width);
    }


    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Data data = new Data();
        data.get();
    }

}

