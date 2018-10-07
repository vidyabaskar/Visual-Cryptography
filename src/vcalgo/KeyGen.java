/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcalgo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author vidyabaskarsivakumar
 */
public class KeyGen {
    public static void main(String[] args) throws IOException{
	final BufferedImage imgKey = Crypt.generateKey(600, 400);
        File outputfile = new File("key.png");
        ImageIO.write(imgKey, "png", outputfile);
    }
}
