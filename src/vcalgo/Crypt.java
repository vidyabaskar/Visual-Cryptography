/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vcalgo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.security.SecureRandom;
import javax.imageio.ImageIO;

/**
 *
 * @author vidyabaskarsivakumar
 */
public class Crypt {
    public static BufferedImage generateKey(int width, int height) {
		width *= 2;
		height *= 2;
		BufferedImage key = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D keyGraphics = key.createGraphics();
		keyGraphics.setColor(new Color(255, 255, 255, 0));
		keyGraphics.fillRect(0, 0, width, height);
		keyGraphics.setColor(new Color(0, 0, 0, 255));
		SecureRandom secureRandom = new SecureRandom();
		for (int y = 0; y < height; y += 2) {
			for (int x = 0; x < width; x += 2) {
				int px1 = secureRandom.nextInt(4);
				int px2 = secureRandom.nextInt(4);
				while (px1 == px2) px2 = secureRandom.nextInt(4);
				int px1x = (px1 < 2) ? px1 : px1 - 2;
				int px1y = (px1 < 2) ? 0 : 1;
				int px2x = (px2 < 2) ? px2 : px2 - 2;
				int px2y = (px2 < 2) ? 0 : 1;
				keyGraphics.fillRect(x + px1x, y + px1y, 1, 1);
				keyGraphics.fillRect(x + px2x, y + px2y, 1, 1);
			}
		}
		keyGraphics.dispose();
		return key;
	}
	
	public static BufferedImage loadAndCheckEncrFile(File keyFile) {
		if (keyFile == null) return null;
		BufferedImage imgKey = null;
		try {
			imgKey = ImageIO.read(keyFile);
		} catch (Exception e) {
			return null;
		}
		if (imgKey.getWidth() % 2 != 0) return null;
		if (imgKey.getHeight() % 2 != 0) return null;
		if (imgKey.getType() != BufferedImage.TYPE_INT_ARGB) {
			BufferedImage raw_image = imgKey;
			imgKey = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			new ColorConvertOp(null).filter(raw_image, imgKey);
                }
		long lAmountOfTotalPixels = 0;
		long lAmountOfBlackPixels = 0;
		for(int i = 0; i < imgKey.getHeight(); i++) {
			for(int j = 0; j < imgKey.getWidth(); j++) {
				int iRgb = imgKey.getRGB(j, i);
				
				if(iRgb == Color.WHITE.getRGB()) {
					imgKey.setRGB(j, i, 0x00FFFFFF);
					iRgb = imgKey.getRGB(j, i);
				}
				
				if(iRgb>>>24 == 0) {
					++lAmountOfTotalPixels;
				} else if (iRgb == Color.BLACK.getRGB()) {
					++lAmountOfTotalPixels;
					++lAmountOfBlackPixels;
				} else {
					return null;
				}
				
			}
		}
		if (lAmountOfTotalPixels / lAmountOfBlackPixels != 2) return null;
		return imgKey;
	}
	public static BufferedImage loadAndCheckSource(File sourceFile, int width, int height, boolean resize) {
		if (sourceFile == null) return null;
		BufferedImage imgSrc = null;
		try {
			imgSrc = ImageIO.read(sourceFile);
		} catch (Exception e) {
			return null;
		}
		
		if (resize && (imgSrc.getWidth() > width || imgSrc.getHeight() > height)) return null;
		
		if (imgSrc.getType() != BufferedImage.TYPE_INT_ARGB) {
			BufferedImage raw_image = imgSrc;
			imgSrc = new BufferedImage(raw_image.getWidth(), raw_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			new ColorConvertOp(null).filter(raw_image, imgSrc);
		}
		
		for(int i = 0; i < imgSrc.getHeight(); i++) {
			for(int j = 0; j < imgSrc.getWidth(); j++) {
				int iRgb = imgSrc.getRGB(j, i);
				
				if(iRgb == Color.WHITE.getRGB()) {
					imgSrc.setRGB(j, i, 0x00FFFFFF);
					iRgb = imgSrc.getRGB(j, i);
				}
				
				if(!(iRgb>>>24 == 0 || iRgb == Color.BLACK.getRGB())) {
					int r = (iRgb & 0x00FF0000)>>16;
					int g = (iRgb & 0x0000FF00)>>8;
					int b = iRgb & 0x000000FF;
					double brightness = (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
					if (brightness > (255/2)) {
						imgSrc.setRGB(j, i, 0x00FFFFFF);
					} else {
						imgSrc.setRGB(j, i, Color.BLACK.getRGB());
					}
				}
				
			}
		}
		if (!resize || (imgSrc.getWidth() == width && imgSrc.getHeight() == height)) return imgSrc;
		BufferedImage imgSrcRes =  new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = imgSrcRes.createGraphics();
		int x = (width - imgSrc.getWidth()) / 2;
		int y = (height - imgSrc.getHeight()) / 2;
		g.drawImage(imgSrc, x, y, imgSrc.getWidth() + x, imgSrc.getHeight() + y, 0, 0, imgSrc.getWidth(), imgSrc.getHeight(), null);
		g.dispose();
		return imgSrcRes;
	}
	
	public static BufferedImage encryptImage(BufferedImage imgKey, BufferedImage imgSrc) {
		if (imgKey == null || imgSrc == null) return null;
		if (imgSrc.getWidth() != imgKey.getWidth() / 2 || imgSrc.getHeight() != imgKey.getHeight() / 2) return null;
		BufferedImage imgSrcRes =  new BufferedImage(imgKey.getWidth(), imgKey.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = imgSrcRes.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(imgSrc, 0, 0, imgKey.getWidth(), imgKey.getHeight(), 0, 0, imgSrc.getWidth(), imgSrc.getHeight(), null);
		g.dispose();
		BufferedImage imgEncr =  new BufferedImage(imgKey.getWidth(), imgKey.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D encrGraphics = imgEncr.createGraphics();
		encrGraphics.setColor(new Color(255, 255, 255, 0));
		encrGraphics.fillRect(0, 0, imgEncr.getWidth(), imgEncr.getHeight());
		encrGraphics.setColor(new Color(0, 0, 0, 255));
		for (int y = 0; y < imgEncr.getHeight(); y += 2) {
			for (int x = 0; x < imgEncr.getWidth(); x += 2) {
				if (imgSrcRes.getRGB(x, y) == Color.BLACK.getRGB()) {
					if (imgKey.getRGB(x, y)>>>24 == 0) encrGraphics.fillRect(x, y, 1, 1);
					if (imgKey.getRGB(x + 1, y)>>>24 == 0) encrGraphics.fillRect(x + 1, y, 1, 1);
					if (imgKey.getRGB(x, y + 1)>>>24 == 0) encrGraphics.fillRect(x, y + 1, 1, 1);
					if (imgKey.getRGB(x + 1, y + 1)>>>24 == 0) encrGraphics.fillRect(x + 1, y + 1, 1, 1);
				} else {
					if (imgKey.getRGB(x, y) == Color.BLACK.getRGB()) encrGraphics.fillRect(x, y, 1, 1);
					if (imgKey.getRGB(x + 1, y) == Color.BLACK.getRGB()) encrGraphics.fillRect(x + 1, y, 1, 1);
					if (imgKey.getRGB(x, y + 1) == Color.BLACK.getRGB()) encrGraphics.fillRect(x, y + 1, 1, 1);
					if (imgKey.getRGB(x + 1, y + 1) == Color.BLACK.getRGB()) encrGraphics.fillRect(x + 1, y + 1, 1, 1);
				}
			}
		}
		encrGraphics.dispose();
		return imgEncr;
	}
	
	public static BufferedImage overlayImages(BufferedImage imgKey, BufferedImage imgEnc) {
		if (imgKey == null || imgEnc == null || imgKey.getWidth() != imgEnc.getWidth() || imgKey.getHeight() != imgEnc.getHeight()) 
                    return null;
		BufferedImage imgOverlay =  new BufferedImage(imgKey.getWidth(), imgKey.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = imgOverlay.createGraphics();
		g.drawImage(imgKey, 0, 0, imgKey.getWidth(), imgKey.getHeight(), 0, 0, imgKey.getWidth(), imgKey.getHeight(), null);
		g.drawImage(imgEnc, 0, 0, imgEnc.getWidth(), imgEnc.getHeight(), 0, 0, imgEnc.getWidth(), imgEnc.getHeight(), null);
		g.dispose();
		return imgOverlay;
	}
	
}
