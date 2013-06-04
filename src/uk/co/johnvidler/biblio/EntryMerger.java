package uk.co.johnvidler.biblio;

import java.util.Iterator;
import java.util.TreeSet;

public class EntryMerger implements EntryReader
{
    protected TreeSet<Entry> database = null;
    protected Iterator<Entry> iter = null;

    public EntryMerger( Iterable<Entry> sources[] )
    {
        database = new TreeSet<Entry>();
        for( Iterable<Entry> source : sources )
        {
            for( Entry e : source )
            {
                if( !database.contains(e) )
                {
                    database.add( e );
                }
                else
                {
                    System.out.println( "Duplicate key '" +e.getProperty("key")+ "'" );
                }
            }
        }

        System.out.println( "Total Records: " +database.size() );
    }

    @Override
    public Entry read()
    {
        if( iter == null )
            iter = database.iterator();

        if( iter.hasNext() )
            return iter.next();
        else
            return null;
    }
}
