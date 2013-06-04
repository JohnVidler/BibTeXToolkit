package uk.co.johnvidler.bibtool;

import uk.co.johnvidler.biblio.Entry;
import uk.co.johnvidler.biblio.bibtex.BibTeXReader;
import uk.co.johnvidler.biblio.bibtex.BibTeXWriter;
import uk.co.johnvidler.biblio.latex.UrlFormatWriter;
import uk.co.johnvidler.latex.LatexReader;
import uk.co.johnvidler.util.ReadOptions;
import uk.co.johnvidler.util.Terminal;

import java.io.*;
import java.util.*;

public class BibTool
{

    public static void main( String args[] ){ new BibTool( args ); }
    public BibTool( String args[] )
    {
        ReadOptions opts = new ReadOptions( args );

        File   latex_src[]    = opts.getFiles("t");
        File   sources[]      = opts.getFiles("i");
        File   destinations[] = opts.getFiles("o");

        PrintStream outputStream = System.out;

        if( opts.count() == 0 || opts.isPresent("-help") )
        {
            System.out.println( "--help\tDisplay this help" );
            System.exit( 0 );
        }

        if( destinations != null && destinations.length > 0 )
        {
            if( destinations.length > 1 )
            {
                System.err.println( "Only one output makes any sense!" );
                System.exit( 1 );
            }

            try
            {
                outputStream = new PrintStream( destinations[0] );
            }
            catch (FileNotFoundException e)
            {
                System.err.println( "Unable to open '" +destinations[0].getAbsolutePath()+ "' for writing, using stdout." );
                outputStream = System.out;
            }
        }


        // If we have latex files to parse, do this first to build a search key index.
        Vector<String> searchKeys = null;
        if( latex_src != null && latex_src.length > 0 )
        {
            searchKeys = new Vector<String>();
            for( File latex : latex_src )
                searchKeys = buildSearchKeyIndex( latex, searchKeys );
        }

        // Build a complete database from our input sources
        ArrayList<Entry> database = new ArrayList<Entry>();
        for( File source : sources )
        {
            System.out.println( "Reading: " + source.getAbsolutePath() );
            database = buildDatabaseFromFile( source, database );
        }

        // Ouptut to stdout!
        BibTeXWriter output = new BibTeXWriter( new BufferedWriter(new OutputStreamWriter( outputStream )) );
        UrlFormatWriter urlFormatWriter = new UrlFormatWriter( output );

        for( Entry e : database )
            urlFormatWriter.write( e );

        System.out.println( "Done!" );
    }

    public Vector<String> buildSearchKeyIndex( File latexFile, Vector<String> parent )
    {
        Vector<String> searchKeys = new Vector<String>();

        if( parent != null )
            searchKeys = (Vector<String>)parent.clone();

        if( latexFile != null )
        {
            try
            {
                LatexReader latexReader = new LatexReader( latexFile );
                searchKeys = latexReader.getCitations();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return searchKeys;
    }

    public ArrayList<Entry> buildDatabaseFromFile( File file, ArrayList<Entry> parent )
    {
        ArrayList<Entry> database = new ArrayList<Entry>();
        if( parent != null )
            database = (ArrayList<Entry>)parent.clone();

        try
        {
            BibTeXReader input = new BibTeXReader( new FileInputStream( file ));

            Entry entry = null;
            while( (entry = input.read()) != null )
            {
                if( !database.contains( entry ) )
                    database.add( entry );
                else
                {
                    Collections.sort( database );
                    Entry resident = database.get(Collections.binarySearch(database, entry));

                    BibTeXWriter terminalOut = new BibTeXWriter( new BufferedWriter(new OutputStreamWriter( System.out ) ) );

                    System.out.println( "In database: " );
                    terminalOut.write( resident );

                    System.out.println( "Conflictin item:" );
                    terminalOut.write( entry );

                    boolean doMerge = Terminal.booleanQuestion( System.in, System.out, "Merge these records?" );
                }
            }
        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        return database;
    }

}