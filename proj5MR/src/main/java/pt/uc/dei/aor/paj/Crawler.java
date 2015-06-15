package pt.uc.dei.aor.paj;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.JMSRuntimeException;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.AttributeWithValueStarting;


public class Crawler {
	private final static String URL = "http://edition.cnn.com";
	
	public static void main(String[] args) throws Exception {
		
		Document doc = null;
		
		int attempt = 1;
		while (attempt <= 4) {
			try {
				doc = Jsoup.connect(URL).timeout(attempt*3000).get();
				break;
			}
			catch(IOException e) {
				attempt++;
			}
		}
		if (doc == null) return;
		
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

		int counter = 1;
		
		List<NoticiaType> news = new ArrayList<>();
		for (String l : links) {
			attempt = 1;
			
			while (attempt <= 4) {
				try {
					NoticiaType noticia = contextOfNews(URL + l, 1);
					System.out.println((counter++)+" "+noticia.getTitulo());
					System.out.println(noticia.getUrl());
					news.add(noticia);
					break;
				} catch (IOException e) {
					attempt++;
				}
			}
		}
		String xml = marshal(news);
		publish(xml);
		System.out.println("published");
		
	}


	public static NoticiaType contextOfNews(String href, int attempt) throws DatatypeConfigurationException, IOException{
		NoticiaType noticia= new NoticiaType();
		noticia.setUrl(href);
		//System.out.println(href);
		// exemplo de teste
		Document doc = Jsoup.connect(href).timeout(attempt*3000).get();
		
		
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
		
		return noticia;
	}
	
	
	
	private static String marshal(List<NoticiaType> noticias) throws Exception{

		  
		String context = "pt.uc.dei.aor.paj";
		
		JAXBContext jc = JAXBContext.newInstance(context);
		
		ObjectFactory factory = new ObjectFactory();
		JornalType jornal = factory.createJornalType();
		
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.AFRICA));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.US));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.ASIA));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.EUROPE));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.MIDDLEAST));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.AMERICAS));
		jornal.getCategoria().add(new CategoriaType(CategoriaTipo.CHINA));
		
		for (NoticiaType n : noticias) {
			jornal.getCategoria().get((int) (Math.random()*7)).getNoticia().add(n);
		}

		
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		m.setProperty("com.sun.xml.internal.bind.xmlHeaders", 
        	    "\n<?xml-stylesheet type=\"text/xsl\" href=\"text.xsl\" ?>");
		StringWriter w = new StringWriter();
		// Write the XML File
		m.marshal(jornal, w);
		return w.toString();
	}
	
	
	
	public static void publish(String xml) throws NamingException {
		ConnectionFactory cf;
		Topic topic;

		cf = InitialContext.doLookup("jms/RemoteConnectionFactory");
		topic = InitialContext.doLookup("jms/topic/testTopic");

		try (JMSContext jcontext = cf.createContext("mr", "mr2015");) {
			JMSProducer mp = jcontext.createProducer();
			mp.send(topic, xml);
		} catch (JMSRuntimeException re) {
			re.printStackTrace();
		}

	}
}
