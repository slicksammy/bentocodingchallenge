import java.io.*;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Philosophy { 
    // Instance Variables 
    String url;   
    ArrayList<String> searches;
    boolean error;
    String errorMessage;
  
    // Constructor Declaration of Class 
     public static void main(String[] args) { 
        Philosophy philosophy = new Philosophy(args[0]); 
        philosophy.getPhilosophy();
    } 

    public Philosophy(String startUrl) { 
        this.url = standardizedUrl(startUrl);
        this.searches = new ArrayList<String>();
    }

    public void getPhilosophy() {
        // we keep looping until there is an error, no new page to check, or we've hit the philosophy page
        while(true) {
            System.out.println("loading : " + this.url);
            
            loadPage();
            
            if (this.error) {
                System.out.println(this.error);
                break;  
            }
        
            if (this.url == null) {
                System.out.println(noUrlError()); // could make this a different error message
                break;   
            }

            if (isPhilosophy()) {
                System.out.println(finalResult());
                break;
            }

        }
    }

    // in the future this method should parse out params
    private String standardizedUrl(String url) {
        return url;
    }

    // you can see that we are using "Philosoph" instead of "Philosophy" that is because there are redirects
    // in the future we should handle redirects
    private boolean isPhilosophy() {
        return this.url.contains("wikipedia.org/wiki/Philosoph");
    }

    public String finalResult() {
        String response = "We were able to find the Philosophy page.";
        response += "It took " + this.searches.size() + " search(es).";
        response += " The path to get here was ";
        response += String.join(",", this.searches);

        return response;
    }

    public String noUrlError() {
        String response =  "We were not able to reach the Philosophy page.";
        return response;
    }

    private void loadPage() {
        try { 
            // get the page contents
            // should handle redirects here
            Document doc = Jsoup.connect(this.url).get();
            Elements elements = doc.select(".mw-parser-output > p a");
            // add url current url to existing searches and reset it
            this.searches.add(this.url);
            this.url = null;
            
            for (Element element : elements) {
                if(eligibleLink(standardizedUrl(element.absUrl("href")))) {
                    this.url = standardizedUrl(element.absUrl("href"));
                    break;
                }
            }
        }
        catch (Exception exception) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exception.printStackTrace(printWriter);
            System.out.println("Exception in String is :: " + writer.toString());
            this.error = true;
            this.errorMessage = "Exception in String is :: " + writer.toString();
        }
    }

    // wiki also mentions things about "non-italicized" and "non-red" and those are not handled here
    private boolean eligibleLink(String link) {
        return link != null && isWikiLink(link) && notLoop(link)  && nonParenthesized(link) && notHelpPage(link)  && notFile(link);
    }

    private boolean notLoop(String link) {
        if (this.searches.contains(link)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isWikiLink(String link) {
        return link.contains("wikipedia.org/wiki");
    }

    private boolean nonParenthesized(String link) {
        return !link.contains("(");
    }

    // ignore pages linking to language help
    private boolean notHelpPage(String link) {
        return !link.contains("Help:IPA");
    }

    // ignore File links, such as pronunciation
    private boolean notFile(String link) {
        return !link.contains("File:");
    }
}
