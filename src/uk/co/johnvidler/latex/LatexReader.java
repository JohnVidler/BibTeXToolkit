package uk.co.johnvidler.latex;

import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatexReader
{
    protected static final Pattern citationPattern = Pattern.compile( "\\\\cite(.?)\\{([^\\{\\}]+)\\}");
    protected static final Pattern includePattern  = Pattern.compile( "\\\\include\\{([^\\{\\}]+)\\}");
    protected static final Pattern inputPattern    = Pattern.compile( "\\\\input\\{([^\\{\\}]+)\\}");
    protected static final Pattern subfilePattern  = Pattern.compile( "\\\\subfile\\{([^\\{\\}]+)\\}");

    protected Vector<File>   fileSet = new Vector<File>();
    protected Vector<String> citations = new Vector<String>();

    public LatexReader( File latexFile ) throws IOException
    {
        parseFile( latexFile );


        for( String citation : citations )
            System.out.println( "Cite: " +citation );
    }


    public void parseFile( File file )
    {
        if( !file.exists() )
        {
            System.err.println( "Could not find '" +file.getAbsolutePath()+ "'" );
            file = new File( file.getAbsoluteFile()+".tex" );
            if( !file.exists() )
            {
                System.err.println( "Could not find '" +file.getAbsolutePath()+ "'" );
                return;
            }
        }

        System.out.println( "Processing " +file.toString()+ " ..." );
        try
        {
            BufferedReader stream = new BufferedReader( new FileReader(file) );

            String lineBuffer = null;
            while( (lineBuffer = stream.readLine()) != null )
            {
                // Citations
                Matcher citation = citationPattern.matcher( lineBuffer );
                if( citation.find() )
                    citations.add( citation.group(2) );

                // Inclusions
                Matcher include = includePattern.matcher( lineBuffer );
                if( include.find() )
                    parseFile( new File(include.group(1)) );

                Matcher input = inputPattern.matcher( lineBuffer );
                if( input.find() )
                {
                    if( !input.group(1).endsWith(".tex") )
                        parseFile( new File(file.getParent(), input.group(1)+".tex") );
                    else
                        parseFile( new File(file.getParent(), input.group(1)) );
                }

                Matcher subfile = subfilePattern.matcher( lineBuffer );
                if( subfile.find() )
                {
                    if( !subfile.group(1).endsWith(".tex") )
                        parseFile( new File(file.getParent(), subfile.group(1)+".tex") );
                    else
                        parseFile( new File(file.getParent(), subfile.group(1)) );
                }
            }

            stream.close();
        }
        catch( IOException err )
        {
            System.out.println( "Failed to parse!" );
        }
    }


    public Vector<String> getCitations()
    {
        return citations;
    }
}
