/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcalgo;

import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author vidyabaskarsivakumar
 */
public class Encrypt {
    public static void main(String[] args) throws IOException{
        
	File fKeyFile = new File("key.png");
        File fSrcFile = new File("org.png");
	BufferedImage imgKey = Crypt.loadAndCheckEncrFile(fKeyFile);
	BufferedImage imgSrc = Crypt.loadAndCheckSource(fSrcFile, imgKey.getWidth() / 2, imgKey.getHeight() / 2, true);
	if (imgKey == null) {
            JOptionPane.showMessageDialog(null, fKeyFile.getName() + " is not a valid key file", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
	}
			
	if (imgSrc == null) {
            JOptionPane.showMessageDialog(null, fSrcFile.getName() + " is not fit for encryption", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
	}
        final BufferedImage imgEncr = Crypt.encryptImage(imgKey, imgSrc);
        File outputfile = new File("overlap.png");
        ImageIO.write(imgEncr, "png", outputfile);
    }
}