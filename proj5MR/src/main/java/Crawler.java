import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {
	public static void main(String[] args) throws IOException {
		
			Document doc = Jsoup.connect("http://www.cnn.com/").get();
			//String title = doc.title();

			List<String> categories = Arrays.asList(new String[]{"us", "europe", "asia", "africa", "china", "americas", "middleeast"}); 
			Elements noticias = doc.getElementsByClass("cd__headline");
			
			List<String> links = new ArrayList<>();
			
			for (Element n : noticias) {
				Element news = n.getElementsByTag("a").first();
				String link = news.attr("href");
				
				String[] fields = link.split("/");
				//System.out.println(Arrays.toString(fields));
				
				try {
					if (fields[1].substring(0,3).equals("201") && categories.indexOf(fields[4]) != -1 &&
							!fields[5].equals("gallery")) links.add(link);
				}
				catch(Exception e) {
					
				}
			}
			
			
			for (String l : links) {
				System.out.println(l);
			}
			
			
			// exemplo de teste
			doc = Jsoup.connect("http://edition.cnn.com/2015/06/11/middleeast/iraqi-baiji-mazraa/index.html").get();
			Element data = doc.getElementsByClass("update-time").first();
			System.out.println(data.text());
			
			Element title = doc.getElementsByClass("pg-headline").first();
			System.out.println(title.text());
			
			Element author = doc.getElementsByClass("metadata__byline__author").first();
			System.out.println(author.text());
			
			Elements paragraphs = doc.getElementsByClass("zn-body__paragraph");
			for (Element e : paragraphs) {
				System.out.println(e.text());
			}
			
			Elements highlights = doc.getElementsByClass("el__storyhighlights__item");
			for (Element e : highlights) {
				System.out.println(e.text());
			}
			
			System.out.println("full Images");
			Elements fullImages = doc.getElementsByClass("el__image--fullwidth");
			for (Element e : fullImages) {
				Element image = e.getElementsByTag("img").first();
				System.out.println(image.attr("data-src-large"));
			}
			
			System.out.println("expand images");
			Elements expandImages= doc.getElementsByClass("el__image--expandable");
			for(Element e: expandImages){
				Element image=e.getElementsByTag("img").first();
				System.out.println(image.attr("data-src-large"));
			}
			
			
			System.out.println("Video");
			Elements video= doc.getElementsByClass("media__video");
			// falta fazer
	}
}
