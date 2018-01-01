package com.grooble.model;

import java.awt.*;
import java.awt.image.*;


public class Scaler {
/*
*	If image is portrait, scale to the height, otherwise, scale to the width.
*/
	 public BufferedImage getScaledInstance(BufferedImage img, int target)
    {
//		System.out.println("Scaler");
		Object hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
		boolean enlarge;
		float ts = target;
        int wd, ht;
		int targetWidth, targetHeight;
		wd = img.getWidth();
		ht = img.getHeight();
		//	set target width and height.
		if (wd >= ht){
			targetWidth = (int)ts;
			targetHeight = (int)((ts/wd)*ht);
		} else {
			targetHeight = (int)ts;
			targetWidth = (int)((ts/ht)*wd);
		}
		//set the enlarge flag depending on image  and target size.
		if (wd < targetWidth) {
			enlarge = true;
		} else {
			enlarge = false;
		}
		if (enlarge) {
			wd = targetWidth;
			ht = targetHeight;
		}
        do {
            if (!enlarge && wd > targetWidth) {
                wd = wd/2;
                if (wd < targetWidth) {
                    wd = targetWidth;
                }
            }

            if (!enlarge && ht > targetHeight) {
                ht = ht/2;
                if (ht < targetHeight) {
                    ht = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(wd, ht, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, wd, ht, null);
            g2.dispose();

            ret = tmp;
        } while (wd != targetWidth || ht != targetHeight);

        return ret;
    }
}