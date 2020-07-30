package edu.knhu;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLLinkExtractor {

    private Pattern patternTag, patternLink;
    private Matcher matcherTag, matcherLink;

    private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_A_HREF_TAG_PATTERN =
            "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";


    public HTMLLinkExtractor() {
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
    }

    /**
     * Validate html with regular expression
     *
     * @param html
     *            html content for validation
     * @return Vector links and link text
     */
    public ArrayList<HtmlLink> grabHTMLLinks(final String html) {

        ArrayList<HtmlLink> result = new ArrayList<HtmlLink>();
        matcherTag = patternTag.matcher(html);
        while (matcherTag.find()) {
            String href = matcherTag.group(1); // href
            String linkText = matcherTag.group(2); // link text
            matcherLink = patternLink.matcher(href);
//            href =href.toLowerCase();
{
                while (matcherLink.find()) {
                    String link = matcherLink.group(1).toLowerCase(); // link
                    link = link.replaceAll("\"", "");
                    boolean hasHttpOrHttps = (link.startsWith("http://") || link.startsWith("https://"));
                    boolean isHomeDomain = (link.startsWith("http://nithyananda.org") || link.startsWith("https://nithyananda.org"));
                    if(!hasHttpOrHttps || isHomeDomain) {
                        if(!hasHttpOrHttps) {
                            if(!link.startsWith("/")) link = "/"+link;
                            link = "https://nithyananda.org" + link;
                            }
                        HtmlLink obj = new HtmlLink(link,linkText);
                        result.add(obj);
                    }
                    }
                }
            }

        return result;
    }

}