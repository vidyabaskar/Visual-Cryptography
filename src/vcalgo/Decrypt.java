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
import javax.swing.JOptionPane;

/**
 *
 * @author vidyabaskarsivakumar
 */
public class Decrypt {
    public static void main (String [] args) throws IOException{
        File fKeyFile = new File ("key.png");
        File fEncrFile = new File ("overlap.png");
        final BufferedImage imgKey = Crypt.loadAndCheckEncrFile(fKeyFile);
        final BufferedImage imgEnc = Crypt.loadAndCheckEncrFile(fEncrFile);
        if (imgKey == null) {
            JOptionPane.showMessageDialog(null, fKeyFile.getName() + " is not a valid key file", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
	}
			
	if (imgEnc == null) {
            JOptionPane.showMessageDialog(null, fEncrFile.getName() + " is not fit for encryption", "ERROR", JOptionPane.ERROR_MESSAGE);
	}
        final BufferedImage imgOverlay = Crypt.overlayImages(imgKey, imgEnc);
        File outputfile = new File("finaloverlap.png");
        ImageIO.write(imgOverlay, "png", outputfile);
    }	
}