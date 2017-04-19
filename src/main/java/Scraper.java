/*** Created by samuel.swayze on 10/12/2016. ***/

import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
//import java.util.ListIterator;


public class Scraper {

    public static void main(String[] args) throws Exception {

        // define recipe index document and return ArrayList of URLs for each recipe/blogpost
        String blog = "http://mamaozzystable.blogspot.com/";
        Document doc = Jsoup.parse(getPageSrc(blog));
        ArrayList<String> recipeIndex = getRecipeIndex(doc);

        // create directory and set path for saving
        String parentFolder = System.getProperty("user.home") + "\\Desktop\\MamaOzzysTable_BlogPosts";
        new File(parentFolder).mkdirs();

        int count = 0;
        for (String recipe : recipeIndex) {

            // scrape relevant text; This will need to be looped for each entry in the recipeIndex
            Document docBlog = Jsoup.connect(recipe).get();
            String postDate = docBlog.select("h2.date-header span").text();
            String postTitle = docBlog.select("h3.post-title.entry-title").text();

            Elements postImages = docBlog.select("div.post-body.entry-content a img, div.separator a img");

            // blogpost body, requires some formatting <br>'s instead of <p>'s so it will be fun to figure out
            Elements entryBody = docBlog.select("div.post-body.entry-content");
            LinkedList<String> postBody = getParagraphsFromHtml(entryBody);
            String postFolder = parentFolder + "\\" + postTitle.replaceAll("\"", "");
            new File(postFolder).mkdirs();
            makeDocxFile(postDate, postTitle, postBody, postFolder);

            // blogpost images, "scrape" and save to same directory as Document created above
            //Elements postImages = docBlog.select("div.post-body.entry-content a img, div.separator a img");
            System.out.println("Expected Image Count = " + postImages.size());
            int imageCount = 0;
            for (Element e : postImages) {
                String postImage = e.attr("abs:src");
                URL imageUrl = new URL(postImage);
                imageCount++;
                File image = new File(postFolder + "\\image" + imageCount + ".jpg");
                FileUtils.copyURLToFile(imageUrl, image);
                System.out.println("Image #" + imageCount + " Saved Successfully");
            }
            if (imageCount == postImages.size()) {
                System.out.println("All Images Saved For Post: " + postTitle);
            } else {
                System.out.println("An Error Occurred When Saving The Images For Post: " + postTitle);
            }

            count++;
            System.out.println("Post #" + count + " Finished Downloading");
            Thread.sleep(15000);
        }
    }

    // Create and return ArrayList indexing the URLs for each recipe/blogpost
    public static ArrayList getRecipeIndex(Document indexHtml) {
        System.out.println("Generating Recipe Index...");
        org.jsoup.select.Elements links = indexHtml.select("ul.posts li a");
        System.out.println("Expected Recipe Count = " + links.size());
        int i = 0;    // integer for counting total recipes in document index
        ArrayList recipes = new ArrayList();

        for (Element e : links) {
            String link = e.attr("abs:href");
            recipes.add(link);
            i++;   // used to count total recipes in document index
            System.out.println(i + e.attr("abs:href"));
        }

        if (links.size() == recipes.size()) {
            System.out.println("Recipe Index Generated Successfully");
        } else {
            System.out.println("Recipe Index Generated With Errors");
        }

        return recipes;
    }

    // Create array with entries for every paragraph based on <br> tags to be used later when making word document
    public static LinkedList<String> getParagraphsFromHtml(Elements eles) {
        /*
        * Clean up after actual use:
        * -roughParagraphs as different list type?
        */
        eles.select("div[style]").remove();     //removes images and captions from the source
        String str1 = Parser.unescapeEntities(eles.html(), false);      //unescapes HTML entities
        String[] roughParagraphs = str1.split("<br>");      //removes <br>'s used to separate paragraphs while splitting the string into an array
        LinkedList<String> postParagraphs = new LinkedList<String>();

        for(int i=0; i < roughParagraphs.length; i++) {
            Document tmpDoc = Jsoup.parse(roughParagraphs[i]);
            roughParagraphs[i] = tmpDoc.text();
            if (!roughParagraphs[i].equals("")){
                postParagraphs.add(roughParagraphs[i]);
            }
        }

        return postParagraphs;
    }

    // Create Word document from scraped information and saved it to a local folder
    public static void makeDocxFile(String date, String title, LinkedList<String> body, String path) throws Exception{
        XWPFDocument doc = new XWPFDocument();

        // Make title Paragraph
        XWPFParagraph p1 = doc.createParagraph();

        // Make run of text for the title
        XWPFRun r1 = p1.createRun();
        r1.setFontFamily("Courier");
        r1.setFontSize(18);
        r1.setBold(true);
        r1.setText(title);
        r1.addCarriageReturn();

        // Make date Paragraph
        XWPFParagraph p2 = doc.createParagraph();

        // Make run of text for the date
        XWPFRun r2 = p2.createRun();
        r2.setFontFamily("Courier");
        r2.setFontSize(10);
        r2.setText(date);
        r2.addCarriageReturn();

        // Make body of document by iterating through the body LinkedList
        for (String s: body) {

            // Make body Paragraph
            XWPFParagraph p3 = doc.createParagraph();

            // Make run of text for the body
            XWPFRun r3 = p3.createRun();
            r3.setFontFamily("Courier");
            r3.setFontSize(12);
            r3.setText(s);
            r3.addCarriageReturn();

        }


        FileOutputStream out = new FileOutputStream(path + "\\" + title.replaceAll("\"", "") + ".docx");
        doc.write(out);
        out.close();

        System.out.println(title + " Saved Successfully");

    }

    public static String getPageSrc(String blogPage) throws Exception{
        /*
        * Clean up after actual use:
        * -be headless using phantomJS
        * -use WebDriver wait (instead of Thread.sleep) to wait for toggles --May or may not work
        */
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\samuel.swayze\\IdeaProjects\\mama-ozzys-scraper\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        //WebDriver driver = new HtmlUnitDriver();
        System.out.println("Browser Powering ON...");
        driver.get(blogPage);

        List<WebElement> toggles = driver.findElements(By.cssSelector("li.archivedate.collapsed a.toggle"));
        System.out.println("Expected Toggle Count = " + toggles.size());

        int j = 0;
        for (int i = 0; i < toggles.size(); i++) {
            if (toggles.get(i).isDisplayed()) {
                toggles.get(i).click();
                j++;
                System.out.println("Toggling #" + j);
                Thread.sleep(1000);
            }
        }

        if (driver.findElements(By.cssSelector("li.archivedate.collapsed a.toggle")).size() == 0) {
            System.out.println("All Toggles Successfully Toggled");
        } else {
            System.out.println("Finished Toggling With Errors");
        }


        String pageSrc = driver.getPageSource();
        System.out.println("New Page Source Stored Successfully");
        driver.quit();
        System.out.println("Browser Powering OFF...");
        return pageSrc;
    }

}
