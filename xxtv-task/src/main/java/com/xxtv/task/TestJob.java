package com.xxtv.task;



import java.io.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

public class TestJob {

    //The url of the website. This is just an example
    private static final String URL = "http://www.lrcgc.com/lrc-81-223223/周传雄-琵琶乱弹.lrc";

    //The path of the folder that you want to save the images to
    private static final String folderPath = "d://";

    public static void main(String[] args) {

        try {
                getImages(URL);

        } catch (IOException ex) {
            System.err.println("There was an error");
            Logger.getLogger(TestJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void getImages(String src) throws IOException {


        //Exctract the name of the image from the src attribute
        int indexname = src.lastIndexOf("/");

        if (indexname == src.length()) {
            src = src.substring(1, indexname);
        }

        indexname = src.lastIndexOf("/");
        String name = src.substring(indexname, src.length());

        System.out.println(name);

        //Open a URL Stream
        URL url = new URL(src);
        InputStream in = url.openStream();

        OutputStream out = new BufferedOutputStream(new FileOutputStream( folderPath+ name));

        for (int b; (b = in.read()) != -1;) {
            out.write(b);
        }
        out.close();
        in.close();

    }
}
