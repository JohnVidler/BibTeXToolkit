package uk.co.johnvidler.util;

import java.io.File;
import java.util.*;

public class ReadOptions
{
    protected int idx = 0;
    protected String args[] = null;

    protected HashMap<String, Vector<String>> options = null;

    public ReadOptions( String args[] )
    {
        options = new HashMap<String, Vector<String>>();
        this.args = args;

        String arg = null;
        String key = null;
        String value = null;
        while( (arg = next()) != null )
        {
            if( arg.startsWith("-") )
            {
                if( key != null )
                {
                    if( value != null )
                        putOption( key, value );
                    else
                        putOption( key, null );
                }
                key = arg.substring( 1 );
                value = null;
            }
            else
            {
                if( value != null )
                    putOption( value, null );
                value = arg;
            }
        }
        if( key != null )
        {
            if( value != null )
                putOption( key, value );
            else
                putOption( key, null );
        }
        else
        {
            if( value != null )
                putOption( value, null );
        }
    }

    public void putOption( String key, String value )
    {
        if( !options.containsKey( key ) )
            options.put( key, new Vector<String>() );

        if( value != null )
        {
            String fields[] = value.split( "," );
            if( fields.length > 1 )
            {
                for( String field : fields )
                    options.get( key ).add( field );
            }
            else
                options.get( key ).add( value );
        }
    }

    public String[] getStrings( String key )
    {
        if( options.containsKey( key ) )
            return options.get( key ).toArray( new String[1] );
        return null;
    }

    public String getFirstString( String key )
    {
        String[] strings = getStrings( key );
        if( strings.length > 0 )
            return strings[0];
        return null;
    }

    public File[] getFiles( String key )
    {
        String[] strings = getStrings( key );
        if( strings == null )
            return null;

        File[] files = new File[strings.length];

        for( int i=0; i<strings.length; i++ )
            files[i] = new File( strings[i] );

        return files;
    }

    public boolean isPresent( String key )
    {
        if( options.containsKey( key ) )
            return true;
        return false;
    }

    public int count()
    {
        return options.keySet().size();
    }

    public String next()
    {
        if( idx < args.length )
            return args[idx++];
        return null;
    }
}
