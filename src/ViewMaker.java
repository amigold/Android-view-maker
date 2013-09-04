import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewMaker {

    private static String sourceCode;

    /**
     * @param args
     */
    public static void main(String[] args) {

        String sourceFile = args[0];

        sourceCode = null;
        try {
            sourceCode = FileTools.readFile(sourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String ids = grabIDs();

        if (ids != null && !ids.equals("")) {
            System.out.println("Success!");
            TextTransfer t = new TextTransfer();
            t.setClipboardContents(ids);
        }

    }

    private static String grabIDs() {
        Pattern pa = Pattern.compile("<(\\w+)[^a]+android:id=\"@\\+id/(\\w+)");
        Matcher matcher = pa.matcher(sourceCode);

        StringBuilder res = new StringBuilder();

        while (matcher.find()) {
            if (!matcher.group(1).equals("include")) {
                String varName = matcher.group(2);
                res.append(String.format("%s = (%s) findViewById(R.id.%s);\n",
                                varName, matcher.group(1), varName));
            }
        }

        return res.toString();
    }

    static class TextTransfer implements ClipboardOwner {

        /**
         * Empty implementation of the ClipboardOwner interface.
         */
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
            // do nothing
        }

        /**
         * Place a String on the clipboard, and make this class the owner of the
         * Clipboard's contents.
         */
        public void setClipboardContents(String aString) {
            StringSelection stringSelection = new StringSelection(aString);
            Clipboard clipboard = Toolkit.getDefaultToolkit()
                            .getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        /**
         * Get the String residing on the clipboard.
         * 
         * @return any text found on the Clipboard; if none found, return an
         *         empty String.
         */
        public String getClipboardContents() {
            String result = "";
            Clipboard clipboard = Toolkit.getDefaultToolkit()
                            .getSystemClipboard();
            // odd: the Object param of getContents is not currently used
            Transferable contents = clipboard.getContents(null);
            boolean hasTransferableText = (contents != null)
                            && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                try {
                    result = (String) contents
                                    .getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException ex) {
                    // highly unlikely since we are using a standard DataFlavor
                    System.out.println(ex);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
            }
            return result;
        }
    }

}