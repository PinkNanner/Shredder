package com.kttg.shredder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.math.BigDecimal;

/*
* Attempts to match images - goal is to piece together a shredded document
* - Reads along the edges of the images looking for black or close to black pixels
* - Potential matches are output as text currently
*
* Future TODO: Scan slices of pages one by one so they are all individual images:
* 		 - Cut each image down to its smallest size based on where black pixels are found
* 		 - Run pixel matching code against all pieces and display visually where they might line up
	*    - Allow user to manually click and drag after matching process to complete mismatched pieces
		*   */

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture[] slices;
	// Image img;
	int r, g, b, sliceCount, inwardDeviation;
	int[] rgb = new int[3];
	boolean[][][] topSideBoo, botSideBoo, leftSideBoo, rightSideBoo;
	int[][] pixels, slicePosition;
	Color color;
	Pixmap pixmap;
	String hexString;

	@Override
	public void create() {
		inwardDeviation = 2; // **Controls how far in the program will check for a black pixel
		
		sliceCount = 4; // Must specify or else crash
		slicePosition = new int[sliceCount / 2][sliceCount / 2]; // General
		slices = new Texture[sliceCount];
		for (int i = 0; i < sliceCount; i++) {
			slices[i] = new Texture("Sample Circle/circle" + i + ".png");
			// slices[i] = new Texture("Sample Text/sample"+i+".png");
		}
		topSideBoo = new boolean[sliceCount][slices[0].getWidth()][inwardDeviation];
		botSideBoo = new boolean[sliceCount][slices[0].getWidth()][inwardDeviation];
		leftSideBoo = new boolean[sliceCount][slices[0].getHeight()][inwardDeviation];
		rightSideBoo = new boolean[sliceCount][slices[0].getHeight()][inwardDeviation];
		for (int i = 0; i < sliceCount; i++) { // Runs pixel check for every
												// image
			pixels = new int[slices[i].getWidth()][slices[i].getHeight()];
			if (slices[i].getTextureData().isPrepared() == false)
				slices[i].getTextureData().prepare();
			pixmap = slices[i].getTextureData().consumePixmap();
			System.out.println("\n\n\n\n\n\n\nIMAGE " + i + " ");
			// Sets boolean values
			getHorizontal(slices[i], i);
			getVertical(slices[i], i);
		}
		checkMatches();
		batch = new SpriteBatch();
	}

	public void checkMatches() {
		int deviationVertical = 1, deviationHorizontal = 1, actualDeviationPos = 0, actualDeviationNeg = 0, sliceWidth,
				sliceHeight;
		boolean match = false;
		System.out.println("\nMATCHING TOP/BOTTOM SIDES:");
		
		
		for (int i = 0; i < sliceCount; i++) { // Loop to run for each piece
			for (int k = 0; k < sliceCount; k++) { // Loop to run each piece against all other pieces
				sliceWidth = slices[0].getWidth();
				for (int h = 0; h < inwardDeviation; h++) { // Loop to run first check inwards
					for (int j = 0; j < sliceWidth; j++) { // Loop to run the width/height (pixels) of each piece
						for (int u = 1; u <= deviationHorizontal; u++) { // Loop to deviate up/down and left/right for each piece
							for (int r = 0; r < inwardDeviation; r++) { // Loop for second check inwards
								actualDeviationPos = u;
								if (actualDeviationPos + j >= sliceWidth - 1)
									actualDeviationPos = 0;
								actualDeviationNeg = u;
								if (j - actualDeviationNeg < 0)
									actualDeviationNeg = 0;

								if ((topSideBoo[i][j][h] == true && botSideBoo[k][j][r] == true)
										|| (topSideBoo[i][j][h] == true
												&& botSideBoo[k][j + actualDeviationPos][r] == true)
										|| (topSideBoo[i][j][h] == true
												&& botSideBoo[k][j - actualDeviationNeg][r] == true))
									match = true;

								if (topSideBoo[i][j][h] == true && (botSideBoo[k][j][r] == false
										&& botSideBoo[k][j + actualDeviationPos][r] == false
										&& botSideBoo[k][j - actualDeviationNeg][r] == false)) {
									match = false;
									break;
								}
							}
						}
					}
					if (match == true) {
						System.out.println("topSideBoo[" + i + "] matches botSideBoo[" + k + "] --> slice[" + i
								+ "] matches slice[" + k + "]");
						match = false;
					}
				}
			}
		}
		System.out.println("\nMATCHING LEFT/RIGHT SIDES:");
		for (int i = 0; i < sliceCount; i++) {
			for (int k = 0; k < sliceCount; k++) {
				sliceHeight = slices[0].getHeight();
				for (int h = 0; h < inwardDeviation; h++) { // Loop to run first check inwards
					for (int j = 0; j < sliceHeight; j++) {
						for (int u = 1; u <= deviationVertical; u++) {
							for (int r = 0; r < inwardDeviation; r++) { // Loop for second check inwards
								actualDeviationPos = u;
								if (actualDeviationPos + j >= sliceHeight)
									actualDeviationPos = 0;
								actualDeviationNeg = u;
								if (j - actualDeviationNeg < 0)
									actualDeviationNeg = 0;
								// System.out.println("j: "+j+" pos:
								// "+actualDeviationPos+" neg:
								// "+actualDeviationNeg+" u: "+u);

								if ((leftSideBoo[i][j][h] == true && rightSideBoo[k][j][r] == true)
										|| (leftSideBoo[i][j][h] == true
												&& rightSideBoo[k][j + actualDeviationPos][r] == true)
										|| (leftSideBoo[i][j][h] == true
												&& rightSideBoo[k][j - actualDeviationNeg][r] == true))
									match = true;

								if (leftSideBoo[i][j][h] == true && (rightSideBoo[k][j][r] == false
										&& rightSideBoo[k][j + actualDeviationPos][r] == false
										&& rightSideBoo[k][j - actualDeviationNeg][r] == false)) {
									match = false;
									break;
								}
							}
						}
					}
					if (match == true) {
						System.out.println("leftSideBoo[" + i + "] matches rightSideBoo[" + k + "] --> slice[" + i
								+ "] matches slice[" + k + "]");
						match = false;
					}
				}
			}
		}
	}

	public void getVertical(Texture t, int i) {
		int r, g, b;
		int[] rgb = new int[3];
		System.out.println("\nLEFT/RIGHT EDGES");
		System.out.println("\nLEFT:");
		for (int j = 0; j < inwardDeviation; j++) {
			for (int k = 0; k < slices[i].getHeight(); k++) {
				color = new Color(pixmap.getPixel(0 + inwardDeviation, k));
				hexString = color.toString();
				hexString = hexString.substring(0, 6);
				rgb = getRGB(hexString);
				r = rgb[0];
				g = rgb[1];
				b = rgb[2];
				if (r < 240 && g < 240 && b < 240)
					leftSideBoo[i][k][j] = true;
				System.out.println("Pixel[" + (0 + inwardDeviation) + "][" + k + "] RGB == " + r + " " + g + " " + b);
				System.out.println(leftSideBoo[i][k][j]);
			}
			System.out.println("\nRIGHT:");
			for (int k = 0; k < (slices[i].getHeight() - inwardDeviation) - 1; k++) {
				color = new Color(pixmap.getPixel((slices[i].getWidth() - inwardDeviation) - 1, k));
				hexString = color.toString();
				hexString = hexString.substring(0, 6);
				rgb = getRGB(hexString);
				r = rgb[0];
				g = rgb[1];
				b = rgb[2];
				if (r < 240 && g < 240 && b < 240)
					rightSideBoo[i][k][j] = true;
				System.out.println("Pixel[" + ((slices[i].getWidth() - inwardDeviation) - 1) + "][" + k + "] RGB == "
						+ r + " " + g + " " + b);
				System.out.println(rightSideBoo[i][k]);
			}
		}
	}

	public boolean checkText(int[] rgb) {
		for (int i = 0; i < 3; i++) {
			if (rgb[i] >= 240)
				return false;
		}
		return true;
	}

	public void getHorizontal(Texture t, int i) {
		int r, g, b;
		int[] rgb = new int[3];
		System.out.println("\nTOP/BOTTOM EDGES");
		System.out.println("\nTOP:");
		for (int j = 0; j < inwardDeviation; j++) {
			for (int k = 0; k < slices[i].getWidth(); k++) {
				color = new Color(pixmap.getPixel(k, (slices[i].getHeight() - inwardDeviation) - 1));
				hexString = color.toString();
				hexString = hexString.substring(0, 6);
				rgb = getRGB(hexString);
				r = rgb[0];
				g = rgb[1];
				b = rgb[2];
				if (r < 240 && g < 240 && b < 240)
					topSideBoo[i][k][j] = true;
				System.out.println("Pixel[" + k + "][" + ((slices[i].getHeight() - inwardDeviation) - 1) + "] RGB == "
						+ r + " " + g + " " + b);
				// System.out.println(topSideBoo[i][k]);
			}
			System.out.println("\nBOTTOM:");
			for (int k = 0; k < slices[i].getWidth(); k++) {
				color = new Color(pixmap.getPixel(k, 0 + inwardDeviation));
				hexString = color.toString();
				hexString = hexString.substring(0, 6);
				rgb = getRGB(hexString);
				r = rgb[0];
				g = rgb[1];
				b = rgb[2];
				if (r < 240 && g < 240 && b < 240)
					botSideBoo[i][k][j] = true;
				System.out.println("Pixel[" + k + "][" + (0 + inwardDeviation) + "] RGB == " + r + " " + g + " " + b);
				// System.out.println(botSideBoo[i][k]);
			}
		}
	}

	public static int[] getRGB(final String rgb) // Converts Hex into RGB
	{
		final int[] ret = new int[3];
		for (int i = 0; i < 3; i++) {
			ret[i] = Integer.parseInt(rgb.substring(i * 2, i * 2 + 2), 16);
		}
		return ret;
	}

	public static float round(float d, int decimalPlace) { // Method to control
															// rounding numbers
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	public void render() { // Controls the window that appears when program is
							// run
		Gdx.gl.glClearColor(0, .25f, 0, 0); // controls background color RGBA
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(slices[0], 0, Gdx.graphics.getHeight() - slices[0].getHeight() * 5);
		for (int i = 1; i < sliceCount; i++) {
			if (i < 8)
				batch.draw(slices[i], (slices[i - 1].getWidth() * i) + (25 * i),
						Gdx.graphics.getHeight() - slices[i - 1].getHeight() * 5); // Hacked out to display the images
			else
				batch.draw(slices[i],
						(slices[(i - 1) - 7].getWidth() * (i - 7)) + (25 * (i - 7)) - slices[0].getWidth() - 25,
						((Gdx.graphics.getHeight() - slices[(i - 1) - 7].getHeight() * 2) - 25) * 5);
		}
		batch.end();
	}
}

// for (int y=0;y<slices[i].getHeight();y++){ //checks each pixel left to right
// then top to bottom
// for (int x=0;x<slices[i].getWidth();x++){
// color = new Color(pixmap.getPixel(x, y));
// hexString = color.toString();
// hexString = hexString.substring(0, 6);
// rgb = getRGB(hexString);
// r = rgb[0];
// g = rgb[1];
// b = rgb[2];
//
// System.out.println("Pixel["+x+"]["+y+"] RGB == "+r+" "+g+" "+b+" Hex code:
// "+hexString);
// }
// }
