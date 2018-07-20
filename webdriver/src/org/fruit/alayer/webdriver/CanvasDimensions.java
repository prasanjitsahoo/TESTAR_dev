package org.fruit.alayer.webdriver;

import org.fruit.Util;
import org.openqa.selenium.WebDriverException;

import java.util.List;


/*
 * Periodically poll position of the browser window (viewport)
 * Browsers don't have event listeners for window movement
 */
public class CanvasDimensions extends Thread {

  private static int canvasX = 0;
  private static int canvasY = 0;
  private static int canvasWidth = 0;
  private static int canvasHeight = 0;
  private static int innerWidth = 0;
  private static int innerHeight = 0;

  private static boolean running = false;

  private CanvasDimensions() {
  }

  public static void startThread() {
    if (running) {
      return;
    }

    running = true;
    new Thread(new CanvasDimensions()).start();
  }

  public static void stopThread() {
    running = false;
  }

  public static int getCanvasX() {
    return canvasX;
  }

  public static int getCanvasY() {
    return canvasY;
  }

  public static int getCanvasWidth() {
    return canvasWidth;
  }

  public static int getCanvasHeight() {
    return canvasHeight;
  }

  public static int getInnerWidth() {
    return innerWidth;
  }

  public static int getInnerHeight() {
    return innerHeight;
  }

  @Override
  public void run() {
    while (running) {
      updateDimensions();
      Util.pause(0.5);
    }
  }

  @SuppressWarnings("unchecked")
  private void updateDimensions() {
    // This assumes no status bars on the left or on the bottom
    try {
      List<Long> screen = (List<Long>)
          WdDriver.executeScript("return canvasDimensionsTestar()");
      if (screen == null) {
        return;
      }
      canvasX = Math.toIntExact(screen.get(0));
      canvasY = Math.toIntExact(screen.get(1));
      canvasWidth = Math.toIntExact(screen.get(2));
      canvasHeight = Math.toIntExact(screen.get(3));
      innerWidth = Math.toIntExact(screen.get(4));
      innerHeight = Math.toIntExact(screen.get(5));
    }
    catch (WebDriverException ignored) {

    }
  }
}
