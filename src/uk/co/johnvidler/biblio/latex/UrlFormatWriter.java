package uk.co.johnvidler.biblio.latex;

import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.EntryWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlFormatWriter implements EntryWriter
{
    protected static final Pattern urlPattern = Pattern.compile( "(\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])" );
    protected static String fields[] = { "url", "howpublished", "note", "ee", "biburl", "doi" };
    protected EntryWriter output = null;

    public UrlFormatWriter( EntryWriter output )
    {
        this.output = output;
    }

    @Override
    public void write( Entry entry )
    {
        if( output == null )
            return;

        for( String property : fields )
        {
            if( entry.hasProperty( property ) )
                entry.setProperty( property, detectUrlAndRewrite( entry.getProperty( property ) ) );
        }

        output.write( entry );
    }

    protected String detectUrlAndRewrite( String value )
    {
        if( value.startsWith("\"") || value.startsWith("'") )
            value = value.substring( 1, value.length()-2 );    // Snip the outer parenthesis or quotes off if they're present to escape data

        Matcher urlMatcher = urlPattern.matcher( value );
        if( urlMatcher.matches() )
        {
            String url = urlMatcher.group( 1 );

            return "\\url{" +url+ "}";
        }

        return value;
    }
}
