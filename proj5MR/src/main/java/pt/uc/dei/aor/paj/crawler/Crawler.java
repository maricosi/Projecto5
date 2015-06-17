package pt.uc.dei.aor.paj.crawler;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uc.dei.aor.paj.CategoriaTipo;
import pt.uc.dei.aor.paj.CategoriaType;
import pt.uc.dei.aor.paj.ImageType;
import pt.uc.dei.aor.paj.JornalType;
import pt.uc.dei.aor.paj.NoticiaType;
import pt.uc.dei.aor.paj.ObjectFactory;
import pt.uc.dei.aor.paj.VideoType;


public class Crawler {
	private static final Logger logger = LogManager.getLogger(Crawler.class);
	private static final String URL = "http://edition.cnn.com";
	private static final String PACKAGE = "pt.uc.dei.aor.paj";
	private static final int NUMBER_ATTEMPTS = 4;
	
	public static void main(String[] args) throws IOException {
		int attempt=1;
		Document doc = null;
		List<CategoriaType> categorias= new ArrayList<>();
		while(attempt <= NUMBER_ATTEMPTS){
			try{
				doc = Jsoup.connect(URL).get();
				break;
			}catch (IOException e){
				attempt++;
			}
		}
		if (doc == null) {
			logger.error("Página inacessível - "+URL);
			return;
		}
		

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
				// formato desconhecido
			}
		}
		
		for (String l : links) {
			attempt=1;
			while(attempt <= NUMBER_ATTEMPTS){
				try{
					adicionaCategory(contextOfNews(URL + l , attempt),categorias);
					break;
				}catch(IOException e){
					attempt++;
					if (attempt > NUMBER_ATTEMPTS) {
						logger.error("Notícia indisponível: "+URL+l);
					}
				} catch (Exception e) {
					logger.error("Notícia não adicionada (erro data): "+URL+l);
				}
			}
		}
		
		String xml="";
		try {
			xml=marshalXML(categorias);
		} catch (Exception e) {
			logger.error("Erro marshalling "+e.getMessage());
		}
		
		try {
			SendTopic st= new SendTopic();
			st.send(xml);
		} catch (Exception e) {
			logger.error("Tópico indisponível");
			return;
		}
		
		
	}
	
	
	public static void adicionaCategory (NoticiaType n , List<CategoriaType> listaCategorias){
		String cat=n.getUrl().split("/")[6];
		boolean exist=false;
		for (CategoriaType c:listaCategorias){	
			if(c.getTipo().equals(CategoriaTipo.fromValue(cat))){
				exist=true;
				c.getNoticia().add(n);
			}
		}
		if(!exist){
			System.out.println(cat);
			CategoriaType catType=new CategoriaType(CategoriaTipo.fromValue(cat));
			listaCategorias.add(catType);
			catType.getNoticia().add(n);
		}
	}
	
	public static String marshalXML(List<CategoriaType> catType) throws Exception {

		String context = PACKAGE;
		
		JAXBContext jc = JAXBContext.newInstance(context);
		
		ObjectFactory factory = new ObjectFactory();
		JornalType jornal = factory.createJornalType();

		jornal.getCategoria().addAll(catType);
		
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		
		StringWriter w=new StringWriter();
		// Write the XML File
		m.marshal(jornal, w);
		return w.toString();

	}

	public static NoticiaType contextOfNews(String href, int attempt) throws Exception{
		NoticiaType noticia= new NoticiaType();
		noticia.setUrl(href);
		//System.out.println(href);
		
		Document doc = Jsoup.connect(href).timeout(attempt*3000).get();
		
		Element title = doc.getElementsByClass("pg-headline").first();
		//System.out.println(title.text());
		noticia.setTitulo(title.text());
		
		Element data = doc.getElementsByClass("update-time").first();
		//System.out.println(data.text());
		GregorianCalendar dateNoticia= new GregorianCalendar();
		// Regular Expression \\d{4} (example:2256) 
		Pattern p1=Pattern.compile("(\\d{1,2})(\\d{2}) GMT");
		Matcher m1 = p1.matcher(data.text());
		int hour = -1, min = -1,day = -1,year=-1;
		int month = -1;
		if(m1.find()){
			hour=Integer.parseInt(m1.group(1));
			min=Integer.parseInt(m1.group(2));
		}
		
		Pattern p2=Pattern.compile("(\\w+) (\\d{1,2}), (\\d{4})");
		Matcher m2 = p2.matcher(data.text());
		List <String> months= Arrays.asList(new String[]{"January", "February", "March", "April","May", "June","July", "August","September","October","November","December"});
		
		if(m2.find()){
			month=months.indexOf(m2.group(1));
			day=Integer.parseInt(m2.group(2));
			year=Integer.parseInt(m2.group(3));
		}
		 dateNoticia.setTimeZone(TimeZone.getTimeZone("GMT"));
		 dateNoticia.set(year, month,day,hour,min);
		 //System.out.println(dateNoticia.getTime());
		
		 XMLGregorianCalendar date = null;
		try {
			date = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateNoticia);
		} catch (DatatypeConfigurationException e1) {
			throw new Exception();
		}
		noticia.setDate(date);

		Element author = doc.getElementsByClass("metadata__byline__author").first();
		//System.out.println(author.text());
		noticia.setAuthor(author.text());
		
		Elements highlights = doc.getElementsByClass("el__storyhighlights__item");
		
		for (Element e : highlights) {
			//System.out.println(e.text());
			noticia.getHighlights().add(e.text());
		}

		Elements paragraphs = doc.getElementsByClass("zn-body__paragraph" );
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
			VideoType videoURL = new VideoType();
			Element url = e.select("meta[itemprop=url]").first();
			//System.out.println(url.attr("content"));
			videoURL.setUrl(url.attr("content"));
			Elements caption=e.getElementsByClass("media__caption");
			if(caption.size()>0){
				//System.out.println(caption.first().text());
				videoURL.setCaption(caption.first().text());
			}
			noticia.getVideoURLS().add(videoURL);
		}
		
		return noticia;
	}
	
	
	
}
