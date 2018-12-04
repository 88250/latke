/*
 * This file is part of the URI Template library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at 
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.weborganic.furi;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.weborganic.furi.URIResolver.MatchRule;


/**
 * Convenience class to invoke this library on the command-line.
 * 
 * @author Christophe Lauret
 * @version 13 February 2009
 */
public final class Main {

    /**
     * Prevents creation of instances.
     */
    private Main() {}

    /**
     * Invokes this tool on the command-line.
     * 
     * @param args The command-line parameters.
     */
    public static void main(String[] args) throws IOException {
        // parse a template
        if (args.length == 2 && "-parse".equals(args[0])) {
            main_parse(args[1]);

            // resolve a URI from a list of patterns
        } else if (args.length == 3 && "-resolve".equals(args[0])) {
            main_resolve(args[1], args[2]);

            // all other cases
        } else {
            usage(null);
        }
    }

    /**
     * Displays the usage of this class on System.err.
     * 
     * @param message Any message (optional)
     */
    public static void usage(String message) {
        if (message != null) {
            System.err.println(message);
        }
        System.err.println("URI Template");
        System.err.println("Usage: java " + Main.class.getName() + " [options]");
        System.err.println("where options include:");
        System.err.println("  -parse <template>      Parse the given URI template");
        System.err.println("  -resolve <file> <uri>  Resolve the given URI from the patterns in file");
    }

    // private helpers
    // --------------------------------------------------------------------------

    /**
     * Parses the template.
     * 
     * Explanations on System.out.
     * 
     * @param exp The template to parse.
     */
    private static void main_parse(String exp) throws IOException {
        try {
            URITemplate template = new URITemplate(exp);

            for (Token t : template.tokens()) {
                System.err.println(t.getClass().getSimpleName() + "\t" + t.expression());
            }
        } catch (URITemplateSyntaxException ex) {
            System.err.println("Not a valid URI template.");
        }
    }

    /**
     * Matches and resolves the given URI with the list of patterns.
     * 
     * Results on System.out.
     * 
     * @param filename The name of the file containing the list of patterns.
     * @param uri The URI to match and resolve.
     */
    private static void main_resolve(String filename, String uri) throws IOException {
        // load the URI Patterns from the file
        File f = new File(filename);

        if (!f.exists()) {
            usage("Could not find file " + f.getName());
            return;
        }
        List<URIPattern> patterns = toPatterns(f);

        if (patterns.size() == 0) {
            usage("No pattern in file " + f.getName());
            return;
        }
        // find the best matching pattern
        URIResolver resolver = new URIResolver(uri);
        Collection<URIPattern> matches = resolver.findAll(patterns);

        // no pattern matching
        if (matches.size() == 0) {
            System.err.println("No matching patterns for URI.");
        } else {
            System.out.println(matches.size() + " matching patterns for URI:");
            for (URIPattern p : matches) {
                boolean best = (p == resolver.find(patterns, MatchRule.BEST_MATCH));
                boolean first = (p == resolver.find(patterns, MatchRule.FIRST_MATCH));

                System.out.println(p + (best ? " [BEST]" : "") + (first ? " [FIRST]" : ""));
                // resolve variables
                ResolvedVariables result = resolver.resolve(p);

                System.out.println("with");
                for (String name : result.names()) {
                    System.out.println("\t" + name + "=" + result.get(name));
                }
            }
        }
    }

    /**
     * Returns the contents of the specified file as a list of URI patterns.
     * 
     * @param file The file containing the URI patterns.
     * 
     * @return The list of URI patterns.
     */
    private static final List<URIPattern> toPatterns(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        // read the file
        ArrayList<URIPattern> patterns = new ArrayList<URIPattern>();

        while ((line = reader.readLine()) != null) {
            try {
                patterns.add(new URIPattern(line));
            } catch (URITemplateSyntaxException ex) {
                System.err.println("Could not parse '" + line + "' as pattern - ignored");
            }
        }
        reader.close();
        return patterns;
    }

}
