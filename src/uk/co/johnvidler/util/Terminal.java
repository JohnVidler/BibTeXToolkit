package uk.co.johnvidler.util;

import java.io.*;

public abstract class Terminal
{

    public static boolean booleanQuestion( InputStream inRaw, OutputStream outRaw, String question )
    {
        PrintWriter out = new PrintWriter( outRaw );
        BufferedReader in = new BufferedReader( new InputStreamReader( inRaw ) );

        out.print( question+ " [Y/n] >" );
        String result = null;
        try {
            result = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if( result.length() == 0 || result.equalsIgnoreCase("y") )
            return true;
        return false;
    }

}
