package edu.knhu;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Crawl {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    ArrayList<HtmlLink> downloaded = new ArrayList<>();
    ArrayList<HtmlLink> toDoLinks = new ArrayList<>();
    HtmlLink seedLink = new HtmlLink("https://nithyananda.org/","Home");

    public static void main(String[] args) throws IOException {
        Crawl crawl = new Crawl();
        crawl.start();
    }

    private  void start() throws IOException {
        toDoLinks.add(seedLink);
        int count = toDoLinks.size();
        int index = 0;

        try{
            do{
                HtmlLink htmlLink =  toDoLinks.get(index++);
                if(!downloaded.contains(htmlLink)) {
                    System.out.println("Index : " + index + " Link : " + htmlLink.link);
                    readLink(htmlLink);
                    count = toDoLinks.size();
                    System.out.println("TODO count: " + count);
                    System.out.println("Downloaded count: " + downloaded.size());
                }
            } while(index<=count);

    } finally {
            httpClient.close();
            System.out.println("Downloded : "+Arrays.toString(downloaded.toArray()));
        }

    }

    private void readLink(HtmlLink link){
        try {
            String fileName = link.linkText.replaceAll("[^0-9a-zA-Z:,]","");
            if(fileName.length()>128) fileName= fileName.substring(0,64);
            System.out.println("fileName: "+fileName);
            HttpGet request = new HttpGet(link.link);
            // add request headers
//            request.addHeader("custom-key", "mkyong");
//            request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Get HttpResponse Status
//                System.out.println(response.getProtocolVersion());              // HTTP/1.1
//                System.out.println(response.getStatusLine().getStatusCode());   // 200
//                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
//                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    downloaded.add(link);
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    FileWriter file = new FileWriter(new File("downloaded", fileName));
                    file.write(result);
                    file.close();
                    System.out.println("..done");
                    HTMLLinkExtractor extractor = new HTMLLinkExtractor();
                    //Regex.Replace(Your String, @"[^0-9a-zA-Z:,]+", "")
                    ArrayList<HtmlLink> links = extractor.grabHTMLLinks(result);
                    ArrayList<HtmlLink> combinedList = (ArrayList<HtmlLink>) Stream.of(toDoLinks, links)
                            .flatMap(x -> x.stream())
                            .collect(Collectors.toList());
                    toDoLinks = combinedList;
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}