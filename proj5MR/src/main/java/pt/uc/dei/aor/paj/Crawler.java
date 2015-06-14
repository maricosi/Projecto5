package pt.uc.dei.aor.paj;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler {
	private final static String URL = "http://edition.cnn.com";
	
	public static void main(String[] args) throws DatatypeConfigurationException, IOException {
		
		Document doc = Jsoup.connect(URL).get();
		//String title = doc.title();

		List<String> categories = Arrays.asList(new String[]{"us", "europe", "asia", "africa", "china", "americas", "middleeast"}); 
		Elements noticias = doc.getElementsByClass("cd__headline");

		List<String> links = new ArrayList<>();

		for (Element n : noticias) {
			Element news = n.getElementsByTag("a").first();
			String link = news.attr("href");

			String[] fields = link.split("/");
			
			try {
				if (fields[1].substring(0,3).equals("201") && categories.indexOf(fields[4]) != -1 &&
						!fields[5].equals("gallery")){
					boolean contemNoticia=false;
					for(String l:links){
						if(l.equals(link)){
							contemNoticia=true;
						}
					}

					if(!contemNoticia){
						links.add(link);
					}
				}
			}
			catch(Exception e) {

			}
		}

		for (String l : links) {
			int attempt = 1;
			System.out.println(URL+l);
			while (attempt <= 3) {
				try {
					contextOfNews(URL + l, 1);
					break;
				} catch (IOException e) {
					attempt++;
				}
			}
		}

	}


	public static void contextOfNews(String href, int attempt) throws DatatypeConfigurationException, IOException{
		NoticiaType noticia= new NoticiaType();
		noticia.setUrl(href);
		//System.out.println(href);
		// exemplo de teste
		Document doc = null;
		if (attempt == 1) 
			doc = Jsoup.connect(href).get();
		else 
			doc = Jsoup.connect(href).timeout(attempt*3000).get();
		
		
		Element title = doc.getElementsByClass("pg-headline").first();
		//System.out.println(title.text());
		noticia.setTitulo(title.text());
		
		List<String> months = Arrays.asList(new String[]{"January", "February", "March", "April",
				"May", "June", "July", "August", "September", "October", "November", "December"});
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set(Calendar.SECOND, 0);
		Element data = doc.getElementsByClass("update-time").first();
		//System.out.println(data.text());
		Pattern timePattern = Pattern.compile("(\\d\\d)(\\d\\d) GMT");
		Matcher timeMatcher = timePattern.matcher(data.text());
		if (timeMatcher.find()) {
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeMatcher.group(1)));
			calendar.set(Calendar.MINUTE, Integer.parseInt(timeMatcher.group(2)));
			
		}
		Pattern datePattern = Pattern.compile("(\\w+) (\\d{1,2}), (\\d{4})");
		Matcher dateMatcher = datePattern.matcher(data.text());
		if (dateMatcher.find()) {
			calendar.set(Calendar.MONTH, months.indexOf(dateMatcher.group(1)));
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateMatcher.group(2)));
			calendar.set(Calendar.YEAR, Integer.parseInt(dateMatcher.group(3)));
		}
		//System.out.println(calendar.getTime());
		//System.out.println((new GregorianCalendar()).getTime());
		noticia.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
		

		Element author = doc.getElementsByClass("metadata__byline__author").first();
		//System.out.println(author.text());
		noticia.setAuthor(author.text());
		
		Elements highlights = doc.getElementsByClass("el__storyhighlights__item");
		
		for (Element e : highlights) {
			//System.out.println(e.text());
			noticia.getHighlights().add(e.text());
		}

		Elements paragraphs = doc.getElementsByClass("zn-body__paragraph");
		String news="";
		for (Element e : paragraphs) {
			//System.out.println(e.text());
			news+=e.text()+"\n";
		}
		noticia.setNewstext(news);


		//System.out.println("full Images");
		Elements fullImages = doc.getElementsByClass("el__image--fullwidth");
		for (Element e : fullImages) {
			Element image = e.getElementsByTag("img").first();
			//System.out.println(image.attr("data-src-large"));
			ImageType img = new ImageType();
			img.setUrl(image.attr("data-src-large"));
			
			Elements caption= e.getElementsByClass("media__caption");
			if(caption.size()>0){
				//System.out.println("Captione-img: "+caption.first().text());
				img.setCaption(caption.first().text());
			}
			noticia.getImage().add(img);
		}

		//System.out.println("expand images");
		Elements expandImages= doc.getElementsByClass("el__image--expandable");
		for(Element e: expandImages){
			Element image=e.getElementsByTag("img").first();
			//System.out.println(image.attr("data-src-large"));
			ImageType img = new ImageType();
			img.setUrl(image.attr("data-src-large"));
			Elements caption= e.getElementsByClass("media__caption");
			if(caption.size()>0){
				//System.out.println("Captione-img: "+caption.first().text());
				img.setCaption(caption.first().text());
			}
			noticia.getImage().add(img);
		}


		//System.out.println("Video");
		Elements video = doc.getElementsByClass("js-media__video");
		for (Element e : video) {
			Element url = e.select("meta[itemprop=url]").first();
			//System.out.println(url.attr("content"));
		}
		
		//System.out.println(noticia.getDate());
	}
}
