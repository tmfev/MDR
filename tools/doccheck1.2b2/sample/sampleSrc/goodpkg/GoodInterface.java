/*
 * Copyright 2003 Sun Microsystems, Inc. ALL RIGHTS RESERVED. 
 * Use of this software is authorized pursuant to the terms of the 
 * license found at http://developer.java.sun.com/berkeley_license.html
 */

package goodpkg;

import java.applet.*;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.ColorModel;
import java.net.URL;
import java.util.Enumeration;

/**
 * From AppletContext. This interface corresponds to an applet's environment: 
 * the document containing the applet and the other applets in 
 * the same document.
 * <p>
 * The methods in this interface can be used by an applet to obtain
 * information about its environment.
 *
 * @author 	Arthur van Hoff
 * @version     1.23, 06/29/98
 * @since       JDK1.0
 */
public interface GoodInterface {
    /**
     * Creates an audio clip.
     *
     * @param   url   an absolute URL giving the location of the audio clip.
     * @return  the audio clip at the specified URL.
     */
    AudioClip getAudioClip(URL url);

    /**
     * Returns an <code>Image</code> object that can then be painted on
     * the screen. The <code>url</code> argument<code> </code>that is
     * passed as an argument must specify an absolute URL.
     * <p>
     * This method always returns immediately, whether or not the image
     * exists. When the applet attempts to draw the image on the screen,
     * the data will be loaded. The graphics primitives that draw the
     * image will incrementally paint on the screen.
     *
     * @param   url   an absolute URL giving the location of the image.
     * @return  the image at the specified URL.
     * @see     java.awt.Image
     */
    Image getImage(URL url);

    /**
     * Finds and returns the applet in the document represented by this
     * applet context with the given name. The name can be set in the
     * HTML tag by setting the <code>name</code> attribute.
     *
     * @param   name   an applet name.
     * @return  the applet with the given name, or <code>null</code> if
     *          not found.
     */
    Applet getApplet(String name);

    /**
     * Finds all the applets in the document represented by this applet
     * context.
     *
     * @return  an enumeration of all applets in the document represented by
     *          this applet context.
     */
    Enumeration getApplets();

    /**
     * Replaces the Web page currently being viewed with the given URL.
     * This method may be ignored by applet contexts that are not
     * browsers.
     *
     * @param   url   an absolute URL giving the location of the document.
     */
    void showDocument(URL url);

    /**
     * Requests that the browser or applet viewer show the Web page
     * indicated by the <code>url</code> argument. The
     * <code>target</code> argument indicates in which HTML frame the
     * document is to be displayed.
     * The target argument is interpreted as follows:
     * <p>
     * <center><table border="3">
     * <tr><td><code>"_self"</code>  <td>Show in the window and frame that
     *                                   contain the applet.</tr>
     * <tr><td><code>"_parent"</code><td>Show in the applet's parent frame. If
     *                                   the applet's frame has no parent frame,
     *                                   acts the same as "_self".</tr>
     * <tr><td><code>"_top"</code>   <td>Show in the top-level frame of the applet's
     *                                   window. If the applet's frame is the
     *                                   top-level frame, acts the same as "_self".</tr>
     * <tr><td><code>"_blank"</code> <td>Show in a new, unnamed
     *                                   top-level window.</tr>
     * <tr><td><i>name</i><td>Show in the frame or window named <i>name</i>. If
     *                        a target named <i>name</i> does not already exist, a
     *                        new top-level window with the specified name is created,
     *                        and the document is shown there.</tr>
     * </table> </center>
     * <p>
     * An applet viewer or browser is free to ignore <code>showDocument</code>.
     *
     * @param   url   an absolute URL giving the location of the document.
     * @param   target   a <code>String</code> indicating where to display
     *                   the page.
     */
    public void showDocument(URL url, String target);

    /**
     * Requests that the argument string be displayed in the
     * "status window". Many browsers and applet viewers
     * provide such a window, where the application can inform users of
     * its current state.
     *
     * @param   status   a string to display in the status window.
     */
    void showStatus(String status);
}
